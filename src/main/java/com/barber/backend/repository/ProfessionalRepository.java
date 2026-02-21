package com.barber.backend.repository;

import com.barber.backend.model.Professional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessionalRepository extends JpaRepository<Professional, Long> {
    
    // NUEVO: Obtener todos los profesionales de una barberia
    List<Professional> findByBarbershopId(Long barbershopId);
    
    // NUEVO: Obtener solo los profesionales activos de una barberia
    List<Professional> findByBarbershopIdAndActiveTrue(Long barbershopId);
    
    // NUEVO: Validar que un profesional pertenece a una barberia específica
    Optional<Professional> findByIdAndBarbershopId(Long id, Long barbershopId);
}