package com.backend.api.descarteeletronico.mapper;

import com.backend.api.descarteeletronico.model.usuario.Usuario;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UsuarioMapper {

  @Override
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "entityStatus", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  Usuario toEntity(UsuarioRequest request);

  @Override
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "entityStatus", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  void updateEntityFromRequest(UsuarioRequest request, @MappingTarget Usuario entity);
}
