package com.ecopilot.project.repository;

import com.ecopilot.project.entity.ProjetArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetArticleRepository extends JpaRepository<ProjetArticle, Long> {
    
    List<ProjetArticle> findByStructureIdStructure(Long structureId);
    
    void deleteByStructureIdStructure(Long structureId);
}
