package com.barber.backend.service;

import com.barber.backend.dto.ServiceRequest;
import com.barber.backend.dto.ServiceResponse;
import com.barber.backend.model.Barbershop;
import com.barber.backend.model.ServiceEntity;
import com.barber.backend.repository.BarbershopRepository;
import com.barber.backend.repository.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final BarbershopRepository barbershopRepository;

    public ServiceService(ServiceRepository serviceRepository,
                         BarbershopRepository barbershopRepository) {
        this.serviceRepository = serviceRepository;
        this.barbershopRepository = barbershopRepository;
    }

    @Transactional(readOnly = true)
    public List<ServiceResponse> getServicesByBarbershop(Long barbershopId) {
        return serviceRepository.findByBarbershopIdAndActiveTrue(barbershopId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServiceResponse> getAllServicesByBarbershop(Long barbershopId) {
        // Incluye inactivos (para admins)
        return serviceRepository.findByBarbershopId(barbershopId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ServiceResponse createService(ServiceRequest request) {
        Barbershop barbershop = barbershopRepository.findById(request.getBarbershopId())
                .orElseThrow(() -> new RuntimeException("Peluquería no encontrada"));

        ServiceEntity service = new ServiceEntity();
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setDuration(request.getDuration());
        service.setActive(request.getActive() != null ? request.getActive() : true);
        service.setBarbershop(barbershop);

        ServiceEntity saved = serviceRepository.save(service);
        return mapToResponse(saved);
    }

    @Transactional
    public ServiceResponse updateService(Long id, ServiceRequest request) {
        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setDuration(request.getDuration());
        
        if (request.getActive() != null) {
            service.setActive(request.getActive());
        }

        ServiceEntity updated = serviceRepository.save(service);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteService(Long id) {
        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        
        // Soft delete: marcar como inactivo en lugar de eliminar
        service.setActive(false);
        serviceRepository.save(service);
    }

    private ServiceResponse mapToResponse(ServiceEntity service) {
        return new ServiceResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getPrice(),
                service.getDuration(),
                service.getActive(),
                service.getBarbershop().getId(),
                service.getBarbershop().getName()
        );
    }
}