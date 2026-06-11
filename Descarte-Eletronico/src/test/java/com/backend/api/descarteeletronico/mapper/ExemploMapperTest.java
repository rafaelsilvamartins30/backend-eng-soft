package com.backend.api.descarteeletronico.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.model.exemplo.Exemplo;
import com.backend.api.descarteeletronico.model.exemplo.dto.ExemploRequest;
import com.backend.api.descarteeletronico.model.exemplo.dto.ExemploResponse;
import java.util.Set;
import org.junit.jupiter.api.*;
import org.mapstruct.factory.Mappers;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class ExemploMapperTest {

  private final ExemploMapper mapper = Mappers.getMapper(ExemploMapper.class);

  @Nested
  @Order(1)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class MapeamentoParaEntidade {

    @Test
    @Order(1)
    void toEntityMapsRequestToEntity() {
      ExemploRequest request = new ExemploRequest("Nome", "Descricao");

      Exemplo entity = mapper.toEntity(request);

      assertThat(entity.getNome()).isEqualTo(request.nome());
      assertThat(entity.getDescricao()).isEqualTo(request.descricao());
    }

    @Test
    @Order(2)
    void updateEntityFromRequestUpdatesFields() {
      Exemplo entity = new Exemplo();
      entity.setNome("Antigo");

      ExemploRequest request = new ExemploRequest("Novo", "Nova Desc");

      mapper.updateEntityFromRequest(request, entity);

      assertThat(entity.getNome()).isEqualTo("Novo");
      assertThat(entity.getDescricao()).isEqualTo("Nova Desc");
    }
  }

  @Nested
  @Order(2)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class MapeamentoParaDTO {

    @Test
    @Order(1)
    void toResponseMapsEntityToResponse() {
      Exemplo entity = new Exemplo();
      entity.setNome("Nome");
      entity.setDescricao("Descricao");

      ExemploResponse response = mapper.toResponse(entity);

      assertThat(response.nome()).isEqualTo(entity.getNome());
      assertThat(response.descricao()).isEqualTo(entity.getDescricao());
    }

    @Test
    @Order(2)
    void toResponseSetMapsSet() {
      Exemplo entity = new Exemplo();
      entity.setNome("Nome");

      Set<ExemploResponse> responses = mapper.toResponseSet(Set.of(entity));

      assertThat(responses).hasSize(1);
      assertThat(responses.iterator().next().nome()).isEqualTo("Nome");
    }
  }
}
