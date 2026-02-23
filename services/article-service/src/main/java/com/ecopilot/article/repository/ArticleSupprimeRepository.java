package com.ecopilot.article.repository;

import com.ecopilot.article.entity.ArticleSupprime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleSupprimeRepository extends JpaRepository<ArticleSupprime, Long> {
    List<ArticleSupprime> findByUserId(String userId);
    List<ArticleSupprime> findByNomArticleContainingIgnoreCase(String nomArticle);
}
