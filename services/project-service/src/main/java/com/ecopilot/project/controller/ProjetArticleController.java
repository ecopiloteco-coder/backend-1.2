package com.ecopilot.project.controller;

import com.ecopilot.project.dto.ProjetArticleDTO;
import com.ecopilot.project.service.ProjetArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.ecopilot.project.dto.ApiResponse;

@RestController
@RequestMapping("/api/projets/articles")
@RequiredArgsConstructor
public class ProjetArticleController {

    private final ProjetArticleService projetArticleService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjetArticleDTO>> createArticle(@RequestBody ProjetArticleDTO dto) {
        return ResponseEntity.ok(ApiResponse.<ProjetArticleDTO>builder()
                .success(true)
                .data(projetArticleService.createArticle(dto))
                .build());
    }

    @GetMapping("/catalog")
    public ResponseEntity<ApiResponse<List<ProjetArticleDTO>>> getArticlesCatalog(
            @RequestParam(required = false) Long structureId) {
        List<ProjetArticleDTO> articles;
        if (structureId != null) {
            articles = projetArticleService.getArticlesByStructureId(structureId);
        } else {
            articles = projetArticleService.getAllArticles();
        }
        return ResponseEntity.ok(ApiResponse.<List<ProjetArticleDTO>>builder()
                .success(true)
                .data(articles)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjetArticleDTO>> updateArticle(@PathVariable Long id, @RequestBody ProjetArticleDTO dto) {
        ProjetArticleDTO updated = projetArticleService.updateArticle(id, dto);
        return ResponseEntity.ok(ApiResponse.<ProjetArticleDTO>builder()
                .success(true)
                .data(updated)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteArticle(@PathVariable Long id) {
        projetArticleService.deleteArticle(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .build());
    }
}
