package com.backend.api.descarteeletronico.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.mapper.TipoProdutoMapper;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.tipoproduto.TipoProduto;
import com.backend.api.descarteeletronico.model.tipoproduto.dto.TipoProdutoRequest;
import com.backend.api.descarteeletronico.model.tipoproduto.dto.TipoProdutoResponse;
import com.backend.api.descarteeletronico.repository.TipoProdutoRepository;
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
            id,
            request.nome(),
            request.descricaoExemplos(),
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
  }

  @Nested
  @Order(2)
  @DisplayName("Cenários de Consulta")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Consulta {

    @Test
    @Order(1)
    void findByIdReturnsMappedActiveEntity() {
      when(tipoProdutoRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
          .thenReturn(Optional.of(tipoProduto));
      when(tipoProdutoMapper.toResponse(tipoProduto)).thenReturn(response);

      TipoProdutoResponse result = tipoProdutoService.findById(id);

      assertThat(result).isEqualTo(response);
      verify(tipoProdutoRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
      verify(tipoProdutoMapper).toResponse(tipoProduto);
      verifyNoMoreInteractions(tipoProdutoRepository, tipoProdutoMapper);
    }

    @Test
    @Order(2)
    void findByIdThrowsWhenEntityDoesNotExist() {
      when(tipoProdutoRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> tipoProdutoService.findById(id))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Tipo de produto não encontrado");
      verify(tipoProdutoRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
      verifyNoInteractions(tipoProdutoMapper);
      verifyNoMoreInteractions(tipoProdutoRepository);
    }

    @Test
    @Order(3)
    void findAllReturnsOnlyActiveEntitiesAndMapsResponses() {
      Set<TipoProduto> tiposProduto = Set.of(tipoProduto);
      Set<TipoProdutoResponse> responses = Set.of(response);
      when(tipoProdutoRepository.findAllByEntityStatus(EntityStatus.ACTIVE)).thenReturn(tiposProduto);
      when(tipoProdutoMapper.toResponseSet(tiposProduto)).thenReturn(responses);

      Set<TipoProdutoResponse> result = tipoProdutoService.findAll();

      assertThat(result).isEqualTo(responses);
      verify(tipoProdutoRepository).findAllByEntityStatus(EntityStatus.ACTIVE);
      verify(tipoProdutoMapper).toResponseSet(tiposProduto);
      verifyNoMoreInteractions(tipoProdutoRepository, tipoProdutoMapper);
    }

    @Test
    @Order(4)
    void findAllWithEmptyResultDelegatesToMapper() {
      Set<TipoProduto> tiposProduto = Set.of();
      Set<TipoProdutoResponse> responses = Set.of();
      when(tipoProdutoRepository.findAllByEntityStatus(EntityStatus.ACTIVE)).thenReturn(tiposProduto);
      when(tipoProdutoMapper.toResponseSet(tiposProduto)).thenReturn(responses);

      Set<TipoProdutoResponse> result = tipoProdutoService.findAll();

      assertThat(result).isEmpty();
      verify(tipoProdutoRepository).findAllByEntityStatus(EntityStatus.ACTIVE);
      verify(tipoProdutoMapper).toResponseSet(tiposProduto);
      verifyNoMoreInteractions(tipoProdutoRepository, tipoProdutoMapper);
    }

    @Test
    @Order(5)
    void findAllPagedReturnsMappedPageOfActiveEntities() {
      Pageable pageable = PageRequest.of(0, 10);
      Page<TipoProduto> page = new PageImpl<>(List.of(tipoProduto));
      when(tipoProdutoRepository.findAllByEntityStatus(EntityStatus.ACTIVE, pageable)).thenReturn(page);
      when(tipoProdutoMapper.toResponse(tipoProduto)).thenReturn(response);

      Page<TipoProdutoResponse> result = tipoProdutoService.findAllPaged(pageable);

      assertThat(result.getContent()).containsExactly(response);
      verify(tipoProdutoRepository).findAllByEntityStatus(EntityStatus.ACTIVE, pageable);
      verify(tipoProdutoMapper).toResponse(tipoProduto);
      verifyNoMoreInteractions(tipoProdutoRepository, tipoProdutoMapper);
    }
  }

  @Nested
  @Order(3)
  @DisplayName("Cenários de Atualização")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Atualizacao {

    @Test
    @Order(1)
    void updateFindsActiveEntityAppliesMapperAndReturnsResponse() {
      when(tipoProdutoRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
          .thenReturn(Optional.of(tipoProduto));
      when(tipoProdutoRepository.save(tipoProduto)).thenReturn(tipoProduto);
      when(tipoProdutoMapper.toResponse(tipoProduto)).thenReturn(response);

      TipoProdutoResponse result = tipoProdutoService.update(id, request);

      assertThat(result).isEqualTo(response);
      verify(tipoProdutoRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
      verify(tipoProdutoMapper).updateEntityFromRequest(request, tipoProduto);
      verify(tipoProdutoRepository).save(tipoProduto);
      verify(tipoProdutoMapper).toResponse(tipoProduto);
      verifyNoMoreInteractions(tipoProdutoRepository, tipoProdutoMapper);
    }

    @Test
    @Order(2)
    void updateThrowsWhenEntityDoesNotExist() {
      when(tipoProdutoRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> tipoProdutoService.update(id, request))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Tipo de produto não encontrado");
      verify(tipoProdutoRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
      verify(tipoProdutoRepository, never()).save(tipoProduto);
      verifyNoInteractions(tipoProdutoMapper);
      verifyNoMoreInteractions(tipoProdutoRepository);
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
      when(tipoProdutoRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
          .thenReturn(Optional.of(tipoProduto));

      tipoProdutoService.delete(id);

      assertThat(tipoProduto.getEntityStatus()).isEqualTo(EntityStatus.DELETED);
      assertThat(tipoProduto.getDeletedAt()).isNotNull();
      verify(tipoProdutoRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
      verify(tipoProdutoRepository).save(tipoProduto);
      verifyNoInteractions(tipoProdutoMapper);
      verifyNoMoreInteractions(tipoProdutoRepository);
    }

    @Test
    @Order(2)
    void deleteThrowsWhenEntityDoesNotExist() {
      when(tipoProdutoRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> tipoProdutoService.delete(id))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Tipo de produto não encontrado");
      verify(tipoProdutoRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
      verify(tipoProdutoRepository, never()).save(tipoProduto);
      verifyNoInteractions(tipoProdutoMapper);
      verifyNoMoreInteractions(tipoProdutoRepository);
    }
  }
}
