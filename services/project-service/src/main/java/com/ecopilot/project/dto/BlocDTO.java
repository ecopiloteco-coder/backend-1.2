package com.ecopilot.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlocDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nom_bloc")
    private String nomBloc;

    @JsonProperty("unite")
    private String unite;

    @JsonProperty("quantite")
    private Integer quantite;

    @JsonProperty("pu")
    private Double pu;

    @JsonProperty("pt")
    private Double pt;

    @JsonProperty("designation")
    private String designation;

    @JsonProperty("ouvrage")
    private Long ouvrageId;

    @JsonProperty("structures")
    private List<StructureDTO> structures;

    @JsonProperty("articles")
    private List<ProjetArticleDTO> articles;
}
