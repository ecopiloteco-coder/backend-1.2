package com.ecopilot.project.controller;

import com.ecopilot.project.dto.OuvrageDTO;
import com.ecopilot.project.service.OuvrageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.ecopilot.project.dto.ApiResponse;

@RestController
@RequiredArgsConstructor
public class OuvrageController {

    private final OuvrageService ouvrageService;

    // Endpoints under /api/projets/ouvrages
    @PostMapping("/api/projets/ouvrages")
    public ResponseEntity<ApiResponse<OuvrageDTO>> createOuvrageInProjet(@RequestBody OuvrageDTO dto) {
        return ResponseEntity.ok(ApiResponse.<OuvrageDTO>builder()
                .success(true)
                .data(ouvrageService.createOuvrage(dto))
                .build());
    }

    @PutMapping("/api/projets/ouvrages/{id}")
    public ResponseEntity<ApiResponse<OuvrageDTO>> updateOuvrageInProjet(@PathVariable Long id, @RequestBody OuvrageDTO dto) {
        OuvrageDTO updated = ouvrageService.updateOuvrage(id, dto);
        return ResponseEntity.ok(ApiResponse.<OuvrageDTO>builder()
                .success(true)
                .data(updated)
                .build());
    }

    @PostMapping("/api/projets/ouvrages/{id}/duplicate")
    public ResponseEntity<ApiResponse<OuvrageDTO>> duplicateOuvrageInProjet(@PathVariable Long id) {
        OuvrageDTO duplicated = ouvrageService.duplicateOuvrage(id);
        return ResponseEntity.ok(ApiResponse.<OuvrageDTO>builder()
                .success(true)
                .data(duplicated)
                .build());
    }

    @DeleteMapping("/api/projets/ouvrages/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOuvrageInProjet(@PathVariable Long id) {
        ouvrageService.deleteOuvrage(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .build());
    }

    // Standalone endpoints under /api/ouvrages
    @PostMapping("/api/ouvrages")
    public ResponseEntity<ApiResponse<OuvrageDTO>> createOuvrage(@RequestBody OuvrageDTO dto) {
        return ResponseEntity.ok(ApiResponse.<OuvrageDTO>builder()
                .success(true)
                .data(ouvrageService.createOuvrage(dto))
                .build());
    }

    @PutMapping("/api/ouvrages/{id}")
    public ResponseEntity<ApiResponse<OuvrageDTO>> updateOuvrage(@PathVariable Long id, @RequestBody OuvrageDTO dto) {
        OuvrageDTO updated = ouvrageService.updateOuvrage(id, dto);
        return ResponseEntity.ok(ApiResponse.<OuvrageDTO>builder()
                .success(true)
                .data(updated)
                .build());
    }

    @PostMapping("/api/ouvrages/{id}/duplicate")
    public ResponseEntity<ApiResponse<OuvrageDTO>> duplicateOuvrage(@PathVariable Long id) {
        OuvrageDTO duplicated = ouvrageService.duplicateOuvrage(id);
        return ResponseEntity.ok(ApiResponse.<OuvrageDTO>builder()
                .success(true)
                .data(duplicated)
                .build());
    }

    @DeleteMapping("/api/ouvrages/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOuvrage(@PathVariable Long id) {
        ouvrageService.deleteOuvrage(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .build());
    }
}
