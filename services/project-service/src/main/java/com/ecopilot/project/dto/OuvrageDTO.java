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
public class OuvrageDTO {
    private Long id;

    @JsonProperty("nom_ouvrage")
    private String nomOuvrage;

    @JsonProperty("prix_total")
    private Double prixTotal;

    @JsonProperty("designation")
    private String designation;

    @JsonProperty("projet_lot")
    private Long projetLotId;

    @JsonProperty("structures")
    private List<StructureDTO> structures;

    @JsonProperty("blocs")
    private List<BlocDTO> blocs;
}
