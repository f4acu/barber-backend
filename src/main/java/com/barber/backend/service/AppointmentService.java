package com.barber.backend.service;

import com.barber.backend.dto.*;
import com.barber.backend.model.*;
import com.barber.backend.repository.*;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    
    private static final LocalTime OPENING_TIME = LocalTime.of(9, 0);
    private static final LocalTime BREAK_START = LocalTime.of(13, 0);
    private static final LocalTime BREAK_END = LocalTime.of(16, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(20, 0);
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ProfessionalRepository professionalRepository;
    private final ServiceRepository serviceRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              UserRepository userRepository,
                              ProfessionalRepository professionalRepository,
                              ServiceRepository serviceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.professionalRepository = professionalRepository;
        this.serviceRepository = serviceRepository;
    }

    public AppointmentResponse createAppointment(AppointmentRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Professional professional = professionalRepository.findById(request.getProfessionalId())
                .orElseThrow(() -> new RuntimeException("Peluquero no encontrado"));

        ServiceEntity service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        LocalDateTime start = LocalDateTime.of(request.getDate(), request.getTime());

        // 1️⃣ No pasado
        if (start.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No se puede reservar el turno");
        }

        // calcular fin
        LocalDateTime end = start.plusMinutes(service.getDuration());

        LocalTime startTime = start.toLocalTime();
        LocalTime endTime = end.toLocalTime();

        if (startTime.isBefore(OPENING_TIME) || endTime.isAfter(CLOSING_TIME)) {
            throw new RuntimeException("Horario fuera de atención (09:00 - 20:00)");
        }

        boolean overlapsBreak =
        startTime.isBefore(BREAK_END) &&
        endTime.isAfter(BREAK_START);

        if (overlapsBreak) {
            throw new RuntimeException("Horario no disponible (descanso 13:00 - 16:00)");
        }

        boolean overlaps = appointmentRepository.existsOverlappingAppointment(
                professional.getId(), start, end
        );

        if (overlaps) {
            throw new RuntimeException("El peluquero ya tiene un turno en ese horario");
        }

        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setProfessional(professional);
        appointment.setService(service);
        appointment.setStartTime(start);
        appointment.setEndTime(end);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        Appointment saved = appointmentRepository.save(appointment);

        return mapToResponse(saved);
    }

    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getStartTime().toLocalDate(),
                appointment.getStartTime().toLocalTime(),
                appointment.getStatus().name(),
                appointment.getUser().getName(),
                appointment.getProfessional().getName(),
                appointment.getService().getName()
        );
    }

    public void cancelAppointment(Long id) {

    Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

    appointment.setStatus(AppointmentStatus.CANCELLED);

    appointmentRepository.save(appointment);
    }

    public void updateStatus(Long id, UpdateStatusRequest request) {
    Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

    try {
        AppointmentStatus status = AppointmentStatus.valueOf(request.getStatus());
        appointment.setStatus(status);
    } catch (IllegalArgumentException e) {
        throw new RuntimeException("Estado inválido");
    }

    appointmentRepository.save(appointment);
    }
}