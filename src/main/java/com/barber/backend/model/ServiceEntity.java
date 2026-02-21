package com.barber.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "services",
       indexes = @Index(name = "idx_service_barbershop", columnList = "barbershop_id"))
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(nullable = false)
    private Integer duration; // minutos
    
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

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    // NUEVO: Getter y Setter para Barbershop
    public Barbershop getBarbershop() { return barbershop; }
    public void setBarbershop(Barbershop barbershop) { this.barbershop = barbershop; }
}