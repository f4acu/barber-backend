package com.barber.backend.dto;

public class ProfessionalResponse {

    private Long id;
    private String name;
    private String specialty;
    private String phone;
    private Boolean active; // NUEVO
    private Long barbershopId; // NUEVO
    private String barbershopName; // NUEVO

    public ProfessionalResponse(Long id, String name, String specialty, String phone, 
                               Boolean active, Long barbershopId, String barbershopName) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.phone = phone;
        this.active = active;
        this.barbershopId = barbershopId;
        this.barbershopName = barbershopName;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSpecialty() { return specialty; }
    public String getPhone() { return phone; }
    public Boolean getActive() { return active; }
    public Long getBarbershopId() { return barbershopId; }
    public String getBarbershopName() { return barbershopName; }
}