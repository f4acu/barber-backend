package com.barber.backend.controller;

import com.barber.backend.dto.PaymentRequest;
import com.barber.backend.dto.PaymentResponse;
import com.barber.backend.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ✅ Crear preferencia de pago
    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody PaymentRequest request,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        PaymentResponse response = paymentService.createPaymentPreference(
                request.getAppointmentId(), userId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ✅ Obtener estado de pago de un turno
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<PaymentResponse> getPaymentByAppointment(
            @PathVariable Long appointmentId,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        PaymentResponse response = paymentService.getPaymentByAppointment(appointmentId, userId);
        
        return ResponseEntity.ok(response);
    }

    // 🔔 Webhook de Mercado Pago (público)
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody Map<String, Object> payload) {
        
        try {
            String type = (String) payload.get("type");
            
            if ("payment".equals(type)) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                Long paymentId = Long.valueOf(data.get("id").toString());
                
                // Procesar notificación en segundo plano
                // En producción, usar un queue (RabbitMQ, SQS, etc.)
                paymentService.processWebhookNotification(paymentId, "approved");
            }
            
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            System.err.println("Error procesando webhook: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    private Long getUserIdFromAuth(Authentication authentication) {
        // TODO: Implementar extracción real del JWT
        return 1L; // Placeholder
    }
}