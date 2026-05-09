package com.backend.api.descarteeletronico.service;

import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.mapper.TipoProdutoMapper;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.tipoproduto.TipoProduto;
import com.backend.api.descarteeletronico.model.tipoproduto.dto.TipoProdutoRequest;
import com.backend.api.descarteeletronico.model.tipoproduto.dto.TipoProdutoResponse;
import com.backend.api.descarteeletronico.repository.TipoProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class TipoProdutoServiceTest {

  @Mock private TipoProdutoRepository tipoProdutoRepository;

  @Mock private TipoProdutoMapper tipoProdutoMapper;

  @InjectMocks private TipoProdutoService tipoProdutoService;

  private UUID id;
  private TipoProduto tipoProduto;
  private TipoProdutoRequest request;
  private TipoProdutoResponse response;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    tipoProduto = new TipoProduto("Computadores", "Notebooks, desktops e monitores");
    request = new TipoProdutoRequest("Computadores", "Notebooks, desktops e monitores");
    response =
        new TipoProdutoResponse(
            id, request.nome(), request.descricaoExemplos(), 0L, null, null, EntityStatus.ACTIVE, null);
  }

  @Test
  void createSavesActiveEntityAndReturnsResponse() {
    when(tipoProdutoMapper.toEntity(request)).thenReturn(tipoProduto);
    when(tipoProdutoRepository.save(tipoProduto)).thenReturn(tipoProduto);
    when(tipoProdutoMapper.toResponse(tipoProduto)).thenReturn(response);

    TipoProdutoResponse result = tipoProdutoService.create(request);

    assertThat(result).isEqualTo(response);
    assertThat(tipoProduto.getEntityStatus()).isEqualTo(EntityStatus.ACTIVE);
    assertThat(tipoProduto.getDeletedAt()).isNull();
    verify(tipoProdutoMapper).toEntity(request);
    verify(tipoProdutoRepository).save(tipoProduto);
    verify(tipoProdutoMapper).toResponse(tipoProduto);
    verifyNoMoreInteractions(tipoProdutoRepository, tipoProdutoMapper);
  }

  @Test
  void updateFindsActiveEntityAppliesMapperAndReturnsResponse() {
    when(tipoProdutoRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED))
        .thenReturn(Optional.of(tipoProduto));
    when(tipoProdutoRepository.save(tipoProduto)).thenReturn(tipoProduto);
    when(tipoProdutoMapper.toResponse(tipoProduto)).thenReturn(response);

    TipoProdutoResponse result = tipoProdutoService.update(id, request);

    assertThat(result).isEqualTo(response);
    verify(tipoProdutoRepository).findByIdAndEntityStatusNot(id, EntityStatus.DELETED);
    verify(tipoProdutoMapper).updateEntityFromRequest(request, tipoProduto);
    verify(tipoProdutoRepository).save(tipoProduto);
    verify(tipoProdutoMapper).toResponse(tipoProduto);
    verifyNoMoreInteractions(tipoProdutoRepository, tipoProdutoMapper);
  }

  @Test
  void deleteMarksEntityAsDeletedAndPersistsSoftDelete() {
    when(tipoProdutoRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED))
        .thenReturn(Optional.of(tipoProduto));

    tipoProdutoService.delete(id);

    assertThat(tipoProduto.getEntityStatus()).isEqualTo(EntityStatus.DELETED);
    assertThat(tipoProduto.getDeletedAt()).isNotNull();
    verify(tipoProdutoRepository).findByIdAndEntityStatusNot(id, EntityStatus.DELETED);
    verify(tipoProdutoRepository).save(tipoProduto);
    verifyNoInteractions(tipoProdutoMapper);
    verifyNoMoreInteractions(tipoProdutoRepository);
  }

  @Test
  void findByIdReturnsMappedActiveEntity() {
    when(tipoProdutoRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED))
        .thenReturn(Optional.of(tipoProduto));
    when(tipoProdutoMapper.toResponse(tipoProduto)).thenReturn(response);

    TipoProdutoResponse result = tipoProdutoService.findById(id);

    assertThat(result).isEqualTo(response);
    verify(tipoProdutoRepository).findByIdAndEntityStatusNot(id, EntityStatus.DELETED);
    verify(tipoProdutoMapper).toResponse(tipoProduto);
    verifyNoMoreInteractions(tipoProdutoRepository, tipoProdutoMapper);
  }

  @Test
  void findByIdThrowsWhenEntityDoesNotExist() {
    when(tipoProdutoRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> tipoProdutoService.findById(id))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Tipo de produto não encontrado");
    verify(tipoProdutoRepository).findByIdAndEntityStatusNot(id, EntityStatus.DELETED);
    verifyNoInteractions(tipoProdutoMapper);
    verifyNoMoreInteractions(tipoProdutoRepository);
  }

  @Test
  void deleteThrowsWhenEntityDoesNotExist() {
    when(tipoProdutoRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> tipoProdutoService.delete(id))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Tipo de produto não encontrado");
    verify(tipoProdutoRepository).findByIdAndEntityStatusNot(id, EntityStatus.DELETED);
    verify(tipoProdutoRepository, never()).save(tipoProduto);
    verifyNoInteractions(tipoProdutoMapper);
    verifyNoMoreInteractions(tipoProdutoRepository);
  }

  @Test
  void findAllIgnoresDeletedEntitiesAndMapsResponses() {
    Set<TipoProduto> tiposProduto = Set.of(tipoProduto);
    Set<TipoProdutoResponse> responses = Set.of(response);
    when(tipoProdutoRepository.findAllByEntityStatusNot(EntityStatus.DELETED))
        .thenReturn(tiposProduto);
    when(tipoProdutoMapper.toResponseSet(tiposProduto)).thenReturn(responses);

    Set<TipoProdutoResponse> result = tipoProdutoService.findAll();

    assertThat(result).isEqualTo(responses);
    verify(tipoProdutoRepository).findAllByEntityStatusNot(EntityStatus.DELETED);
    verify(tipoProdutoMapper).toResponseSet(tiposProduto);
    verifyNoMoreInteractions(tipoProdutoRepository, tipoProdutoMapper);
  }

  @Test
  void findAllWithEmptyResultDelegatesToMapper() {
    Set<TipoProduto> tiposProduto = Set.of();
    Set<TipoProdutoResponse> responses = Set.of();
    when(tipoProdutoRepository.findAllByEntityStatusNot(EntityStatus.DELETED))
        .thenReturn(tiposProduto);
    when(tipoProdutoMapper.toResponseSet(tiposProduto)).thenReturn(responses);

    Set<TipoProdutoResponse> result = tipoProdutoService.findAll();

    assertThat(result).isEmpty();
    verify(tipoProdutoRepository).findAllByEntityStatusNot(EntityStatus.DELETED);
    verify(tipoProdutoMapper).toResponseSet(tiposProduto);
    verifyNoMoreInteractions(tipoProdutoRepository, tipoProdutoMapper);
  }

  @Test
  void updateThrowsWhenEntityDoesNotExist() {
    when(tipoProdutoRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> tipoProdutoService.update(id, request))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Tipo de produto não encontrado");
    verify(tipoProdutoRepository).findByIdAndEntityStatusNot(id, EntityStatus.DELETED);
    verify(tipoProdutoRepository, never()).save(tipoProduto);
    verifyNoInteractions(tipoProdutoMapper);
    verifyNoMoreInteractions(tipoProdutoRepository);
  }
}
