package com.ecopilot.fournisseur.controller;

import com.ecopilot.fournisseur.dto.FournisseurDTO;
import com.ecopilot.fournisseur.dto.ApiResponse;
import com.ecopilot.fournisseur.service.FournisseurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fournisseurs")
@RequiredArgsConstructor
public class FournisseurController {

    private final FournisseurService fournisseurService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FournisseurDTO>>> getAllFournisseurs() {
        return ResponseEntity.ok(ApiResponse.<List<FournisseurDTO>>builder()
                .success(true)
                .data(fournisseurService.getAllFournisseurs())
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FournisseurDTO>> createFournisseur(@RequestBody FournisseurDTO fournisseurDTO) {
        return ResponseEntity.ok(ApiResponse.<FournisseurDTO>builder()
                .success(true)
                .data(fournisseurService.createFournisseur(fournisseurDTO))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FournisseurDTO>> updateFournisseur(@PathVariable Long id, @RequestBody FournisseurDTO fournisseurDTO) {
        return ResponseEntity.ok(ApiResponse.<FournisseurDTO>builder()
                .success(true)
                .data(fournisseurService.updateFournisseur(id, fournisseurDTO))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFournisseur(@PathVariable Long id) {
        fournisseurService.deleteFournisseur(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .build());
    }

    @GetMapping("/types")
    public ResponseEntity<ApiResponse<List<String>>> getFournisseurTypes() {
        return ResponseEntity.ok(ApiResponse.<List<String>>builder()
                .success(true)
                .data(fournisseurService.getFournisseurTypes())
                .build());
    }
}
