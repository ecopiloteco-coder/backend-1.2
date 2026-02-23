package com.ecopilot.article.controller;

import com.ecopilot.article.dto.ApiResponse;
import com.ecopilot.article.dto.ArticleDTO;
import com.ecopilot.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pending-articles")
@RequiredArgsConstructor
public class PendingArticleController {

    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ArticleDTO>>> getAllPendingArticles() {
        return ResponseEntity.ok(ApiResponse.<List<ArticleDTO>>builder()
                .success(true)
                .data(articleService.getAllPendingArticles())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleDTO>> getPendingArticleById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<ArticleDTO>builder()
                .success(true)
                .data(articleService.getPendingArticleById(id))
                .build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ArticleDTO>>> getUserPendingArticles(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.<List<ArticleDTO>>builder()
                .success(true)
                .data(articleService.getUserPendingArticles(userId))
                .build());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approvePendingArticle(@PathVariable Long id) {
        articleService.approvePendingArticle(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Article approved successfully")
                .build());
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectPendingArticle(@PathVariable Long id) {
        articleService.rejectPendingArticle(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Article rejected successfully")
                .build());
    }

    @PostMapping("/{id}/reset-status")
    public ResponseEntity<ApiResponse<Void>> resetPendingArticleStatus(@PathVariable Long id) {
        articleService.resetPendingArticleStatus(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Pending article status reset successfully")
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleDTO>> updatePendingArticle(@PathVariable Long id, @RequestBody ArticleDTO dto) {
        return ResponseEntity.ok(ApiResponse.<ArticleDTO>builder()
                .success(true)
                .data(articleService.updatePendingArticle(id, dto))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePendingArticle(@PathVariable Long id) {
        articleService.deletePendingArticle(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Pending article deleted successfully")
                .build());
    }
}
