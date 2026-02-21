package com.barber.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "professionals", 
       indexes = @Index(name = "idx_professional_barbershop", columnList = "barbershop_id"))
public class Professional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    
    private String specialty;
    private String phone;
    
    private Boolean active = true;

    // NUEVO: Relación con Barbershop
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barbershop_id", nullable = false)
    private Barbershop barbershop;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    // NUEVO: Getter y Setter para Barbershop
    public Barbershop getBarbershop() { return barbershop; }
    public void setBarbershop(Barbershop barbershop) { this.barbershop = barbershop; }
}