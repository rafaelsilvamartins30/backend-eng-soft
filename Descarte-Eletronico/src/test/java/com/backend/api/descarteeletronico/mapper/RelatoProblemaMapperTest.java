package com.backend.api.descarteeletronico.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.model.enums.TipoRelato;
import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import com.backend.api.descarteeletronico.model.relato.RelatoProblema;
import com.backend.api.descarteeletronico.model.relato.dto.RelatoProblemaRequest;
import com.backend.api.descarteeletronico.model.relato.dto.RelatoProblemaResponse;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.mapstruct.factory.Mappers;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class RelatoProblemaMapperTest {

  private final RelatoProblemaMapper mapper = Mappers.getMapper(RelatoProblemaMapper.class);

  @Nested
  @Order(1)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class MapeamentoParaEntidade {

    @Test
    @Order(1)
    void toEntityMapsRequestToEntity() {
      RelatoProblemaRequest request =
          new RelatoProblemaRequest(TipoRelato.LIXEIRA_CHEIA, "João", "joao@test.com", "Cheio");

      RelatoProblema entity = mapper.toEntity(request);

      assertThat(entity.getTipoRelato()).isEqualTo(request.tipoRelato());
      assertThat(entity.getNome()).isEqualTo(request.nome());
      assertThat(entity.getEmail()).isEqualTo(request.email());
      assertThat(entity.getObservacao()).isEqualTo(request.observacao());
    }

    @Test
    @Order(2)
    void updateEntityFromRequestUpdatesFields() {
      RelatoProblema entity = new RelatoProblema();
      entity.setNome("Antigo");

      RelatoProblemaRequest request =
              new RelatoProblemaRequest(TipoRelato.LIXEIRA_DANIFICADA, "Novo", "novo@test.com", "Nova Obs");

      mapper.updateEntityFromRequest(request, entity);

      assertThat(entity.getNome()).isEqualTo("Novo");
      assertThat(entity.getTipoRelato()).isEqualTo(TipoRelato.LIXEIRA_DANIFICADA);
      assertThat(entity.getEmail()).isEqualTo("novo@test.com");
      assertThat(entity.getObservacao()).isEqualTo("Nova Obs");
    }
  }

  @Nested
  @Order(2)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class MapeamentoParaDTO {

    @Test
    @Order(1)
    void toResponseMapsEntityToResponseIncludingPontoColetaInfo() {
      PontoColeta pontoColeta = new PontoColeta();
      pontoColeta.setId(UUID.randomUUID());
      pontoColeta.setNome("Ponto Central");

      RelatoProblema entity = new RelatoProblema(pontoColeta, TipoRelato.OUTRO, "Maria", "maria@test.com", "Bom");

      RelatoProblemaResponse response = mapper.toResponse(entity);

      assertThat(response.nome()).isEqualTo(entity.getNome());
      assertThat(response.pontoColetaId()).isEqualTo(pontoColeta.getId());
      assertThat(response.pontoColetaNome()).isEqualTo(pontoColeta.getNome());
    }

    @Test
    @Order(2)
    void toResponseSetMapsEntitySetToResponseSet() {
      PontoColeta pontoColeta = new PontoColeta();
      pontoColeta.setId(UUID.randomUUID());
      pontoColeta.setNome("Ponto Central");

      RelatoProblema entity = new RelatoProblema(pontoColeta, TipoRelato.OUTRO, "Maria", "maria@test.com", "Bom");

      Set<RelatoProblemaResponse> responses = mapper.toResponseSet(Set.of(entity));

      assertThat(responses).hasSize(1);
      RelatoProblemaResponse response = responses.iterator().next();
      assertThat(response.nome()).isEqualTo(entity.getNome());
      assertThat(response.pontoColetaId()).isEqualTo(pontoColeta.getId());
    }

    @Test
    @Order(3)
    void toResponseWithNullPontoColeta() {
      RelatoProblema entity = new RelatoProblema(null, TipoRelato.OUTRO, "Maria", "maria@test.com", "Bom");

      RelatoProblemaResponse response = mapper.toResponse(entity);

      assertThat(response.pontoColetaId()).isNull();
      assertThat(response.pontoColetaNome()).isNull();
    }
  }
}
