package com.barber.backend.service;

import com.barber.backend.dto.BarbershopConfigRequest;
import com.barber.backend.dto.BarbershopDTO;
import com.barber.backend.dto.BarbershopListDTO;
import com.barber.backend.model.Barbershop;
import com.barber.backend.repository.BarbershopRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BarbershopService {
    
    private final BarbershopRepository barbershopRepository;
    
    @Value("${app.base-url}")
    private String baseUrl; // ← ESTA variable se inyecta desde application.properties
    
    public BarbershopService(BarbershopRepository barbershopRepository) {
        this.barbershopRepository = barbershopRepository;
    }
    
    @Transactional(readOnly = true)
    public List<BarbershopListDTO> getAllBarbershops() {
        return barbershopRepository.findAll().stream()
                .map(barbershop -> new BarbershopListDTO(
                        barbershop.getId(),
                        barbershop.getName(),
                        baseUrl + "/barberia/" + barbershop.getSlug()
                ))
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public BarbershopDTO getBarbershopById(Long id) {
        Barbershop barbershop = barbershopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Peluquería no encontrada"));
        
        return convertToDTO(barbershop);
    }
    
    @Transactional(readOnly = true)
    public BarbershopDTO getBarbershopBySlug(String slug) {
        Barbershop barbershop = barbershopRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Peluquería no encontrada"));
        
        return convertToDTO(barbershop);
    }

    // NUEVO: Configurar Mercado Pago para una barbershop
    @Transactional
    public void configureMercadoPago(Long barbershopId, BarbershopConfigRequest request) {
        Barbershop barbershop = barbershopRepository.findById(barbershopId)
                .orElseThrow(() -> new RuntimeException("Peluquería no encontrada"));

        barbershop.setMercadoPagoAccessToken(request.getMercadoPagoAccessToken());
        barbershop.setMercadoPagoPublicKey(request.getMercadoPagoPublicKey());
        barbershop.setPaymentEnabled(request.getPaymentEnabled());

        barbershopRepository.save(barbershop);
    }
    
    // ESTE MÉTODO va aquí en BarbershopService, NO en BarbershopDTO
    private BarbershopDTO convertToDTO(Barbershop barbershop) {
        BarbershopDTO dto = new BarbershopDTO();
        dto.setId(barbershop.getId());
        dto.setName(barbershop.getName());
        dto.setAddress(barbershop.getAddress());
        dto.setPhone(barbershop.getPhone());
        dto.setSlug(barbershop.getSlug());
        dto.setDescription(barbershop.getDescription());
        dto.setLink(baseUrl + "/barberia/" + barbershop.getSlug()); // ← Aquí se usa baseUrl
        dto.setPaymentEnabled(barbershop.getPaymentEnabled());
        dto.setMercadoPagoPublicKey(barbershop.getMercadoPagoPublicKey());
        return dto;
    }
}