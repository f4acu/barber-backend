package com.barber.backend.dto;

public class BarbershopConfigRequest {
    
    private String mercadoPagoAccessToken;
    private String mercadoPagoPublicKey;
    private Boolean paymentEnabled;

    // Getters & Setters
    public String getMercadoPagoAccessToken() { return mercadoPagoAccessToken; }
    public void setMercadoPagoAccessToken(String mercadoPagoAccessToken) { 
        this.mercadoPagoAccessToken = mercadoPagoAccessToken; 
    }

    public String getMercadoPagoPublicKey() { return mercadoPagoPublicKey; }
    public void setMercadoPagoPublicKey(String mercadoPagoPublicKey) { 
        this.mercadoPagoPublicKey = mercadoPagoPublicKey; 
    }

    public Boolean getPaymentEnabled() { return paymentEnabled; }
    public void setPaymentEnabled(Boolean paymentEnabled) { 
        this.paymentEnabled = paymentEnabled; 
    }
}