package com.ecopilot.project.controller;

import com.ecopilot.project.dto.ProjetDetailsDTO;
import com.ecopilot.project.service.ProjetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecopilot.project.dto.ApiResponse;

@RestController
@RequestMapping("/api/projet-details")
@RequiredArgsConstructor
public class ProjetDetailsController {

    private final ProjetService projetService;

    /**
     * Get full project details with complete hierarchy:
     * Projet -> ProjetLots -> Ouvrages -> (Structures -> Blocs) + (Structures -> ProjetArticles)
     */
    @GetMapping("/{id}/details")
    public ResponseEntity<ApiResponse<ProjetDetailsDTO>> getProjetDetails(@PathVariable Long id) {
        ProjetDetailsDTO details = projetService.getProjetFullDetails(id);
        return ResponseEntity.ok(ApiResponse.<ProjetDetailsDTO>builder()
                .success(true)
                .data(details)
                .build());
    }
}
