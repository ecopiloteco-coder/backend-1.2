package com.ecopilot.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjetArticleDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("article")
    private Integer article;

    @JsonProperty("quantite")
    private Integer quantite;

    @JsonProperty("pu")
    private Double pu;

    @JsonProperty("prix_total_ht")
    private Double prixTotalHt;

    @JsonProperty("tva")
    private Double tva;

    @JsonProperty("total_ttc")
    private Double totalTtc;

    @JsonProperty("localisation")
    private String localisation;

    @JsonProperty("description")
    private String description;

    @JsonProperty("nouv_prix")
    private Double nouvPrix;

    @JsonProperty("designation_article")
    private String designationArticle;

    @JsonProperty("article_import")
    private String articleImport;

    @JsonProperty("unite")
    private String unite;

    @JsonProperty("unite_import")
    private String uniteImport;

    @JsonProperty("structure")
    private Long structureId;

    @JsonProperty("bloc_id")
    private Long blocId;

    @JsonProperty("position")
    private String position;
}
