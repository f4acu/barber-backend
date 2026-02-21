package com.barber.backend.repository;

import com.barber.backend.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM Appointment a
        WHERE a.professional.id = :professionalId
        AND (
            (a.startTime < :endTime AND a.endTime > :startTime)
        )
    """)
    boolean existsOverlappingAppointment(
            Long professionalId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
}