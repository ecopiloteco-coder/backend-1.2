package com.ecopilot.article.repository;

import com.ecopilot.article.entity.Niveau3;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface Niveau3Repository extends JpaRepository<Niveau3, Long> {
    List<Niveau3> findByNiveau2Id(Long niveau2Id);
}
