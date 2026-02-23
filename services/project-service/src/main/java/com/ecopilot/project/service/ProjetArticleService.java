package com.ecopilot.project.service;

import com.ecopilot.project.dto.ProjetArticleDTO;
import com.ecopilot.project.entity.ProjetArticle;
import com.ecopilot.project.entity.Structure;
import com.ecopilot.project.entity.Bloc;
import com.ecopilot.project.entity.Ouvrage;
import com.ecopilot.project.entity.ProjetLot;
import com.ecopilot.project.entity.Projet;
import com.ecopilot.project.repository.ProjetArticleRepository;
import com.ecopilot.project.repository.StructureRepository;
import com.ecopilot.project.repository.BlocRepository;
import com.ecopilot.project.kafka.producer.ProjectEventProducer;
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
public class ProjetArticleService {

    private final ProjetArticleRepository projetArticleRepository;
    private final StructureRepository structureRepository;
    private final BlocRepository blocRepository;
    private final ProjectEventProducer eventProducer;

    public List<ProjetArticleDTO> getAllArticles() {
        return projetArticleRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ProjetArticleDTO> getArticlesByStructureId(Long structureId) {
        return projetArticleRepository.findByStructureIdStructure(structureId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ProjetArticleDTO getArticleById(Long id) {
        return projetArticleRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProjetArticle not found"));
    }

    public ProjetArticleDTO createArticle(ProjetArticleDTO dto) {
        ProjetArticle article = mapToEntity(dto);
        ProjetArticle saved = projetArticleRepository.save(article);

        Long projetId = null;
        Long lotId = null;
        Long ouvrageId = null;
        Long blocId = null;

        Structure structure = saved.getStructure();
        if (structure != null) {
            Bloc bloc = structure.getBloc();
            if (bloc != null) {
                blocId = bloc.getId();
            }
            Ouvrage ouvrage = structure.getOuvrage();
            if (ouvrage == null && bloc != null) {
                ouvrage = bloc.getOuvrage();
            }
            if (ouvrage != null) {
                ouvrageId = ouvrage.getId();
                ProjetLot lot = ouvrage.getProjetLot();
                if (lot != null) {
                    lotId = Long.valueOf(lot.getIdLot());
                    Projet projet = lot.getProjet();
                    if (projet != null) {
                        projetId = projet.getId();
                    }
                }
            }
        }

        String designation = saved.getDesignationArticle();
        Integer quantite = saved.getQuantite();
        Double nouvPrix = saved.getNouvPrix();
        Integer articleCatalogId = saved.getArticle();
        String userId = null;

        if (projetId != null) {
            ProjetLot lot = null;
            if (ouvrageId != null) {
                Ouvrage ouvrage = null;
                if (structure != null) {
                    if (structure.getOuvrage() != null) {
                        ouvrage = structure.getOuvrage();
                    } else if (structure.getBloc() != null && structure.getBloc().getOuvrage() != null) {
                        ouvrage = structure.getBloc().getOuvrage();
                    }
                }
                if (ouvrage != null) {
                    lot = ouvrage.getProjetLot();
                }
            }
            if (lot != null) {
                Projet projet = lot.getProjet();
                if (projet != null) {
                    userId = projet.getAjoutePar();
                }
            }
        }

        eventProducer.sendProjetArticleCreatedEvent(
                projetId,
                lotId,
                ouvrageId,
                blocId,
                saved.getId(),
                articleCatalogId,
                designation,
                quantite,
                nouvPrix,
                userId
        );

        return mapToDTO(saved);
    }

    public ProjetArticleDTO updateArticle(Long id, ProjetArticleDTO dto) {
        ProjetArticle article = projetArticleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProjetArticle not found"));

        Long projetId = null;
        Long lotId = null;
        Long ouvrageId = null;
        Long blocId = null;

        Structure structure = article.getStructure();
        if (structure != null) {
            Bloc bloc = structure.getBloc();
            if (bloc != null) {
                blocId = bloc.getId();
            }
            Ouvrage ouvrage = structure.getOuvrage();
            if (ouvrage == null && bloc != null) {
                ouvrage = bloc.getOuvrage();
            }
            if (ouvrage != null) {
                ouvrageId = ouvrage.getId();
                ProjetLot lot = ouvrage.getProjetLot();
                if (lot != null) {
                    lotId = Long.valueOf(lot.getIdLot());
                    Projet projet = lot.getProjet();
                    if (projet != null) {
                        projetId = projet.getId();
                    }
                }
            }
        }

        Integer articleCatalogId = article.getArticle();

        java.util.Map<String, Object> fieldChanges = new java.util.HashMap<>();

        if (dto.getNouvPrix() != null && (article.getNouvPrix() == null || !dto.getNouvPrix().equals(article.getNouvPrix()))) {
            java.util.Map<String, Object> change = new java.util.HashMap<>();
            change.put("old", article.getNouvPrix());
            change.put("new", dto.getNouvPrix());
            fieldChanges.put("nouv_prix", change);
        }

        if (dto.getQuantite() != null && (article.getQuantite() == null || !dto.getQuantite().equals(article.getQuantite()))) {
            java.util.Map<String, Object> change = new java.util.HashMap<>();
            change.put("old", article.getQuantite());
            change.put("new", dto.getQuantite());
            fieldChanges.put("quantite", change);
        }

        if (dto.getTva() != null && (article.getTva() == null || !dto.getTva().equals(article.getTva()))) {
            java.util.Map<String, Object> change = new java.util.HashMap<>();
            change.put("old", article.getTva());
            change.put("new", dto.getTva());
            fieldChanges.put("tva", change);
        }

        if (dto.getDesignationArticle() != null && (article.getDesignationArticle() == null || !dto.getDesignationArticle().equals(article.getDesignationArticle()))) {
            java.util.Map<String, Object> change = new java.util.HashMap<>();
            change.put("old", article.getDesignationArticle());
            change.put("new", dto.getDesignationArticle());
            fieldChanges.put("designation_article", change);
        }

        if (dto.getArticle() != null) {
            article.setArticle(dto.getArticle());
        }
        article.setQuantite(dto.getQuantite());
        article.setPu(dto.getPu());
        article.setPrixTotalHt(dto.getPrixTotalHt());
        article.setTva(dto.getTva());
        article.setTotalTtc(dto.getTotalTtc());
        article.setLocalisation(dto.getLocalisation());
        article.setDescription(dto.getDescription());
        article.setNouvPrix(dto.getNouvPrix());
        article.setDesignationArticle(dto.getDesignationArticle());
        article.setArticleImport(dto.getArticleImport());
        if (dto.getUnite() != null) {
            article.setUnite(dto.getUnite());
        }
        if (dto.getUniteImport() != null) {
            article.setUniteImport(dto.getUniteImport());
        }

        ProjetArticle updated = projetArticleRepository.save(article);

        String userId = null;
        if (projetId != null && structure != null) {
            Ouvrage ouvrage = structure.getOuvrage();
            Bloc bloc = structure.getBloc();
            if (ouvrage == null && bloc != null) {
                ouvrage = bloc.getOuvrage();
            }
            if (ouvrage != null) {
                ProjetLot lot = ouvrage.getProjetLot();
                if (lot != null) {
                    Projet projet = lot.getProjet();
                    if (projet != null) {
                        userId = projet.getAjoutePar();
                    }
                }
            }
        }

        eventProducer.sendProjetArticleUpdatedEvent(
                projetId,
                lotId,
                ouvrageId,
                blocId,
                updated.getId(),
                articleCatalogId,
                fieldChanges,
                userId
        );

        return mapToDTO(updated);
    }

    public void deleteArticle(Long id) {
        ProjetArticle article = projetArticleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProjetArticle not found"));

        Long projetId = null;
        Long lotId = null;
        Long ouvrageId = null;
        Long blocId = null;

        Structure structure = article.getStructure();
        if (structure != null) {
            Bloc bloc = structure.getBloc();
            if (bloc != null) {
                blocId = bloc.getId();
            }
            Ouvrage ouvrage = structure.getOuvrage();
            if (ouvrage == null && bloc != null) {
                ouvrage = bloc.getOuvrage();
            }
            if (ouvrage != null) {
                ouvrageId = ouvrage.getId();
                ProjetLot lot = ouvrage.getProjetLot();
                if (lot != null) {
                    lotId = Long.valueOf(lot.getIdLot());
                    Projet projet = lot.getProjet();
                    if (projet != null) {
                        projetId = projet.getId();
                    }
                }
            }
        }

        Integer articleCatalogId = article.getArticle();
        String designation = article.getDesignationArticle();
        Integer quantite = article.getQuantite();

        String userId = null;
        if (projetId != null && structure != null) {
            Ouvrage ouvrage = structure.getOuvrage();
            Bloc bloc = structure.getBloc();
            if (ouvrage == null && bloc != null) {
                ouvrage = bloc.getOuvrage();
            }
            if (ouvrage != null) {
                ProjetLot lot = ouvrage.getProjetLot();
                if (lot != null) {
                    Projet projet = lot.getProjet();
                    if (projet != null) {
                        userId = projet.getAjoutePar();
                    }
                }
            }
        }

        eventProducer.sendProjetArticleDeletedEvent(
            projetId,
            lotId,
            ouvrageId,
            blocId,
            article.getId(),
            articleCatalogId,
            designation,
            quantite,
            userId
        );

        projetArticleRepository.delete(article);
    }

    // Mapper Methods
    private ProjetArticleDTO mapToDTO(ProjetArticle article) {
        return ProjetArticleDTO.builder()
                .id(article.getId())
                .article(article.getArticle())
                .quantite(article.getQuantite())
                .pu(article.getPu())
                .prixTotalHt(article.getPrixTotalHt())
                .tva(article.getTva())
                .totalTtc(article.getTotalTtc())
                .localisation(article.getLocalisation())
                .description(article.getDescription())
                .nouvPrix(article.getNouvPrix())
                .designationArticle(article.getDesignationArticle())
                .articleImport(article.getArticleImport())
                .unite(article.getUnite())
                .uniteImport(article.getUniteImport())
                .structureId(article.getStructure() != null ? article.getStructure().getIdStructure() : null)
                .build();
    }

    private ProjetArticle mapToEntity(ProjetArticleDTO dto) {
        Structure structure = resolveStructure(dto);

        return ProjetArticle.builder()
                .id(dto.getId())
                .article(dto.getArticle())
                .quantite(dto.getQuantite())
                .pu(dto.getPu())
                .prixTotalHt(dto.getPrixTotalHt())
                .tva(dto.getTva())
                .totalTtc(dto.getTotalTtc())
                .localisation(dto.getLocalisation())
                .description(dto.getDescription())
                .nouvPrix(dto.getNouvPrix())
                .designationArticle(dto.getDesignationArticle())
                .articleImport(dto.getArticleImport())
                .unite(dto.getUnite())
                .uniteImport(dto.getUniteImport())
                .structure(structure)
                .build();
    }

    private Structure resolveStructure(ProjetArticleDTO dto) {
        if (dto.getStructureId() != null) {
            return structureRepository.findById(dto.getStructureId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Structure not found"));
        }

        if (dto.getBlocId() != null && "bottom".equalsIgnoreCase(dto.getPosition())) {
            Bloc bloc = blocRepository.findById(dto.getBlocId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bloc not found"));

            if (bloc.getOuvrage() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bloc is not attached to an ouvrage");
            }

            Long ouvrageId = bloc.getOuvrage().getId();
            String action = "ouvrage_bottom_" + bloc.getId();

            return structureRepository.findByOuvrageId(ouvrageId).stream()
                    .filter(s -> s.getBloc() == null && action.equals(s.getAction()))
                    .findFirst()
                    .orElseGet(() -> {
                        Structure newStructure = Structure.builder()
                                .ouvrage(bloc.getOuvrage())
                                .bloc(null)
                                .action(action)
                                .build();
                        return structureRepository.save(newStructure);
                    });
        }

        return null;
    }
}
