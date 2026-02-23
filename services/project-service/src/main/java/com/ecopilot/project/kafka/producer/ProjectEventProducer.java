package com.ecopilot.project.kafka.producer;

import com.ecopilot.project.kafka.event.ProjectEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectEventProducer {

    private final KafkaTemplate<String, ProjectEvent> kafkaTemplate;
    private static final String PROJECT_EVENTS_TOPIC = "project.events";

    public void sendProjectCreatedEvent(Long projectId, String projectName, String userId) {
        java.util.Map<String, Object> metadata = new java.util.HashMap<>();
        metadata.put("projet", projectId);
        metadata.put("nom_projet", projectName);

        ProjectEvent event = ProjectEvent.builder()
                .eventType("PROJECT_CREATED")
                .action("projet_ajouter")
                .projectId(projectId)
                .projectName(projectName)
                .userId(userId)
                .timestamp(LocalDateTime.now().toString())
                .projet(projectId)
                .metadata(metadata)
                .build();

        sendEvent(event);
        log.info("Sent PROJECT_CREATED event for project: {}", projectId);
    }

    public void sendProjectUpdatedEvent(Long projectId, String projectName, String userId) {
        java.util.Map<String, Object> metadata = new java.util.HashMap<>();
        metadata.put("projet", projectId);
        metadata.put("nom_projet", projectName);

        ProjectEvent event = ProjectEvent.builder()
                .eventType("PROJECT_UPDATED")
                .action("projet_modifier")
                .projectId(projectId)
                .projectName(projectName)
                .userId(userId)
                .timestamp(LocalDateTime.now().toString())
                .projet(projectId)
                .metadata(metadata)
                .build();

        sendEvent(event);
        log.info("Sent PROJECT_UPDATED event for project: {}", projectId);
    }

    public void sendProjectAssignedEvent(Long projectId, String projectName, String userId) {
        java.util.Map<String, Object> metadata = new java.util.HashMap<>();
        metadata.put("projet", projectId);
        metadata.put("nom_projet", projectName);
        metadata.put("assigned_user", userId);

        ProjectEvent event = ProjectEvent.builder()
                .eventType("PROJECT_ASSIGNED")
                .action("projet_assigner")
                .projectId(projectId)
                .projectName(projectName)
                .userId(userId)
                .timestamp(LocalDateTime.now().toString())
                .projet(projectId)
                .metadata(metadata)
                .build();

        sendEvent(event);
        log.info("Sent PROJECT_ASSIGNED event for project: {} to user: {}", projectId, userId);
    }

    public void sendOuvrageUpdatedEvent(Long projetId, Long lotId, Long ouvrageId, String oldName, String newName, String userId) {
        java.util.Map<String, Object> metadata = new java.util.HashMap<>();
        if (projetId != null) {
            metadata.put("projet", projetId);
        }
        if (lotId != null) {
            metadata.put("lot", lotId);
        }
        if (ouvrageId != null) {
            metadata.put("ouvrage", ouvrageId);
        }
        if (oldName != null && !oldName.isEmpty()) {
            metadata.put("ouvrage_nom_anc", oldName);
        }
        if (newName != null && !newName.isEmpty()) {
            java.util.Map<String, Object> nameChange = new java.util.HashMap<>();
            nameChange.put("old", oldName);
            nameChange.put("new", newName);
            metadata.put("nom_ouvrage", nameChange);
        }

        ProjectEvent event = ProjectEvent.builder()
                .eventType("OUVRAGE_UPDATED")
                .action("ouvrage_modifier")
                .projectId(projetId)
                .userId(userId)
                .timestamp(LocalDateTime.now().toString())
                .projet(projetId)
                .lot(lotId)
                .ouvrage(ouvrageId)
                .metadata(metadata)
                .build();

        sendEvent(event);
        log.info("Sent OUVRAGE_UPDATED event for ouvrage: {} in project: {}", ouvrageId, projetId);
    }

    public void sendProjetArticleCreatedEvent(
            Long projetId,
            Long lotId,
            Long ouvrageId,
            Long blocId,
            Long projetArticleId,
            Integer articleCatalogId,
            String designation,
            Integer quantite,
            Double nouvPrix,
            String userId
    ) {
        java.util.Map<String, Object> metadata = new java.util.HashMap<>();
        if (projetId != null) {
            metadata.put("projet", projetId);
        }
        if (lotId != null) {
            metadata.put("lot", lotId);
        }
        if (ouvrageId != null) {
            metadata.put("ouvrage", ouvrageId);
        }
        if (blocId != null) {
            metadata.put("bloc", blocId);
        }
        if (projetArticleId != null) {
            metadata.put("projet_article_id", projetArticleId);
        }
        if (articleCatalogId != null) {
            metadata.put("article", articleCatalogId);
        }
        if (designation != null) {
            metadata.put("designation", designation);
        }
        if (quantite != null) {
            metadata.put("quantite", quantite);
        }
        if (nouvPrix != null) {
            metadata.put("nouv_prix", nouvPrix);
        }

        ProjectEvent event = ProjectEvent.builder()
                .eventType("PROJET_ARTICLE_CREATED")
                .action("projet_article_ajouter")
                .projectId(projetId)
                .userId(userId)
                .timestamp(LocalDateTime.now().toString())
                .projet(projetId)
                .lot(lotId)
                .ouvrage(ouvrageId)
                .bloc(blocId)
                .article(projetArticleId)
                .metadata(metadata)
                .build();

        sendEvent(event);
        log.info("Sent PROJET_ARTICLE_CREATED event for article: {} in project: {}", projetArticleId, projetId);
    }

    public void sendProjetArticleUpdatedEvent(
            Long projetId,
            Long lotId,
            Long ouvrageId,
            Long blocId,
            Long projetArticleId,
            Integer articleCatalogId,
            java.util.Map<String, Object> fieldChanges,
            String userId
    ) {
        java.util.Map<String, Object> metadata = new java.util.HashMap<>();
        if (projetId != null) {
            metadata.put("projet", projetId);
        }
        if (lotId != null) {
            metadata.put("lot", lotId);
        }
        if (ouvrageId != null) {
            metadata.put("ouvrage", ouvrageId);
        }
        if (blocId != null) {
            metadata.put("bloc", blocId);
        }
        if (projetArticleId != null) {
            metadata.put("projet_article_id", projetArticleId);
        }
        if (articleCatalogId != null) {
            metadata.put("article", articleCatalogId);
        }
        if (fieldChanges != null && !fieldChanges.isEmpty()) {
            metadata.putAll(fieldChanges);
        }

        ProjectEvent event = ProjectEvent.builder()
                .eventType("PROJET_ARTICLE_UPDATED")
                .action("projet_article_modifier")
                .projectId(projetId)
                .userId(userId)
                .timestamp(LocalDateTime.now().toString())
                .projet(projetId)
                .lot(lotId)
                .ouvrage(ouvrageId)
                .bloc(blocId)
                .article(projetArticleId)
                .metadata(metadata)
                .build();

        sendEvent(event);
        log.info("Sent PROJET_ARTICLE_UPDATED event for article: {} in project: {}", projetArticleId, projetId);
    }

    public void sendProjetArticleDeletedEvent(
            Long projetId,
            Long lotId,
            Long ouvrageId,
            Long blocId,
            Long projetArticleId,
            Integer articleCatalogId,
            String designation,
            Integer quantite,
            String userId
    ) {
        java.util.Map<String, Object> metadata = new java.util.HashMap<>();
        if (projetId != null) {
            metadata.put("projet", projetId);
        }
        if (lotId != null) {
            metadata.put("lot", lotId);
        }
        if (ouvrageId != null) {
            metadata.put("ouvrage", ouvrageId);
        }
        if (blocId != null) {
            metadata.put("bloc", blocId);
        }
        if (projetArticleId != null) {
            metadata.put("projet_article_id", projetArticleId);
        }
        if (articleCatalogId != null) {
            metadata.put("article", articleCatalogId);
        }
        if (designation != null) {
            metadata.put("designation", designation);
        }
        if (quantite != null) {
            metadata.put("quantite", quantite);
        }

        ProjectEvent event = ProjectEvent.builder()
                .eventType("PROJET_ARTICLE_DELETED")
                .action("projet_article_supprimer")
                .projectId(projetId)
                .userId(userId)
                .timestamp(LocalDateTime.now().toString())
                .projet(projetId)
                .lot(lotId)
                .ouvrage(ouvrageId)
                .bloc(blocId)
                .article(projetArticleId)
                .metadata(metadata)
                .build();

        sendEvent(event);
        log.info("Sent PROJET_ARTICLE_DELETED event for article: {} in project: {}", projetArticleId, projetId);
    }

    private void sendEvent(ProjectEvent event) {
        try {
            kafkaTemplate.send(PROJECT_EVENTS_TOPIC, event.getProjectId().toString(), event);
        } catch (Exception e) {
            log.error("Failed to send project event: {}", event, e);
        }
    }
}
