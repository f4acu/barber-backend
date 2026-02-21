package com.barber.backend.dto;

public class BarbershopDTO {
    
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String slug;
    private String description;
    private String link;
    private Boolean paymentEnabled; // NUEVO
    private String mercadoPagoPublicKey; // NUEVO

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public Boolean getPaymentEnabled() { return paymentEnabled; }
    public void setPaymentEnabled(Boolean paymentEnabled) { this.paymentEnabled = paymentEnabled; }

    public String getMercadoPagoPublicKey() { return mercadoPagoPublicKey; }
    public void setMercadoPagoPublicKey(String mercadoPagoPublicKey) { 
        this.mercadoPagoPublicKey = mercadoPagoPublicKey; 
    }
}