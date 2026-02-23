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
@Table(name = "projet_article")
public class ProjetArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "article")
    private Integer article; // References articles.ID

    private Integer quantite;

    @Column(name = "pu")
    private Double pu;

    @Column(name = "prix_total_ht")
    private Double prixTotalHt;

    private Double tva;

    @Column(name = "total_ttc")
    private Double totalTtc;

    private String localisation;

    private String description;

    @Column(name = "nouv_prix")
    private Double nouvPrix;

    @Column(name = "designation_article")
    private String designationArticle;

    @Column(name = "article_import")
    private String articleImport;

    @Column(name = "unite")
    private String unite;

    @Column(name = "unite_import")
    private String uniteImport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "structure")
    private Structure structure;
}
