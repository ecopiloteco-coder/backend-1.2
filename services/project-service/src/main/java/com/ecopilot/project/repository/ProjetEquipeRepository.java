package com.ecopilot.project.repository;

import com.ecopilot.project.entity.ProjetEquipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetEquipeRepository extends JpaRepository<ProjetEquipe, Long> {
    
    List<ProjetEquipe> findByProjetId(Long projetId);
    
    List<ProjetEquipe> findByEquipe(String userId);
    
    void deleteByProjetId(Long projetId);
    
    boolean existsByProjetIdAndEquipe(Long projetId, String userId);
}
