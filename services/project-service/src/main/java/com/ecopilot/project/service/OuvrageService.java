package com.ecopilot.project.service;

import com.ecopilot.project.dto.OuvrageDTO;
import com.ecopilot.project.entity.Ouvrage;
import com.ecopilot.project.entity.Bloc;
import com.ecopilot.project.entity.Structure;
import com.ecopilot.project.entity.ProjetLot;
import com.ecopilot.project.entity.Projet;
import com.ecopilot.project.repository.OuvrageRepository;
import com.ecopilot.project.repository.ProjetLotRepository;
import com.ecopilot.project.repository.StructureRepository;
import com.ecopilot.project.repository.BlocRepository;
import com.ecopilot.project.repository.ProjetArticleRepository;
import com.ecopilot.project.kafka.producer.ProjectEventProducer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OuvrageService {

    private final OuvrageRepository ouvrageRepository;
    private final ProjetLotRepository projetLotRepository;
    private final StructureRepository structureRepository;
    private final BlocRepository blocRepository;
    private final ProjetArticleRepository projetArticleRepository;
    private final ProjectEventProducer eventProducer;

    @PersistenceContext
    private EntityManager entityManager;

    public List<OuvrageDTO> getAllOuvrages() {
        return ouvrageRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<OuvrageDTO> getOuvragesByLotId(Long projetLotId) {
        return ouvrageRepository.findByProjetLotIdProjetLot(projetLotId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public OuvrageDTO getOuvrageById(Long id) {
        return ouvrageRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ouvrage not found"));
    }

    public OuvrageDTO createOuvrage(OuvrageDTO dto) {
        Ouvrage ouvrage = mapToEntity(dto);
        Ouvrage saved = ouvrageRepository.save(ouvrage);

        Long oldId = saved.getId();
        Long candidate = oldId;

        if (blocRepository.existsById(candidate)) {
            Set<Long> reservedIds = new HashSet<>(
                    blocRepository.findAll().stream().map(com.ecopilot.project.entity.Bloc::getId).collect(Collectors.toList())
            );
            reservedIds.addAll(
                    ouvrageRepository.findAll().stream().map(Ouvrage::getId).collect(Collectors.toList())
            );

            do {
                candidate += 1;
            } while (reservedIds.contains(candidate));

            entityManager.createNativeQuery(
                            "UPDATE structure SET ouvrage = :newId WHERE ouvrage = :oldId")
                    .setParameter("newId", candidate)
                    .setParameter("oldId", oldId)
                    .executeUpdate();

            entityManager.createNativeQuery(
                            "UPDATE bloc SET ouvrage = :newId WHERE ouvrage = :oldId")
                    .setParameter("newId", candidate)
                    .setParameter("oldId", oldId)
                    .executeUpdate();

            entityManager.createNativeQuery(
                            "UPDATE ouvrage SET id = :newId WHERE id = :oldId")
                    .setParameter("newId", candidate)
                    .setParameter("oldId", oldId)
                    .executeUpdate();

            saved = ouvrageRepository.findById(candidate)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ouvrage not found after id adjustment"));
        }

        com.ecopilot.project.entity.Structure structure = com.ecopilot.project.entity.Structure.builder()
                .ouvrage(saved)
                .bloc(null)
                .action("ouvrage")
                .build();
        structureRepository.save(structure);

        return mapToDTO(saved);
    }

    public OuvrageDTO updateOuvrage(Long id, OuvrageDTO dto) {
        Ouvrage ouvrage = ouvrageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ouvrage not found"));

        String oldName = ouvrage.getNomOuvrage();

        ouvrage.setNomOuvrage(dto.getNomOuvrage());
        ouvrage.setPrixTotal(dto.getPrixTotal());
        ouvrage.setDesignation(dto.getDesignation());

        Ouvrage updated = ouvrageRepository.save(ouvrage);
        ProjetLot lot = updated.getProjetLot();
        Projet projet = lot != null ? lot.getProjet() : null;
        Long projetId = projet != null ? projet.getId() : null;
        Long lotId = lot != null ? Long.valueOf(lot.getIdLot()) : null;

        eventProducer.sendOuvrageUpdatedEvent(
                projetId,
                lotId,
                updated.getId(),
                oldName,
                updated.getNomOuvrage(),
                projet != null ? projet.getAjoutePar() : null
        );

        return mapToDTO(updated);
    }

    public OuvrageDTO duplicateOuvrage(Long id) {
        Ouvrage original = ouvrageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ouvrage not found"));

        Ouvrage duplicate = Ouvrage.builder()
                .nomOuvrage(original.getNomOuvrage() + " (Copie)")
                .prixTotal(original.getPrixTotal())
                .designation(original.getDesignation())
                .projetLot(original.getProjetLot())
                .build();

        Ouvrage saved = ouvrageRepository.save(duplicate);
        return mapToDTO(saved);
    }

    public void deleteOuvrage(Long id) {
        Ouvrage ouvrage = ouvrageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ouvrage not found"));

        List<Bloc> blocs = blocRepository.findByOuvrageId(id);
        for (Bloc bloc : blocs) {
            List<Structure> structuresByBloc = structureRepository.findByBlocId(bloc.getId());
            for (Structure structure : structuresByBloc) {
                projetArticleRepository.deleteByStructureIdStructure(structure.getIdStructure());
                structureRepository.delete(structure);
            }
        }

        List<Structure> structuresByOuvrage = structureRepository.findByOuvrageId(id);
        for (Structure structure : structuresByOuvrage) {
            projetArticleRepository.deleteByStructureIdStructure(structure.getIdStructure());
            structureRepository.delete(structure);
        }

        blocRepository.deleteByOuvrageId(id);
        ouvrageRepository.delete(ouvrage);
    }

    // Mapper Methods
    private OuvrageDTO mapToDTO(Ouvrage ouvrage) {
        return OuvrageDTO.builder()
                .id(ouvrage.getId())
                .nomOuvrage(ouvrage.getNomOuvrage())
                .prixTotal(ouvrage.getPrixTotal())
                .designation(ouvrage.getDesignation())
                .projetLotId(ouvrage.getProjetLot() != null ? ouvrage.getProjetLot().getIdProjetLot() : null)
                .build();
    }

    private Ouvrage mapToEntity(OuvrageDTO dto) {
        ProjetLot projetLot = null;
        if (dto.getProjetLotId() != null) {
            projetLot = projetLotRepository.findById(dto.getProjetLotId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProjetLot not found"));
        }

        return Ouvrage.builder()
                .id(dto.getId())
                .nomOuvrage(dto.getNomOuvrage())
                .prixTotal(dto.getPrixTotal())
                .designation(dto.getDesignation())
                .projetLot(projetLot)
                .build();
    }
}
