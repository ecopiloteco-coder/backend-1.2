package com.ecopilot.article.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleHierarchyDTO {

    private Long articleId;
    private String nomArticle;

    private String niveau1;
    private String niveau2;
    private String niveau3;
    private String niveau4;
    private String niveau5;
    private String niveau6;
}

