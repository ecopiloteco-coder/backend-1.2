package com.ecopilot.project.service;

import com.ecopilot.project.dto.BlocDTO;
import com.ecopilot.project.entity.Bloc;
import com.ecopilot.project.entity.Ouvrage;
import com.ecopilot.project.entity.Structure;
import com.ecopilot.project.repository.BlocRepository;
import com.ecopilot.project.repository.OuvrageRepository;
import com.ecopilot.project.repository.StructureRepository;
import com.ecopilot.project.repository.ProjetArticleRepository;
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
public class BlocService {

    private final BlocRepository blocRepository;
    private final OuvrageRepository ouvrageRepository;
    private final StructureRepository structureRepository;
    private final ProjetArticleRepository projetArticleRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<BlocDTO> getAllBlocs() {
        return blocRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<BlocDTO> getBlocsByOuvrageId(Long ouvrageId) {
        return blocRepository.findByOuvrageId(ouvrageId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public BlocDTO getBlocById(Long id) {
        return blocRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bloc not found"));
    }

    public BlocDTO createBloc(BlocDTO dto) {
        Bloc bloc = mapToEntity(dto);
        Bloc saved = blocRepository.save(bloc);

        Long oldId = saved.getId();
        Long candidate = oldId;

        if (ouvrageRepository.existsById(candidate)) {
            Set<Long> reservedIds = new HashSet<>(
                    ouvrageRepository.findAll().stream().map(Ouvrage::getId).collect(Collectors.toList())
            );
            reservedIds.addAll(
                    blocRepository.findAll().stream().map(Bloc::getId).collect(Collectors.toList())
            );

            do {
                candidate += 1;
            } while (reservedIds.contains(candidate));

            entityManager.createNativeQuery(
                            "UPDATE structure SET bloc = :newId WHERE bloc = :oldId")
                    .setParameter("newId", candidate)
                    .setParameter("oldId", oldId)
                    .executeUpdate();

            entityManager.createNativeQuery(
                            "UPDATE bloc SET id = :newId WHERE id = :oldId")
                    .setParameter("newId", candidate)
                    .setParameter("oldId", oldId)
                    .executeUpdate();

            saved = blocRepository.findById(candidate)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bloc not found after id adjustment"));
        }

        if (saved.getOuvrage() != null) {
            Structure structure = Structure.builder()
                    .ouvrage(saved.getOuvrage())
                    .bloc(saved)
                    .action("bloc")
                    .build();
            structureRepository.save(structure);
        }

        return mapToDTO(saved);
    }

    public BlocDTO updateBloc(Long id, BlocDTO dto) {
        Bloc bloc = blocRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bloc not found"));

        bloc.setNomBloc(dto.getNomBloc());
        bloc.setUnite(dto.getUnite());
        bloc.setQuantite(dto.getQuantite());
        bloc.setPu(dto.getPu());
        bloc.setPt(dto.getPt());
        bloc.setDesignation(dto.getDesignation());

        Bloc updated = blocRepository.save(bloc);
        return mapToDTO(updated);
    }

    public void deleteBloc(Long id) {
        Bloc bloc = blocRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bloc not found"));

        java.util.List<Structure> structuresByBloc = structureRepository.findByBlocId(bloc.getId());
        for (Structure structure : structuresByBloc) {
            if (structure.getArticles() != null && !structure.getArticles().isEmpty()) {
                projetArticleRepository.deleteByStructureIdStructure(structure.getIdStructure());
            }
            structureRepository.delete(structure);
        }

        blocRepository.delete(bloc);
    }

    // Mapper Methods
    private BlocDTO mapToDTO(Bloc bloc) {
        return BlocDTO.builder()
                .id(bloc.getId())
                .nomBloc(bloc.getNomBloc())
                .unite(bloc.getUnite())
                .quantite(bloc.getQuantite())
                .pu(bloc.getPu())
                .pt(bloc.getPt())
                .designation(bloc.getDesignation())
                .ouvrageId(bloc.getOuvrage() != null ? bloc.getOuvrage().getId() : null)
                .build();
    }

    private Bloc mapToEntity(BlocDTO dto) {
        Ouvrage ouvrage = null;
        if (dto.getOuvrageId() != null) {
            ouvrage = ouvrageRepository.findById(dto.getOuvrageId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ouvrage not found"));
        }

        return Bloc.builder()
                .id(dto.getId())
                .nomBloc(dto.getNomBloc())
                .unite(dto.getUnite())
                .quantite(dto.getQuantite())
                .pu(dto.getPu())
                .pt(dto.getPt())
                .designation(dto.getDesignation())
                .ouvrage(ouvrage)
                .build();
    }
}
