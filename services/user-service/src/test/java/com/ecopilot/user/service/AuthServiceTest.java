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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService
 * 
 * Tests cover:
 * - User login with JWT token generation
 * - User signup with Keycloak integration
 * - Error handling and validation
 * - User DTO mapping
 * 
 * @author EcoPilot Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private Keycloak keycloak;

    @Mock
    private UserRepository userRepository;



    @Mock
    private UserEventProducer eventProducer;

    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;

    @Mock
    private KeycloakAuthClient keycloakAuthClient;

    @Mock
    private Response keycloakResponse;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequest loginRequest;
    private SignupRequest signupRequest;

    @BeforeEach
    void setUp() {
        // Set realm via reflection (simulates @Value injection)
        ReflectionTestUtils.setField(authService, "realm", "ecopilot");

        // Test user setup
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .nomUtilisateur("John Doe")
                .titrePoste(UserRole.ASSISTANTE_ADMINISTRATIVE)
                .isAdmin(false)
                .keycloakId("kc-123")
                .dateCreationCompte(LocalDateTime.now())
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .motDePasse("password123")
                .build();

        signupRequest = SignupRequest.builder()
                .email("newuser@example.com")
                .motDePasse("securePass123")
                .nomUtilisateur("Jane Smith")
                .role(UserRole.ASSISTANTE_ADMINISTRATIVE.getValue())
                .build();
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should successfully login user and return JWT token")
        void shouldLoginSuccessfully() {
            // Arrange
            String expectedToken = "jwt-token-12345";
            when(userRepository.findByEmail(loginRequest.getEmail()))
                    .thenReturn(Optional.of(testUser));
            when(keycloakAuthClient.authenticate(loginRequest.getEmail(), loginRequest.getMotDePasse()))
                    .thenReturn(Map.of("access_token", expectedToken, "refresh_token", "refresh-xyz", "expires_in", 300));

            // Act
            LoginResponse response = authService.login(loginRequest);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getToken()).isEqualTo(expectedToken);
            assertThat(response.getUser()).isNotNull();
            assertThat(response.getUser().getEmail()).isEqualTo(testUser.getEmail());
            assertThat(response.getUser().getId()).isEqualTo(testUser.getId());

            verify(userRepository).findByEmail(loginRequest.getEmail());
            verify(keycloakAuthClient).authenticate(loginRequest.getEmail(), loginRequest.getMotDePasse());
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Arrange
            when(keycloakAuthClient.authenticate(anyString(), anyString()))
                    .thenReturn(Map.of("access_token", "token", "refresh_token", "refresh-xyz", "expires_in", 300));
            when(userRepository.findByEmail(loginRequest.getEmail()))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("User not found")
                    .extracting("status")
                    .isEqualTo(HttpStatus.UNAUTHORIZED);

            verify(keycloakAuthClient).authenticate(anyString(), anyString());
            verify(userRepository).findByEmail(loginRequest.getEmail());
        }

        @Test
        @DisplayName("Should include correct user info in response")
        void shouldIncludeCorrectUserInfo() {
            // Arrange
            when(userRepository.findByEmail(loginRequest.getEmail()))
                    .thenReturn(Optional.of(testUser));
            when(keycloakAuthClient.authenticate(anyString(), anyString()))
                    .thenReturn(Map.of("access_token", "token", "refresh_token", "refresh-xyz", "expires_in", 300));

            // Act
            LoginResponse response = authService.login(loginRequest);

            // Assert
            UserDTO userDTO = response.getUser();
            assertThat(userDTO.getEmail()).isEqualTo(testUser.getEmail());
            assertThat(userDTO.getNomUtilisateur()).isEqualTo(testUser.getNomUtilisateur());
            assertThat(userDTO.getTitrePoste()).isEqualTo(testUser.getTitrePoste());
            assertThat(userDTO.getIsAdmin()).isEqualTo(testUser.getIsAdmin());
        }
    }

    @Nested
    @DisplayName("Signup Tests")
    class SignupTests {

        @BeforeEach
        void setUpSignup() {
            lenient().when(keycloak.realm("ecopilot")).thenReturn(realmResource);
            lenient().when(realmResource.users()).thenReturn(usersResource);
        }

        @Test
        @DisplayName("Should successfully create user in Keycloak and local DB")
        void shouldSignupSuccessfully() {
            // Arrange
            when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
            when(usersResource.create(any(UserRepresentation.class))).thenReturn(keycloakResponse);
            when(keycloakResponse.getStatus()).thenReturn(201);
            lenient().when(keycloakResponse.getStatusInfo()).thenReturn(Response.Status.CREATED);
            when(keycloakResponse.getLocation()).thenReturn(
                    java.net.URI.create("http://localhost/users/kc-new-id")
            );

            User savedUser = User.builder()
                    .id(2L)
                    .email(signupRequest.getEmail())
                    .nomUtilisateur(signupRequest.getNomUtilisateur())
                    .titrePoste(UserRole.ASSISTANTE_ADMINISTRATIVE)
                    .isAdmin(false)
                    .keycloakId("kc-new-id")
                    .dateCreationCompte(LocalDateTime.now())
                    .build();

            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            doNothing().when(eventProducer).sendUserCreatedEvent(anyLong(), anyString(), anyString(), anyString());

            // Act
            UserDTO result = authService.signup(signupRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(signupRequest.getEmail());
            assertThat(result.getNomUtilisateur()).isEqualTo(signupRequest.getNomUtilisateur());
            assertThat(result.getIsAdmin()).isFalse();

            verify(userRepository, atLeastOnce()).existsByEmail(signupRequest.getEmail());
            verify(usersResource).create(any(UserRepresentation.class));
            verify(userRepository).save(any(User.class));
            verify(eventProducer).sendUserCreatedEvent(
                    eq(savedUser.getId()),
                    eq(savedUser.getEmail()),
                    eq(savedUser.getNomUtilisateur()),
                    eq(savedUser.getTitrePoste().getValue())
            );
        }

        @Test
        @DisplayName("Should throw exception when email already exists locally")
        void shouldThrowExceptionWhenEmailExists() {
            // Arrange
            when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> authService.signup(signupRequest))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Email already exists")
                    .extracting("status")
                    .isEqualTo(HttpStatus.CONFLICT);

            verify(userRepository).existsByEmail(signupRequest.getEmail());
            verify(usersResource, never()).create(any());
        }

        @Test
        @DisplayName("Should set isAdmin true for ADMIN role")
        void shouldSetAdminFlagForAdminRole() {
            SignupRequest adminRequest = SignupRequest.builder()
                    .email("admin@example.com")
                    .motDePasse("adminPass123")
                    .nomUtilisateur("Admin User")
                    .role(UserRole.ADMIN.getValue())
                    .build();

            when(userRepository.existsByEmail(adminRequest.getEmail())).thenReturn(false);
            when(usersResource.create(any(UserRepresentation.class))).thenReturn(keycloakResponse);
            when(keycloakResponse.getStatus()).thenReturn(201);
            lenient().when(keycloakResponse.getStatusInfo()).thenReturn(Response.Status.CREATED);
            when(keycloakResponse.getLocation()).thenReturn(
                    java.net.URI.create("http://localhost/users/kc-admin-id")
            );

            User savedAdmin = User.builder()
                    .id(3L)
                    .email(adminRequest.getEmail())
                    .nomUtilisateur(adminRequest.getNomUtilisateur())
                    .titrePoste(UserRole.ADMIN)
                    .isAdmin(true)
                    .keycloakId("kc-admin-id")
                    .build();

            when(userRepository.save(any(User.class))).thenReturn(savedAdmin);

            UserDTO result = authService.signup(adminRequest);

            assertThat(result.getIsAdmin()).isTrue();
            verify(userRepository).save(argThat(user ->
                    Boolean.TRUE.equals(user.getIsAdmin()) &&
                            user.getTitrePoste() == UserRole.ADMIN
            ));
        }

        @Test
        @DisplayName("Should handle Keycloak user conflict (409) gracefully")
        void shouldHandleKeycloakConflict() {
            // Arrange
            when(userRepository.existsByEmail(signupRequest.getEmail()))
                    .thenReturn(false)
                    .thenReturn(false); // Will be called twice

            when(usersResource.create(any(UserRepresentation.class))).thenReturn(keycloakResponse);
            when(keycloakResponse.getStatus()).thenReturn(409); // Conflict

            UserRepresentation existingKcUser = new UserRepresentation();
            existingKcUser.setId("kc-existing-id");
            existingKcUser.setEmail(signupRequest.getEmail());

            when(usersResource.search(signupRequest.getEmail()))
                    .thenReturn(List.of(existingKcUser));

            User savedUser = User.builder()
                    .id(4L)
                    .email(signupRequest.getEmail())
                    .nomUtilisateur(signupRequest.getNomUtilisateur())
                    .titrePoste(UserRole.ASSISTANTE_ADMINISTRATIVE)
                    .isAdmin(false)
                    .keycloakId("kc-existing-id")
                    .build();

            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            // Act
            UserDTO result = authService.signup(signupRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(signupRequest.getEmail());
            verify(usersResource).search(signupRequest.getEmail());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when Keycloak creation fails")
        void shouldThrowExceptionOnKeycloakFailure() {
            // Arrange
            when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
            when(usersResource.create(any(UserRepresentation.class))).thenReturn(keycloakResponse);
            when(keycloakResponse.getStatus()).thenReturn(500); // Server error

            // Act & Assert
            assertThatThrownBy(() -> authService.signup(signupRequest))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Error creating user in Keycloak")
                    .extracting("status")
                    .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

            verify(userRepository, never()).save(any());
            verify(eventProducer, never()).sendUserCreatedEvent(anyLong(), anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Should publish Kafka event after successful signup")
        void shouldPublishKafkaEventAfterSignup() {
            // Arrange
            when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
            when(usersResource.create(any(UserRepresentation.class))).thenReturn(keycloakResponse);
            when(keycloakResponse.getStatus()).thenReturn(201);
            lenient().when(keycloakResponse.getStatusInfo()).thenReturn(Response.Status.CREATED);
            when(keycloakResponse.getLocation()).thenReturn(
                    java.net.URI.create("http://localhost/users/kc-kafka-id")
            );

            User savedUser = User.builder()
                    .id(5L)
                    .email(signupRequest.getEmail())
                    .nomUtilisateur(signupRequest.getNomUtilisateur())
                    .titrePoste(UserRole.ASSISTANTE_ADMINISTRATIVE)
                    .isAdmin(false)
                    .keycloakId("kc-kafka-id")
                    .build();

            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            // Act
            authService.signup(signupRequest);

            // Assert
            verify(eventProducer).sendUserCreatedEvent(
                    eq(savedUser.getId()),
                    eq(savedUser.getEmail()),
                    eq(savedUser.getNomUtilisateur()),
                    eq(savedUser.getTitrePoste().getValue())
            );
        }
    }

    @Nested
    @DisplayName("DTO Mapping Tests")
    class DtoMappingTests {

        @Test
        @DisplayName("Should correctly map User entity to UserDTO")
        void shouldMapUserToDTO() {
            // Arrange
            when(userRepository.findByEmail(testUser.getEmail()))
                    .thenReturn(Optional.of(testUser));
            when(keycloakAuthClient.authenticate(anyString(), anyString()))
                    .thenReturn(Map.of("access_token", "token", "refresh_token", "refresh-xyz", "expires_in", 300));

            // Act
            LoginResponse response = authService.login(loginRequest);
            UserDTO dto = response.getUser();

            // Assert
            assertThat(dto.getId()).isEqualTo(testUser.getId());
            assertThat(dto.getEmail()).isEqualTo(testUser.getEmail());
            assertThat(dto.getNomUtilisateur()).isEqualTo(testUser.getNomUtilisateur());
            assertThat(dto.getTitrePoste()).isEqualTo(testUser.getTitrePoste());
            assertThat(dto.getIsAdmin()).isEqualTo(testUser.getIsAdmin());
            assertThat(dto.getDateCreationCompte()).isEqualTo(testUser.getDateCreationCompte());
        }

        @Test
        @DisplayName("Should not expose sensitive data in DTO")
        void shouldNotExposeSensitiveData() {
            // Arrange
            when(userRepository.findByEmail(testUser.getEmail()))
                    .thenReturn(Optional.of(testUser));
            when(keycloakAuthClient.authenticate(anyString(), anyString()))
                    .thenReturn(Map.of("access_token", "token", "refresh_token", "refresh-xyz", "expires_in", 300));

            // Act
            LoginResponse response = authService.login(loginRequest);
            UserDTO dto = response.getUser();

            // Assert - DTO should not contain Keycloak ID or password
            assertThat(dto.toString()).doesNotContain("keycloakId");
            assertThat(dto.toString()).doesNotContain("password");
            assertThat(dto.toString()).doesNotContain("kc-");
        }
    }
}
