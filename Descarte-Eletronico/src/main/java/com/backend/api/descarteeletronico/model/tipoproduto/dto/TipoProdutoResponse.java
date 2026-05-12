package com.backend.api.descarteeletronico.model.tipoproduto.dto;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados retornados para um tipo de produto")
public record TipoProdutoResponse(
    @Schema(
            description = "Identificador do tipo de produto",
            example = "4fbb2c8e-8737-4e24-9ef0-0db72a231ce8")
        UUID id,
    @Schema(description = "Nome do tipo de produto", example = "Computadores") String nome,
    @Schema(
            description = "Descrição com exemplos de produtos aceitos",
            example = "Notebooks, desktops, monitores e periféricos")
        String descricaoExemplos,
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
