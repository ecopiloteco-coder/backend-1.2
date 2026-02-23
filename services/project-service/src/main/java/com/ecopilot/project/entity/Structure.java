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
@Table(name = "structure")
public class Structure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_structure")
    private Long idStructure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ouvrage")
    private Ouvrage ouvrage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bloc")
    private Bloc bloc;

    private String action;

    @OneToMany(mappedBy = "structure", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjetArticle> articles = new ArrayList<>();

    // Helper methods for bidirectional relationship
    public void addArticle(ProjetArticle article) {
        articles.add(article);
        article.setStructure(this);
    }

    public void removeArticle(ProjetArticle article) {
        articles.remove(article);
        article.setStructure(null);
    }
}
