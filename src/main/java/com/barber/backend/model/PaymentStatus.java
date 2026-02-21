package com.barber.backend.model;

public enum PaymentStatus {
    PENDING,      // Pago pendiente (esperando confirmación)
    APPROVED,     // Pago aprobado
    REJECTED,     // Pago rechazado
    CANCELLED,    // Pago cancelado
    REFUNDED,     // Pago reembolsado
    IN_PROCESS    // Pago en proceso
}