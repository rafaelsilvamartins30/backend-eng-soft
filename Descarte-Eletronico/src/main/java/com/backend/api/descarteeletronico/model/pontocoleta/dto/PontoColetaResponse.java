package com.backend.api.descarteeletronico.model.pontocoleta.dto;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.tipoproduto.dto.TipoProdutoResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Dados retornados para um ponto de coleta")
public record PontoColetaResponse(
    @Schema(
            description = "Identificador do ponto de coleta",
            example = "4fbb2c8e-8737-4e24-9ef0-0db72a231ce8")
        UUID id,
    @Schema(description = "Nome do ponto de coleta", example = "EcoPonto Centro") String nome,
    @Schema(description = "Endereço do ponto de coleta", example = "Rua das Flores, 123")
        String endereco,
    @Schema(description = "Descrição do ponto de coleta", example = "Recebe eletrônicos de pequeno porte")
        String descricao,
    @Schema(description = "Latitude do ponto de coleta", example = "-23.5505200")
        BigDecimal latitude,
    @Schema(description = "Longitude do ponto de coleta", example = "-46.6333080")
        BigDecimal longitude,
    @Schema(description = "Tipos de produto aceitos pelo ponto de coleta")
        Set<TipoProdutoResponse> tiposProduto,
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
