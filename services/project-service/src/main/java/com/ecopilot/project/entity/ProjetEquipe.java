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
@Table(name = "projet_equipe")
public class ProjetEquipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "equipe")
    private String equipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet", nullable = false)
    private Projet projet;
}
