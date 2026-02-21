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
    private final BarbershopRepository barbershopRepository; // NUEVO

    public AppointmentService(AppointmentRepository appointmentRepository,
                              UserRepository userRepository,
                              ProfessionalRepository professionalRepository,
                              ServiceRepository serviceRepository,
                              BarbershopRepository barbershopRepository) { // NUEVO
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.professionalRepository = professionalRepository;
        this.serviceRepository = serviceRepository;
        this.barbershopRepository = barbershopRepository; // NUEVO
    }

    // MODIFICADO: Ahora recibe userId como parámetro (viene del usuario autenticado)
    public AppointmentResponse createAppointment(AppointmentRequest request, Long userId) {

        // 1. Validar que la barberia existe
        Barbershop barbershop = barbershopRepository.findById(request.getBarbershopId())
                .orElseThrow(() -> new RuntimeException("Peluquería no encontrada"));

        // 2. Validar usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. Validar que el profesional existe Y pertenece a esta barberia
        Professional professional = professionalRepository
                .findByIdAndBarbershopId(request.getProfessionalId(), request.getBarbershopId())
                .orElseThrow(() -> new RuntimeException(
                        "Profesional no encontrado o no pertenece a esta peluquería"));

        // 4. Validar que el servicio existe Y pertenece a esta barberia
        ServiceEntity service = serviceRepository
                .findByIdAndBarbershopId(request.getServiceId(), request.getBarbershopId())
                .orElseThrow(() -> new RuntimeException(
                        "Servicio no encontrado o no pertenece a esta peluquería"));

        LocalDateTime start = LocalDateTime.of(request.getDate(), request.getTime());

        // 5. No turno para el pasado
        if (start.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No se puede reservar ese turno");
        }

        // 6. Calcular fin
        LocalDateTime end = start.plusMinutes(service.getDuration());

        LocalTime startTime = start.toLocalTime();
        LocalTime endTime = end.toLocalTime();

        // 7. Validar horario laboral
        if (startTime.isBefore(OPENING_TIME) || endTime.isAfter(CLOSING_TIME)) {
            throw new RuntimeException("Horario fuera de atención (09:00 - 20:00)");
        }

        // 8. Validar horario de descanso
        boolean overlapsBreak = startTime.isBefore(BREAK_END) && endTime.isAfter(BREAK_START);
        if (overlapsBreak) {
            throw new RuntimeException("Horario no disponible (descanso 13:00 - 16:00)");
        }

        // 9. Validar superposición de turnos (MODIFICADO: ahora con barbershopId)
        boolean overlaps = appointmentRepository.existsOverlappingAppointment(
                professional.getId(),
                barbershop.getId(), // NUEVO parámetro
                start,
                end
        );

        if (overlaps) {
            throw new RuntimeException("El profesional ya tiene un turno en ese horario");
        }

        // 10. Crear el turno
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setProfessional(professional);
        appointment.setService(service);
        appointment.setBarbershop(barbershop); // NUEVO
        appointment.setStartTime(start);
        appointment.setEndTime(end);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setNotes(request.getNotes()); // NUEVO

        Appointment saved = appointmentRepository.save(appointment);

        return mapToResponse(saved);
    }

    // Obtener todos los turnos (considerar filtrar por barberia si es admin)
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // NUEVO: Obtener turnos por barberia
    public List<AppointmentResponse> getAppointmentsByBarbershop(Long barbershopId) {
        return appointmentRepository.findByBarbershopId(barbershopId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // NUEVO: Obtener turnos de un usuario
    public List<AppointmentResponse> getMyAppointments(Long userId) {
        return appointmentRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // MODIFICADO: mapToResponse con los nuevos campos
    private AppointmentResponse mapToResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getStartTime().toLocalDate(),
                appointment.getStartTime().toLocalTime(),
                appointment.getEndTime().toLocalTime(), // NUEVO
                appointment.getStatus().name(),
                appointment.getUser().getName(),
                appointment.getProfessional().getName(),
                appointment.getService().getName(),
                appointment.getService().getPrice(), // NUEVO
                appointment.getService().getDuration(), // NUEVO
                appointment.getBarbershop().getName(), // NUEVO
                appointment.getBarbershop().getId(), // NUEVO
                appointment.getNotes() // NUEVO
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

    // Eliminar turno
    public void deleteAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        appointmentRepository.delete(appointment);
    }
}