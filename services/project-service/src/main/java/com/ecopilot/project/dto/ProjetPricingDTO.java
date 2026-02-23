package com.ecopilot.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjetPricingDTO {
    private Double totalLotTTC;
    private Double totalVenteLot;
    private Double totalProjetTTC;
    private Double totalVenteProjet;
}
