package com.ecopilot.project.repository;

import com.ecopilot.project.entity.Bloc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlocRepository extends JpaRepository<Bloc, Long> {
    
    List<Bloc> findByOuvrageId(Long ouvrageId);
    
    void deleteByOuvrageId(Long ouvrageId);
}
