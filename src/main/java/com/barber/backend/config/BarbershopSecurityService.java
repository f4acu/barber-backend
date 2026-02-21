package com.barber.backend.config;

import com.barber.backend.model.Appointment;
import com.barber.backend.model.Professional;
import com.barber.backend.model.Role;
import com.barber.backend.model.ServiceEntity;
import com.barber.backend.model.User;
import com.barber.backend.repository.AppointmentRepository;
import com.barber.backend.repository.ProfessionalRepository;
import com.barber.backend.repository.ServiceRepository;
import com.barber.backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("barbershopSecurity")
public class BarbershopSecurityService {
    
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final ProfessionalRepository professionalRepository;
    private final ServiceRepository serviceRepository;

    public BarbershopSecurityService(UserRepository userRepository,
                                     AppointmentRepository appointmentRepository,
                                     ProfessionalRepository professionalRepository,
                                     ServiceRepository serviceRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.professionalRepository = professionalRepository;
        this.serviceRepository = serviceRepository;
    }

    /**
     * Valida que un ADMIN pueda acceder a una barbershop específica
     */
    public boolean canAccessBarbershop(Authentication authentication, Long barbershopId) {
        if (authentication == null || barbershopId == null) {
            return false;
        }
        
        User user = getUserFromAuth(authentication);
        if (user == null) {
            return false;
        }
        
        // Si es CLIENT, no puede acceder a endpoints de administración
        if (user.getRole() == Role.CLIENT) {
            return false;
        }
        
        // Si es ADMIN, verificar que sea de esta barbershop
        if (user.getRole() == Role.ADMIN) {
            return user.getManagedBarbershop() != null 
                   && user.getManagedBarbershop().getId().equals(barbershopId);
        }
        
        return false;
    }
    
    /**
     * Valida que un ADMIN pueda modificar/eliminar un turno de su barbershop
     */
    public boolean canModifyAppointment(Authentication authentication, Long appointmentId) {
        if (authentication == null || appointmentId == null) {
            return false;
        }
        
        User user = getUserFromAuth(authentication);
        if (user == null) {
            return false;
        }
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElse(null);
        
        if (appointment == null) {
            return false;
        }
        
        // El usuario puede modificar si:
        // 1. Es el dueño del turno (CLIENT)
        if (user.getId().equals(appointment.getUser().getId())) {
            return true;
        }
        
        // 2. Es ADMIN de la barbershop del turno
        if (user.getRole() == Role.ADMIN) {
            return user.getManagedBarbershop() != null
                   && user.getManagedBarbershop().getId().equals(appointment.getBarbershop().getId());
        }
        
        return false;
    }
    
    /**
     * Valida que un ADMIN pueda modificar/eliminar un profesional de su barbershop
     */
    public boolean canModifyProfessional(Authentication authentication, Long professionalId) {
        if (authentication == null || professionalId == null) {
            return false;
        }
        
        User user = getUserFromAuth(authentication);
        if (user == null || user.getRole() != Role.ADMIN) {
            return false;
        }
        
        Professional professional = professionalRepository.findById(professionalId)
                .orElse(null);
        
        if (professional == null) {
            return false;
        }
        
        // Verificar que el profesional pertenece a la barbershop del admin
        return user.getManagedBarbershop() != null
               && user.getManagedBarbershop().getId().equals(professional.getBarbershop().getId());
    }
    
    /**
     * Valida que un ADMIN pueda modificar/eliminar un servicio de su barbershop
     */
    public boolean canModifyService(Authentication authentication, Long serviceId) {
        if (authentication == null || serviceId == null) {
            return false;
        }
        
        User user = getUserFromAuth(authentication);
        if (user == null || user.getRole() != Role.ADMIN) {
            return false;
        }
        
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElse(null);
        
        if (service == null) {
            return false;
        }
        
        // Verificar que el servicio pertenece a la barbershop del admin
        return user.getManagedBarbershop() != null
               && user.getManagedBarbershop().getId().equals(service.getBarbershop().getId());
    }
    
    /**
     * Obtiene el barbershopId del ADMIN autenticado
     */
    public Long getAdminBarbershopId(Authentication authentication) {
        User user = getUserFromAuth(authentication);
        if (user == null || user.getRole() != Role.ADMIN || user.getManagedBarbershop() == null) {
            return null;
        }
        return user.getManagedBarbershop().getId();
    }
    
    private User getUserFromAuth(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElse(null);
    }
}