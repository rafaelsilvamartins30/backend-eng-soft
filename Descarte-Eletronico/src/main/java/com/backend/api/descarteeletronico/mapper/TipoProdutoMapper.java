package com.backend.api.descarteeletronico.mapper;

import com.backend.api.descarteeletronico.model.tipoproduto.TipoProduto;
import com.backend.api.descarteeletronico.model.tipoproduto.dto.TipoProdutoRequest;
import com.backend.api.descarteeletronico.model.tipoproduto.dto.TipoProdutoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TipoProdutoMapper
    extends BaseMapper<TipoProduto, TipoProdutoRequest, TipoProdutoResponse> {

  @Override
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "entityStatus", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  TipoProduto toEntity(TipoProdutoRequest request);

  @Override
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "entityStatus", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  void updateEntityFromRequest(TipoProdutoRequest request, @MappingTarget TipoProduto entity);
}
