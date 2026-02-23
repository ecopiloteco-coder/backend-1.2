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
@Table(name = "niveau_3")
public class Niveau3 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_niveau_3")
    private Long id;

    @Column(name = "niveau_3")
    private String nom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_niv_2")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Niveau2 niveau2;

    @OneToMany(mappedBy = "niveau3", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private java.util.List<Niveau4> niveau4s;
}
