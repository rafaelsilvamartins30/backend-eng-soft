package com.backend.api.descarteeletronico.mapper;

import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import com.backend.api.descarteeletronico.model.pontocoleta.dto.PontoColetaRequest;
import com.backend.api.descarteeletronico.model.pontocoleta.dto.PontoColetaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", uses = TipoProdutoMapper.class)
public interface PontoColetaMapper
    extends BaseMapper<PontoColeta, PontoColetaRequest, PontoColetaResponse> {

  @Override
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "entityStatus", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "tiposProduto", ignore = true)
  PontoColeta toEntity(PontoColetaRequest request);

  @Override
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "entityStatus", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "tiposProduto", ignore = true)
  void updateEntityFromRequest(PontoColetaRequest request, @MappingTarget PontoColeta entity);

  @Override
  @Mapping(target = "aberto", expression = "java(calcularAberto(entity.getHorarioAbertura(), entity.getHorarioFechamento()))")
  @Mapping(target = "horarioFormatado", expression = "java(formatarHorario(entity.getHorarioAbertura(), entity.getHorarioFechamento()))")
  PontoColetaResponse toResponse(PontoColeta entity);

  default boolean calcularAberto(LocalTime abertura, LocalTime fechamento) {
    if (abertura == null || fechamento == null) {
      return false;
    }
    LocalTime agora = LocalTime.now(ZoneId.of("America/Sao_Paulo"));
    return !agora.isBefore(abertura) && agora.isBefore(fechamento);
  }

  default String formatarHorario(LocalTime abertura, LocalTime fechamento) {
    if (abertura == null || fechamento == null) {
      return "Horário indisponível";
    }
    return abertura.toString() + " às " + fechamento.toString();
  }
}
