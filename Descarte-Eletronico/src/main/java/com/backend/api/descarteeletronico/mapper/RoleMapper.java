package com.backend.api.descarteeletronico.mapper;

import com.backend.api.descarteeletronico.model.role.Role;
import com.backend.api.descarteeletronico.model.role.dto.RoleResponse;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface RoleMapper {

  RoleResponse toResponse(Role role);

  Set<RoleResponse> toResponseSet(Set<Role> roles);
}
