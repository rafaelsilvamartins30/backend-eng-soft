package com.backend.api.descarteeletronico.mapper;

import com.backend.api.descarteeletronico.model.usuario.Usuario;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UsuarioMapper {

  UsuarioResponse toResponse(Usuario usuario);
}
