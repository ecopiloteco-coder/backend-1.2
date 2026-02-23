package com.ecopilot.project.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("nom_client")
    @Column(name = "nom_client", nullable = false)
    private String nomClient;

    @JsonProperty("marge_brut")
    @Column(name = "marge_brut")
    private Double margeBrut;

    @JsonProperty("marge_net")
    @Column(name = "marge_net")
    private Double margeNet;

    private String agence;
    private String responsable;

    @JsonProperty("effectif_chantier")
    @Column(name = "effectif_chantier", columnDefinition = "TEXT")
    private String effectifChantier; // JSON or File URL

    @JsonProperty("created_at")
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
