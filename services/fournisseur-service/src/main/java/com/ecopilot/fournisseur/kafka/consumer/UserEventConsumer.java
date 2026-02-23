package com.ecopilot.fournisseur.kafka.consumer;

import com.ecopilot.fournisseur.entity.Fournisseur;
import com.ecopilot.fournisseur.kafka.event.UserEvent;
import com.ecopilot.fournisseur.repository.FournisseurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final FournisseurRepository fournisseurRepository;

    @KafkaListener(topics = "user.events", groupId = "fournisseur-group")
    @Transactional
    public void handleUserEvent(UserEvent event) {
        log.info("Received User Event: {} for email: {}", event.getEventType(), event.getEmail());

        if ("USER_CREATED".equals(event.getEventType())) {
            processUserCreated(event);
        }
    }

    private void processUserCreated(UserEvent event) {
        // We only care about linking if it's a Fournisseur
        if (event.getTitrePoste() == null || !event.getTitrePoste().equalsIgnoreCase("Fournisseurs")) {
            log.debug("Ignoring USER_CREATED event for non-fournisseur role: {}", event.getTitrePoste());
            return;
        }

        fournisseurRepository.findByEmail(event.getEmail()).ifPresentOrElse(f -> {
            log.info("Linking user ID {} and Keycloak ID {} to existing Fournisseur: {}", 
                    event.getUserId(), event.getKeycloakId(), f.getNomFournisseur());
            f.setUserId(event.getUserId());
            f.setKeycloakId(event.getKeycloakId());
            fournisseurRepository.save(f);
        }, () -> {
            log.info("No existing Fournisseur found for email: {}. Creating a new one.", event.getEmail());
            Fournisseur newFournisseur = new Fournisseur();
            newFournisseur.setNomFournisseur(event.getNomUtilisateur());
            newFournisseur.setEmail(event.getEmail());
            newFournisseur.setUserId(event.getUserId());
            newFournisseur.setKeycloakId(event.getKeycloakId());
            // Other fields will be empty/null and should be completed by the supplier later
            fournisseurRepository.save(newFournisseur);
        });
    }
}
