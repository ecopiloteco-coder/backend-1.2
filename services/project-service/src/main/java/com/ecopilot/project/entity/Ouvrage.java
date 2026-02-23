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
@Table(name = "ouvrage")
public class Ouvrage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_ouvrage")
    private String nomOuvrage;

    @Column(name = "prix_total")
    private Double prixTotal;

    private String designation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet_lot", nullable = false)
    private ProjetLot projetLot;

    @OneToMany(mappedBy = "ouvrage", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Structure> structures = new ArrayList<>();

    // Helper methods for bidirectional relationship
    public void addStructure(Structure structure) {
        structures.add(structure);
        structure.setOuvrage(this);
    }

    public void removeStructure(Structure structure) {
        structures.remove(structure);
        structure.setOuvrage(null);
    }
}
