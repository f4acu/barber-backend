package com.barber.backend.controller;

import com.barber.backend.dto.*;
import com.barber.backend.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
@CrossOrigin
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // ✅ Crear turno (requiere autenticación)
    @PostMapping
    public ResponseEntity<AppointmentResponse> create(
            @RequestBody AppointmentRequest request,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        AppointmentResponse response = appointmentService.createAppointment(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 🔒 Obtener turnos por barbershop (solo ADMIN de esa barbershop)
    @PreAuthorize("@barbershopSecurity.canAccessBarbershop(authentication, #barbershopId)")
    @GetMapping("/barbershop/{barbershopId}")
    public ResponseEntity<List<AppointmentResponse>> getByBarbershop(
            @PathVariable Long barbershopId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByBarbershop(barbershopId));
    }

    // ✅ Obtener MIS turnos (cliente autenticado)
    @GetMapping("/my")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        return ResponseEntity.ok(appointmentService.getMyAppointments(userId));
    }

    // 🔒 Cancelar turno (dueño del turno o ADMIN de la barbershop)
    @PreAuthorize("@barbershopSecurity.canModifyAppointment(authentication, #id)")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.ok().build();
    }

    // 🔒 Cambiar estado (solo ADMIN de la barbershop)
    @PreAuthorize("@barbershopSecurity.canModifyAppointment(authentication, #id)")
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request) {
        
        appointmentService.updateStatus(id, request);
        return ResponseEntity.ok().build();
    }
    
    // 🔒 Eliminar turno (solo ADMIN de la barbershop)
    @PreAuthorize("@barbershopSecurity.canModifyAppointment(authentication, #id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
    
    private Long getUserIdFromAuth(Authentication authentication) {
        // Extraer userId del JWT
        // Por ahora temporal:
        String email = authentication.getName();
        // TODO: Implementar método que obtenga el ID desde el UserDetails
        return 1L; // Placeholder
    }
}