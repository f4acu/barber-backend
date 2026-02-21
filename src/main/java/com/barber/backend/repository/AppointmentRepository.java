package com.barber.backend.repository;

import com.barber.backend.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // MODIFICADO: Agregar barbershopId y excluir turnos cancelados
    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM Appointment a
        WHERE a.professional.id = :professionalId
        AND a.barbershop.id = :barbershopId
        AND a.status != 'CANCELLED'
        AND (
            (a.startTime < :endTime AND a.endTime > :startTime)
        )
    """)
    boolean existsOverlappingAppointment(
            @Param("professionalId") Long professionalId,
            @Param("barbershopId") Long barbershopId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    
    // NUEVO: Obtener todos los turnos de una barberia
    List<Appointment> findByBarbershopId(Long barbershopId);
    
    // NUEVO: Obtener todos los turnos de un usuario
    List<Appointment> findByUserId(Long userId);
    
    // NUEVO: Obtener turnos de un usuario en una barberia específica
    List<Appointment> findByBarbershopIdAndUserId(Long barbershopId, Long userId);
}