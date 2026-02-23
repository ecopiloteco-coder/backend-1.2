package com.ecopilot.project.service;

import com.ecopilot.project.dto.ProjetDTO;
import com.ecopilot.project.entity.Client;
import com.ecopilot.project.entity.Projet;
import com.ecopilot.project.kafka.producer.ProjectEventProducer;
import com.ecopilot.project.repository.ClientRepository;
import com.ecopilot.project.repository.ProjetRepository;
import com.ecopilot.project.repository.ProjetLotRepository;
import com.ecopilot.project.repository.OuvrageRepository;
import com.ecopilot.project.repository.StructureRepository;
import com.ecopilot.project.repository.ProjetArticleRepository;
import com.ecopilot.project.repository.BlocRepository;
import com.ecopilot.project.repository.ProjetEquipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjetService Tests")
class ProjetServiceTest {

    @Mock
    private ProjetRepository projetRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ProjetLotRepository projetLotRepository;

    @Mock
    private OuvrageRepository ouvrageRepository;

    @Mock
    private StructureRepository structureRepository;

    @Mock
    private ProjetArticleRepository projetArticleRepository;

    @Mock
    private BlocRepository blocRepository;

    @Mock
    private ProjetEquipeRepository projetEquipeRepository;

    @Mock
    private ProjectEventProducer eventProducer;

    @Mock
    private jakarta.persistence.EntityManager entityManager;

    @InjectMocks
    private ProjetService projetService;

    private Projet projet;
    private ProjetDTO dto;

    @BeforeEach
    void setUp() {
        Client client = Client.builder()
                .id(10L)
                .build();

        projet = Projet.builder()
                .id(1L)
                .nomProjet("Projet A")
                .description("Desc")
                .etat("EN_COURS")
                .cout(1000.0)
                .dateDebut(LocalDate.of(2024, 1, 1))
                .dateLimite(LocalDate.of(2024, 12, 31))
                .ajoutePar("5")
                .adresse("Paris")
                .client(client)
                .build();

        dto = ProjetDTO.builder()
                .id(1L)
                .nomProjet("Projet A")
                .description("Desc")
                .etat("EN_COURS")
                .cout(1000.0)
                .dateDebut(LocalDate.of(2024, 1, 1))
                .dateLimite(LocalDate.of(2024, 12, 31))
                .clientId(10L)
                .ajoutePar("5")
                .adresse("Paris")
                .build();
    }

    @Test
    @DisplayName("getProjetById returns mapped DTO when project exists")
    void getProjetById_returnsDto() {
        when(projetRepository.findById(1L)).thenReturn(Optional.of(projet));

        ProjetDTO result = projetService.getProjetById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNomProjet()).isEqualTo("Projet A");
        verify(projetRepository).findById(1L);
    }

    @Test
    @DisplayName("getProjetById throws NOT_FOUND when project does not exist")
    void getProjetById_notFound() {
        when(projetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projetService.getProjetById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Projet not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("createProjet saves entity and publishes created event")
    void createProjet_publishesCreatedEvent() {
        when(clientRepository.findById(10L)).thenReturn(Optional.of(Client.builder().id(10L).build()));
        when(projetRepository.save(any(Projet.class))).thenAnswer(invocation -> {
            Projet p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        ProjetDTO result = projetService.createProjet(dto);

        assertThat(result.getId()).isNotNull();
        verify(projetRepository).save(any(Projet.class));
        verify(eventProducer).sendProjectCreatedEvent(
                eq(1L),
                eq("Projet A"),
                eq("5")
        );
    }

    @Test
    @DisplayName("updateProjet updates fields and publishes updated event")
    void updateProjet_updatesAndPublishesEvent() {
        when(projetRepository.findById(1L)).thenReturn(Optional.of(projet));
        when(clientRepository.findById(10L)).thenReturn(Optional.of(projet.getClient()));
        when(projetRepository.save(any(Projet.class))).thenReturn(projet);

        ProjetDTO update = ProjetDTO.builder()
                .nomProjet("Projet B")
                .description("New")
                .etat("TERMINE")
                .cout(2000.0)
                .clientId(10L)
                .ajoutePar("5")
                .adresse("Lyon")
                .build();

        ProjetDTO result = projetService.updateProjet(1L, update);

        assertThat(result.getNomProjet()).isEqualTo("Projet B");
        verify(projetRepository).save(any(Projet.class));
        verify(eventProducer).sendProjectUpdatedEvent(
                eq(1L),
                eq("Projet B"),
                eq("5")
        );
    }

    @Test
    @DisplayName("updateProjet throws NOT_FOUND when project does not exist")
    void updateProjet_notFound() {
        when(projetRepository.findById(99L)).thenReturn(Optional.empty());

        ProjetDTO update = ProjetDTO.builder()
                .nomProjet("X")
                .build();

        assertThatThrownBy(() -> projetService.updateProjet(99L, update))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Projet not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("deleteProjet deletes existing project")
    void deleteProjet_deletes() {
        when(projetRepository.findById(1L)).thenReturn(Optional.of(projet));

        projetService.deleteProjet(1L);

        verify(projetRepository).delete(projet);
    }

    @Test
    @DisplayName("deleteProjet throws NOT_FOUND when project does not exist")
    void deleteProjet_notFound() {
        when(projetRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projetService.deleteProjet(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Projet not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}

