package com.ecopilot.article.repository;

import com.ecopilot.article.entity.Niveau6;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface Niveau6Repository extends JpaRepository<Niveau6, Long> {
    List<Niveau6> findByNiveau5Id(Long niveau5Id);
}
