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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pending_articles")
public class PendingArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private java.time.LocalDate date;

    @Column(name = "nom_article", nullable = false)
    private String nomArticle;

    @Column(name = "unite", nullable = false)
    private String unite;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "expertise", nullable = false)
    private String expertise;

    @Column(name = "fourniture")
    private BigDecimal fourniture;

    @Column(name = "cadence")
    private BigDecimal cadence;

    @Column(name = "accessoires")
    private BigDecimal accessoires;

    @Column(name = "pertes")
    private String pertes;

    @Column(name = "pu", nullable = false)
    private String pu;

    @Column(name = "prix_cible")
    private BigDecimal prixCible;

    @Column(name = "prix_estime")
    private BigDecimal prixEstime;

    @Column(name = "prix_consulte")
    private BigDecimal prixConsulte;

    @Column(name = "rabais")
    private String rabais;

    @Column(name = "commentaires", columnDefinition = "TEXT")
    private String commentaires;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "status")
    private String status;

    @Column(name = "submitted_at")
    @CreationTimestamp
    private LocalDateTime submittedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "accepted_by")
    private String acceptedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "indice_de_confiance")
    private Integer indiceDeConfiance;

    @Column(name = "files", columnDefinition = "TEXT")
    private String files;

    @Column(name = "rejected_by")
    private String rejectedBy;

    @Column(name = "fournisseur")
    private Long fournisseurId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_niv_6")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Niveau6 niveau6;

    @Column(name = "approved_article_id")
    private Long approvedArticleId;
}
