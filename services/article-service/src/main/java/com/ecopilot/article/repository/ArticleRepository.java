package com.ecopilot.article.repository;

import com.ecopilot.article.entity.Article;
import com.ecopilot.article.dto.ArticleHierarchyDTO;
import com.ecopilot.article.dto.FilterDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecopilot.article.entity.Niveau1;
import com.ecopilot.article.entity.Niveau2;
import com.ecopilot.article.entity.Niveau3;
import com.ecopilot.article.entity.Niveau4;
import com.ecopilot.article.entity.Niveau5;
import com.ecopilot.article.entity.Niveau6;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByNomArticleContainingIgnoreCase(String nomArticle);

    @Query("SELECT DISTINCT a.unite FROM Article a WHERE a.unite IS NOT NULL")
    List<String> findDistinctUnites();

    @Query("SELECT DISTINCT a.expertise FROM Article a WHERE a.expertise IS NOT NULL")
    List<String> findDistinctExpertises();

    @Query("SELECT DISTINCT new com.ecopilot.article.dto.FilterDTO(n1.id, n1.nom) FROM Article a JOIN a.niveau6 n6 JOIN n6.niveau5 n5 JOIN n5.niveau4 n4 JOIN n4.niveau3 n3 JOIN n3.niveau2 n2 JOIN n2.niveau1 n1")
    List<FilterDTO> findDistinctNiveau1();

    @Query("SELECT DISTINCT new com.ecopilot.article.dto.FilterDTO(n2.id, n2.nom) FROM Article a JOIN a.niveau6 n6 JOIN n6.niveau5 n5 JOIN n5.niveau4 n4 JOIN n4.niveau3 n3 JOIN n3.niveau2 n2 WHERE (:parentId IS NULL OR n2.niveau1.id = :parentId)")
    List<FilterDTO> findDistinctNiveau2(Long parentId);

    @Query("SELECT DISTINCT new com.ecopilot.article.dto.FilterDTO(n3.id, n3.nom) FROM Article a JOIN a.niveau6 n6 JOIN n6.niveau5 n5 JOIN n5.niveau4 n4 JOIN n4.niveau3 n3 WHERE (:parentId IS NULL OR n3.niveau2.id = :parentId)")
    List<FilterDTO> findDistinctNiveau3(Long parentId);

    @Query("SELECT DISTINCT new com.ecopilot.article.dto.FilterDTO(n4.id, n4.nom) FROM Article a JOIN a.niveau6 n6 JOIN n6.niveau5 n5 JOIN n5.niveau4 n4 WHERE (:parentId IS NULL OR n4.niveau3.id = :parentId)")
    List<FilterDTO> findDistinctNiveau4(Long parentId);

    @Query("SELECT DISTINCT new com.ecopilot.article.dto.FilterDTO(n5.id, n5.nom) FROM Article a JOIN a.niveau6 n6 JOIN n6.niveau5 n5 WHERE (:parentId IS NULL OR n5.niveau4.id = :parentId)")
    List<FilterDTO> findDistinctNiveau5(Long parentId);

    @Query("SELECT DISTINCT new com.ecopilot.article.dto.FilterDTO(n6.id, n6.nom) FROM Article a JOIN a.niveau6 n6 WHERE (:parentId IS NULL OR n6.niveau5.id = :parentId)")
    List<FilterDTO> findDistinctNiveau6(Long parentId);

    @Query("SELECT new com.ecopilot.article.dto.ArticleHierarchyDTO(" +
           "a.id, a.nomArticle, n1.nom, n2.nom, n3.nom, n4.nom, n5.nom, n6.nom) " +
           "FROM Article a " +
           "JOIN a.niveau6 n6 " +
           "JOIN n6.niveau5 n5 " +
           "JOIN n5.niveau4 n4 " +
           "JOIN n4.niveau3 n3 " +
           "JOIN n3.niveau2 n2 " +
           "JOIN n2.niveau1 n1")
    List<ArticleHierarchyDTO> findAllArticleHierarchy();

    List<Article> findByNiveau6IdIn(List<Long> ids);
}
