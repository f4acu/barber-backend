package com.barber.backend.dto;

public class ProfessionalResponse {

    private Long id;
    private String name;
    private String specialty;
    private String phone;

    public ProfessionalResponse(Long id, String name, String specialty, String phone) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.phone = phone;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSpecialty() { return specialty; }
    public String getPhone() { return phone; }
}