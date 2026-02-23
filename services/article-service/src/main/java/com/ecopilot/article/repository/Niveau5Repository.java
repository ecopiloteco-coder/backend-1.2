package com.ecopilot.article.repository;

import com.ecopilot.article.entity.Niveau5;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface Niveau5Repository extends JpaRepository<Niveau5, Long> {
    List<Niveau5> findByNiveau4Id(Long niveau4Id);
}
