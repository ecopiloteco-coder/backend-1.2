package com.ecopilot.user.entity;

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
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keycloak_id")
    private String keycloakId;

    @Column(name = "nom_utilisateur", nullable = false)
    private String nomUtilisateur;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "titre_poste", nullable = false)
    private UserRole titrePoste;

    // Mot de passe est stocké dans Keycloak, on ne le stocke pas ici ou on le garde vide/null
    // Pour compatibilité, on peut garder la colonne mais ne pas l'utiliser pour l'auth
    @Transient 
    private String motDePasse;

    @Column(name = "date_creation_compte")
    @CreationTimestamp
    private LocalDateTime dateCreationCompte;

    @Column(name = "is_admin")
    @Builder.Default
    private Boolean isAdmin = false;

    @Column(name = "fournisseur_id")
    private Long fournisseurId;
}
