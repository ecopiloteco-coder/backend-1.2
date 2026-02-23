package com.ecopilot.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "keycloakId")
public class UserDTO {
    private Long id;
    private String keycloakId;
    private String nomUtilisateur;
    private String email;
    private com.ecopilot.user.entity.UserRole titrePoste;
    private LocalDateTime dateCreationCompte;
    private Boolean isAdmin;
    private Long fournisseurId;
}
