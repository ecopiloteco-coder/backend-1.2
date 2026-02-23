package com.ecopilot.fournisseur.repository;

import com.ecopilot.fournisseur.entity.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {
    @Query("SELECT DISTINCT f.type FROM Fournisseur f WHERE f.type IS NOT NULL")
    List<String> findDistinctType();

    Optional<Fournisseur> findByEmail(String email);

    Optional<Fournisseur> findByKeycloakId(String keycloakId);

    Optional<Fournisseur> findByUserId(Long userId);
}
