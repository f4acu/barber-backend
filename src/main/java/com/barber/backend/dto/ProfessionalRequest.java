package com.barber.backend.dto;

public class ProfessionalRequest {

    private Long barbershopId; // NUEVO
    private String name;
    private String specialty;
    private String phone;
    private Boolean active; // NUEVO

    // Getters & Setters
    public Long getBarbershopId() { return barbershopId; }
    public void setBarbershopId(Long barbershopId) { this.barbershopId = barbershopId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}