package com.ecopilot.user.service;
import com.ecopilot.user.dto.LoginRequest;
import com.ecopilot.user.dto.LoginResponse;
import com.ecopilot.user.dto.SignupRequest;
import com.ecopilot.user.dto.UserDTO;
import com.ecopilot.user.entity.User;
import com.ecopilot.user.entity.UserRole;
import com.ecopilot.user.kafka.producer.UserEventProducer;
import com.ecopilot.user.repository.UserRepository;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final Keycloak keycloak;
    private final UserRepository userRepository;
    private final UserEventProducer eventProducer;
    private final KeycloakAuthClient keycloakAuthClient;

    @Value("${keycloak.realm}")
    private String realm;

    // ================= LOGIN =================
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        // 1. Authenticate via Keycloak — returns full token response
        Map<String, Object> tokenResponse;
        try {
            tokenResponse = keycloakAuthClient.authenticate(request.getEmail(), request.getMotDePasse());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
        }

        // 2. Retrieve local user for additional profile info
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        // 3. Return Keycloak tokens + user profile
        return LoginResponse.builder()
                .token((String) tokenResponse.get("access_token"))
                .refreshToken((String) tokenResponse.get("refresh_token"))
                .expiresIn(((Number) tokenResponse.get("expires_in")).longValue())
                .user(mapToDTO(user))
                .build();
    }

    // ================= REFRESH =================
    public LoginResponse refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token is required");
        }

        Map<String, Object> tokenResponse;
        try {
            tokenResponse = keycloakAuthClient.refreshToken(refreshToken);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
        }

        return LoginResponse.builder()
                .token((String) tokenResponse.get("access_token"))
                .refreshToken((String) tokenResponse.get("refresh_token"))
                .expiresIn(((Number) tokenResponse.get("expires_in")).longValue())
                .build();
    }

    // ================= SIGNUP =================
    public UserDTO signup(SignupRequest request) {
        log.info("Starting signup process for: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        // 🔹 1️⃣ Créer User dans Keycloak
        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(request.getEmail());
        kcUser.setEmail(request.getEmail());
        
        // Split name for firstName/lastName if possible, or just use both
        String fullName = request.getNomUtilisateur();
        if (fullName != null && fullName.contains(" ")) {
            String[] parts = fullName.split(" ", 2);
            kcUser.setFirstName(parts[0]);
            kcUser.setLastName(parts[1]);
        } else {
            kcUser.setFirstName(fullName);
            kcUser.setLastName(fullName); // Set lastName to avoid validation errors if required
        }
        
        kcUser.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getMotDePasse());
        credential.setTemporary(false);
        kcUser.setCredentials(List.of(credential));

        UsersResource usersResource = keycloak.realm(realm).users();

        Response response;
        try {
            response = usersResource.create(kcUser);
        } catch (Exception e) {
            log.error("Failed to connect to Keycloak: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Keycloak connection failed: " + e.getMessage());
        }

        int status = response.getStatus();
        log.debug("Keycloak response status: {}", status);

        // 🔹 Si l’utilisateur existe déjà dans Keycloak (409 ou parfois 400 validation error on duplicate)
        if (status == 409 || status == 400) {
            List<UserRepresentation> existingUsers = usersResource.search(request.getEmail());
            if (!existingUsers.isEmpty()) {
                log.info("User already exists in Keycloak (status {}), linking to local DB.", status);
                String userId = existingUsers.get(0).getId();
                return createLocalUser(request, userId);
            }
            
            if (status == 400) {
                // If it's 400 and user doesn't exist, it's a real validation error
                String errorBody = response.readEntity(String.class);
                log.error("Keycloak validation error (400): {}", errorBody);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Keycloak validation error: " + errorBody);
            }
        }

        if (status != 201) {
            String errorBody = response.readEntity(String.class);
            log.error("Error creating user in Keycloak: {} - {}", status, errorBody);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error creating user in Keycloak: " + status);
        }

        String userId = CreatedResponseUtil.getCreatedId(response);
        log.debug("User created in Keycloak with ID: {}", userId);

        // 🔹 Créer dans DB locale
        return createLocalUser(request, userId);
    }

    private UserDTO createLocalUser(SignupRequest request, String keycloakId) {
        if (userRepository.existsByEmail(request.getEmail())) {
            User existing = userRepository.findByEmail(request.getEmail()).orElseThrow();
            return mapToDTO(existing);
        }

        UserRole role = null;
        String roleStr = request.getRole();
        String titreRecu = request.getTitrePoste();
        String toMap = roleStr != null ? roleStr : titreRecu;
        if (toMap != null) {
            try {
                String titre = toMap.trim();
                for (UserRole r : UserRole.values()) {
                    if (r.getValue().equalsIgnoreCase(titre) || r.name().equalsIgnoreCase(titre)) {
                        role = r;
                        break;
                    }
                }
                if (role == null) {
                    log.warn("[TITRE_POSTE] Titre poste inconnu reçu: '{}' . Fallback sur USER.", titre);
                    role = UserRole.USER;
                }
            } catch (Exception e) {
                log.error("[TITRE_POSTE] Erreur mapping titre_poste: '{}' . Exception: {}", toMap, e.getMessage());
                role = UserRole.USER;
            }
        } else {
            role = UserRole.USER;
        }
        log.info("[TITRE_POSTE] Valeur reçue: '{}' , valeur enregistrée: '{}'", toMap, role);
        User user = User.builder()
                .keycloakId(keycloakId)
                .email(request.getEmail())
                .nomUtilisateur(request.getNomUtilisateur())
                .titrePoste(role)
                .isAdmin(role == UserRole.ADMIN || role == UserRole.SUPER_ADMIN)
                .build();

        User saved;
        try {
            saved = userRepository.save(user);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.CONFLICT,
                "Email already exists (DB constraint)");
        }
        log.debug("User saved to local DB: {}", saved.getEmail());

        eventProducer.sendUserCreatedEvent(
            saved.getId(),
            saved.getEmail(),
            saved.getNomUtilisateur(),
            saved.getTitrePoste().getValue(),
            saved.getKeycloakId()
    );

        return mapToDTO(saved);
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .keycloakId(user.getKeycloakId())
                .email(user.getEmail())
                .nomUtilisateur(user.getNomUtilisateur())
                .titrePoste(user.getTitrePoste())
                .dateCreationCompte(user.getDateCreationCompte())
                .isAdmin(user.getIsAdmin())
                .fournisseurId(user.getFournisseurId())
                .build();
    }
}
