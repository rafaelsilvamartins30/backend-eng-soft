package com.backend.api.descarteeletronico.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.backend.api.descarteeletronico.exception.BusinessException;
import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.mapper.PontoColetaMapper;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import com.backend.api.descarteeletronico.model.pontocoleta.dto.PontoColetaRequest;
import com.backend.api.descarteeletronico.model.pontocoleta.dto.PontoColetaResponse;
import com.backend.api.descarteeletronico.model.tipoproduto.TipoProduto;
import com.backend.api.descarteeletronico.repository.PontoColetaRepository;
import com.backend.api.descarteeletronico.repository.TipoProdutoRepository;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class PontoColetaServiceTest {

  @Mock private PontoColetaRepository pontoColetaRepository;

  @Mock private TipoProdutoRepository tipoProdutoRepository;

  @Mock private PontoColetaMapper pontoColetaMapper;

  @InjectMocks private PontoColetaService pontoColetaService;

  private UUID id;
  private UUID tipoProdutoId;
  private PontoColeta pontoColeta;
  private TipoProduto tipoProduto;
  private PontoColetaRequest request;
  private PontoColetaResponse response;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    tipoProdutoId = UUID.randomUUID();
    tipoProduto = new TipoProduto("Computadores", "Notebooks, desktops e monitores");
    pontoColeta =
        new PontoColeta(
            "EcoPonto Centro",
            "Rua das Flores, 123",
            "Recebe eletrônicos de pequeno porte",
            new BigDecimal("-23.5505200"),
            new BigDecimal("-46.6333080"),
            LocalTime.of(8, 0),
            LocalTime.of(18, 0),
            Set.of());
    request =
        new PontoColetaRequest(
            pontoColeta.getNome(),
            pontoColeta.getEndereco(),
            pontoColeta.getDescricao(),
            pontoColeta.getLatitude(),
            pontoColeta.getLongitude(),
            pontoColeta.getHorarioAbertura(),
            pontoColeta.getHorarioFechamento(),
            Set.of(tipoProdutoId));
    response =
        new PontoColetaResponse(
            id,
            request.nome(),
            request.endereco(),
            request.descricao(),
            request.latitude(),
            request.longitude(),
            request.horarioAbertura(),
            request.horarioFechamento(),
            true,
            "08:00 às 18:00",
            Set.of(),
            0L,
            null,
            null,
            EntityStatus.ACTIVE,
            null);
  }

  @Nested
  @Order(1)
  @DisplayName("Cenários de Cadastro")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Cadastro {

    @Test
    @Order(1)
    void createLinksActiveTiposProdutoSavesActiveEntityAndReturnsResponse() {
      Set<TipoProduto> tiposProduto = Set.of(tipoProduto);
      when(pontoColetaMapper.toEntity(request)).thenReturn(pontoColeta);
      when(tipoProdutoRepository.findAllByIdInAndEntityStatus(
              request.tipoProdutoIds(), EntityStatus.ACTIVE))
          .thenReturn(tiposProduto);
      when(pontoColetaRepository.save(pontoColeta)).thenReturn(pontoColeta);
      when(pontoColetaMapper.toResponse(pontoColeta)).thenReturn(response);

      PontoColetaResponse result = pontoColetaService.create(request);

      assertThat(result).isEqualTo(response);
      assertThat(pontoColeta.getTiposProduto()).isEqualTo(tiposProduto);
      assertThat(pontoColeta.getEntityStatus()).isEqualTo(EntityStatus.ACTIVE);
      assertThat(pontoColeta.getDeletedAt()).isNull();
      verify(pontoColetaMapper).toEntity(request);
      verify(tipoProdutoRepository)
          .findAllByIdInAndEntityStatus(request.tipoProdutoIds(), EntityStatus.ACTIVE);
      verify(pontoColetaRepository).save(pontoColeta);
      verify(pontoColetaMapper).toResponse(pontoColeta);
      verifyNoMoreInteractions(pontoColetaRepository, tipoProdutoRepository, pontoColetaMapper);
    }

    @Test
    @Order(2)
    void createThrowsWhenAnyTipoProdutoDoesNotExistOrIsInactive() {
      when(pontoColetaMapper.toEntity(request)).thenReturn(pontoColeta);
      when(tipoProdutoRepository.findAllByIdInAndEntityStatus(
              request.tipoProdutoIds(), EntityStatus.ACTIVE))
          .thenReturn(Set.of());

      assertThatThrownBy(() -> pontoColetaService.create(request))
          .isInstanceOf(BusinessException.class)
          .hasMessage("Informe apenas tipos de produto ativos e existentes");
      verify(pontoColetaMapper).toEntity(request);
      verify(tipoProdutoRepository)
          .findAllByIdInAndEntityStatus(request.tipoProdutoIds(), EntityStatus.ACTIVE);
      verify(pontoColetaRepository, never()).save(pontoColeta);
      verifyNoMoreInteractions(pontoColetaRepository, tipoProdutoRepository, pontoColetaMapper);
    }
  }

  @Nested
  @Order(2)
  @DisplayName("Cenários de Consulta")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Consulta {

    @Test
    @Order(1)
    void findByIdReturnsMappedActiveEntity() {
      when(pontoColetaRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
          .thenReturn(Optional.of(pontoColeta));
      when(pontoColetaMapper.toResponse(pontoColeta)).thenReturn(response);

      PontoColetaResponse result = pontoColetaService.findById(id);

      assertThat(result).isEqualTo(response);
      verify(pontoColetaRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
      verify(pontoColetaMapper).toResponse(pontoColeta);
      verifyNoInteractions(tipoProdutoRepository);
      verifyNoMoreInteractions(pontoColetaRepository, pontoColetaMapper);
    }

    @Test
    @Order(2)
    void findByIdThrowsWhenEntityDoesNotExist() {
      when(pontoColetaRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> pontoColetaService.findById(id))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Ponto de coleta não encontrado");
      verify(pontoColetaRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
      verifyNoInteractions(tipoProdutoRepository, pontoColetaMapper);
      verifyNoMoreInteractions(pontoColetaRepository);
    }

    @Test
    @Order(3)
    void findAllReturnsOnlyActiveEntitiesAndMapsResponses() {
      Set<PontoColeta> pontosColeta = Set.of(pontoColeta);
      Set<PontoColetaResponse> responses = Set.of(response);
      when(pontoColetaRepository.findAllByEntityStatus(EntityStatus.ACTIVE)).thenReturn(pontosColeta);
      when(pontoColetaMapper.toResponseSet(pontosColeta)).thenReturn(responses);

      Set<PontoColetaResponse> result = pontoColetaService.findAll();

      assertThat(result).isEqualTo(responses);
      verify(pontoColetaRepository).findAllByEntityStatus(EntityStatus.ACTIVE);
      verify(pontoColetaMapper).toResponseSet(pontosColeta);
      verifyNoInteractions(tipoProdutoRepository);
      verifyNoMoreInteractions(pontoColetaRepository, pontoColetaMapper);
    }

    @Test
    @Order(4)
    void findAllWithEmptyResultDelegatesToMapper() {
      Set<PontoColeta> pontosColeta = Set.of();
      Set<PontoColetaResponse> responses = Set.of();
      when(pontoColetaRepository.findAllByEntityStatus(EntityStatus.ACTIVE)).thenReturn(pontosColeta);
      when(pontoColetaMapper.toResponseSet(pontosColeta)).thenReturn(responses);

      Set<PontoColetaResponse> result = pontoColetaService.findAll();

      assertThat(result).isEmpty();
      verify(pontoColetaRepository).findAllByEntityStatus(EntityStatus.ACTIVE);
      verify(pontoColetaMapper).toResponseSet(pontosColeta);
      verifyNoInteractions(tipoProdutoRepository);
      verifyNoMoreInteractions(pontoColetaRepository, pontoColetaMapper);
    }

    @Test
    @Order(5)
    void findAllPagedReturnsMappedPageOfActiveEntities() {
      Pageable pageable = PageRequest.of(0, 10);
      Page<PontoColeta> page = new PageImpl<>(List.of(pontoColeta));
      when(pontoColetaRepository.findAllByEntityStatus(EntityStatus.ACTIVE, pageable)).thenReturn(page);
      when(pontoColetaMapper.toResponse(pontoColeta)).thenReturn(response);

      Page<PontoColetaResponse> result = pontoColetaService.findAllPaged(pageable);

      assertThat(result.getContent()).containsExactly(response);
      verify(pontoColetaRepository).findAllByEntityStatus(EntityStatus.ACTIVE, pageable);
      verify(pontoColetaMapper).toResponse(pontoColeta);
      verifyNoMoreInteractions(pontoColetaRepository, pontoColetaMapper);
    }
  }

  @Nested
  @Order(3)
  @DisplayName("Cenários de Atualização")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Atualizacao {

    @Test
    @Order(1)
    void updateFindsActiveEntityUpdatesFieldsAndReplacesTiposProduto() {
      Set<TipoProduto> tiposProduto = Set.of(tipoProduto);
      when(pontoColetaRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
          .thenReturn(Optional.of(pontoColeta));
      when(tipoProdutoRepository.findAllByIdInAndEntityStatus(
              request.tipoProdutoIds(), EntityStatus.ACTIVE))
          .thenReturn(tiposProduto);
      when(pontoColetaRepository.save(pontoColeta)).thenReturn(pontoColeta);
      when(pontoColetaMapper.toResponse(pontoColeta)).thenReturn(response);

      PontoColetaResponse result = pontoColetaService.update(id, request);

      assertThat(result).isEqualTo(response);
      assertThat(pontoColeta.getTiposProduto()).isEqualTo(tiposProduto);
      verify(pontoColetaRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
      verify(pontoColetaMapper).updateEntityFromRequest(request, pontoColeta);
      verify(tipoProdutoRepository)
          .findAllByIdInAndEntityStatus(request.tipoProdutoIds(), EntityStatus.ACTIVE);
      verify(pontoColetaRepository).save(pontoColeta);
      verify(pontoColetaMapper).toResponse(pontoColeta);
      verifyNoMoreInteractions(pontoColetaRepository, tipoProdutoRepository, pontoColetaMapper);
    }

    @Test
    @Order(2)
    void updateThrowsWhenEntityDoesNotExist() {
      when(pontoColetaRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> pontoColetaService.update(id, request))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Ponto de coleta não encontrado");
      verify(pontoColetaRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
      verify(pontoColetaRepository, never()).save(pontoColeta);
      verifyNoInteractions(tipoProdutoRepository, pontoColetaMapper);
      verifyNoMoreInteractions(pontoColetaRepository);
    }

    @Test
    @Order(3)
    void updateThrowsWhenAnyTipoProdutoDoesNotExistOrIsInactive() {
      when(pontoColetaRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
          .thenReturn(Optional.of(pontoColeta));
      when(tipoProdutoRepository.findAllByIdInAndEntityStatus(
              request.tipoProdutoIds(), EntityStatus.ACTIVE))
          .thenReturn(Set.of());

      assertThatThrownBy(() -> pontoColetaService.update(id, request))
          .isInstanceOf(BusinessException.class)
          .hasMessage("Informe apenas tipos de produto ativos e existentes");
      verify(pontoColetaRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
      verify(pontoColetaMapper).updateEntityFromRequest(request, pontoColeta);
      verify(tipoProdutoRepository)
          .findAllByIdInAndEntityStatus(request.tipoProdutoIds(), EntityStatus.ACTIVE);
      verify(pontoColetaRepository, never()).save(pontoColeta);
      verifyNoMoreInteractions(pontoColetaRepository, tipoProdutoRepository, pontoColetaMapper);
    }
  }

  @Nested
  @Order(4)
  @DisplayName("Cenários de Exclusão")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Exclusao {

    @Test
    @Order(1)
    void deleteMarksEntityAsDeletedAndPersistsSoftDelete() {
      when(pontoColetaRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
          .thenReturn(Optional.of(pontoColeta));

      pontoColetaService.delete(id);

      assertThat(pontoColeta.getEntityStatus()).isEqualTo(EntityStatus.DELETED);
      assertThat(pontoColeta.getDeletedAt()).isNotNull();
      verify(pontoColetaRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
      verify(pontoColetaRepository).save(pontoColeta);
      verifyNoInteractions(tipoProdutoRepository, pontoColetaMapper);
      verifyNoMoreInteractions(pontoColetaRepository);
    }

    @Test
    @Order(2)
    void deleteThrowsWhenEntityDoesNotExist() {
      when(pontoColetaRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> pontoColetaService.delete(id))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Ponto de coleta não encontrado");
      verify(pontoColetaRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
      verify(pontoColetaRepository, never()).save(pontoColeta);
      verifyNoInteractions(tipoProdutoRepository, pontoColetaMapper);
      verifyNoMoreInteractions(pontoColetaRepository);
    }
  }
}
