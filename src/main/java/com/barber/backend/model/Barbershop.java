package com.barber.backend.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "barbershops")
public class Barbershop {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private String phone;
    
    @Column(nullable = false, unique = true)
    private String slug;
    
    @Column(length = 500)
    private String description;
    
    private Boolean active = true;
    
    // Credenciales de Mercado Pago
    @Column(name = "mercadopago_access_token", length = 500)
    private String mercadoPagoAccessToken;
    
    @Column(name = "mercadopago_public_key", length = 500)
    private String mercadoPagoPublicKey;
    
    // Configuración de pagos
    private Boolean paymentEnabled = false; // Si la barberia acepta pagos online
    
    // Relaciones
    @OneToMany(mappedBy = "barbershop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Professional> professionals = new ArrayList<>();
    
    @OneToMany(mappedBy = "barbershop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceEntity> services = new ArrayList<>();
    
    @OneToMany(mappedBy = "barbershop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();

    // ===== GETTERS & SETTERS =====
    
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

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    // NUEVO
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

    public List<Professional> getProfessionals() { return professionals; }
    public void setProfessionals(List<Professional> professionals) { 
        this.professionals = professionals; 
    }

    public List<ServiceEntity> getServices() { return services; }
    public void setServices(List<ServiceEntity> services) { 
        this.services = services; 
    }

    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> appointments) { 
        this.appointments = appointments; 
    }
}