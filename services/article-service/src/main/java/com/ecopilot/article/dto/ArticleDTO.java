package com.ecopilot.article.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleDTO {
    private Long id;

    @JsonProperty("nom_article")
    @JsonAlias({"nomArticle", "Nom_Article"})
    private String nomArticle;

    @JsonProperty("unite")
    @JsonAlias({"Unite", "UNITE"})
    private String unite;

    @JsonProperty("type")
    @JsonAlias({"Type", "TYPE"})
    private String type;

    @JsonProperty("expertise")
    @JsonAlias({"Expertise", "EXPERTISE"})
    private String expertise;

    @JsonProperty("fourniture")
    @JsonAlias({"Fourniture", "FOURNITURE"})
    private String fourniture;

    @JsonProperty("cadence")
    @JsonAlias({"Cadence", "CADENCE"})
    private String cadence;

    @JsonProperty("accessoires")
    @JsonAlias({"Accessoires", "ACCESSOIRES"})
    private String accessoires;

    @JsonProperty("pertes")
    @JsonAlias({"Pertes", "PERTES"})
    private String pertes;

    @JsonProperty("pu")
    @JsonAlias({"PU", "Pu"})
    private String pu;

    @JsonProperty("prix_cible")
    @JsonAlias({"Prix_Cible", "prixCible", "Prix_cible"})
    private String prixCible;

    @JsonProperty("prix_estime")
    @JsonAlias({"Prix_estime", "prixEstime", "Prix_Estime"})
    private String prixEstime;

    @JsonProperty("prix_consulte")
    @JsonAlias({"Prix_consulte", "prixConsulte", "Prix_Consulte"})
    private String prixConsulte;

    @JsonProperty("rabais")
    @JsonAlias({"Rabais"})
    private String rabais;

    @JsonProperty("commentaires")
    @JsonAlias({"Commentaires", "COMMENTAIRES"})
    private String commentaires;

    @JsonProperty("origine")
    @JsonAlias({"Origine", "ORIGINE"})
    private String origine;

    @JsonProperty("user_id")
    @JsonAlias({"User", "userId", "user"})
    private String userId;

    @JsonProperty("status")
    @JsonAlias({"Status", "STATUS"})
    private String status;

    @JsonProperty("accepted_by")
    @JsonAlias({"acceptedBy"})
    private String acceptedBy;

    @JsonProperty("rejected_by")
    @JsonAlias({"rejectedBy"})
    private String rejectedBy;

    @JsonProperty("approved_article_id")
    @JsonAlias({"approvedArticleId"})
    private Long approvedArticleId;

    @JsonProperty("submitted_at")
    @JsonAlias({"submittedAt"})
    private LocalDateTime submittedAt;

    @JsonProperty("updated_at")
    @JsonAlias({"updatedAt"})
    private LocalDateTime updatedAt;

    @JsonProperty("indice_de_confiance")
    @JsonAlias({"Indice_de_confiance", "indiceDeConfiance"})
    private Integer indiceDeConfiance;

    @JsonProperty("files")
    private String files;

    @JsonProperty("fournisseur_id")
    @JsonAlias({"fournisseur", "Fournisseur"})
    private Long fournisseurId;

    @JsonProperty("id_niv_6")
    @JsonAlias({"niveau6Id"})
    private Long niveau6Id;

    @JsonProperty("date")
    @JsonAlias({"Date", "Date_Prix"})
    private LocalDate date;

    @JsonProperty("created_at")
    @JsonAlias({"createdAt"})
    private LocalDateTime createdAt;

    @JsonProperty("niveau_1")
    @JsonAlias({"Niveau_1", "niveau1"})
    private String niveau1;

    @JsonProperty("niveau_2")
    @JsonAlias({"Niveau_2", "niveau2"})
    private String niveau2;

    @JsonProperty("niveau_3")
    @JsonAlias({"Niveau_3", "niveau3"})
    private String niveau3;

    @JsonProperty("niveau_4")
    @JsonAlias({"Niveau_4", "niveau4"})
    private String niveau4;

    @JsonProperty("niv_5")
    @JsonAlias({"Niveau_5", "niveau5", "niveau_5"})
    private String niveau5;

    @JsonProperty("niv_6")
    @JsonAlias({"Niveau_6", "niveau6", "niveau_6"})
    private String niveau6;
}
