package com.ecopilot.project.controller;

import com.ecopilot.project.dto.BlocDTO;
import com.ecopilot.project.service.BlocService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.ecopilot.project.dto.ApiResponse;

@RestController
@RequiredArgsConstructor
public class BlocController {

    private final BlocService blocService;

    // Endpoints under /api/projets/blocs
    @PostMapping("/api/projets/blocs")
    public ResponseEntity<ApiResponse<BlocDTO>> createBlocInProjet(@RequestBody BlocDTO dto) {
        return ResponseEntity.ok(ApiResponse.<BlocDTO>builder()
                .success(true)
                .data(blocService.createBloc(dto))
                .build());
    }

    @GetMapping("/api/projets/blocs/{id}")
    public ResponseEntity<ApiResponse<BlocDTO>> getBlocInProjet(@PathVariable Long id) {
        BlocDTO bloc = blocService.getBlocById(id);
        return ResponseEntity.ok(ApiResponse.<BlocDTO>builder()
                .success(true)
                .data(bloc)
                .build());
    }

    @PutMapping("/api/projets/blocs/{id}")
    public ResponseEntity<ApiResponse<BlocDTO>> updateBlocInProjet(@PathVariable Long id, @RequestBody BlocDTO dto) {
        BlocDTO updated = blocService.updateBloc(id, dto);
        return ResponseEntity.ok(ApiResponse.<BlocDTO>builder()
                .success(true)
                .data(updated)
                .build());
    }

    @DeleteMapping("/api/projets/blocs/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBlocInProjet(@PathVariable Long id) {
        blocService.deleteBloc(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .build());
    }

    // Standalone endpoints under /api/blocs
    @PostMapping("/api/blocs")
    public ResponseEntity<ApiResponse<BlocDTO>> createBloc(@RequestBody BlocDTO dto) {
        return ResponseEntity.ok(ApiResponse.<BlocDTO>builder()
                .success(true)
                .data(blocService.createBloc(dto))
                .build());
    }

    @GetMapping("/api/blocs/{id}")
    public ResponseEntity<ApiResponse<BlocDTO>> getBloc(@PathVariable Long id) {
        BlocDTO bloc = blocService.getBlocById(id);
        return ResponseEntity.ok(ApiResponse.<BlocDTO>builder()
                .success(true)
                .data(bloc)
                .build());
    }

    @PutMapping("/api/blocs/{id}")
    public ResponseEntity<ApiResponse<BlocDTO>> updateBloc(@PathVariable Long id, @RequestBody BlocDTO dto) {
        BlocDTO updated = blocService.updateBloc(id, dto);
        return ResponseEntity.ok(ApiResponse.<BlocDTO>builder()
                .success(true)
                .data(updated)
                .build());
    }

    @DeleteMapping("/api/blocs/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBloc(@PathVariable Long id) {
        blocService.deleteBloc(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .build());
    }
}
