package com.backend.api.descarteeletronico.mapper;

import com.backend.api.descarteeletronico.model.notificacao.Notificacao;
import com.backend.api.descarteeletronico.model.notificacao.dto.NotificacaoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificacaoMapper {

  @Mapping(target = "pontoColetaId", source = "pontoColeta.id")
  @Mapping(target = "pontoColetaNome", source = "pontoColeta.nome")
  @Mapping(target = "relatoProblemaId", source = "relatoProblema.id")
  NotificacaoResponse toResponse(Notificacao notificacao);

  java.util.Set<NotificacaoResponse> toResponseSet(java.util.Set<Notificacao> notificacoes);
}