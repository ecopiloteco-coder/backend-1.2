package com.ecopilot.project.dto;

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
public class ClientDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nom_client")
    private String nomClient;

    @JsonProperty("marge_brut")
    private Double margeBrut;

    @JsonProperty("marge_net")
    private Double margeNet;

    @JsonProperty("agence")
    private String agence;

    @JsonProperty("responsable")
    private String responsable;

    @JsonProperty("effectif_chantier")
    private String effectifChantier;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}

