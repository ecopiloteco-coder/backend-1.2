package com.ecopilot.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bloc")
public class Bloc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_bloc")
    private String nomBloc;

    private String unite;

    private Integer quantite;

    @Column(name = "pu") // Prix unitaire
    private Double pu;

    @Column(name = "pt") // Prix total
    private Double pt;

    private String designation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ouvrage")
    private Ouvrage ouvrage;
}
