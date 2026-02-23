package com.ecopilot.article.repository;

import com.ecopilot.article.entity.Niveau1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Niveau1Repository extends JpaRepository<Niveau1, Long> {
}
