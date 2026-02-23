package com.ecopilot.user.kafka.producer;

import com.ecopilot.user.kafka.event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    private static final String USER_EVENTS_TOPIC = "user.events";

    public void sendUserCreatedEvent(Long userId, String email, String nomUtilisateur, String titrePoste, String keycloakId) {
        UserEvent event = UserEvent.builder()
                .eventType("USER_CREATED")
                .userId(userId)
                .email(email)
                .nomUtilisateur(nomUtilisateur)
                .titrePoste(titrePoste)
                .keycloakId(keycloakId)
                .timestamp(LocalDateTime.now())
                .build();

        sendEvent(event);
        log.info("Sent USER_CREATED event for user: {}", userId);
    }

    public void sendUserUpdatedEvent(Long userId, String email, String nomUtilisateur, String titrePoste) {
        UserEvent event = UserEvent.builder()
                .eventType("USER_UPDATED")
                .userId(userId)
                .email(email)
                .nomUtilisateur(nomUtilisateur)
                .titrePoste(titrePoste)
                .timestamp(LocalDateTime.now())
                .build();

        sendEvent(event);
        log.info("Sent USER_UPDATED event for user: {}", userId);
    }

    public void sendUserDeletedEvent(Long userId, String email) {
        UserEvent event = UserEvent.builder()
                .eventType("USER_DELETED")
                .userId(userId)
                .email(email)
                .timestamp(LocalDateTime.now())
                .build();

        sendEvent(event);
        log.info("Sent USER_DELETED event for user: {}", userId);
    }

    private void sendEvent(UserEvent event) {
        try {
            kafkaTemplate.send(USER_EVENTS_TOPIC, event.getUserId().toString(), event);
        } catch (Exception e) {
            log.error("Failed to send user event: {}", event, e);
        }
    }
}
