package com.ecopilot.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjetEquipeDTO {
    private Long id;
    private String equipe;
    private Long projetId;
}
