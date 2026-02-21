package com.barber.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentRequest {

    private Long barbershopId;
    private Long professionalId;
    private Long serviceId;
    private LocalDate date;
    private LocalTime time;
    private String notes;
    private Boolean requiresPayment; // NUEVO: Si quiere pagar online

    // Getters & Setters
    public Long getBarbershopId() { return barbershopId; }
    public void setBarbershopId(Long barbershopId) { this.barbershopId = barbershopId; }

    public Long getProfessionalId() { return professionalId; }
    public void setProfessionalId(Long professionalId) { this.professionalId = professionalId; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getRequiresPayment() { return requiresPayment; }
    public void setRequiresPayment(Boolean requiresPayment) { this.requiresPayment = requiresPayment; }
}