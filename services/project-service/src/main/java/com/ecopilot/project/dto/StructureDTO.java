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
public class StructureDTO {

    @JsonProperty("id_structure")
    private Long idStructure;

    @JsonProperty("ouvrage")
    private Long ouvrageId;

    @JsonProperty("bloc")
    private Long blocId;

    @JsonProperty("action")
    private String action;

    @JsonProperty("articles")
    private List<ProjetArticleDTO> articles;
}
