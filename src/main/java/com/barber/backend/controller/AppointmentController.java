package com.barber.backend.controller;

import com.barber.backend.dto.*;
import com.barber.backend.service.AppointmentService;
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

    // ✅ Crear turno
    @PostMapping
    public AppointmentResponse create(@RequestBody AppointmentRequest request) {
        return appointmentService.createAppointment(request);
    }

    // ✅ Listar todos
    @GetMapping
    public List<AppointmentResponse> getAll() {
        return appointmentService.getAllAppointments();
    }

    // ✅ Cancelar turno
    @PutMapping("/{id}/cancel")
    public void cancel(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
    }

    // ✅ Cambiar estado
    @PutMapping("/{id}/status")
    public void updateStatus(@PathVariable Long id,
                             @RequestBody UpdateStatusRequest request) {
        appointmentService.updateStatus(id, request);
    }
}