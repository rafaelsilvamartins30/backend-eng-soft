package com.backend.api.descarteeletronico.mapper;

import com.backend.api.descarteeletronico.model.feedback.Feedback;
import com.backend.api.descarteeletronico.model.feedback.dto.FeedbackRequest;
import com.backend.api.descarteeletronico.model.feedback.dto.FeedbackResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FeedbackMapper extends BaseMapper<Feedback, FeedbackRequest, FeedbackResponse> {

  @Override
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "entityStatus", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "pontoColeta", ignore = true)
  Feedback toEntity(FeedbackRequest request);

  @Override
  @Mapping(target = "pontoColetaId", source = "pontoColeta.id")
  @Mapping(target = "pontoColetaNome", source = "pontoColeta.nome")
  FeedbackResponse toResponse(Feedback entity);

  @Override
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "entityStatus", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "pontoColeta", ignore = true)
  void updateEntityFromRequest(FeedbackRequest request, @MappingTarget Feedback entity);
}
