package com.backend.api.descarteeletronico.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
            Set.of());
    request =
        new PontoColetaRequest(
            pontoColeta.getNome(),
            pontoColeta.getEndereco(),
            pontoColeta.getDescricao(),
            pontoColeta.getLatitude(),
            pontoColeta.getLongitude(),
            Set.of(tipoProdutoId));
    response =
        new PontoColetaResponse(
            id,
            request.nome(),
            request.endereco(),
            request.descricao(),
            request.latitude(),
            request.longitude(),
            Set.of(),
            0L,
            null,
            null,
            EntityStatus.ACTIVE,
            null);
  }

  @Test
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

  @Test
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

  @Test
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

  @Test
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
  void findAllReturnsOnlyActiveEntitiesAndMapsResponses() {
    Set<PontoColeta> pontosColeta = Set.of(pontoColeta);
    Set<PontoColetaResponse> responses = Set.of(response);
    when(pontoColetaRepository.findAllByEntityStatus(EntityStatus.ACTIVE))
        .thenReturn(pontosColeta);
    when(pontoColetaMapper.toResponseSet(pontosColeta)).thenReturn(responses);

    Set<PontoColetaResponse> result = pontoColetaService.findAll();

    assertThat(result).isEqualTo(responses);
    verify(pontoColetaRepository).findAllByEntityStatus(EntityStatus.ACTIVE);
    verify(pontoColetaMapper).toResponseSet(pontosColeta);
    verifyNoInteractions(tipoProdutoRepository);
    verifyNoMoreInteractions(pontoColetaRepository, pontoColetaMapper);
  }

  @Test
  void findAllWithEmptyResultDelegatesToMapper() {
    Set<PontoColeta> pontosColeta = Set.of();
    Set<PontoColetaResponse> responses = Set.of();
    when(pontoColetaRepository.findAllByEntityStatus(EntityStatus.ACTIVE))
        .thenReturn(pontosColeta);
    when(pontoColetaMapper.toResponseSet(pontosColeta)).thenReturn(responses);

    Set<PontoColetaResponse> result = pontoColetaService.findAll();

    assertThat(result).isEmpty();
    verify(pontoColetaRepository).findAllByEntityStatus(EntityStatus.ACTIVE);
    verify(pontoColetaMapper).toResponseSet(pontosColeta);
    verifyNoInteractions(tipoProdutoRepository);
    verifyNoMoreInteractions(pontoColetaRepository, pontoColetaMapper);
  }
}
