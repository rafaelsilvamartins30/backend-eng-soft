package com.backend.api.descarteeletronico.model.auth.dto;

import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token JWT emitido para o usuário administrador autenticado")
public record LoginResponse(
    @Schema(description = "Token JWT de acesso") String accessToken,
    @Schema(description = "Tipo do token", example = "Bearer") String tokenType,
    @Schema(description = "Tempo de expiração em segundos", example = "3600") long expiresIn,
    @Schema(description = "Usuário administrador autenticado") UsuarioResponse usuario) {}
