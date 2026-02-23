package com.ecopilot.article.repository;

import com.ecopilot.article.entity.Niveau4;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface Niveau4Repository extends JpaRepository<Niveau4, Long> {
    List<Niveau4> findByNiveau3Id(Long niveau3Id);
}
