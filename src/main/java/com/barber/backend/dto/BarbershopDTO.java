package com.barber.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BarbershopDTO {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String slug;
    private String description;
    private String link; // URL completa: https://miapp.com/barberia/elite-cuts
    
    public BarbershopDTO(Long id, String name, String slug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
    }
}