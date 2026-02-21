package com.barber.backend.repository;

import com.barber.backend.model.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    
    // NUEVO: Obtener todos los servicios de una barberia
    List<ServiceEntity> findByBarbershopId(Long barbershopId);
    
    // NUEVO: Obtener solo los servicios activos de una barberia
    List<ServiceEntity> findByBarbershopIdAndActiveTrue(Long barbershopId);
    
    // NUEVO: Validar que un servicio pertenece a una barberia específica
    Optional<ServiceEntity> findByIdAndBarbershopId(Long id, Long barbershopId);
}