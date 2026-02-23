package com.ecopilot.article.repository;

import com.ecopilot.article.entity.PendingArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PendingArticleRepository extends JpaRepository<PendingArticle, Long> {
    List<PendingArticle> findByStatus(String status);
    List<PendingArticle> findByCreatedBy(String createdBy);
    List<PendingArticle> findByNomArticleContainingIgnoreCase(String nomArticle);
}
