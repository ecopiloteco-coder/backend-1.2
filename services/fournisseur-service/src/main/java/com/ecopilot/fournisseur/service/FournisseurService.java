package com.ecopilot.fournisseur.service;

import com.ecopilot.fournisseur.dto.FournisseurDTO;
import com.ecopilot.fournisseur.entity.Fournisseur;
import com.ecopilot.fournisseur.repository.FournisseurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;

    public List<FournisseurDTO> getAllFournisseurs() {
        return fournisseurRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public FournisseurDTO getFournisseurById(Long id) {
        return fournisseurRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fournisseur not found"));
    }

    public FournisseurDTO createFournisseur(FournisseurDTO dto) {
        Fournisseur fournisseur = mapToEntity(dto);
        Fournisseur saved = fournisseurRepository.save(fournisseur);
        return mapToDTO(saved);
    }

    public FournisseurDTO updateFournisseur(Long id, FournisseurDTO dto) {
        Fournisseur existing = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fournisseur not found"));
        
        existing.setNomFournisseur(dto.getNomFournisseur());
        existing.setEmail(dto.getEmail());
        existing.setTelephone(dto.getTelephone());
        existing.setAdresse(dto.getAdresse());
        existing.setSpecialite(dto.getSpecialite());
        existing.setCategorie(dto.getCategorie());
        existing.setLot(dto.getLot());
        existing.setType(dto.getType());
        existing.setUrl(dto.getUrl());
        
        Fournisseur updated = fournisseurRepository.save(existing);
        return mapToDTO(updated);
    }

    public void deleteFournisseur(Long id) {
        if (!fournisseurRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fournisseur not found");
        }
        fournisseurRepository.deleteById(id);
    }

    public List<String> getFournisseurTypes() {
        return fournisseurRepository.findDistinctType();
    }
    
    // Mapper Methods
    private FournisseurDTO mapToDTO(Fournisseur fournisseur) {
        return FournisseurDTO.builder()
                .id(fournisseur.getId())
                .nomFournisseur(fournisseur.getNomFournisseur())
                .email(fournisseur.getEmail())
                .telephone(fournisseur.getTelephone())
                .adresse(fournisseur.getAdresse())
                .specialite(fournisseur.getSpecialite())
                .categorie(fournisseur.getCategorie())
                .lot(fournisseur.getLot())
                .type(fournisseur.getType())
                .url(fournisseur.getUrl())
                .keycloakId(fournisseur.getKeycloakId())
                .userId(fournisseur.getUserId())
                .createdAt(fournisseur.getCreatedAt())
                .build();
    }

    private Fournisseur mapToEntity(FournisseurDTO dto) {
        return Fournisseur.builder()
                .nomFournisseur(dto.getNomFournisseur())
                .email(dto.getEmail())
                .telephone(dto.getTelephone())
                .adresse(dto.getAdresse())
                .specialite(dto.getSpecialite())
                .categorie(dto.getCategorie())
                .lot(dto.getLot())
                .type(dto.getType())
                .url(dto.getUrl())
                .keycloakId(dto.getKeycloakId())
                .userId(dto.getUserId())
                .build();
    }
}
