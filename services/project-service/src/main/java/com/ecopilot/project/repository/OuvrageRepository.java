package com.ecopilot.project.repository;

import com.ecopilot.project.entity.Ouvrage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OuvrageRepository extends JpaRepository<Ouvrage, Long> {
    
    List<Ouvrage> findByProjetLotIdProjetLot(Long projetLotId);
    
    void deleteByProjetLotIdProjetLot(Long projetLotId);
}
