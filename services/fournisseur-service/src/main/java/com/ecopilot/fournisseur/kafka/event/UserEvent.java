package com.ecopilot.fournisseur.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    private String eventType;
    private Long userId;
    private String email;
    private String nomUtilisateur;
    private String titrePoste;
    private String keycloakId;
    private LocalDateTime timestamp;
}
