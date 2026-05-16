package com.backend.api.descarteeletronico.model.usuario.dto;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import java.util.UUID;

public record UsuarioResponse(
                UUID id,
                String nome,
                String email,
                EntityStatus entityStatus) {
}