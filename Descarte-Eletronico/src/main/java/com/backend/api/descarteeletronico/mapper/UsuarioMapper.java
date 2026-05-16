package com.backend.api.descarteeletronico.mapper;

import com.backend.api.descarteeletronico.model.usuario.Usuario;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioRequest;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UsuarioMapper extends BaseMapper<Usuario, UsuarioRequest, UsuarioResponse> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "entityStatus", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(source = "senha", target = "senhaHash")
    Usuario toEntity(UsuarioRequest request);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "entityStatus", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(source = "senha", target = "senhaHash")
    void updateEntityFromRequest(UsuarioRequest request, @MappingTarget Usuario entity);
}