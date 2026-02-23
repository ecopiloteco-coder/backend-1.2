package com.ecopilot.article.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "niveau_6")
public class Niveau6 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_niveau_6")
    private Long id;

    @Column(name = "niveau_6")
    private String nom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_niv_5")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Niveau5 niveau5;

    @OneToMany(mappedBy = "niveau6", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private java.util.List<Article> articles;

    @Column(name = "id_niv_4")
    private Long idNiveau4;

    @Column(name = "id_niv_3")
    private Long idNiveau3;
}
