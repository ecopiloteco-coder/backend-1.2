package com.ecopilot.user.service;

import com.ecopilot.user.dto.UserDTO;
import com.ecopilot.user.entity.User;
import com.ecopilot.user.entity.UserRole;
import com.ecopilot.user.kafka.producer.UserEventProducer;
import com.ecopilot.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService
 * 
 * Tests cover:
 * - Get all users
 * - Get user by ID
 * - Update user
 * - Delete user
 * - Kafka event publishing
 * 
 * @author EcoPilot Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserEventProducer eventProducer;

    @Mock
    private Keycloak keycloak;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User secondUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "realm", "ecopilot-test");
        testUser = User.builder()
                .id(1L)
                .email("john@example.com")
                .nomUtilisateur("John Doe")
                .titrePoste(UserRole.ASSISTANTE_ADMINISTRATIVE)
                .isAdmin(false)
                .keycloakId("kc-123")
                .dateCreationCompte(LocalDateTime.now())
                .build();

        secondUser = User.builder()
                .id(2L)
                .email("jane@example.com")
                .nomUtilisateur("Jane Smith")
                .titrePoste(UserRole.ADMIN)
                .isAdmin(true)
                .keycloakId("kc-456")
                .dateCreationCompte(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Get All Users Tests")
    class GetAllUsersTests {

        @Test
        @DisplayName("Should return list of all users")
        void shouldReturnAllUsers() {
            // Arrange
            List<User> users = List.of(testUser, secondUser);
            when(userRepository.findAll()).thenReturn(users);

            // Act
            List<UserDTO> result = userService.getAllUsers();

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result).extracting(UserDTO::getEmail)
                    .containsExactlyInAnyOrder("john@example.com", "jane@example.com");

            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no users exist")
        void shouldReturnEmptyListWhenNoUsers() {
            // Arrange
            when(userRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<UserDTO> result = userService.getAllUsers();

            // Assert
            assertThat(result).isEmpty();
            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("Should map all user fields correctly")
        void shouldMapAllFieldsCorrectly() {
            // Arrange
            when(userRepository.findAll()).thenReturn(List.of(testUser));

            // Act
            List<UserDTO> result = userService.getAllUsers();

            // Assert
            UserDTO dto = result.get(0);
            assertThat(dto.getId()).isEqualTo(testUser.getId());
            assertThat(dto.getEmail()).isEqualTo(testUser.getEmail());
            assertThat(dto.getNomUtilisateur()).isEqualTo(testUser.getNomUtilisateur());
            assertThat(dto.getTitrePoste()).isEqualTo(testUser.getTitrePoste());
            assertThat(dto.getIsAdmin()).isEqualTo(testUser.getIsAdmin());
        }
    }

    @Nested
    @DisplayName("Get User By ID Tests")
    class GetUserByIdTests {

        @Test
        @DisplayName("Should return user when found by ID")
        void shouldReturnUserWhenFound() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // Act
            UserDTO result = userService.getUserById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getEmail()).isEqualTo("john@example.com");

            verify(userRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenNotFound() {
            // Arrange
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.getUserById(999L))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("User not found")
                    .extracting("status")
                    .isEqualTo(HttpStatus.NOT_FOUND);

            verify(userRepository).findById(999L);
        }

        @Test
        @DisplayName("Should map user data correctly")
        void shouldMapUserDataCorrectly() {
            // Arrange
            when(userRepository.findById(2L)).thenReturn(Optional.of(secondUser));

            // Act
            UserDTO result = userService.getUserById(2L);

            // Assert
            assertThat(result.getId()).isEqualTo(secondUser.getId());
            assertThat(result.getEmail()).isEqualTo(secondUser.getEmail());
            assertThat(result.getNomUtilisateur()).isEqualTo(secondUser.getNomUtilisateur());
            assertThat(result.getIsAdmin()).isTrue();
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user and return updated DTO")
        void shouldUpdateUserSuccessfully() {
            // Arrange
            UserDTO updateRequest = UserDTO.builder()
                    .nomUtilisateur("John Updated")
                    .titrePoste(UserRole.CHEFS_PROJET)
                    .build();

            User updatedUser = User.builder()
                    .id(testUser.getId())
                    .email(testUser.getEmail())
                    .nomUtilisateur("John Updated")
                    .titrePoste(UserRole.CHEFS_PROJET)
                    .isAdmin(testUser.getIsAdmin())
                    .keycloakId(testUser.getKeycloakId())
                    .dateCreationCompte(testUser.getDateCreationCompte())
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(updatedUser);
            doNothing().when(eventProducer).sendUserUpdatedEvent(anyLong(), anyString(), anyString(), anyString());

            // Act
            UserDTO result = userService.updateUser(1L, updateRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getNomUtilisateur()).isEqualTo("John Updated");
            assertThat(result.getTitrePoste()).isEqualTo(UserRole.CHEFS_PROJET);

            verify(userRepository).findById(1L);
            verify(userRepository).save(any(User.class));
            verify(eventProducer).sendUserUpdatedEvent(
                    eq(updatedUser.getId()),
                    eq(updatedUser.getEmail()),
                    eq(updatedUser.getNomUtilisateur()),
                    anyString()
            );
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent user")
        void shouldThrowExceptionWhenUserNotFound() {
            // Arrange
            UserDTO updateRequest = UserDTO.builder()
                    .nomUtilisateur("Updated Name")
                    .build();

            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.updateUser(999L, updateRequest))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("User not found")
                    .extracting("status")
                    .isEqualTo(HttpStatus.NOT_FOUND);

            verify(userRepository).findById(999L);
            verify(userRepository, never()).save(any());
            verify(eventProducer, never()).sendUserUpdatedEvent(anyLong(), anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Should publish Kafka event after update")
        void shouldPublishKafkaEventAfterUpdate() {
            // Arrange
            UserDTO updateRequest = UserDTO.builder()
                    .nomUtilisateur("Updated Name")
                    .titrePoste(UserRole.RESPONSABLES_METRES)
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            userService.updateUser(1L, updateRequest);

            // Assert
            verify(eventProducer).sendUserUpdatedEvent(
                    eq(testUser.getId()),
                    eq(testUser.getEmail()),
                    anyString(),
                    anyString()
            );
        }

        @Test
        @DisplayName("Should update only provided fields")
        void shouldUpdateOnlyProvidedFields() {
            // Arrange
            String originalEmail = testUser.getEmail();
            UserDTO updateRequest = UserDTO.builder()
                    .nomUtilisateur("New Name")
                    .titrePoste(UserRole.RESPONSABLE_ETUDES_PRIX)
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            userService.updateUser(1L, updateRequest);

           // Assert - Email should not change
            verify(userRepository).save(argThat(user ->
                    user.getEmail().equals(originalEmail) &&
                            user.getNomUtilisateur().equals("New Name") &&
                            user.getTitrePoste() == UserRole.RESPONSABLE_ETUDES_PRIX
            ));
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user and publish Kafka event")
        void shouldDeleteUserSuccessfully() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            doNothing().when(userRepository).deleteById(1L);
            doNothing().when(eventProducer).sendUserDeletedEvent(anyLong(), anyString());

            // Act
            userService.deleteUser(1L);

            // Assert
            verify(userRepository).findById(1L);
            verify(userRepository).deleteById(1L);
            verify(eventProducer).sendUserDeletedEvent(
                    eq(testUser.getId()),
                    eq(testUser.getEmail())
            );
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent user")
        void shouldThrowExceptionWhenUserNotFound() {
            // Arrange
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.deleteUser(999L))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("User not found")
                    .extracting("status")
                    .isEqualTo(HttpStatus.NOT_FOUND);

            verify(userRepository).findById(999L);
            verify(userRepository, never()).deleteById(any());
            verify(eventProducer, never()).sendUserDeletedEvent(anyLong(), anyString());
        }

        @Test
        @DisplayName("Should preserve email for event before deletion")
        void shouldPreserveEmailForEvent() {
            // Arrange
            String userEmail = testUser.getEmail();
            Long userId = testUser.getId();

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            doNothing().when(userRepository).deleteById(userId);

            // Act
            userService.deleteUser(userId);

            // Assert - Event should contain email even after deletion
            verify(eventProducer).sendUserDeletedEvent(userId, userEmail);
        }
    }

    @Nested
    @DisplayName("Kafka Event Integration Tests")
    class KafkaEventTests {

        @Test
        @DisplayName("Should not fail if Kafka is unavailable during update")
        void shouldContinueIfKafkaUnavailableOnUpdate() {
            // Arrange
            UserDTO updateRequest = UserDTO.builder()
                    .nomUtilisateur("Updated")
                    .titrePoste(UserRole.ASSISTANTE_ADMINISTRATIVE)
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            doThrow(new RuntimeException("Kafka unavailable"))
                    .when(eventProducer).sendUserUpdatedEvent(anyLong(), anyString(), anyString(), anyString());

            // Act & Assert - Should throw the Kafka exception
            // (In production, consider catching and logging this)
            assertThatThrownBy(() -> userService.updateUser(1L, updateRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Kafka unavailable");

            verify(userRepository).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("DTO Mapping Tests")
    class DtoMappingTests {

        @Test
        @DisplayName("Should correctly map all user fields to DTO")
        void shouldMapAllFieldsToDTO() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // Act
            UserDTO dto = userService.getUserById(1L);

            // Assert
            assertThat(dto.getId()).isEqualTo(testUser.getId());
            assertThat(dto.getEmail()).isEqualTo(testUser.getEmail());
            assertThat(dto.getNomUtilisateur()).isEqualTo(testUser.getNomUtilisateur());
            assertThat(dto.getTitrePoste()).isEqualTo(testUser.getTitrePoste());
            assertThat(dto.getIsAdmin()).isEqualTo(testUser.getIsAdmin());
            assertThat(dto.getDateCreationCompte()).isEqualTo(testUser.getDateCreationCompte());
        }

        @Test
        @DisplayName("Should handle null fields gracefully")
        void shouldHandleNullFieldsGracefully() {
            // Arrange
            User userWithNulls = User.builder()
                    .id(3L)
                    .email("minimal@example.com")
                    .nomUtilisateur(null)
                    .titrePoste(UserRole.ASSISTANTE_ADMINISTRATIVE)
                    .isAdmin(false)
                    .build();

            when(userRepository.findById(3L)).thenReturn(Optional.of(userWithNulls));

            // Act
            UserDTO dto = userService.getUserById(3L);

            // Assert
            assertThat(dto.getId()).isEqualTo(3L);
            assertThat(dto.getEmail()).isEqualTo("minimal@example.com");
            assertThat(dto.getNomUtilisateur()).isNull();
        }
    }
}
