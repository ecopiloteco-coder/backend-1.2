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
public class ProjetLotDTO {

    @JsonProperty("id_projet_lot")
    private Long idProjetLot;

    @JsonProperty("id_projet")
    private Long projetId;

    @JsonProperty("id_lot")
    private Integer idLot;

    @JsonProperty("designation_lot")
    private String designationLot;

    @JsonProperty("prix_total")
    private Double prixTotal;

    @JsonProperty("prix_vente")
    private Double prixVente;

    @JsonProperty("ouvrages")
    private List<OuvrageDTO> ouvrages;

    @JsonProperty("blocs")
    private List<BlocDTO> blocs;
}
