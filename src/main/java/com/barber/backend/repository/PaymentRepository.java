package com.barber.backend.repository;

import com.barber.backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByAppointmentId(Long appointmentId);
    
    Optional<Payment> findByMercadoPagoId(String mercadoPagoId);
}