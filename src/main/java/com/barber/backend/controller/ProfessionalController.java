package com.barber.backend.controller;

import com.barber.backend.dto.ProfessionalRequest;
import com.barber.backend.dto.ProfessionalResponse;
import com.barber.backend.service.ProfessionalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/professionals")
@CrossOrigin
public class ProfessionalController {

    private final ProfessionalService professionalService;

    public ProfessionalController(ProfessionalService professionalService) {
        this.professionalService = professionalService;
    }

    // ✅ Obtener profesionales activos por barbershop (público)
    @GetMapping
    public ResponseEntity<List<ProfessionalResponse>> getByBarbershop(
            @RequestParam Long barbershopId) {
        return ResponseEntity.ok(professionalService.getProfessionalsByBarbershop(barbershopId));
    }

    // 🔒 Obtener TODOS los profesionales (incluyendo inactivos) - Solo ADMIN
    @PreAuthorize("@barbershopSecurity.canAccessBarbershop(authentication, #barbershopId)")
    @GetMapping("/all")
    public ResponseEntity<List<ProfessionalResponse>> getAllByBarbershop(
            @RequestParam Long barbershopId) {
        return ResponseEntity.ok(professionalService.getAllProfessionalsByBarbershop(barbershopId));
    }

    // 🔒 Crear profesional (solo ADMIN de esa barbershop)
    @PreAuthorize("@barbershopSecurity.canAccessBarbershop(authentication, #request.barbershopId)")
    @PostMapping
    public ResponseEntity<ProfessionalResponse> create(
            @RequestBody ProfessionalRequest request) {
        
        ProfessionalResponse response = professionalService.createProfessional(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 🔒 Actualizar profesional (solo ADMIN de esa barbershop)
    @PreAuthorize("@barbershopSecurity.canModifyProfessional(authentication, #id)")
    @PutMapping("/{id}")
    public ResponseEntity<ProfessionalResponse> update(
            @PathVariable Long id,
            @RequestBody ProfessionalRequest request) {
        
        ProfessionalResponse response = professionalService.updateProfessional(id, request);
        return ResponseEntity.ok(response);
    }

    // 🔒 Eliminar profesional (soft delete) - Solo ADMIN de esa barbershop
    @PreAuthorize("@barbershopSecurity.canModifyProfessional(authentication, #id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        professionalService.deleteProfessional(id);
        return ResponseEntity.noContent().build();
    }
}