package com.ecopilot.project.service;

import com.ecopilot.project.dto.StructureDTO;
import com.ecopilot.project.entity.Bloc;
import com.ecopilot.project.entity.Ouvrage;
import com.ecopilot.project.entity.Structure;
import com.ecopilot.project.repository.BlocRepository;
import com.ecopilot.project.repository.OuvrageRepository;
import com.ecopilot.project.repository.StructureRepository;
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
public class StructureService {

    private final StructureRepository structureRepository;
    private final OuvrageRepository ouvrageRepository;
    private final BlocRepository blocRepository;

    public List<StructureDTO> getAllStructures() {
        return structureRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<StructureDTO> getStructuresByOuvrageId(Long ouvrageId) {
        return structureRepository.findByOuvrageId(ouvrageId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public StructureDTO getStructureById(Long id) {
        return structureRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Structure not found"));
    }

    public StructureDTO createStructure(StructureDTO dto) {
        Structure structure = mapToEntity(dto);
        Structure saved = structureRepository.save(structure);
        return mapToDTO(saved);
    }

    public StructureDTO updateStructure(Long id, StructureDTO dto) {
        Structure structure = structureRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Structure not found"));

        structure.setAction(dto.getAction());

        Structure updated = structureRepository.save(structure);
        return mapToDTO(updated);
    }

    public void deleteStructure(Long id) {
        if (!structureRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Structure not found");
        }
        structureRepository.deleteById(id);
    }

    // Mapper Methods
    private StructureDTO mapToDTO(Structure structure) {
        return StructureDTO.builder()
                .idStructure(structure.getIdStructure())
                .ouvrageId(structure.getOuvrage() != null ? structure.getOuvrage().getId() : null)
                .blocId(structure.getBloc() != null ? structure.getBloc().getId() : null)
                .action(structure.getAction())
                .build();
    }

    private Structure mapToEntity(StructureDTO dto) {
        Ouvrage ouvrage = null;
        if (dto.getOuvrageId() != null) {
            ouvrage = ouvrageRepository.findById(dto.getOuvrageId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ouvrage not found"));
        }

        Bloc bloc = null;
        if (dto.getBlocId() != null) {
            bloc = blocRepository.findById(dto.getBlocId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bloc not found"));
        }

        return Structure.builder()
                .idStructure(dto.getIdStructure())
                .ouvrage(ouvrage)
                .bloc(bloc)
                .action(dto.getAction())
                .build();
    }
}
