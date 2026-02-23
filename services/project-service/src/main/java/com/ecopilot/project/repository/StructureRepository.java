package com.ecopilot.project.repository;

import com.ecopilot.project.entity.Structure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StructureRepository extends JpaRepository<Structure, Long> {
    
    List<Structure> findByOuvrageId(Long ouvrageId);
    
    List<Structure> findByBlocId(Long blocId);
    
    void deleteByOuvrageId(Long ouvrageId);
}
