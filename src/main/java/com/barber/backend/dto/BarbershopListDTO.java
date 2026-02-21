package com.barber.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BarbershopListDTO {
    private Long id;
    private String name;
    private String link;
}