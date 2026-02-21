package com.barber.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentResponse {

    private Long id;
    private LocalDate date;
    private LocalTime time;
    private String status;

    private String clientName;
    private String professionalName;
    private String serviceName;

    public AppointmentResponse(Long id, LocalDate date, LocalTime time, String status,
                               String clientName, String professionalName, String serviceName) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.status = status;
        this.clientName = clientName;
        this.professionalName = professionalName;
        this.serviceName = serviceName;
    }

    // Getters
    public Long getId() { return id; }
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public String getStatus() { return status; }
    public String getClientName() { return clientName; }
    public String getProfessionalName() { return professionalName; }
    public String getServiceName() { return serviceName; }
}