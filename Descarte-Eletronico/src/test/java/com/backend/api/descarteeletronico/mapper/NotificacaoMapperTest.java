package com.backend.api.descarteeletronico.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.model.enums.TipoRelato;
import com.backend.api.descarteeletronico.model.notificacao.Notificacao;
import com.backend.api.descarteeletronico.model.notificacao.dto.NotificacaoResponse;
import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import com.backend.api.descarteeletronico.model.relato.RelatoProblema;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class NotificacaoMapperTest {

  private final NotificacaoMapper mapper = Mappers.getMapper(NotificacaoMapper.class);

  @Test
  void toResponseMapsNotificacaoToResponse() {
    PontoColeta pontoColeta = new PontoColeta();
    pontoColeta.setId(UUID.randomUUID());
    pontoColeta.setNome("Ponto A");

    RelatoProblema relato = new RelatoProblema(pontoColeta, TipoRelato.OUTRO, "Autor", "email", "msg");
    relato.setId(UUID.randomUUID());

    Notificacao notificacao = new Notificacao("Título", "Mensagem", pontoColeta, relato);

    NotificacaoResponse response = mapper.toResponse(notificacao);

    assertThat(response.titulo()).isEqualTo(notificacao.getTitulo());
    assertThat(response.pontoColetaId()).isEqualTo(pontoColeta.getId());
    assertThat(response.pontoColetaNome()).isEqualTo(pontoColeta.getNome());
    assertThat(response.relatoProblemaId()).isEqualTo(relato.getId());
  }
}
