package com.barber.backend.controller;

import com.barber.backend.dto.BarbershopDTO;
import com.barber.backend.dto.BarbershopListDTO;
import com.barber.backend.service.BarbershopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barbershops")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BarbershopController {
    
    private final BarbershopService barbershopService;
    
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
}