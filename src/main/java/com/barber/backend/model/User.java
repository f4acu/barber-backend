package com.barber.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String name;

    private String phone;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.CLIENT;

    private boolean enabled = true;

    // NUEVO: Para ADMINS, asociarlos a una peluquería
    // Los CLIENTES tendrán este campo en null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "managed_barbershop_id")
    private Barbershop managedBarbershop; // Solo para usuarios con role = ADMIN

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // NUEVO: Getter y Setter para managedBarbershop
    public Barbershop getManagedBarbershop() {
        return managedBarbershop;
    }

    public void setManagedBarbershop(Barbershop managedBarbershop) {
        this.managedBarbershop = managedBarbershop;
    }
}