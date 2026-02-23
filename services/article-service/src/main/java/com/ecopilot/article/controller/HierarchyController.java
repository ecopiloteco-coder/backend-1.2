package com.ecopilot.article.controller;

import com.ecopilot.article.service.HierarchyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/niveaux")
@RequiredArgsConstructor
public class HierarchyController {

    private final HierarchyService hierarchyService;

    @GetMapping
    public ResponseEntity<List<Object>>  getAllHierarchy() {
        return ResponseEntity.ok(hierarchyService.getFullHierarchy());
    }

    @GetMapping("/{level}")
    public ResponseEntity<Object> getNiveauByLevel(@PathVariable int level, 
                                                    @RequestParam(required = false) Long id_niv_1,
                                                    @RequestParam(required = false) Long id_niv_2,
                                                    @RequestParam(required = false) Long id_niv_3,
                                                    @RequestParam(required = false) Long id_niv_4,
                                                    @RequestParam(required = false) Long id_niv_5,
                                                    @RequestParam(required = false) Long id_niv_6) {
        // Handle different level requests with query parameters
        switch (level) {
            case 1:
                return ResponseEntity.ok(hierarchyService.getFullHierarchy());
            case 2:
                return id_niv_1 != null ? ResponseEntity.ok(hierarchyService.getNiveauChildren(2, id_niv_1)) 
                                       : ResponseEntity.ok(hierarchyService.getAllNiveau2());
            case 3:
                return id_niv_2 != null ? ResponseEntity.ok(hierarchyService.getNiveauChildren(3, id_niv_2))
                                       : ResponseEntity.ok(hierarchyService.getAllNiveau3());
            case 4:
                return id_niv_3 != null ? ResponseEntity.ok(hierarchyService.getNiveauChildren(4, id_niv_3))
                                       : ResponseEntity.ok(hierarchyService.getAllNiveau4());
            case 5:
                return id_niv_4 != null ? ResponseEntity.ok(hierarchyService.getNiveauChildren(5, id_niv_4))
                                       : ResponseEntity.ok(hierarchyService.getAllNiveau5());
            case 6:
                return id_niv_5 != null ? ResponseEntity.ok(hierarchyService.getNiveauChildren(6, id_niv_5))
                                       : ResponseEntity.ok(hierarchyService.getAllNiveau6());
            case 7:
                // Niveau 7 correspond aux articles enfants d'un niveau 6
                return id_niv_6 != null ? ResponseEntity.ok(hierarchyService.getNiveauChildren(7, id_niv_6))
                                       : ResponseEntity.ok(List.of()); // Pas de "tous les articles" via ce endpoint pour l'instant
            default:
                return ResponseEntity.badRequest().body("Invalid level");
        }
    }

    @GetMapping("/{level}/{id}")
    public ResponseEntity<Object> getNiveau(@PathVariable int level, @PathVariable Long id) {
        return ResponseEntity.ok(hierarchyService.getNiveau(level, id));
    }
}
