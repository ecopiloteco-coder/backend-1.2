package com.ecopilot.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjetDTO {
    private Long id;

    @JsonProperty("Nom_Projet")
    private String nomProjet;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("etat")
    private String etat;

    @JsonProperty("Cout")
    private Double cout;

    @JsonProperty("Date_Debut")
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
    private java.time.LocalDate dateDebut;

    @JsonProperty("Date_Limite")
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
    private java.time.LocalDate dateLimite;

    @JsonProperty("client")
    private Long clientId;

    @JsonProperty("clientData")
    private ClientDTO clientData;

    @JsonProperty("ajoute_par")
    private String ajoutePar;

    @JsonProperty("adresse")
    private String adresse;

    @JsonProperty("file")
    private String file;

    @JsonProperty("lots")
    private List<ProjetLotDTO> lots;

    @JsonProperty("team")
    private List<String> teamMembers;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
