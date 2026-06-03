package com.backend.api.descarteeletronico.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.model.tipoproduto.TipoProduto;
import com.backend.api.descarteeletronico.model.tipoproduto.dto.TipoProdutoRequest;
import com.backend.api.descarteeletronico.model.tipoproduto.dto.TipoProdutoResponse;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class TipoProdutoMapperTest {

  private final TipoProdutoMapper mapper = Mappers.getMapper(TipoProdutoMapper.class);

  @Test
  void toEntityMapsRequestToEntityIgnoringTechnicalFields() {
    TipoProdutoRequest request = new TipoProdutoRequest("Baterias", "Descarte de baterias");

    TipoProduto entity = mapper.toEntity(request);

    assertThat(entity.getNome()).isEqualTo(request.nome());
    assertThat(entity.getDescricaoExemplos()).isEqualTo(request.descricaoExemplos());
    assertThat(entity.getId()).isNull();
    assertThat(entity.getCreatedAt()).isNull();
  }

  @Test
  void toResponseMapsEntityToResponse() {
    TipoProduto entity = new TipoProduto("Baterias", "Descarte de baterias");

    TipoProdutoResponse response = mapper.toResponse(entity);

    assertThat(response.nome()).isEqualTo(entity.getNome());
    assertThat(response.descricaoExemplos()).isEqualTo(entity.getDescricaoExemplos());
  }

  @Test
  void toResponseSetMapsEntitySetToResponseSet() {
    TipoProduto entity = new TipoProduto("Baterias", "Descarte de baterias");

    Set<TipoProdutoResponse> responseSet = mapper.toResponseSet(Set.of(entity));

    assertThat(responseSet).hasSize(1);
    assertThat(responseSet.iterator().next().nome()).isEqualTo(entity.getNome());
  }

  @Test
  void updateEntityFromRequestUpdatesOnlyNonTechnicalFields() {
    TipoProduto entity = new TipoProduto("Antigo", "Desc Antiga");
    TipoProdutoRequest request = new TipoProdutoRequest("Novo", "Desc Nova");

    mapper.updateEntityFromRequest(request, entity);

    assertThat(entity.getNome()).isEqualTo(request.nome());
    assertThat(entity.getDescricaoExemplos()).isEqualTo(request.descricaoExemplos());
  }
}
