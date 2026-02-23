package com.ecopilot.article.repository;

import com.ecopilot.article.entity.Niveau2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface Niveau2Repository extends JpaRepository<Niveau2, Long> {
    List<Niveau2> findByNiveau1Id(Long niveau1Id);
}
