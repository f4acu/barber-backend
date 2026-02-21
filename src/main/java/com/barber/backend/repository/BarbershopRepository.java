package com.barber.backend.repository;

import com.barber.backend.model.Barbershop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BarbershopRepository extends JpaRepository<Barbershop, Long> {
    
    Optional<Barbershop> findBySlug(String slug);
    
    boolean existsBySlug(String slug);
}