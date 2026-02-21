package com.barber.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentResponse {

    private Long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;

    private String clientName;
    private String professionalName;
    private String serviceName;
    private Double servicePrice;      
    private Integer serviceDuration;  // (en minutos)
    
    private String barbershopName;    
    private Long barbershopId;        
    
    private String notes;             

    // Constructor completo
    public AppointmentResponse(Long id, LocalDate date, LocalTime startTime, LocalTime endTime, 
                               String status, String clientName, String professionalName, 
                               String serviceName, Double servicePrice, Integer serviceDuration,
                               String barbershopName, Long barbershopId, String notes) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.clientName = clientName;
        this.professionalName = professionalName;
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
        this.serviceDuration = serviceDuration;
        this.barbershopName = barbershopName;
        this.barbershopId = barbershopId;
    }

    // Getters
    public Long getId() { return id; }
    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getStatus() { return status; }
    public String getClientName() { return clientName; }
    public String getProfessionalName() { return professionalName; }
    public String getServiceName() { return serviceName; }
    public Double getServicePrice() { return servicePrice; }
    public Integer getServiceDuration() { return serviceDuration; }
    public String getBarbershopName() { return barbershopName; }
    public Long getBarbershopId() { return barbershopId; }
    public String getNotes() { return notes; }
}