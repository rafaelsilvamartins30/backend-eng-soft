package com.backend.api.descarteeletronico.model.exemplo.dto;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados retornados para um exemplo")
public record ExemploResponse(
    @Schema(
            description = "Identificador do exemplo",
            example = "4fbb2c8e-8737-4e24-9ef0-0db72a231ce8")
        UUID id,
    @Schema(description = "Nome do exemplo", example = "Coleta de notebook") String nome,
    @Schema(description = "Descrição do exemplo", example = "Equipamento antigo para descarte")
        String descricao,
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
