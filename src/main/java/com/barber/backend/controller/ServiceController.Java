package com.barber.backend.controller;

import com.barber.backend.dto.ServiceRequest;
import com.barber.backend.dto.ServiceResponse;
import com.barber.backend.service.ServiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
@CrossOrigin
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    // ✅ Obtener servicios por barbershop (público)
    @GetMapping
    public ResponseEntity<List<ServiceResponse>> getByBarbershop(
            @RequestParam Long barbershopId) {
        return ResponseEntity.ok(serviceService.getServicesByBarbershop(barbershopId));
    }

    // 🔒 Crear servicio (solo ADMIN de esa barbershop)
    @PreAuthorize("@barbershopSecurity.canAccessBarbershop(authentication, #request.barbershopId)")
    @PostMapping
    public ResponseEntity<ServiceResponse> create(
            @RequestBody ServiceRequest request) {
        
        ServiceResponse response = serviceService.createService(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 🔒 Actualizar servicio (solo ADMIN de esa barbershop)
    @PreAuthorize("@barbershopSecurity.canModifyService(authentication, #id)")
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> update(
            @PathVariable Long id,
            @RequestBody ServiceRequest request) {
        
        ServiceResponse response = serviceService.updateService(id, request);
        return ResponseEntity.ok(response);
    }

    // 🔒 Eliminar servicio (solo ADMIN de esa barbershop)
    @PreAuthorize("@barbershopSecurity.canModifyService(authentication, #id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }

    // 🔒 Obtener TODOS los servicios (incluyendo inactivos) - Solo ADMIN
    @PreAuthorize("@barbershopSecurity.canAccessBarbershop(authentication, #barbershopId)")
    @GetMapping("/all")
    public ResponseEntity<List<ServiceResponse>> getAllByBarbershop(
        @RequestParam Long barbershopId) {
        return ResponseEntity.ok(serviceService.getAllServicesByBarbershop(barbershopId));
    }

}