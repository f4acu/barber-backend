package com.barber.backend.service;

import com.barber.backend.dto.BarbershopDTO;
import com.barber.backend.dto.BarbershopListDTO;
import com.barber.backend.model.Barbershop;
import com.barber.backend.repository.BarbershopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BarbershopService {
    
    private final BarbershopRepository barbershopRepository;
    
    @Value("${app.base-url:https://miapp.com}")
    private String baseUrl;
    
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
    
    private BarbershopDTO convertToDTO(Barbershop barbershop) {
        BarbershopDTO dto = new BarbershopDTO();
        dto.setId(barbershop.getId());
        dto.setName(barbershop.getName());
        dto.setAddress(barbershop.getAddress());
        dto.setPhone(barbershop.getPhone());
        dto.setSlug(barbershop.getSlug());
        dto.setDescription(barbershop.getDescription());
        dto.setLink(baseUrl + "/barberia/" + barbershop.getSlug());
        return dto;
    }
}