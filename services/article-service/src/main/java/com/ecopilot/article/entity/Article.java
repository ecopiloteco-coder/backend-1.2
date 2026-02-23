package com.ecopilot.article.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "nom_article")
    private String nomArticle;

    @Column(name = "unite")
    private String unite;

    @Column(name = "type")
    private String type;

    @Column(name = "expertise")
    private String expertise;

    @Column(name = "fourniture")
    private String fourniture;

    @Column(name = "cadence")
    private String cadence;

    @Column(name = "accessoires")
    private String accessoires;

    @Column(name = "pertes")
    private String pertes;

    @Column(name = "pu")
    private String pu;

    @Column(name = "prix_cible")
    private String prixCible;

    @Column(name = "prix_estime")
    private String prixEstime;

    @Column(name = "prix_consulte")
    private String prixConsulte;

    @Column(name = "rabais")
    private String rabais;

    @Column(name = "commentaires", columnDefinition = "TEXT")
    private String commentaires;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "indice_de_confiance")
    private Integer indiceDeConfiance;

    @Column(name = "files", columnDefinition = "TEXT")
    private String files;

    @Column(name = "fournisseur")
    private Long fournisseurId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_niv_6")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Niveau6 niveau6;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
