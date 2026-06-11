package com.backend.api.descarteeletronico.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.model.enums.TipoRelato;
import com.backend.api.descarteeletronico.model.notificacao.Notificacao;
import com.backend.api.descarteeletronico.model.notificacao.dto.NotificacaoResponse;
import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import com.backend.api.descarteeletronico.model.relato.RelatoProblema;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.mapstruct.factory.Mappers;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class NotificacaoMapperTest {

  private final NotificacaoMapper mapper = Mappers.getMapper(NotificacaoMapper.class);

  @Nested
  @Order(1)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class MapeamentoParaDTO {

    @Test
    @Order(1)
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

    @Test
    @Order(2)
    void toResponseSetMapsSet() {
      PontoColeta pontoColeta = new PontoColeta();
      pontoColeta.setId(UUID.randomUUID());
      pontoColeta.setNome("Ponto A");

      RelatoProblema relato = new RelatoProblema(pontoColeta, TipoRelato.OUTRO, "Autor", "email", "msg");
      relato.setId(UUID.randomUUID());

      Notificacao notificacao = new Notificacao("Título", "Mensagem", pontoColeta, relato);

      Set<NotificacaoResponse> responses = mapper.toResponseSet(Set.of(notificacao));

      assertThat(responses).hasSize(1);
      assertThat(responses.iterator().next().titulo()).isEqualTo("Título");
    }

    @Test
    @Order(3)
    void toResponseWithNullFields() {
      Notificacao notificacao = new Notificacao("Título", "Mensagem", null, null);

      NotificacaoResponse response = mapper.toResponse(notificacao);

      assertThat(response.titulo()).isEqualTo("Título");
      assertThat(response.pontoColetaId()).isNull();
      assertThat(response.relatoProblemaId()).isNull();
    }
  }
}
