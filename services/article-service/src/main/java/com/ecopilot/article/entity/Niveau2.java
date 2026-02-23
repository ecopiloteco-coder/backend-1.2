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
@Table(name = "niveau_2")
public class Niveau2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_niveau_2")
    private Long id;

    @Column(name = "niveau_2")
    private String nom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_niv_1")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Niveau1 niveau1;

    @OneToMany(mappedBy = "niveau2", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private java.util.List<Niveau3> niveau3s;
}
