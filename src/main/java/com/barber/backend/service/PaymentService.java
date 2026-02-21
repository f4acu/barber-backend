package com.barber.backend.service;

import com.barber.backend.dto.PaymentResponse;
import com.barber.backend.model.*;
import com.barber.backend.repository.AppointmentRepository;
import com.barber.backend.repository.PaymentRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;

    @Value("${app.base-url}")
    private String baseUrl;

    public PaymentService(PaymentRepository paymentRepository,
                         AppointmentRepository appointmentRepository,
                         EmailService emailService) {
        this.paymentRepository = paymentRepository;
        this.appointmentRepository = appointmentRepository;
        this.emailService = emailService;
    }

    @Transactional
    public PaymentResponse createPaymentPreference(Long appointmentId, Long userId) {
        
        // Validar que el turno existe y pertenece al usuario
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        if (!appointment.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para pagar este turno");
        }

        // Validar que el turno no esté cancelado
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("No se puede pagar un turno cancelado");
        }

        // NUEVO: Validar que la barberia tenga pagos habilitados
        Barbershop barbershop = appointment.getBarbershop();
        if (!barbershop.getPaymentEnabled() || 
            barbershop.getMercadoPagoAccessToken() == null || 
            barbershop.getMercadoPagoAccessToken().isEmpty()) {
            throw new RuntimeException("Esta peluquería no tiene pagos online habilitados");
        }

        // Validar que no tenga un pago pendiente o aprobado
        paymentRepository.findByAppointmentId(appointmentId).ifPresent(existingPayment -> {
            if (existingPayment.getStatus() == PaymentStatus.APPROVED) {
                throw new RuntimeException("Este turno ya fue pagado");
            }
            if (existingPayment.getStatus() == PaymentStatus.PENDING) {
                throw new RuntimeException("Ya existe un pago pendiente para este turno");
            }
        });

        try {
            // CRÍTICO: Configurar el Access Token de la barberia específica
            MercadoPagoConfig.setAccessToken(barbershop.getMercadoPagoAccessToken());

            // Crear item de Mercado Pago
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .title(appointment.getService().getName() + " - " + barbershop.getName())
                    .description("Turno con " + appointment.getProfessional().getName() + 
                               " el " + formatDate(appointment.getStartTime()))
                    .quantity(1)
                    .unitPrice(BigDecimal.valueOf(appointment.getService().getPrice()))
                    .currencyId("ARS")
                    .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            // URLs de retorno
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(baseUrl + "/payment-success?appointmentId=" + appointmentId)
                    .failure(baseUrl + "/payment-failure?appointmentId=" + appointmentId)
                    .pending(baseUrl + "/payment-pending?appointmentId=" + appointmentId)
                    .build();

            // Crear preferencia de pago
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    .autoReturn("approved")
                    .externalReference(String.valueOf(appointmentId))
                    .notificationUrl(baseUrl + "/api/payments/webhook") // Webhook
                    .statementDescriptor(barbershop.getName()) // Aparece en el resumen de la tarjeta
                    .payer(com.mercadopago.client.preference.PreferencePayerRequest.builder()
                            .email(appointment.getUser().getEmail())
                            .name(appointment.getUser().getName())
                            .build())
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            // Guardar pago en la BD
            Payment payment = new Payment();
            payment.setAppointment(appointment);
            payment.setMercadoPagoId(preference.getId());
            payment.setAmount(appointment.getService().getPrice());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setCheckoutUrl(preference.getInitPoint());
            payment.setPayerEmail(appointment.getUser().getEmail());

            Payment savedPayment = paymentRepository.save(payment);

            // Marcar turno como requiere pago
            appointment.setRequiresPayment(true);
            appointmentRepository.save(appointment);

            return new PaymentResponse(
                    savedPayment.getId(),
                    appointment.getId(),
                    preference.getId(),
                    savedPayment.getAmount(),
                    savedPayment.getStatus(),
                    preference.getInitPoint(),
                    savedPayment.getCreatedAt()
            );

        } catch (MPApiException e) {
            throw new RuntimeException("Error de Mercado Pago: " + e.getMessage());
        } catch (MPException e) {
            throw new RuntimeException("Error al conectar con Mercado Pago: " + e.getMessage());
        }
    }

    @Transactional
    public void processWebhookNotification(Long paymentMPId, String status) {
        
        Payment payment = paymentRepository.findByMercadoPagoId(String.valueOf(paymentMPId))
                .orElse(null);

        if (payment == null) {
            System.err.println("Pago no encontrado: " + paymentMPId);
            return;
        }

        PaymentStatus newStatus = mapMercadoPagoStatus(status);
        payment.setStatus(newStatus);

        if (newStatus == PaymentStatus.APPROVED) {
            payment.setPaidAt(LocalDateTime.now());
            
            // Actualizar estado del turno
            Appointment appointment = payment.getAppointment();
            appointment.setStatus(AppointmentStatus.SCHEDULED);
            appointmentRepository.save(appointment);

            // Enviar email de confirmación
            try {
                emailService.sendAppointmentConfirmation(
                    appointment.getUser().getEmail(),
                    appointment.getUser().getName(),
                    appointment.getBarbershop().getName(),
                    formatDate(appointment.getStartTime()),
                    formatTime(appointment.getStartTime())
                );
            } catch (Exception e) {
                System.err.println("Error al enviar email de confirmación: " + e.getMessage());
            }
        }

        paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByAppointment(Long appointmentId, Long userId) {
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        if (!appointment.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para ver este pago");
        }

        Payment payment = paymentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("No se encontró un pago para este turno"));

        return new PaymentResponse(
                payment.getId(),
                appointment.getId(),
                payment.getMercadoPagoId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCheckoutUrl(),
                payment.getCreatedAt()
        );
    }

    private PaymentStatus mapMercadoPagoStatus(String mpStatus) {
        return switch (mpStatus.toLowerCase()) {
            case "approved", "accredited" -> PaymentStatus.APPROVED;
            case "rejected", "cancelled" -> PaymentStatus.REJECTED;
            case "refunded" -> PaymentStatus.REFUNDED;
            case "in_process", "in_mediation" -> PaymentStatus.IN_PROCESS;
            default -> PaymentStatus.PENDING;
        };
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String formatTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}