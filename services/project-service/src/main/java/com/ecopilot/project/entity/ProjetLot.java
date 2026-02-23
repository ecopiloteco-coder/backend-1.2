package com.ecopilot.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "projet_lot")
public class ProjetLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_projet_lot")
    private Long idProjetLot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_projet", nullable = false)
    private Projet projet;

    @Column(name = "id_lot", nullable = false)
    private Integer idLot; // References niveau_2.id_niveau_2

    @Column(name = "designation_lot")
    private String designationLot;

    @Column(name = "prix_total")
    private Double prixTotal;

    @Column(name = "prix_vente")
    private Double prixVente;

    @OneToMany(mappedBy = "projetLot", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Ouvrage> ouvrages = new ArrayList<>();

    // Helper methods for bidirectional relationship
    public void addOuvrage(Ouvrage ouvrage) {
        ouvrages.add(ouvrage);
        ouvrage.setProjetLot(this);
    }

    public void removeOuvrage(Ouvrage ouvrage) {
        ouvrages.remove(ouvrage);
        ouvrage.setProjetLot(null);
    }
}
