package com.ecopilot.project.service;

import com.ecopilot.project.dto.ProjetLotDTO;
import com.ecopilot.project.entity.Projet;
import com.ecopilot.project.entity.ProjetLot;
import com.ecopilot.project.entity.Ouvrage;
import com.ecopilot.project.entity.Structure;
import com.ecopilot.project.repository.ProjetLotRepository;
import com.ecopilot.project.repository.ProjetRepository;
import com.ecopilot.project.repository.OuvrageRepository;
import com.ecopilot.project.repository.StructureRepository;
import com.ecopilot.project.repository.BlocRepository;
import com.ecopilot.project.repository.ProjetArticleRepository;
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
public class ProjetLotService {

    private final ProjetLotRepository projetLotRepository;
    private final ProjetRepository projetRepository;
    private final OuvrageRepository ouvrageRepository;
    private final StructureRepository structureRepository;
    private final BlocRepository blocRepository;
    private final ProjetArticleRepository projetArticleRepository;

    public List<ProjetLotDTO> getAllLots() {
        return projetLotRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ProjetLotDTO> getLotsByProjetId(Long projetId) {
        return projetLotRepository.findByProjetId(projetId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ProjetLotDTO getLotById(Long id) {
        return projetLotRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProjetLot not found"));
    }

    public ProjetLotDTO createLot(ProjetLotDTO dto) {
        ProjetLot lot = mapToEntity(dto);
        ProjetLot saved = projetLotRepository.save(lot);
        return mapToDTO(saved);
    }

    public ProjetLotDTO updateLot(Long id, ProjetLotDTO dto) {
        ProjetLot lot = projetLotRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProjetLot not found"));

        lot.setIdLot(dto.getIdLot());
        lot.setDesignationLot(dto.getDesignationLot());
        lot.setPrixTotal(dto.getPrixTotal());
        lot.setPrixVente(dto.getPrixVente());

        ProjetLot updated = projetLotRepository.save(lot);
        return mapToDTO(updated);
    }

    public void deleteLot(Long id) {
        if (!projetLotRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ProjetLot not found");
        }

        List<Ouvrage> ouvrages = ouvrageRepository.findByProjetLotIdProjetLot(id);

        for (Ouvrage ouvrage : ouvrages) {
            List<Structure> structures = structureRepository.findByOuvrageId(ouvrage.getId());
            for (Structure structure : structures) {
                projetArticleRepository.deleteByStructureIdStructure(structure.getIdStructure());
                structureRepository.deleteById(structure.getIdStructure());
            }
            blocRepository.deleteByOuvrageId(ouvrage.getId());
        }

        ouvrageRepository.deleteByProjetLotIdProjetLot(id);
        projetLotRepository.deleteById(id);
    }

    // Mapper Methods
    private ProjetLotDTO mapToDTO(ProjetLot lot) {
        return ProjetLotDTO.builder()
                .idProjetLot(lot.getIdProjetLot())
                .projetId(lot.getProjet() != null ? lot.getProjet().getId() : null)
                .idLot(lot.getIdLot())
                .designationLot(lot.getDesignationLot())
                .prixTotal(lot.getPrixTotal())
                .prixVente(lot.getPrixVente())
                .build();
    }

    private ProjetLot mapToEntity(ProjetLotDTO dto) {
        Projet projet = null;
        if (dto.getProjetId() != null) {
            projet = projetRepository.findById(dto.getProjetId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projet not found"));
        }

        return ProjetLot.builder()
                .idProjetLot(dto.getIdProjetLot())
                .projet(projet)
                .idLot(dto.getIdLot())
                .designationLot(dto.getDesignationLot())
                .prixTotal(dto.getPrixTotal())
                .prixVente(dto.getPrixVente())
                .build();
    }
}
