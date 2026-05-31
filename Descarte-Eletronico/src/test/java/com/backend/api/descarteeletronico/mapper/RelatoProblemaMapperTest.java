package com.backend.api.descarteeletronico.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.model.enums.TipoRelato;
import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import com.backend.api.descarteeletronico.model.relato.RelatoProblema;
import com.backend.api.descarteeletronico.model.relato.dto.RelatoProblemaRequest;
import com.backend.api.descarteeletronico.model.relato.dto.RelatoProblemaResponse;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class RelatoProblemaMapperTest {

  private final RelatoProblemaMapper mapper = Mappers.getMapper(RelatoProblemaMapper.class);

  @Test
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
}
