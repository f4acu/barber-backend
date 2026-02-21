package com.barber.backend.controller;

import com.barber.backend.model.Professional;
import com.barber.backend.repository.ProfessionalRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/professionals")
@CrossOrigin
public class ProfessionalController {

    private final ProfessionalRepository professionalRepository;

    public ProfessionalController(ProfessionalRepository professionalRepository) {
        this.professionalRepository = professionalRepository;
    }

    @GetMapping
    public List<Professional> getAllProfessionals() {
        return professionalRepository.findAll();
    }

    @GetMapping("/{id}")
    public Professional getProfessionalById(@PathVariable Long id) {
        return professionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professional not found"));
    }

    @PostMapping
    public Professional createProfessional(@RequestBody Professional professional) {
        return professionalRepository.save(professional);
    }

    @PutMapping("/{id}")
    public Professional updateProfessional(@PathVariable Long id, @RequestBody Professional updatedProfessional) {
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professional not found"));

        professional.setName(updatedProfessional.getName());
        professional.setSpecialty(updatedProfessional.getSpecialty());
        professional.setPhone(updatedProfessional.getPhone());

        return professionalRepository.save(professional);
    }

    @DeleteMapping("/{id}")
    public void deleteProfessional(@PathVariable Long id) {
        professionalRepository.deleteById(id);
    }
}