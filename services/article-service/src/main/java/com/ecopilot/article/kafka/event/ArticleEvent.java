package com.ecopilot.article.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleEvent {
    private String eventType; // ARTICLE_CREATED, ARTICLE_UPDATED, ARTICLE_DELETED, ARTICLE_VALIDATED
    private Long articleId;
    private String designation;
    private String unite;
    private Double prixUnitaire;
    private String userId; // User who performed the action
    private LocalDateTime timestamp;
}
