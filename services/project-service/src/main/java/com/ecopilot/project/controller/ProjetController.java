package com.ecopilot.project.controller;

import com.ecopilot.project.dto.ProjetDTO;
import com.ecopilot.project.service.ProjetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import com.ecopilot.project.dto.ApiResponse;

@RestController
@RequestMapping("/api/projets")
@RequiredArgsConstructor
public class ProjetController {

    private final ProjetService projetService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjetDTO>>> getAllProjets() {
        return ResponseEntity.ok(ApiResponse.<List<ProjetDTO>>builder()
                .success(true)
                .data(projetService.getAllProjets())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjetDTO>> getProjetById(@PathVariable Long id) {
        ProjetDTO projet = projetService.getProjetById(id);
        if (projet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.<ProjetDTO>builder()
                .success(true)
                .data(projet)
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProjetDTO>> createProjet(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestPart("project") ProjetDTO projetDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        
        if (userId != null) {
            projetDTO.setAjoutePar(userId);
        }

        return ResponseEntity.ok(ApiResponse.<ProjetDTO>builder()
                .success(true)
                .data(projetService.createProjet(projetDTO))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjetDTO>> updateProjet(
            @PathVariable Long id,
            @RequestPart("project") ProjetDTO projetDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        // Files handling logic could be added here or in service
        ProjetDTO updated = projetService.updateProjet(id, projetDTO);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.<ProjetDTO>builder()
                .success(true)
                .data(updated)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProjet(@PathVariable Long id) {
        projetService.deleteProjet(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .build());
    }

    @PostMapping("/{id}/import-dpgf-data")
    public ResponseEntity<ApiResponse<Void>> importDpgfData(
            @PathVariable Long id,
            @RequestBody com.ecopilot.project.dto.ProjetImportDTO importDTO) {
        projetService.importDpgfData(id, importDTO);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Données DPGF importées avec succès")
                .build());
    }
}
