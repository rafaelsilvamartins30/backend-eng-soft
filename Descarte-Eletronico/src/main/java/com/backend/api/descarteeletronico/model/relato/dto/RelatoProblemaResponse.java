package com.backend.api.descarteeletronico.model.relato.dto;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.enums.TipoRelato;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados retornados para um relato de problema de ponto de coleta")
public record RelatoProblemaResponse(
        @Schema(description = "Identificador do relato", example = "4fbb2c8e-8737-4e24-9ef0-0db72a231ce8")
        UUID id,

        @Schema(description = "Identificador do ponto de coleta", example = "1d0fd3bb-e0dc-4512-bb2f-31d64d8e7e50")
        UUID pontoColetaId,

        @Schema(description = "Nome do ponto de coleta", example = "EcoPonto Centro")
        String pontoColetaNome,

        @Schema(description = "Tipo do problema relatado")
        TipoRelato tipoRelato,

        @Schema(description = "Nome de quem enviou o relato", example = "Maria Silva")
        String nome,

        @Schema(description = "E-mail para possível resposta", example = "maria@email.com")
        String email,

        @Schema(description = "Observação opcional enviada", example = "A lixeira estava transbordando.")
        String observacao,

        @Schema(description = "Versão para controle de concorrência otimista", example = "0")
        Long version,

        @Schema(description = "Data de criação", example = "2026-05-04T21:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Data da última atualização", example = "2026-05-04T21:30:00")
        LocalDateTime updatedAt,

        @Schema(description = "ACTIVE para não visto, INACTIVE para visto", example = "ACTIVE")
        EntityStatus entityStatus,

        @Schema(description = "Data do soft delete, quando houver", nullable = true, example = "2026-05-04T21:40:00")
        LocalDateTime deletedAt
) {}