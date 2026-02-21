package com.barber.backend.dto;

public class ServiceRequest {

    private Long barbershopId; // NUEVO
    private String name;
    private String description;
    private Double price;
    private Integer duration;
    private Boolean active; // NUEVO

    // Getters & Setters
    public Long getBarbershopId() { return barbershopId; }
    public void setBarbershopId(Long barbershopId) { this.barbershopId = barbershopId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}