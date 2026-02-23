package com.ecopilot.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "projets")
public class Projet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_projet", nullable = false)
    private String nomProjet;

    private String description;
    private String etat; // En cours, Terminé...
    private Double cout;

    @Column(name = "prix_vente")
    private Double prixVente;

    private String adresse;

    @Column(name = "file", columnDefinition = "TEXT")
    private String file; // File paths/URLs

    @Column(name = "date_debut")
    private java.time.LocalDate dateDebut;

    @Column(name = "date_limite")
    private java.time.LocalDate dateLimite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client")
    private Client client;

    @Column(name = "ajoute_par")
    private String ajoutePar;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Relationships to ProjetLot and ProjetEquipe
    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjetLot> lots = new ArrayList<>();

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjetEquipe> equipe = new ArrayList<>();

    // Helper methods for bidirectional relationships
    public void addLot(ProjetLot lot) {
        lots.add(lot);
        lot.setProjet(this);
    }

    public void removeLot(ProjetLot lot) {
        lots.remove(lot);
        lot.setProjet(null);
    }

    public void addMember(ProjetEquipe member) {
        equipe.add(member);
        member.setProjet(this);
    }

    public void removeMember(ProjetEquipe member) {
        equipe.remove(member);
        member.setProjet(null);
    }
}
