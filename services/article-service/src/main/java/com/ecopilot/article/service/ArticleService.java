package com.ecopilot.article.service;

import com.ecopilot.article.dto.ArticleDTO;
import com.ecopilot.article.dto.ArticleHierarchyDTO;
import com.ecopilot.article.dto.FilterDTO;
import com.ecopilot.article.entity.Article;
import com.ecopilot.article.entity.ArticleSupprime;
import com.ecopilot.article.entity.PendingArticle;
import com.ecopilot.article.entity.Niveau1;
import com.ecopilot.article.entity.Niveau2;
import com.ecopilot.article.entity.Niveau3;
import com.ecopilot.article.entity.Niveau4;
import com.ecopilot.article.entity.Niveau5;
import com.ecopilot.article.entity.Niveau6;
import com.ecopilot.article.kafka.producer.ArticleEventProducer;
import com.ecopilot.article.repository.ArticleRepository;
import com.ecopilot.article.repository.ArticleSupprimeRepository;
import com.ecopilot.article.repository.Niveau2Repository;
import com.ecopilot.article.repository.Niveau3Repository;
import com.ecopilot.article.repository.Niveau4Repository;
import com.ecopilot.article.repository.Niveau5Repository;
import com.ecopilot.article.repository.Niveau6Repository;
import com.ecopilot.article.repository.PendingArticleRepository;
import com.ecopilot.article.strategy.PriceStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final PendingArticleRepository pendingArticleRepository;
    private final ArticleSupprimeRepository articleSupprimeRepository;
    private final Niveau2Repository niveau2Repository;
    private final Niveau3Repository niveau3Repository;
    private final Niveau4Repository niveau4Repository;
    private final Niveau5Repository niveau5Repository;
    private final Niveau6Repository niveau6Repository;
    private final PriceStrategy priceStrategy;
    private final ArticleEventProducer eventProducer;

    private String getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("sub");
        }
        return authentication != null ? authentication.getName() : "system";
    }

    private boolean isCurrentUserAdmin() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            try {
                java.util.Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
                if (realmAccess != null) {
                    Object roles = realmAccess.get("roles");
                    if (roles instanceof java.util.List<?> roleList) {
                        return roleList.contains("admin") || roleList.contains("ADMIN");
                    }
                }
            } catch (Exception e) {
                // Pas admin
            }
        }
        return false;
    }

    public List<String> getAllExpertises() {
        return articleRepository.findDistinctExpertises();
    }

    public List<FilterDTO> getUsedNiveau1() {
        return articleRepository.findDistinctNiveau1();
    }

    public List<FilterDTO> getUsedNiveau2(Long parentId) {
        return articleRepository.findDistinctNiveau2(parentId);
    }

    public List<FilterDTO> getUsedNiveau3(Long parentId) {
        return articleRepository.findDistinctNiveau3(parentId);
    }

    public List<FilterDTO> getUsedNiveau4(Long parentId) {
        return articleRepository.findDistinctNiveau4(parentId);
    }

    public List<FilterDTO> getUsedNiveau5(Long parentId) {
        return articleRepository.findDistinctNiveau5(parentId);
    }

    public List<FilterDTO> getUsedNiveau6(Long parentId) {
        return articleRepository.findDistinctNiveau6(parentId);
    }

    public List<ArticleHierarchyDTO> getArticleHierarchy() {
        return articleRepository.findAllArticleHierarchy();
    }

    public List<ArticleDTO> getAllArticles() {
        return articleRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ArticleDTO> getAllArticles(int page, int limit, String sortBy, String sortOrder) {
        // For now, return all articles (pagination can be implemented later with Pageable)
        // This is a basic implementation that ignores pagination parameters
        return articleRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ArticleDTO> getAllArticles(int page, int limit, String sortBy, String sortOrder, List<Long> idNiveau6) {
        if (idNiveau6 != null && !idNiveau6.isEmpty()) {
             return articleRepository.findByNiveau6IdIn(idNiveau6).stream()
                     .map(this::mapToDTO)
                     .collect(Collectors.toList());
        }
        return getAllArticles(page, limit, sortBy, sortOrder);
    }

    public List<ArticleDTO> searchArticles(String query) {
        return articleRepository.findByNomArticleContainingIgnoreCase(query).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ArticleDTO getArticleById(Long id) {
        return articleRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
    }

    public ArticleDTO createArticle(ArticleDTO articleDTO) {
        String currentUserId = getCurrentUserId();
        
        // Si pas admin, crÃ©er dans pending_articles
        if (!isCurrentUserAdmin()) {
            return createPendingArticle(articleDTO, currentUserId);
        }
        
        // Admin : crÃ©er directement dans articles
        Article article = mapToEntity(articleDTO);
        article.setUserId(currentUserId);
        
        // Apply Strategy Pattern for Price Calculation if needed
        if (articleDTO.getPu() != null) {
             try {
                 BigDecimal basePrice = new BigDecimal(articleDTO.getPu());
                 BigDecimal calculated = priceStrategy.calculatePrice(basePrice);
                 // For now we just log or use it, as the logic itself is placeholder
             } catch (NumberFormatException e) {
                 // ignore
             }
        }

        Article saved = articleRepository.save(article);
        
        // Publish Kafka event
        Double prixUnitaire = null;
        try {
            if (saved.getPu() != null) {
                prixUnitaire = Double.parseDouble(saved.getPu());
            }
        } catch (NumberFormatException e) {
            // ignore
        }
        
        eventProducer.sendArticleCreatedEvent(
                saved.getId(),
                saved.getNomArticle(),
                saved.getUnite(),
                prixUnitaire,
                saved.getUserId()
        );
        
        return mapToDTO(saved);
    }

    public ArticleDTO updateArticle(Long id, ArticleDTO articleDTO) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));

        if (articleDTO.getNomArticle() != null) {
            article.setNomArticle(articleDTO.getNomArticle());
        }
        if (articleDTO.getUnite() != null) {
            article.setUnite(articleDTO.getUnite());
        }
        if (articleDTO.getType() != null) {
            article.setType(articleDTO.getType());
        }
        if (articleDTO.getExpertise() != null) {
            article.setExpertise(articleDTO.getExpertise());
        }
        if (articleDTO.getFourniture() != null) {
            article.setFourniture(articleDTO.getFourniture());
        }
        if (articleDTO.getCadence() != null) {
            article.setCadence(articleDTO.getCadence());
        }
        if (articleDTO.getAccessoires() != null) {
            article.setAccessoires(articleDTO.getAccessoires());
        }
        if (articleDTO.getPertes() != null) {
            article.setPertes(articleDTO.getPertes());
        }
        if (articleDTO.getPu() != null) {
            article.setPu(articleDTO.getPu());
        }
        boolean hasAnyPriceUpdate =
                articleDTO.getPrixCible() != null ||
                articleDTO.getPrixEstime() != null ||
                articleDTO.getPrixConsulte() != null;
        if (hasAnyPriceUpdate) {
            article.setPrixCible(articleDTO.getPrixCible());
            article.setPrixEstime(articleDTO.getPrixEstime());
            article.setPrixConsulte(articleDTO.getPrixConsulte());
        }
        if (articleDTO.getRabais() != null) {
            article.setRabais(articleDTO.getRabais());
        }
        if (articleDTO.getCommentaires() != null) {
            article.setCommentaires(articleDTO.getCommentaires());
        }
        if (articleDTO.getIndiceDeConfiance() != null) {
            article.setIndiceDeConfiance(articleDTO.getIndiceDeConfiance());
        }
        if (articleDTO.getFiles() != null) {
            article.setFiles(articleDTO.getFiles());
        }
        if (articleDTO.getFournisseurId() != null) {
            article.setFournisseurId(articleDTO.getFournisseurId());
        }

        if (articleDTO.getNiveau6Id() != null) {
            niveau6Repository.findById(articleDTO.getNiveau6Id())
                    .ifPresent(article::setNiveau6);
        }

        Article updated = articleRepository.save(article);

        Double prixUnitaire = null;
        try {
            if (updated.getPu() != null) {
                prixUnitaire = Double.parseDouble(updated.getPu());
            }
        } catch (NumberFormatException e) {
        }

        eventProducer.sendArticleUpdatedEvent(
                updated.getId(),
                updated.getNomArticle(),
                updated.getUnite(),
                prixUnitaire,
                updated.getUserId()
        );

        return mapToDTO(updated);
    }
    
    public void deleteArticle(Long id) {
         Article article = articleRepository.findById(id)
                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
         
         // Create ArticleSupprime (Historisation)
        ArticleSupprime supprime = ArticleSupprime.builder()
                .date(article.getDate() != null ? article.getDate() : java.time.LocalDate.now())
                 .nomArticle(article.getNomArticle())
                 .unite(article.getUnite())
                 .type(article.getType())
                 .expertise(article.getExpertise())
                 .fourniture(article.getFourniture())
                 .cadence(article.getCadence())
                 .accessoires(article.getAccessoires())
                 .pertes(article.getPertes())
                 .pu(article.getPu())
                 .prixCible(article.getPrixCible())
                 .prixEstime(article.getPrixEstime())
                 .prixConsulte(article.getPrixConsulte())
                 .rabais(article.getRabais())
                 .commentaires(article.getCommentaires())
                 .userId(article.getUserId())
                 .deletedBy(getCurrentUserId())
                 .indiceDeConfiance(article.getIndiceDeConfiance())
                .files(article.getFiles())
                .fournisseurId(article.getFournisseurId())
                .niveau6(article.getNiveau6())
                .date(java.time.LocalDate.now())
                .build();

         articleSupprimeRepository.save(supprime);

         articleRepository.deleteById(id);
         
         // Publish Kafka event
         eventProducer.sendArticleDeletedEvent(id, article.getUserId());
     }

    public ArticleDTO getPendingArticleById(Long id) {
        return pendingArticleRepository.findById(id)
                .map(this::mapPendingToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pending Article not found"));
    }

    public List<ArticleDTO> getUserPendingArticles(String userId) {
        return pendingArticleRepository.findByCreatedBy(userId).stream()
                .map(this::mapPendingToDTO)
                .collect(Collectors.toList());
    }

    public List<ArticleDTO> getAllPendingArticles() {
        return pendingArticleRepository.findAll().stream()
                .map(this::mapPendingToDTO)
                .collect(Collectors.toList());
    }

    public ArticleDTO updatePendingArticle(Long id, ArticleDTO dto) {
        PendingArticle pending = pendingArticleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pending Article not found"));

        if (dto.getNomArticle() != null) {
            pending.setNomArticle(dto.getNomArticle());
        }
        if (dto.getUnite() != null) {
            pending.setUnite(dto.getUnite());
        }
        if (dto.getType() != null) {
            pending.setType(dto.getType());
        }
        if (dto.getExpertise() != null) {
            pending.setExpertise(dto.getExpertise());
        }
        if (dto.getFourniture() != null) {
            pending.setFourniture(new BigDecimal(dto.getFourniture()));
        }
        if (dto.getCadence() != null) {
            pending.setCadence(new BigDecimal(dto.getCadence()));
        }
        if (dto.getAccessoires() != null) {
            pending.setAccessoires(new BigDecimal(dto.getAccessoires()));
        }
        if (dto.getPertes() != null) {
            pending.setPertes(dto.getPertes());
        }
        if (dto.getPu() != null) {
            pending.setPu(dto.getPu());
        }
        if (dto.getPrixCible() != null) {
            pending.setPrixCible(new BigDecimal(dto.getPrixCible()));
        }
        if (dto.getPrixEstime() != null) {
            pending.setPrixEstime(new BigDecimal(dto.getPrixEstime()));
        }
        if (dto.getPrixConsulte() != null) {
            pending.setPrixConsulte(new BigDecimal(dto.getPrixConsulte()));
        }
        if (dto.getRabais() != null) {
            pending.setRabais(dto.getRabais());
        }
        if (dto.getCommentaires() != null) {
            pending.setCommentaires(dto.getCommentaires());
        }
        if (dto.getIndiceDeConfiance() != null) {
            pending.setIndiceDeConfiance(dto.getIndiceDeConfiance());
        }
        if (dto.getFiles() != null) {
            pending.setFiles(dto.getFiles());
        }
        if (dto.getFournisseurId() != null) {
            pending.setFournisseurId(dto.getFournisseurId());
        }
        if (dto.getDate() != null) {
            pending.setDate(dto.getDate());
        }
        if (dto.getNiveau6Id() != null) {
            niveau6Repository.findById(dto.getNiveau6Id()).ifPresent(pending::setNiveau6);
        }

        PendingArticle saved = pendingArticleRepository.save(pending);
        return mapPendingToDTO(saved);
    }

    public void deletePendingArticle(Long id) {
        PendingArticle pending = pendingArticleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pending Article not found"));

        pendingArticleRepository.delete(pending);
    }

    public List<ArticleDTO> getAllDeletedArticles() {
        return articleSupprimeRepository.findAll().stream()
                .map(this::mapSupprimeToDTO)
                .collect(Collectors.toList());
    }

    public void approvePendingArticle(Long id) {
        PendingArticle pending = pendingArticleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pending Article not found"));
        
        pending.setAcceptedBy(getCurrentUserId());
        pending.setStatus("Approuve");
        pending.setReviewedAt(LocalDateTime.now());
        pending.setUpdatedAt(LocalDateTime.now());

        Article article = Article.builder()
                .date(pending.getDate())
                .nomArticle(pending.getNomArticle())
                .unite(pending.getUnite())
                .type(pending.getType())
                .expertise(pending.getExpertise())
                .fourniture(pending.getFourniture() != null ? pending.getFourniture().toString() : null)
                .cadence(pending.getCadence() != null ? pending.getCadence().toString() : null)
                .accessoires(pending.getAccessoires() != null ? pending.getAccessoires().toString() : null)
                .pertes(pending.getPertes())
                .pu(pending.getPu())
                .prixCible(pending.getPrixCible() != null ? pending.getPrixCible().toString() : null)
                .prixEstime(pending.getPrixEstime() != null ? pending.getPrixEstime().toString() : null)
                .prixConsulte(pending.getPrixConsulte() != null ? pending.getPrixConsulte().toString() : null)
                .rabais(pending.getRabais())
                .commentaires(pending.getCommentaires())
                .userId(pending.getCreatedBy())
                .indiceDeConfiance(pending.getIndiceDeConfiance())
                .files(pending.getFiles())
                .fournisseurId(pending.getFournisseurId())
                .niveau6(pending.getNiveau6())
                .build();
        
        articleRepository.save(article);
        pending.setApprovedArticleId(article.getId());
        pendingArticleRepository.save(pending);
        
        // Publish event
        Double prixUnitaire = null;
        try {
            if (article.getPu() != null) {
                prixUnitaire = Double.parseDouble(article.getPu());
            }
        } catch (NumberFormatException e) {}
        
        eventProducer.sendArticleCreatedEvent(
                article.getId(),
                article.getNomArticle(),
                article.getUnite(),
                prixUnitaire,
                article.getUserId()
        );
    }

    public void rejectPendingArticle(Long id) {
        PendingArticle pending = pendingArticleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pending Article not found"));
        
        pending.setRejectedBy(getCurrentUserId());
        pending.setStatus("Rejete");
        pending.setReviewedAt(LocalDateTime.now());
        pending.setUpdatedAt(LocalDateTime.now());
        pendingArticleRepository.save(pending);
    }

    public void resetPendingArticleStatus(Long id) {
        PendingArticle pending = pendingArticleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pending Article not found"));

        pending.setStatus("En attente");
        pending.setAcceptedBy(null);
        pending.setRejectedBy(null);
        pending.setReviewedAt(null);
        pending.setUpdatedAt(LocalDateTime.now());
        pendingArticleRepository.save(pending);
    }

    // Mapper for PendingArticle
    private ArticleDTO mapPendingToDTO(PendingArticle article) {
        ArticleDTO.ArticleDTOBuilder builder = ArticleDTO.builder()
                .id(article.getId())
                .date(article.getDate())
                .nomArticle(article.getNomArticle())
                .unite(article.getUnite())
                .type(article.getType())
                .expertise(article.getExpertise())
                .fourniture(article.getFourniture() != null ? article.getFourniture().toString() : null)
                .cadence(article.getCadence() != null ? article.getCadence().toString() : null)
                .accessoires(article.getAccessoires() != null ? article.getAccessoires().toString() : null)
                .pertes(article.getPertes())
                .pu(article.getPu())
                .prixCible(article.getPrixCible() != null ? article.getPrixCible().toString() : null)
                .prixEstime(article.getPrixEstime() != null ? article.getPrixEstime().toString() : null)
                .prixConsulte(article.getPrixConsulte() != null ? article.getPrixConsulte().toString() : null)
                .rabais(article.getRabais())
                .commentaires(article.getCommentaires())
                .userId(article.getCreatedBy() != null ? article.getCreatedBy().toString() : null)
                .status(article.getStatus())
                .acceptedBy(article.getAcceptedBy())
                .rejectedBy(article.getRejectedBy())
                .approvedArticleId(article.getApprovedArticleId())
                .indiceDeConfiance(article.getIndiceDeConfiance())
                .files(article.getFiles())
                .fournisseurId(article.getFournisseurId())
                .niveau6Id(article.getNiveau6() != null ? article.getNiveau6().getId() : null)
                .createdAt(article.getSubmittedAt())
                .submittedAt(article.getSubmittedAt())
                .updatedAt(article.getUpdatedAt());

        boolean hasPrixConsulte = article.getPrixConsulte() != null && article.getPrixConsulte().compareTo(java.math.BigDecimal.ZERO) > 0;
        boolean hasPrixEstime = article.getPrixEstime() != null && article.getPrixEstime().compareTo(java.math.BigDecimal.ZERO) > 0;
        boolean hasPrixCible = article.getPrixCible() != null && article.getPrixCible().compareTo(java.math.BigDecimal.ZERO) > 0;

        if (hasPrixConsulte) {
            builder.origine("consulte");
        } else if (hasPrixEstime) {
            builder.origine("estime");
        } else if (hasPrixCible) {
            builder.origine("cible");
        }

        // Populate Hierarchy
        if (article.getNiveau6() != null) {
            populateHierarchy(builder, article.getNiveau6());
        }

        return builder.build();
    }

    // Mapper for ArticleSupprime
    private ArticleDTO mapSupprimeToDTO(ArticleSupprime article) {
        ArticleDTO.ArticleDTOBuilder builder = ArticleDTO.builder()
                .id(article.getId())
                .date(article.getDate())
                .nomArticle(article.getNomArticle())
                .unite(article.getUnite())
                .type(article.getType())
                .expertise(article.getExpertise())
                .fourniture(article.getFourniture())
                .cadence(article.getCadence())
                .accessoires(article.getAccessoires())
                .pertes(article.getPertes())
                .pu(article.getPu())
                .prixCible(article.getPrixCible())
                .prixEstime(article.getPrixEstime())
                .prixConsulte(article.getPrixConsulte())
                .rabais(article.getRabais())
                .commentaires(article.getCommentaires())
                .userId(article.getUserId() != null ? article.getUserId().toString() : null)
                .indiceDeConfiance(article.getIndiceDeConfiance())
                .files(article.getFiles())
                .fournisseurId(article.getFournisseurId())
                .niveau6Id(article.getNiveau6() != null ? article.getNiveau6().getId() : null);
                if (article.getDate() != null) {
                    builder.createdAt(article.getDate().atStartOfDay());
                }

        // Derive Origine
        if (article.getPrixConsulte() != null && !article.getPrixConsulte().isEmpty()) {
            builder.origine("consulte");
        } else if (article.getPrixEstime() != null && !article.getPrixEstime().isEmpty()) {
            builder.origine("estime");
        } else if (article.getPrixCible() != null && !article.getPrixCible().isEmpty()) {
            builder.origine("cible");
        }

        // Populate Hierarchy
        if (article.getNiveau6() != null) {
            populateHierarchy(builder, article.getNiveau6());
        }

        return builder.build();
    }

    private void populateHierarchy(ArticleDTO.ArticleDTOBuilder builder, Niveau6 niveau6) {
        builder.niveau6(niveau6.getNom());

        Niveau5 n5 = niveau6.getNiveau5();
        if (n5 != null) {
            builder.niveau5(n5.getNom());
        }

        Niveau4 n4 = n5 != null ? n5.getNiveau4() : null;
        if (n4 == null && niveau6.getIdNiveau4() != null) {
            n4 = niveau4Repository.findById(niveau6.getIdNiveau4()).orElse(null);
        }
        if (n4 != null) {
            builder.niveau4(n4.getNom());
        }

        Niveau3 n3 = null;
        if (n4 != null) {
            n3 = n4.getNiveau3();
        }
        if (n3 == null && n5 != null && n5.getIdNiveau3() != null) {
            n3 = niveau3Repository.findById(n5.getIdNiveau3()).orElse(null);
        }
        if (n3 == null && niveau6.getIdNiveau3() != null) {
            n3 = niveau3Repository.findById(niveau6.getIdNiveau3()).orElse(null);
        }
        if (n3 != null) {
            builder.niveau3(n3.getNom());
        }

        Niveau2 n2 = n3 != null ? n3.getNiveau2() : null;
        if (n2 != null) {
            builder.niveau2(n2.getNom());
        }

        Niveau1 n1 = n2 != null ? n2.getNiveau1() : null;
        if (n1 != null) {
            builder.niveau1(n1.getNom());
        }
    }

    // Mapper Methods
    private ArticleDTO mapToDTO(Article article) {
        ArticleDTO.ArticleDTOBuilder builder = ArticleDTO.builder()
                .id(article.getId())
                .date(article.getDate())
                .nomArticle(article.getNomArticle())
                .unite(article.getUnite())
                .type(article.getType())
                .expertise(article.getExpertise())
                .fourniture(article.getFourniture())
                .cadence(article.getCadence())
                .accessoires(article.getAccessoires())
                .pertes(article.getPertes())
                .pu(article.getPu())
                .prixCible(article.getPrixCible())
                .prixEstime(article.getPrixEstime())
                .prixConsulte(article.getPrixConsulte())
                .rabais(article.getRabais())
                .commentaires(article.getCommentaires())
                .userId(article.getUserId())
                .indiceDeConfiance(article.getIndiceDeConfiance())
                .files(article.getFiles())
                .fournisseurId(article.getFournisseurId())
                .niveau6Id(article.getNiveau6() != null ? article.getNiveau6().getId() : null)
                .createdAt(article.getCreatedAt());

        // Derive Origine
        if (article.getPrixConsulte() != null && !article.getPrixConsulte().isEmpty()) {
            builder.origine("consulte");
        } else if (article.getPrixEstime() != null && !article.getPrixEstime().isEmpty()) {
            builder.origine("estime");
        } else if (article.getPrixCible() != null && !article.getPrixCible().isEmpty()) {
            builder.origine("cible");
        }

        // Populate Hierarchy
        if (article.getNiveau6() != null) {
            populateHierarchy(builder, article.getNiveau6());
        }

        return builder.build();
    }

    private Article mapToEntity(ArticleDTO dto) {
        Article.ArticleBuilder builder = Article.builder()
                .date(dto.getDate())
                .nomArticle(dto.getNomArticle())
                .unite(dto.getUnite())
                .type(dto.getType())
                .expertise(dto.getExpertise())
                .fourniture(dto.getFourniture())
                .cadence(dto.getCadence())
                .accessoires(dto.getAccessoires())
                .pertes(dto.getPertes())
                .pu(dto.getPu())
                .prixCible(dto.getPrixCible())
                .prixEstime(dto.getPrixEstime())
                .prixConsulte(dto.getPrixConsulte())
                .rabais(dto.getRabais())
                .commentaires(dto.getCommentaires())
                .userId(dto.getUserId())
                .indiceDeConfiance(dto.getIndiceDeConfiance())
                .files(dto.getFiles())
                .fournisseurId(dto.getFournisseurId());

        if (dto.getNiveau6Id() != null) {
            niveau6Repository.findById(dto.getNiveau6Id())
                    .ifPresent(builder::niveau6);
        }

        return builder.build();
    }

    public ArticleDTO createPendingArticle(ArticleDTO dto, String userId) {
        PendingArticle pending = PendingArticle.builder()
                .date(dto.getDate())
                .nomArticle(dto.getNomArticle())
                .unite(dto.getUnite())
                .type(dto.getType())
                .expertise(dto.getExpertise())
                .fourniture(dto.getFourniture() != null ? new java.math.BigDecimal(dto.getFourniture()) : null)
                .cadence(dto.getCadence() != null ? new java.math.BigDecimal(dto.getCadence()) : null)
                .accessoires(dto.getAccessoires() != null ? new java.math.BigDecimal(dto.getAccessoires()) : null)
                .pertes(dto.getPertes())
                .pu(dto.getPu() != null ? dto.getPu() : "0")
                .prixCible(dto.getPrixCible() != null ? new java.math.BigDecimal(dto.getPrixCible()) : null)
                .prixEstime(dto.getPrixEstime() != null ? new java.math.BigDecimal(dto.getPrixEstime()) : null)
                .prixConsulte(dto.getPrixConsulte() != null ? new java.math.BigDecimal(dto.getPrixConsulte()) : null)
                .rabais(dto.getRabais())
                .commentaires(dto.getCommentaires())
                .createdBy(userId)
                .status("En attente")
                .indiceDeConfiance(dto.getIndiceDeConfiance())
                .files(dto.getFiles())
                .fournisseurId(dto.getFournisseurId())
                .build();

        if (dto.getNiveau6Id() != null) {
            niveau6Repository.findById(dto.getNiveau6Id()).ifPresent(pending::setNiveau6);
        }

        PendingArticle saved = pendingArticleRepository.save(pending);
        return mapPendingToDTO(saved);
    }

    public List<ArticleDTO> suggestArticles(String query) {
        return articleRepository.findByNomArticleContainingIgnoreCase(query).stream()
                .limit(10)
                .map(a -> ArticleDTO.builder()
                        .id(a.getId())
                        .nomArticle(a.getNomArticle())
                        .build())
                .collect(Collectors.toList());
    }

    public List<String> getAllUnits() {
        return articleRepository.findDistinctUnites();
    }
}
