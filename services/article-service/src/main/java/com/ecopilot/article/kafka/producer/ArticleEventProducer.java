package com.ecopilot.article.kafka.producer;

import com.ecopilot.article.kafka.event.ArticleEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleEventProducer {

    private final KafkaTemplate<String, ArticleEvent> kafkaTemplate;
    private static final String ARTICLE_EVENTS_TOPIC = "article.events";

    public void sendArticleCreatedEvent(Long articleId, String designation, String unite, Double prixUnitaire, String userId) {
        ArticleEvent event = ArticleEvent.builder()
                .eventType("ARTICLE_CREATED")
                .articleId(articleId)
                .designation(designation)
                .unite(unite)
                .prixUnitaire(prixUnitaire)
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .build();

        sendEvent(event);
        log.info("Sent ARTICLE_CREATED event for article: {}", articleId);
    }

    public void sendArticleUpdatedEvent(Long articleId, String designation, String unite, Double prixUnitaire, String userId) {
        ArticleEvent event = ArticleEvent.builder()
                .eventType("ARTICLE_UPDATED")
                .articleId(articleId)
                .designation(designation)
                .unite(unite)
                .prixUnitaire(prixUnitaire)
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .build();

        sendEvent(event);
        log.info("Sent ARTICLE_UPDATED event for article: {}", articleId);
    }

    public void sendArticleDeletedEvent(Long articleId, String userId) {
        ArticleEvent event = ArticleEvent.builder()
                .eventType("ARTICLE_DELETED")
                .articleId(articleId)
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .build();

        sendEvent(event);
        log.info("Sent ARTICLE_DELETED event for article: {}", articleId);
    }

    public void sendArticleValidatedEvent(Long articleId, String designation, String userId) {
        ArticleEvent event = ArticleEvent.builder()
                .eventType("ARTICLE_VALIDATED")
                .articleId(articleId)
                .designation(designation)
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .build();

        sendEvent(event);
        log.info("Sent ARTICLE_VALIDATED event for article: {}", articleId);
    }

    private void sendEvent(ArticleEvent event) {
        try {
            kafkaTemplate.send(ARTICLE_EVENTS_TOPIC, event.getArticleId().toString(), event);
        } catch (Exception e) {
            log.error("Failed to send article event: {}", event, e);
        }
    }
}
