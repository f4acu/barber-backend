package com.barber.backend.dto;

import com.barber.backend.model.PaymentStatus;

import java.time.LocalDateTime;

public class PaymentResponse {

    private Long id;
    private Long appointmentId;
    private String mercadoPagoId;
    private Double amount;
    private PaymentStatus status;
    private String checkoutUrl;
    private LocalDateTime createdAt;

    public PaymentResponse(Long id, Long appointmentId, String mercadoPagoId, 
                          Double amount, PaymentStatus status, String checkoutUrl, 
                          LocalDateTime createdAt) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.mercadoPagoId = mercadoPagoId;
        this.amount = amount;
        this.status = status;
        this.checkoutUrl = checkoutUrl;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() { return id; }
    public Long getAppointmentId() { return appointmentId; }
    public String getMercadoPagoId() { return mercadoPagoId; }
    public Double getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public String getCheckoutUrl() { return checkoutUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}