package com.backend.api.descarteeletronico.model.usuario.dto;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.role.dto.RoleResponse;
import java.util.Set;
import java.util.UUID;

public record UsuarioResponse(
                UUID id,
                String nome,
                String email,
                Set<RoleResponse> roles,
                EntityStatus entityStatus) {
}
