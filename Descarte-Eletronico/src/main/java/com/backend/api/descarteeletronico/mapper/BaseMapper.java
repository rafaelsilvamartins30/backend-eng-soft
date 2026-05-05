package com.backend.api.descarteeletronico.mapper;

import org.mapstruct.MappingTarget;

import java.util.Set;

/** Contrato base para mappers MapStruct. */
public interface BaseMapper<ENTITY, REQUEST, RESPONSE> {

  /** Converte um request em entidade. */
  ENTITY toEntity(REQUEST request);

  /** Converte uma entidade em response. */
  RESPONSE toResponse(ENTITY entity);

  /** Converte um conjunto de entidades em responses. */
  Set<RESPONSE> toResponseSet(Set<ENTITY> entities);

  /** Atualiza uma entidade existente a partir do request. */
  void updateEntityFromRequest(REQUEST request, @MappingTarget ENTITY entity);
}
