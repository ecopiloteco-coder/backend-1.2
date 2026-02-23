package com.ecopilot.article.controller;

import com.ecopilot.article.dto.ApiResponse;
import com.ecopilot.article.dto.ArticleDTO;
import com.ecopilot.article.dto.ArticleHierarchyDTO;
import com.ecopilot.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ArticleDTO>>> getAllArticles(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "30") int limit,
            @RequestParam(required = false, defaultValue = "ID") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(name = "id_niv_6", required = false) List<Long> idNiveau6) {
        List<ArticleDTO> articles;
        if (search != null) {
            articles = articleService.searchArticles(search);
        } else {
            articles = articleService.getAllArticles(page, limit, sortBy, sortOrder, idNiveau6);
        }
        return ResponseEntity.ok(ApiResponse.<List<ArticleDTO>>builder()
                .success(true)
                .data(articles)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleDTO>> getArticleById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<ArticleDTO>builder()
                .success(true)
                .data(articleService.getArticleById(id))
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ArticleDTO>> createArticle(@RequestBody ArticleDTO articleDTO) {
        System.out.println("=== ARTICLE RECU ===");
        System.out.println("nomArticle: " + articleDTO.getNomArticle());
        System.out.println("unite: " + articleDTO.getUnite());
        System.out.println("type: " + articleDTO.getType());
        System.out.println("expertise: " + articleDTO.getExpertise());
        System.out.println("origine: " + articleDTO.getOrigine());
        System.out.println("prixEstime: " + articleDTO.getPrixEstime());
        System.out.println("commentaires: " + articleDTO.getCommentaires());
        System.out.println("date: " + articleDTO.getDate());
        System.out.println("==================");
        System.out.println("=== ARTICLE RECU ===");
        System.out.println("nomArticle: " + articleDTO.getNomArticle());
        System.out.println("unite: " + articleDTO.getUnite());
        System.out.println("type: " + articleDTO.getType());
        System.out.println("expertise: " + articleDTO.getExpertise());
        System.out.println("origine: " + articleDTO.getOrigine());
        System.out.println("prix_estime: " + articleDTO.getPrixEstime());
        System.out.println("commentaires: " + articleDTO.getCommentaires());
        System.out.println("date: " + articleDTO.getDate());
        System.out.println("user_id: " + articleDTO.getUserId());
        System.out.println("==================");
        return ResponseEntity.ok(ApiResponse.<ArticleDTO>builder()
                .success(true)
                .data(articleService.createArticle(articleDTO))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleDTO>> updateArticle(@PathVariable Long id, @RequestBody ArticleDTO articleDTO) {
        return ResponseEntity.ok(ApiResponse.<ArticleDTO>builder()
                .success(true)
                .data(articleService.updateArticle(id, articleDTO))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .build());
    }

    @GetMapping("/suggest")
    public ResponseEntity<ApiResponse<List<ArticleDTO>>> suggestArticles(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.<List<ArticleDTO>>builder()
                .success(true)
                .data(articleService.suggestArticles(q))
                .build());
    }

    @GetMapping("/units")
    public ResponseEntity<ApiResponse<List<String>>> getUnits() {
        return ResponseEntity.ok(ApiResponse.<List<String>>builder()
                .success(true)
                .data(articleService.getAllUnits())
                .build());
    }

    @GetMapping("/expertises")
    public ResponseEntity<ApiResponse<List<String>>> getExpertises() {
        return ResponseEntity.ok(ApiResponse.<List<String>>builder()
                .success(true)
                .data(articleService.getAllExpertises())
                .build());
    }

    @GetMapping("/filters/niveau1")
    public ResponseEntity<ApiResponse<List<Object>>> getUsedNiveau1() {
        return ResponseEntity.ok(ApiResponse.<List<Object>>builder()
                .success(true)
                .data((List) articleService.getUsedNiveau1())
                .build());
    }

    @GetMapping("/filters/niveau2")
    public ResponseEntity<ApiResponse<List<Object>>> getUsedNiveau2(@RequestParam(required = false) Long parentId) {
        return ResponseEntity.ok(ApiResponse.<List<Object>>builder()
                .success(true)
                .data((List) articleService.getUsedNiveau2(parentId))
                .build());
    }

    @GetMapping("/filters/niveau3")
    public ResponseEntity<ApiResponse<List<Object>>> getUsedNiveau3(@RequestParam(required = false) Long parentId) {
        return ResponseEntity.ok(ApiResponse.<List<Object>>builder()
                .success(true)
                .data((List) articleService.getUsedNiveau3(parentId))
                .build());
    }

    @GetMapping("/filters/niveau4")
    public ResponseEntity<ApiResponse<List<Object>>> getUsedNiveau4(@RequestParam(required = false) Long parentId) {
        return ResponseEntity.ok(ApiResponse.<List<Object>>builder()
                .success(true)
                .data((List) articleService.getUsedNiveau4(parentId))
                .build());
    }

    @GetMapping("/filters/niveau5")
    public ResponseEntity<ApiResponse<List<Object>>> getUsedNiveau5(@RequestParam(required = false) Long parentId) {
        return ResponseEntity.ok(ApiResponse.<List<Object>>builder()
                .success(true)
                .data((List) articleService.getUsedNiveau5(parentId))
                .build());
    }

    @GetMapping("/filters/niveau6")
    public ResponseEntity<ApiResponse<List<Object>>> getUsedNiveau6(@RequestParam(required = false) Long parentId) {
        return ResponseEntity.ok(ApiResponse.<List<Object>>builder()
                .success(true)
                .data((List) articleService.getUsedNiveau6(parentId))
                .build());
    }

    @GetMapping("/hierarchy")
    public ResponseEntity<ApiResponse<List<ArticleHierarchyDTO>>> getArticlesHierarchy() {
        return ResponseEntity.ok(ApiResponse.<List<ArticleHierarchyDTO>>builder()
                .success(true)
                .data(articleService.getArticleHierarchy())
                .build());
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<ArticleDTO>>> getPendingArticles() {
        return ResponseEntity.ok(ApiResponse.<List<ArticleDTO>>builder()
                .success(true)
                .data(articleService.getAllPendingArticles())
                .build());
    }

    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<List<ArticleDTO>>> getDeletedArticles() {
        return ResponseEntity.ok(ApiResponse.<List<ArticleDTO>>builder()
                .success(true)
                .data(articleService.getAllDeletedArticles())
                .build());
    }
}
