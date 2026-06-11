package com.backend.api.descarteeletronico.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.mapper.ExemploMapper;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.exemplo.Exemplo;
import com.backend.api.descarteeletronico.model.exemplo.dto.ExemploRequest;
import com.backend.api.descarteeletronico.model.exemplo.dto.ExemploResponse;
import com.backend.api.descarteeletronico.repository.ExemploRepository;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class ExemploServiceTest {

  @Mock private ExemploRepository exemploRepository;

  @Mock private ExemploMapper exemploMapper;

  @InjectMocks private ExemploService exemploService;

  private UUID id;
  private Exemplo exemplo;
  private ExemploRequest request;
  private ExemploResponse response;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    exemplo = new Exemplo("Coleta de notebook", "Equipamento antigo para descarte");
    request = new ExemploRequest("Coleta de notebook", "Equipamento antigo para descarte");
    response =
        new ExemploResponse(
            id, request.nome(), request.descricao(), 0L, null, null, EntityStatus.ACTIVE, null);
  }

  @Nested
  @Order(1)
  @DisplayName("Cenários de Cadastro")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class CadastroTests {

    @Test
    @Order(1)
    void createSavesActiveEntityAndReturnsResponse() {
      when(exemploMapper.toEntity(request)).thenReturn(exemplo);
      when(exemploRepository.save(exemplo)).thenReturn(exemplo);
      when(exemploMapper.toResponse(exemplo)).thenReturn(response);

      ExemploResponse result = exemploService.create(request);

      assertThat(result).isEqualTo(response);
      assertThat(exemplo.getEntityStatus()).isEqualTo(EntityStatus.ACTIVE);
      assertThat(exemplo.getDeletedAt()).isNull();
      verify(exemploMapper).toEntity(request);
      verify(exemploRepository).save(exemplo);
      verify(exemploMapper).toResponse(exemplo);
      verifyNoMoreInteractions(exemploRepository, exemploMapper);
    }
  }

  @Nested
  @Order(2)
  @DisplayName("Cenários de Consulta")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class ConsultaTests {

    @Test
    @Order(1)
    void findByIdReturnsMappedActiveEntity() {
      when(exemploRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
          .thenReturn(Optional.of(exemplo));
      when(exemploMapper.toResponse(exemplo)).thenReturn(response);

      ExemploResponse result = exemploService.findById(id);

      assertThat(result).isEqualTo(response);
      verify(exemploRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
      verify(exemploMapper).toResponse(exemplo);
      verifyNoMoreInteractions(exemploRepository, exemploMapper);
    }

    @Test
    @Order(2)
    void findByIdThrowsWhenEntityDoesNotExist() {
      when(exemploRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> exemploService.findById(id))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Exemplo não encontrado");
      verify(exemploRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
      verifyNoInteractions(exemploMapper);
      verifyNoMoreInteractions(exemploRepository);
    }

    @Test
    @Order(3)
    void findAllReturnsOnlyActiveEntitiesAndMapsResponses() {
      Set<Exemplo> exemplos = Set.of(exemplo);
      Set<ExemploResponse> responses = Set.of(response);
      when(exemploRepository.findAllByEntityStatus(EntityStatus.ACTIVE)).thenReturn(exemplos);
      when(exemploMapper.toResponseSet(exemplos)).thenReturn(responses);

      Set<ExemploResponse> result = exemploService.findAll();

      assertThat(result).isEqualTo(responses);
      verify(exemploRepository).findAllByEntityStatus(EntityStatus.ACTIVE);
      verify(exemploMapper).toResponseSet(exemplos);
      verifyNoMoreInteractions(exemploRepository, exemploMapper);
    }

    @Test
    @Order(4)
    void findAllWithEmptyResultDelegatesToMapper() {
      Set<Exemplo> exemplos = Set.of();
      Set<ExemploResponse> responses = Set.of();
      when(exemploRepository.findAllByEntityStatus(EntityStatus.ACTIVE)).thenReturn(exemplos);
      when(exemploMapper.toResponseSet(exemplos)).thenReturn(responses);

      Set<ExemploResponse> result = exemploService.findAll();

      assertThat(result).isEmpty();
      verify(exemploRepository).findAllByEntityStatus(EntityStatus.ACTIVE);
      verify(exemploMapper).toResponseSet(exemplos);
      verifyNoMoreInteractions(exemploRepository, exemploMapper);
    }
  }

  @Nested
  @Order(3)
  @DisplayName("Cenários de Atualização")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class AtualizacaoTests {

    @Test
    @Order(1)
    void updateFindsActiveEntityAppliesMapperAndReturnsResponse() {
      when(exemploRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
          .thenReturn(Optional.of(exemplo));
      when(exemploRepository.save(exemplo)).thenReturn(exemplo);
      when(exemploMapper.toResponse(exemplo)).thenReturn(response);

      ExemploResponse result = exemploService.update(id, request);

      assertThat(result).isEqualTo(response);
      verify(exemploRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
      verify(exemploMapper).updateEntityFromRequest(request, exemplo);
      verify(exemploRepository).save(exemplo);
      verify(exemploMapper).toResponse(exemplo);
      verifyNoMoreInteractions(exemploRepository, exemploMapper);
    }

    @Test
    @Order(2)
    void updateThrowsWhenEntityDoesNotExist() {
      when(exemploRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> exemploService.update(id, request))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Exemplo não encontrado");
      verify(exemploRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
      verify(exemploRepository, never()).save(exemplo);
      verifyNoInteractions(exemploMapper);
      verifyNoMoreInteractions(exemploRepository);
    }
  }

  @Nested
  @Order(4)
  @DisplayName("Cenários de Exclusão")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class ExclusaoTests {

    @Test
    @Order(1)
    void deleteMarksEntityAsDeletedAndPersistsSoftDelete() {
      when(exemploRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
          .thenReturn(Optional.of(exemplo));

      exemploService.delete(id);

      assertThat(exemplo.getEntityStatus()).isEqualTo(EntityStatus.DELETED);
      assertThat(exemplo.getDeletedAt()).isNotNull();
      verify(exemploRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
      verify(exemploRepository).save(exemplo);
      verifyNoInteractions(exemploMapper);
      verifyNoMoreInteractions(exemploRepository);
    }

    @Test
    @Order(2)
    void deleteThrowsWhenEntityDoesNotExist() {
      when(exemploRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> exemploService.delete(id))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Exemplo não encontrado");
      verify(exemploRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
      verify(exemploRepository, never()).save(exemplo);
      verifyNoInteractions(exemploMapper);
      verifyNoMoreInteractions(exemploRepository);
    }
  }
}
