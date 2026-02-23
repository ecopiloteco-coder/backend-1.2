package com.ecopilot.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjetImportDTO {
    private List<LotImportDTO> data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LotImportDTO {
        private String name;
        private Long lotId;
        private List<OuvrageImportDTO> ouvrages;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OuvrageImportDTO {
        private String name;
        private String designation;
        private List<ArticleImportDTO> articles;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleImportDTO {
        private String designation;
        private String unite;
        private Double qte;
        private Double pu;
        private String type;
    }
}
