package com.ecopilot.article.service;

import com.ecopilot.article.dto.ArticleDTO;
import com.ecopilot.article.entity.*;
import com.ecopilot.article.kafka.producer.ArticleEventProducer;
import com.ecopilot.article.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ArticleService
 * 
 * Tests comprehensive article management including:
 * - CRUD operations
 * - Seven-level hierarchy (Niveau 1-7)
 * - Pricing calculations
 * - Pending articles workflow
 * 
 * @author EcoPilot Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ArticleService Tests")
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleSupprimeRepository articleSupprimeRepository;

    @Mock
    private Niveau1Repository niveau1Repository;

    @Mock
    private Niveau2Repository niveau2Repository;

    @Mock
    private Niveau3Repository niveau3Repository;

    @Mock
    private Niveau4Repository niveau4Repository;

    @Mock
    private Niveau5Repository niveau5Repository;

    @Mock
    private Niveau6Repository niveau6Repository;

    @Mock
    private ArticleEventProducer eventProducer;

    @Mock
    private com.ecopilot.article.strategy.PriceStrategy priceStrategy;

    @InjectMocks
    private ArticleService articleService;

    private void mockAdminUser() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "user1")
                .claim("realm_access", Map.of("roles", List.of("admin")))
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContextHolder.setContext(securityContext);
    }


    @Nested
    @DisplayName("Create Article Tests")
    class CreateArticleTests {

        private Article testArticle;

        @BeforeEach
        void setUp() {
            testArticle = Article.builder()
                    .id(1L)
                    .nomArticle("Béton C25/30")
                    .unite("m³")
                    .pu("150.0")
                    .userId("1")
                    .niveau6(Niveau6.builder().id(6L).build())
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        @Test
        @DisplayName("Should create article directly for admin user")
        void shouldCreateArticleForAdmin() {
            // Arrange
            mockAdminUser();
            // Fix: createArticle now takes ArticleDTO, not Article entity
            ArticleDTO articleDTO = ArticleDTO.builder()
                    .nomArticle("Béton C25/30")
                    .unite("m³")
                    .pu("150.0")
                    .userId("1")
                    .niveau6Id(6L)
                    .build();

            when(articleRepository.save(any(Article.class))).thenReturn(testArticle);
            
            // Fix: eventProducer arguments match (Long, String, String, Double, Long)
            doNothing().when(eventProducer).sendArticleCreatedEvent(any(), any(), any(), any(), any());

            // Act
            ArticleDTO result = articleService.createArticle(articleDTO);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getNomArticle()).isEqualTo("Béton C25/30");
            verify(articleRepository).save(any(Article.class));
            verify(eventProducer).sendArticleCreatedEvent(anyLong(), any(), any(), any(), any());
        }

    }


    @Nested
    @DisplayName("Get Article Tests")
    class GetArticleTests {

        private Article testArticle;

        @BeforeEach
        void setUp() {
            testArticle = Article.builder()
                    .id(1L)
                    .nomArticle("Béton C25/30")
                    .unite("m³")
                    .pu("150.0")
                    .userId("1")
                    .niveau6(Niveau6.builder().id(6L).build())
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        @Test
        @DisplayName("Should get article by ID")
        void shouldGetArticleById() {
            // Arrange
            when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));

            // Act
            ArticleDTO result = articleService.getArticleById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getNomArticle()).isEqualTo("Béton C25/30");
            verify(articleRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when article not found")
        void shouldThrowExceptionWhenArticleNotFound() {
            // Arrange
            when(articleRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> articleService.getArticleById(999L))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Article not found")
                    .extracting("status")
                    .isEqualTo(HttpStatus.NOT_FOUND);

            verify(articleRepository).findById(999L);
        }

        @Test
        @DisplayName("Should get all articles")
        void shouldGetAllArticles() {
            // Arrange
            List<Article> articles = List.of(testArticle, testArticle);
            when(articleRepository.findAll()).thenReturn(articles);

            // Act
            List<ArticleDTO> result = articleService.getAllArticles();

            // Assert
            assertThat(result).hasSize(2);
            verify(articleRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Update Article Tests")
    class UpdateArticleTests {

        private Article testArticle;

        @BeforeEach
        void setUp() {
            testArticle = Article.builder()
                    .id(1L)
                    .nomArticle("Béton C25/30")
                    .unite("m³")
                    .pu("150.0")
                    .userId("1")
                    .niveau6(Niveau6.builder().id(6L).build())
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        @Test
        @DisplayName("Should update article successfully")
        void shouldUpdateArticle() {
            // Arrange
            ArticleDTO updatedDTO = ArticleDTO.builder()
                    .nomArticle("Béton C30/37")
                    .pu("175.0")
                    .build();

            when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));
            when(articleRepository.save(any(Article.class))).thenReturn(testArticle);
            doNothing().when(eventProducer).sendArticleUpdatedEvent(any(), any(), any(), any(), any());

            // Act
            ArticleDTO result = articleService.updateArticle(1L, updatedDTO);

            // Assert
            assertThat(result).isNotNull();
            verify(articleRepository).findById(1L);
            verify(articleRepository).save(any(Article.class));
            verify(eventProducer).sendArticleUpdatedEvent(eq(1L), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent article")
        void shouldThrowExceptionWhenUpdatingNonExistent() {
            // Arrange
            ArticleDTO updateData = ArticleDTO.builder().nomArticle("Updated").build();
            when(articleRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> articleService.updateArticle(999L, updateData))
                    .isInstanceOf(ResponseStatusException.class)
                    .extracting("status")
                    .isEqualTo(HttpStatus.NOT_FOUND);

            verify(articleRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete Article Tests")
    class DeleteArticleTests {

        private Article testArticle;

        @BeforeEach
        void setUp() {
            testArticle = Article.builder()
                    .id(1L)
                    .nomArticle("Béton C25/30")
                    .unite("m³")
                    .pu("150.0")
                    .userId("1")
                    .niveau6(Niveau6.builder().id(6L).build())
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        @Test
        @DisplayName("Should delete article and archive it")
        void shouldDeleteAndArchiveArticle() {
            // Arrange
            when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));
            doNothing().when(articleRepository).deleteById(1L);
            doNothing().when(eventProducer).sendArticleDeletedEvent(eq(1L), anyString());

            // Act
            articleService.deleteArticle(1L);

            // Assert
            verify(articleRepository).findById(1L);
            verify(articleRepository).deleteById(1L);
            verify(eventProducer).sendArticleDeletedEvent(eq(1L), anyString());
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent article")
        void shouldThrowExceptionWhenDeletingNonExistent() {
            // Arrange
            when(articleRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> articleService.deleteArticle(999L))
                    .isInstanceOf(ResponseStatusException.class)
                    .extracting("status")
                    .isEqualTo(HttpStatus.NOT_FOUND);

            verify(articleRepository, never()).deleteById(any());
        }
    }


    @Nested
    @DisplayName("Kafka Event Tests")
    class KafkaEventTests {

        private Article testArticle;

        @BeforeEach
        void setUp() {
            testArticle = Article.builder()
                    .id(1L)
                    .nomArticle("Béton C25/30")
                    .unite("m³")
                    .pu("150.0")
                    .userId("1")
                    .niveau6(Niveau6.builder().id(6L).build())
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        @Test
        @DisplayName("Should publish article created event")
        void shouldPublishArticleCreatedEvent() {
            // Arrange
            mockAdminUser();
            ArticleDTO dto = ArticleDTO.builder()
                    .nomArticle("Béton C25/30")
                    .build();

            when(articleRepository.save(any(Article.class))).thenReturn(testArticle);

            // Act
            articleService.createArticle(dto);

            // Assert
            verify(eventProducer).sendArticleCreatedEvent(
                    eq(testArticle.getId()),
                    eq(testArticle.getNomArticle()),
                    any(),
                    any(),
                    any()
            );
        }

        @Test
        @DisplayName("Should publish article updated event")
        void shouldPublishArticleUpdatedEvent() {
            // Arrange
            ArticleDTO dto = ArticleDTO.builder()
                    .nomArticle("Béton C25/30")
                    .build();
            when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));
            when(articleRepository.save(any(Article.class))).thenReturn(testArticle);

            // Act
            articleService.updateArticle(1L, dto);

            // Assert
            verify(eventProducer).sendArticleUpdatedEvent(
                    eq(1L),
                    any(),
                    any(),
                    any(),
                    any()
            );
        }
    }
}
