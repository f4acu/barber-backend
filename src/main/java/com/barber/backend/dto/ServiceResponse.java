package com.barber.backend.dto;

public class ServiceResponse {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer duration;
    private Boolean active; // NUEVO
    private Long barbershopId; // NUEVO
    private String barbershopName; // NUEVO

    public ServiceResponse(Long id, String name, String description, Double price, 
                          Integer duration, Boolean active, Long barbershopId, String barbershopName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.active = active;
        this.barbershopId = barbershopId;
        this.barbershopName = barbershopName;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Double getPrice() { return price; }
    public Integer getDuration() { return duration; }
    public Boolean getActive() { return active; }
    public Long getBarbershopId() { return barbershopId; }
    public String getBarbershopName() { return barbershopName; }
}