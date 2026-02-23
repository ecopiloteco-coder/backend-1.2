package com.ecopilot.article.service;

import com.ecopilot.article.entity.*;
import com.ecopilot.article.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class HierarchyService {

    private final Niveau1Repository niveau1Repository;
    private final Niveau2Repository niveau2Repository;
    private final Niveau3Repository niveau3Repository;
    private final Niveau4Repository niveau4Repository;
    private final Niveau5Repository niveau5Repository;
    private final Niveau6Repository niveau6Repository;
    private final ArticleRepository articleRepository;

    public List<Object> getFullHierarchy() {
        // Retourne la liste des Niveaux 1 (qui contiennent les enfants grace au fetch lazy mais serialisation?)
        // Attention au LazyLoadingException si on retourne directement les entités sans transaction ou DTO
        // Pour l'instant on retourne les N1
        return (List<Object>)(List<?>) niveau1Repository.findAll();
    }

    public Object getNiveau(int level, Long id) {
        switch (level) {
            case 1: return niveau1Repository.findById(id).orElse(null);
            case 2: return niveau2Repository.findById(id).orElse(null);
            case 3: return niveau3Repository.findById(id).orElse(null);
            case 4: return niveau4Repository.findById(id).orElse(null);
            case 5: return niveau5Repository.findById(id).orElse(null);
            case 6: return niveau6Repository.findById(id).orElse(null);
            default: return null;
        }
    }

    public List<Object> getAllNiveau2() {
        return (List<Object>)(List<?>) niveau2Repository.findAll();
    }

    public List<Object> getAllNiveau3() {
        return (List<Object>)(List<?>) niveau3Repository.findAll();
    }

    public List<Object> getAllNiveau4() {
        return (List<Object>)(List<?>) niveau4Repository.findAll();
    }

    public List<Object> getAllNiveau5() {
        return (List<Object>)(List<?>) niveau5Repository.findAll();
    }

    public List<Object> getAllNiveau6() {
        return (List<Object>)(List<?>) niveau6Repository.findAll();
    }

    public List<Object> getNiveauChildren(int level, Long parentId) {
        switch (level) {
            case 2: 
                return (List<Object>)(List<?>) niveau2Repository.findByNiveau1Id(parentId);
            case 3: 
                return (List<Object>)(List<?>) niveau3Repository.findByNiveau2Id(parentId);
            case 4: 
                return (List<Object>)(List<?>) niveau4Repository.findByNiveau3Id(parentId);
            case 5: 
                return (List<Object>)(List<?>) niveau5Repository.findByNiveau4Id(parentId);
            case 6: 
                return (List<Object>)(List<?>) niveau6Repository.findByNiveau5Id(parentId);
            case 7:
                // Niveau 7 corresponds to Articles
                return (List<Object>)(List<?>) articleRepository.findByNiveau6IdIn(Collections.singletonList(parentId));
            default: 
                return Collections.emptyList();
        }
    }
}
