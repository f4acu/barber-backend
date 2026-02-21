package com.barber.backend.controller;

import com.barber.backend.dto.BarbershopConfigRequest;
import com.barber.backend.dto.BarbershopDTO;
import com.barber.backend.dto.BarbershopListDTO;
import com.barber.backend.service.BarbershopService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/barbershops")
@CrossOrigin
public class BarbershopController {
    
    private final BarbershopService barbershopService;

    public BarbershopController(BarbershopService barbershopService) {
        this.barbershopService = barbershopService;
    }

    /**
     * GET /api/barbershops
     * Retorna lista de todas las peluquerías con sus links
     */
    @GetMapping
    public ResponseEntity<List<BarbershopListDTO>> getAllBarbershops() {
        return ResponseEntity.ok(barbershopService.getAllBarbershops());
    }

    /**
     * GET /api/barbershops/{id}
     * Obtiene detalles completos de una peluquería
     */
    @GetMapping("/{id}")
    public ResponseEntity<BarbershopDTO> getBarbershopById(@PathVariable Long id) {
        return ResponseEntity.ok(barbershopService.getBarbershopById(id));
    }

    /**
     * GET /api/barbershops/slug/{slug}
     * Obtiene peluquería por slug (para URLs amigables)
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<BarbershopDTO> getBarbershopBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(barbershopService.getBarbershopBySlug(slug));
    }

    /**
     * PUT /api/barbershops/{id}/configure-payment
     * Configurar credenciales de Mercado Pago (solo ADMIN de esa barbershop)
     */
    @PreAuthorize("@barbershopSecurity.canAccessBarbershop(authentication, #id)")
    @PutMapping("/{id}/configure-payment")
    public ResponseEntity<?> configurePayment(
            @PathVariable Long id,
            @RequestBody BarbershopConfigRequest request) {
        
        barbershopService.configureMercadoPago(id, request);
        return ResponseEntity.ok(Map.of(
            "message", "Configuración de Mercado Pago actualizada correctamente"
        ));
    }
}