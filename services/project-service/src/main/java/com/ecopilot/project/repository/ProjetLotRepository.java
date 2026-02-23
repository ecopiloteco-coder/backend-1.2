package com.ecopilot.project.repository;

import com.ecopilot.project.entity.ProjetLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetLotRepository extends JpaRepository<ProjetLot, Long> {
    
    List<ProjetLot> findByProjetId(Long projetId);
    
    void deleteByProjetId(Long projetId);
}
