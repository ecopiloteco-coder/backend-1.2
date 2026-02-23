package com.ecopilot.project.service;

import com.ecopilot.project.dto.ProjetEquipeDTO;
import com.ecopilot.project.entity.Projet;
import com.ecopilot.project.entity.ProjetEquipe;
import com.ecopilot.project.kafka.producer.ProjectEventProducer;
import com.ecopilot.project.repository.ProjetEquipeRepository;
import com.ecopilot.project.repository.ProjetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjetEquipeService {

    private final ProjetEquipeRepository projetEquipeRepository;
    private final ProjetRepository projetRepository;
    private final ProjectEventProducer eventProducer;

    public List<ProjetEquipeDTO> getAllMembers() {
        return projetEquipeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ProjetEquipeDTO> getMembersByProjetId(Long projetId) {
        return projetEquipeRepository.findByProjetId(projetId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ProjetEquipeDTO> getProjetsByUserId(String userId) {
        return projetEquipeRepository.findByEquipe(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ProjetEquipeDTO getMemberById(Long id) {
        return projetEquipeRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProjetEquipe not found"));
    }

    public ProjetEquipeDTO addMember(ProjetEquipeDTO dto) {
        // Check if member already exists in the project
        if (projetEquipeRepository.existsByProjetIdAndEquipe(dto.getProjetId(), dto.getEquipe())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already in project team");
        }

        ProjetEquipe member = mapToEntity(dto);
        ProjetEquipe saved = projetEquipeRepository.save(member);
        
        // Publish Kafka event
                eventProducer.sendProjectAssignedEvent(
                        saved.getProjet().getId(),
                        saved.getProjet().getNomProjet(),
                        saved.getEquipe()
                );
        
        return mapToDTO(saved);
    }

    public void removeMember(Long id) {
        if (!projetEquipeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ProjetEquipe not found");
        }
        projetEquipeRepository.deleteById(id);
    }

    // Mapper Methods
    private ProjetEquipeDTO mapToDTO(ProjetEquipe member) {
        return ProjetEquipeDTO.builder()
                .id(member.getId())
                .equipe(member.getEquipe())
                .projetId(member.getProjet() != null ? member.getProjet().getId() : null)
                .build();
    }

    private ProjetEquipe mapToEntity(ProjetEquipeDTO dto) {
        Projet projet = null;
        if (dto.getProjetId() != null) {
            projet = projetRepository.findById(dto.getProjetId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projet not found"));
        }

        return ProjetEquipe.builder()
                .id(dto.getId())
                .equipe(dto.getEquipe())
                .projet(projet)
                .build();
    }
}
