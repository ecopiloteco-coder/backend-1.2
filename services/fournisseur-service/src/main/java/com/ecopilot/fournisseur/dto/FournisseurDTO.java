package com.ecopilot.fournisseur.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FournisseurDTO {
    private Long id;

    @JsonProperty("nom_fournisseur")
    private String nomFournisseur;

    private String email;
    private String telephone;
    private String adresse;
    private String specialite;

    private String categorie;
    private Integer lot;
    private String type;

    @JsonProperty("URL")
    private String url;

    private String keycloakId;
    private Long userId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
