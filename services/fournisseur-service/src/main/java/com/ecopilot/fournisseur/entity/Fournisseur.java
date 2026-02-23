package com.ecopilot.fournisseur.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fournisseurs")
public class Fournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_fournisseur", nullable = false)
    private String nomFournisseur;

    private String email;
    private String telephone;
    private String adresse;
    private String specialite; // e.g., "Gros Oeuvre"

    private String categorie;
    private Integer lot;
    private String type;

    @Column(name = "url")
    private String url;

    @Column(name = "keycloak_id", unique = true)
    private String keycloakId;

    @Column(name = "user_id", unique = true)
    private Long userId;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
