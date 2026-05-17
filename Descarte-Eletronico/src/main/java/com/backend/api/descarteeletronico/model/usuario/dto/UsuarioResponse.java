package com.backend.api.descarteeletronico.model.usuario.dto;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados retornados para um usuário administrador")
public record UsuarioResponse(
    @Schema(
            description = "Identificador do usuário administrador",
            example = "4fbb2c8e-8737-4e24-9ef0-0db72a231ce8")
        UUID id,
    @Schema(description = "Nome do usuário administrador", example = "Maria Silva") String nome,
    @Schema(description = "E-mail do usuário administrador", example = "maria@descarte.com")
        String email,
    @Schema(description = "Versão para controle de concorrência otimista", example = "0")
        Long version,
    @Schema(description = "Data de criação", example = "2026-05-04T21:30:00")
        LocalDateTime createdAt,
    @Schema(description = "Data da última atualização", example = "2026-05-04T21:30:00")
        LocalDateTime updatedAt,
    @Schema(description = "Status técnico da entidade", example = "ACTIVE")
        EntityStatus entityStatus,
    @Schema(
            description = "Data do soft delete, quando houver",
            nullable = true,
            example = "2026-05-04T21:40:00")
        LocalDateTime deletedAt) {}
