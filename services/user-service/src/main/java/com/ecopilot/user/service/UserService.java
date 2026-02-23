package com.ecopilot.user.service;

import com.ecopilot.user.dto.UserDTO;
import com.ecopilot.user.entity.User;
import com.ecopilot.user.entity.UserRole;
import com.ecopilot.user.kafka.producer.UserEventProducer;
import com.ecopilot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserEventProducer eventProducer;
    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public void deleteUserByKeycloakId(String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId).orElse(null);
        if (user != null) {
            userRepository.deleteById(user.getId());
            eventProducer.sendUserDeletedEvent(user.getId(), user.getEmail());
        }
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Update nom et poste
        if (userDTO.getNomUtilisateur() != null) {
            user.setNomUtilisateur(userDTO.getNomUtilisateur());
        }
        if (userDTO.getTitrePoste() != null) {
            user.setTitrePoste(userDTO.getTitrePoste());
        }

        // Update email en base ET dans Keycloak
        if (userDTO.getEmail() != null && !userDTO.getEmail().isBlank()
                && !userDTO.getEmail().equals(user.getEmail())) {
            // Mettre Ã  jour dans Keycloak
            if (user.getKeycloakId() != null) {
                try {
                    UserRepresentation keycloakUser = keycloak.realm(realm)
                            .users()
                            .get(user.getKeycloakId())
                            .toRepresentation();
                    keycloakUser.setEmail(userDTO.getEmail());
                    keycloakUser.setEmailVerified(true);
                    keycloak.realm(realm)
                            .users()
                            .get(user.getKeycloakId())
                            .update(keycloakUser);
                } catch (Exception e) {
                    throw new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Erreur lors de la mise Ã  jour de l email dans Keycloak: " + e.getMessage()
                    );
                }
            }
            // Mettre Ã  jour en base
            user.setEmail(userDTO.getEmail());
        }

        User updated = userRepository.save(user);

        // Publish Kafka event
        eventProducer.sendUserUpdatedEvent(
                updated.getId(),
                updated.getEmail(),
                updated.getNomUtilisateur(),
                updated.getTitrePoste().name()
        );

        return mapToDTO(updated);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        String email = user.getEmail();
        try {
            keycloak.realm(realm).users().delete(user.getKeycloakId());
        } catch (Exception e) {
            // Log error
        }
        userRepository.deleteById(id);
        eventProducer.sendUserDeletedEvent(id, email);
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nomUtilisateur(user.getNomUtilisateur())
                .titrePoste(user.getTitrePoste())
                .dateCreationCompte(user.getDateCreationCompte())
                .isAdmin(user.getIsAdmin())
                .build();
    }
}