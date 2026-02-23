package com.ecopilot.project.service;

import com.ecopilot.project.dto.ClientDTO;
import com.ecopilot.project.dto.ProjetDTO;
import com.ecopilot.project.dto.OuvrageDTO;
import com.ecopilot.project.dto.BlocDTO;
import com.ecopilot.project.dto.StructureDTO;
import com.ecopilot.project.dto.ProjetArticleDTO;
import com.ecopilot.project.entity.Projet;
import com.ecopilot.project.kafka.producer.ProjectEventProducer;
import com.ecopilot.project.repository.ClientRepository;
import com.ecopilot.project.repository.ProjetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import com.ecopilot.project.dto.ProjetDetailsDTO;
import com.ecopilot.project.dto.ProjetPricingDTO;
import com.ecopilot.project.dto.ProjetLotDTO;
import com.ecopilot.project.dto.ProjetImportDTO;
import com.ecopilot.project.entity.*;
import com.ecopilot.project.repository.*;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjetService {

    private final ProjetRepository projetRepository;
    private final ClientRepository clientRepository;
    private final ProjetLotRepository projetLotRepository;
    private final OuvrageRepository ouvrageRepository;
    private final StructureRepository structureRepository;
    private final ProjetArticleRepository projetArticleRepository;
    private final BlocRepository blocRepository;
    private final ProjetEquipeRepository projetEquipeRepository;
    private final ProjectEventProducer eventProducer;

    @PersistenceContext
    private EntityManager entityManager;

    public List<ProjetDTO> getAllProjets() {
        return projetRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void importDpgfData(Long projectId, ProjetImportDTO importDTO) {
        Projet projet = projetRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projet not found"));

        for (ProjetImportDTO.LotImportDTO lotDTO : importDTO.getData()) {
            // 1. Find or create ProjetLot
            ProjetLot lot = projetLotRepository.findByProjetId(projectId).stream()
                    .filter(l -> l.getIdLot().equals(lotDTO.getLotId() != null ? lotDTO.getLotId().intValue() : null))
                    .findFirst()
                    .orElseGet(() -> {
                        ProjetLot newLot = ProjetLot.builder()
                                .projet(projet)
                                .idLot(lotDTO.getLotId() != null ? lotDTO.getLotId().intValue() : 0)
                                .designationLot(lotDTO.getName())
                                .build();
                        return projetLotRepository.save(newLot);
                    });

            // 2. Process Ouvrages
            for (ProjetImportDTO.OuvrageImportDTO ouvDTO : lotDTO.getOuvrages()) {
                Ouvrage ouvrage = Ouvrage.builder()
                        .projetLot(lot)
                        .nomOuvrage(ouvDTO.getName())
                        .designation(ouvDTO.getDesignation())
                        .build();
                Ouvrage savedOuvrage = ouvrageRepository.save(ouvrage);

                // 3. Process Articles and Blocs
                if (ouvDTO.getArticles() != null && !ouvDTO.getArticles().isEmpty()) {
                    // Track the current bloc and its structure for grouping articles
                    Bloc currentBloc = null;
                    Structure currentBlocStructure = null;
                    Structure defaultStructure = null;
                    
                    // Process all articles in order to maintain hierarchy
                    for (ProjetImportDTO.ArticleImportDTO artDTO : ouvDTO.getArticles()) {
                        String rawType = artDTO.getType() != null ? artDTO.getType().toLowerCase() : "";

                        if ("bloc".equals(rawType)) {
                            // Create a Bloc instead of an Article
                            Double total = (artDTO.getQte() != null ? artDTO.getQte() : 0) * (artDTO.getPu() != null ? artDTO.getPu() : 0);
                            Bloc bloc = Bloc.builder()
                                    .nomBloc(artDTO.getDesignation())
                                    .designation(artDTO.getDesignation())
                                    .unite(artDTO.getUnite())
                                    .quantite(artDTO.getQte() != null ? artDTO.getQte().intValue() : 0)
                                    .pu(artDTO.getPu())
                                    .pt(total)
                                    .ouvrage(savedOuvrage)
                                    .build();
                            blocRepository.save(bloc);

                            currentBloc = bloc;
                            
                            // Create a structure for the bloc and set it as current
                            currentBlocStructure = Structure.builder()
                                    .ouvrage(savedOuvrage)
                                    .bloc(bloc)
                                    .action("bloc")
                                    .build();
                            structureRepository.save(currentBlocStructure);
                        } else if ("article_ouvrage".equals(rawType)) {
                            // Articles d'ouvrage : articles de l'ouvrage positionnés "sous" un bloc
                            Structure targetStructure;

                            if (currentBloc != null) {
                                Long ouvrageId = savedOuvrage.getId();
                                String action = "ouvrage_bottom_" + currentBloc.getId();

                                targetStructure = structureRepository.findByOuvrageId(ouvrageId).stream()
                                        .filter(s -> s.getBloc() == null && action.equals(s.getAction()))
                                        .findFirst()
                                        .orElseGet(() -> {
                                            Structure newStructure = Structure.builder()
                                                    .ouvrage(savedOuvrage)
                                                    .bloc(null)
                                                    .action(action)
                                                    .build();
                                            return structureRepository.save(newStructure);
                                        });
                            } else {
                                // Pas de bloc courant : repli sur la structure par défaut de l'ouvrage
                                if (defaultStructure == null) {
                                    defaultStructure = Structure.builder()
                                            .ouvrage(savedOuvrage)
                                            .action("Imported from DPGF")
                                            .build();
                                    structureRepository.save(defaultStructure);
                                }
                                targetStructure = defaultStructure;
                            }

                            Double total = (artDTO.getQte() != null ? artDTO.getQte() : 0) * (artDTO.getPu() != null ? artDTO.getPu() : 0);
                            ProjetArticle article = ProjetArticle.builder()
                                    .structure(targetStructure)
                                    .designationArticle(artDTO.getDesignation())
                                    .uniteImport(artDTO.getUnite())
                                    .quantite(artDTO.getQte() != null ? artDTO.getQte().intValue() : 0)
                                    .pu(artDTO.getPu())
                                    .nouvPrix(artDTO.getPu())
                                    .prixTotalHt(total)
                                    .totalTtc(total)
                                    .build();
                            projetArticleRepository.save(article);
                        } else {
                            // Articles classiques : dans le bloc courant ou dans la structure par défaut de l'ouvrage
                            Structure targetStructure = currentBlocStructure != null ? currentBlocStructure : defaultStructure;

                            if (targetStructure == null) {
                                defaultStructure = Structure.builder()
                                        .ouvrage(savedOuvrage)
                                        .action("Imported from DPGF")
                                        .build();
                                structureRepository.save(defaultStructure);
                                targetStructure = defaultStructure;
                            }

                            Double total = (artDTO.getQte() != null ? artDTO.getQte() : 0) * (artDTO.getPu() != null ? artDTO.getPu() : 0);
                            ProjetArticle article = ProjetArticle.builder()
                                    .structure(targetStructure)
                                    .designationArticle(artDTO.getDesignation())
                                    .uniteImport(artDTO.getUnite())
                                    .quantite(artDTO.getQte() != null ? artDTO.getQte().intValue() : 0)
                                    .pu(artDTO.getPu())
                                    .nouvPrix(artDTO.getPu())
                                    .prixTotalHt(total)
                                    .totalTtc(total)
                                    .build();
                            projetArticleRepository.save(article);
                        }
                    }
                }
            }
        }
    }

    public ProjetDTO getProjetById(Long id) {
        return projetRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projet not found"));
    }

    public ProjetDetailsDTO getProjetFullDetails(Long id) {
        Projet projet = projetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projet not found"));
        
        List<ProjetLot> lots = projetLotRepository.findByProjetId(id);
        
        ProjetDTO projetDTO = mapToDTO(projet);
        projetDTO.setLots(lots.stream().map(this::mapLotWithHierarchy).collect(Collectors.toList()));
        
        return ProjetDetailsDTO.builder()
                .project(projetDTO)
                .pricing(calculatePricing(projet, lots))
                .build();
    }

    private ProjetPricingDTO calculatePricing(Projet project, List<ProjetLot> lots) {
        double totalLotTTC = lots.stream()
                .mapToDouble(l -> l.getPrixTotal() != null ? l.getPrixTotal() : 0.0)
                .sum();
        double totalVenteLot = lots.stream()
                .mapToDouble(l -> l.getPrixVente() != null ? l.getPrixVente() : 0.0)
                .sum();
        
        return ProjetPricingDTO.builder()
                .totalLotTTC(totalLotTTC)
                .totalVenteLot(totalVenteLot)
                .totalProjetTTC(totalLotTTC) 
                .totalVenteProjet(totalVenteLot)
                .build();
    }

    private ProjetLotDTO mapLotToDTO(ProjetLot lot) {
        return ProjetLotDTO.builder()
                .idProjetLot(lot.getIdProjetLot())
                .projetId(lot.getProjet() != null ? lot.getProjet().getId() : null)
                .idLot(lot.getIdLot())
                .designationLot(lot.getDesignationLot())
                .prixTotal(lot.getPrixTotal())
                .prixVente(lot.getPrixVente())
                .build();
    }

    private ProjetLotDTO mapLotWithHierarchy(ProjetLot lot) {
        ProjetLotDTO dto = mapLotToDTO(lot);

        java.util.List<Ouvrage> ouvrages = ouvrageRepository.findByProjetLotIdProjetLot(lot.getIdProjetLot());
        java.util.List<OuvrageDTO> ouvrageDTOs = ouvrages.stream()
                .map(this::mapOuvrageWithHierarchy)
                .collect(Collectors.toList());
        dto.setOuvrages(ouvrageDTOs);

        java.util.List<BlocDTO> blocDTOs = ouvrages.stream()
                .flatMap(ouv -> blocRepository.findByOuvrageId(ouv.getId()).stream())
                .map(this::mapBlocWithHierarchy)
                .collect(Collectors.toList());
        dto.setBlocs(blocDTOs);

        return dto;
    }

    private OuvrageDTO mapOuvrageWithHierarchy(Ouvrage ouvrage) {
        java.util.List<StructureDTO> structureDTOs = structureRepository.findByOuvrageId(ouvrage.getId()).stream()
                .map(this::mapStructureWithArticles)
                .collect(Collectors.toList());

        java.util.List<BlocDTO> blocDTOs = blocRepository.findByOuvrageId(ouvrage.getId()).stream()
                .map(this::mapBlocWithHierarchy)
                .collect(Collectors.toList());

        return OuvrageDTO.builder()
                .id(ouvrage.getId())
                .nomOuvrage(ouvrage.getNomOuvrage())
                .prixTotal(ouvrage.getPrixTotal())
                .designation(ouvrage.getDesignation())
                .projetLotId(ouvrage.getProjetLot() != null ? ouvrage.getProjetLot().getIdProjetLot() : null)
                .structures(structureDTOs)
                .blocs(blocDTOs)
                .build();
    }

    private BlocDTO mapBlocWithHierarchy(Bloc bloc) {
        java.util.List<Structure> structuresByBloc = structureRepository.findByBlocId(bloc.getId());

        java.util.List<StructureDTO> structureDTOs = structuresByBloc.stream()
                .map(this::mapStructureWithArticles)
                .collect(Collectors.toList());

        java.util.List<ProjetArticleDTO> articleDTOs = structuresByBloc.stream()
                .flatMap(s -> s.getArticles().stream())
                .map(this::mapProjetArticleToDTO)
                .collect(Collectors.toList());

        return BlocDTO.builder()
                .id(bloc.getId())
                .nomBloc(bloc.getNomBloc())
                .unite(bloc.getUnite())
                .quantite(bloc.getQuantite())
                .pu(bloc.getPu())
                .pt(bloc.getPt())
                .designation(bloc.getDesignation())
                .ouvrageId(bloc.getOuvrage() != null ? bloc.getOuvrage().getId() : null)
                .structures(structureDTOs)
                .articles(articleDTOs)
                .build();
    }

    private StructureDTO mapStructureWithArticles(Structure structure) {
        java.util.List<ProjetArticleDTO> articleDTOs = structure.getArticles().stream()
                .map(this::mapProjetArticleToDTO)
                .collect(Collectors.toList());

        return StructureDTO.builder()
                .idStructure(structure.getIdStructure())
                .ouvrageId(structure.getOuvrage() != null ? structure.getOuvrage().getId() : null)
                .blocId(structure.getBloc() != null ? structure.getBloc().getId() : null)
                .action(structure.getAction())
                .articles(articleDTOs)
                .build();
    }

    private ProjetArticleDTO mapProjetArticleToDTO(ProjetArticle article) {
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

    public ProjetDTO createProjet(ProjetDTO dto) {
        Projet projet = mapToEntity(dto);

        if (dto.getTeamMembers() != null) {
            for (String userId : dto.getTeamMembers()) {
                ProjetEquipe equipe = ProjetEquipe.builder()
                        .projet(projet)
                        .equipe(userId)
                        .build();
                projet.getEquipe().add(equipe);
            }
        }

        Projet saved = projetRepository.save(projet);
        
        // Publish Kafka event
        eventProducer.sendProjectCreatedEvent(
                saved.getId(),
                saved.getNomProjet(),
                saved.getAjoutePar()
        );
        
        return mapToDTO(saved);
    }

    @Transactional
    public ProjetDTO updateProjet(Long id, ProjetDTO dto) {
        Projet projet = projetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projet not found"));

        projet.setNomProjet(dto.getNomProjet());
        projet.setDescription(dto.getDescription());
        projet.setEtat(dto.getEtat());
        projet.setCout(dto.getCout());
        projet.setDateDebut(dto.getDateDebut());
        projet.setDateLimite(dto.getDateLimite());
        projet.setAdresse(dto.getAdresse());
        
        if (dto.getClientId() != null) {
            projet.setClient(clientRepository.findById(dto.getClientId()).orElse(null));
        }

        if (dto.getTeamMembers() != null) {
            projet.getEquipe().clear();
            for (String userId : dto.getTeamMembers()) {
                ProjetEquipe equipe = ProjetEquipe.builder()
                        .projet(projet)
                        .equipe(userId)
                        .build();
                projet.getEquipe().add(equipe);
            }
        }
        
        Projet updated = projetRepository.save(projet);
        
        eventProducer.sendProjectUpdatedEvent(
                updated.getId(),
                updated.getNomProjet(),
                updated.getAjoutePar()
        );
        
        return mapToDTO(updated);
    }

    @Transactional
    public void deleteProjet(Long id) {
        Projet projet = projetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projet not found"));

        List<ProjetLot> lots = projetLotRepository.findByProjetId(id);
        for (ProjetLot lot : lots) {
            List<Ouvrage> ouvrages = ouvrageRepository.findByProjetLotIdProjetLot(lot.getIdProjetLot());
            for (Ouvrage ouvrage : ouvrages) {
                List<Structure> structuresByOuvrage = structureRepository.findByOuvrageId(ouvrage.getId());
                for (Structure structure : structuresByOuvrage) {
                    projetArticleRepository.deleteByStructureIdStructure(structure.getIdStructure());
                }

                List<Bloc> blocs = blocRepository.findByOuvrageId(ouvrage.getId());
                for (Bloc bloc : blocs) {
                    List<Structure> structuresByBloc = structureRepository.findByBlocId(bloc.getId());
                    for (Structure structure : structuresByBloc) {
                        projetArticleRepository.deleteByStructureIdStructure(structure.getIdStructure());
                        structureRepository.delete(structure);
                    }
                }

                structureRepository.deleteByOuvrageId(ouvrage.getId());
                blocRepository.deleteByOuvrageId(ouvrage.getId());
            }
        }

        projetRepository.delete(projet);
    }

    @Transactional
    public void fixBlocIdsSoTheyDoNotCollideWithOuvrages() {
        List<Long> ouvrageIds = ouvrageRepository.findAll().stream()
                .map(Ouvrage::getId)
                .collect(Collectors.toList());
        Set<Long> reservedIds = new HashSet<>(ouvrageIds);

        List<Bloc> blocs = blocRepository.findAll();
        Set<Long> existingBlocIds = blocs.stream()
                .map(Bloc::getId)
                .collect(Collectors.toSet());

        for (Bloc bloc : blocs) {
            Long oldId = bloc.getId();
            Long candidate = oldId;

            if (!reservedIds.contains(candidate)) {
                reservedIds.add(candidate);
                continue;
            }

            do {
                candidate += 1;
            } while (reservedIds.contains(candidate) || existingBlocIds.contains(candidate));

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

            reservedIds.add(candidate);
            existingBlocIds.remove(oldId);
            existingBlocIds.add(candidate);
        }
    }

    // Mapper Methods
    private ProjetDTO mapToDTO(Projet projet) {
        List<String> teamMemberIds = projet.getEquipe() != null
                ? projet.getEquipe().stream()
                .map(ProjetEquipe::getEquipe)
                .collect(Collectors.toList())
                : null;

        ClientDTO clientData = null;
        if (projet.getClient() != null) {
            clientData = ClientDTO.builder()
                    .id(projet.getClient().getId())
                    .nomClient(projet.getClient().getNomClient())
                    .margeBrut(projet.getClient().getMargeBrut())
                    .margeNet(projet.getClient().getMargeNet())
                    .agence(projet.getClient().getAgence())
                    .responsable(projet.getClient().getResponsable())
                    .effectifChantier(projet.getClient().getEffectifChantier())
                    .createdAt(projet.getClient().getCreatedAt())
                    .build();
        }

        return ProjetDTO.builder()
                .id(projet.getId())
                .nomProjet(projet.getNomProjet())
                .description(projet.getDescription())
                .etat(projet.getEtat())
                .cout(projet.getCout())
                .dateDebut(projet.getDateDebut())
                .dateLimite(projet.getDateLimite())
                .clientId(projet.getClient() != null ? projet.getClient().getId() : null)
                .clientData(clientData)
                .ajoutePar(projet.getAjoutePar())
                .adresse(projet.getAdresse())
                .file(projet.getFile())
                .teamMembers(teamMemberIds)
                .createdAt(projet.getCreatedAt())
                .build();
    }

    private Projet mapToEntity(ProjetDTO dto) {
        return Projet.builder()
                .nomProjet(dto.getNomProjet())
                .description(dto.getDescription())
                .etat(dto.getEtat())
                .cout(dto.getCout())
                .dateDebut(dto.getDateDebut())
                .dateLimite(dto.getDateLimite())
                .ajoutePar(dto.getAjoutePar())
                .adresse(dto.getAdresse())
                .file(dto.getFile())
                .client(dto.getClientId() != null ? clientRepository.findById(dto.getClientId()).orElse(null) : null)
                .build();
    }
}
