package com.ecopilot.project.controller;

import com.ecopilot.project.dto.ProjetLotDTO;
import com.ecopilot.project.service.ProjetLotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.ecopilot.project.dto.ApiResponse;

@RestController
@RequestMapping("/api/projet-details/lots")
@RequiredArgsConstructor
public class LotController {

    private final ProjetLotService projetLotService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjetLotDTO>> createLot(@RequestBody ProjetLotDTO dto) {
        return ResponseEntity.ok(ApiResponse.<ProjetLotDTO>builder()
                .success(true)
                .data(projetLotService.createLot(dto))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjetLotDTO>> updateLot(@PathVariable Long id, @RequestBody ProjetLotDTO dto) {
        ProjetLotDTO updated = projetLotService.updateLot(id, dto);
        return ResponseEntity.ok(ApiResponse.<ProjetLotDTO>builder()
                .success(true)
                .data(updated)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLot(@PathVariable Long id) {
        projetLotService.deleteLot(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .build());
    }
}
