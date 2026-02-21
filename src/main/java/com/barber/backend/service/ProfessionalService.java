package com.barber.backend.service;

import com.barber.backend.dto.ProfessionalRequest;
import com.barber.backend.dto.ProfessionalResponse;
import com.barber.backend.model.Barbershop;
import com.barber.backend.model.Professional;
import com.barber.backend.repository.BarbershopRepository;
import com.barber.backend.repository.ProfessionalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;
    private final BarbershopRepository barbershopRepository;

    public ProfessionalService(ProfessionalRepository professionalRepository,
                              BarbershopRepository barbershopRepository) {
        this.professionalRepository = professionalRepository;
        this.barbershopRepository = barbershopRepository;
    }

    @Transactional(readOnly = true)
    public List<ProfessionalResponse> getProfessionalsByBarbershop(Long barbershopId) {
        return professionalRepository.findByBarbershopIdAndActiveTrue(barbershopId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProfessionalResponse> getAllProfessionalsByBarbershop(Long barbershopId) {
        // Incluye inactivos (para admins)
        return professionalRepository.findByBarbershopId(barbershopId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProfessionalResponse createProfessional(ProfessionalRequest request) {
        Barbershop barbershop = barbershopRepository.findById(request.getBarbershopId())
                .orElseThrow(() -> new RuntimeException("Peluquería no encontrada"));

        Professional professional = new Professional();
        professional.setName(request.getName());
        professional.setSpecialty(request.getSpecialty());
        professional.setPhone(request.getPhone());
        professional.setActive(request.getActive() != null ? request.getActive() : true);
        professional.setBarbershop(barbershop);

        Professional saved = professionalRepository.save(professional);
        return mapToResponse(saved);
    }

    @Transactional
    public ProfessionalResponse updateProfessional(Long id, ProfessionalRequest request) {
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));

        professional.setName(request.getName());
        professional.setSpecialty(request.getSpecialty());
        professional.setPhone(request.getPhone());
        
        if (request.getActive() != null) {
            professional.setActive(request.getActive());
        }

        Professional updated = professionalRepository.save(professional);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteProfessional(Long id) {
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
        
        // Soft delete: marcar como inactivo en lugar de eliminar
        professional.setActive(false);
        professionalRepository.save(professional);
    }

    private ProfessionalResponse mapToResponse(Professional professional) {
        return new ProfessionalResponse(
                professional.getId(),
                professional.getName(),
                professional.getSpecialty(),
                professional.getPhone(),
                professional.getActive(),
                professional.getBarbershop().getId(),
                professional.getBarbershop().getName()
        );
    }
}
    

