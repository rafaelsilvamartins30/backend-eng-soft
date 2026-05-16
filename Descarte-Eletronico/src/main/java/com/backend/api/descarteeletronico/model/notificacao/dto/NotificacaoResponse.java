package com.backend.api.descarteeletronico.model.notificacao.dto;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.enums.NotificacaoTipo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados retornados para uma notificação administrativa")
public record NotificacaoResponse(
    @Schema(
            description = "Identificador da notificação",
            example = "4fbb2c8e-8737-4e24-9ef0-0db72a231ce8")
        UUID id,
    @Schema(description = "Tipo da notificação", example = "PONTO_COLETA_CHEIO")
        NotificacaoTipo tipo,
    @Schema(description = "Título resumido", example = "Ponto de coleta cheio") String titulo,
    @Schema(
            description = "Mensagem da notificação",
            example = "O EcoPonto Centro foi reportado como cheio.")
        String mensagem,
    @Schema(
            description = "Identificador do ponto de coleta relacionado",
            nullable = true,
            example = "1d0fd3bb-e0dc-4512-bb2f-31d64d8e7e50")
        UUID pontoColetaId,
    @Schema(description = "Nome do ponto de coleta relacionado", nullable = true)
        String pontoColetaNome,
    @Schema(
            description = "Identificador do feedback relacionado",
            nullable = true,
            example = "06f80f54-5423-41e8-b10f-8060e8ac5dc1")
        UUID feedbackId,
    @Schema(description = "Versão para controle de concorrência otimista", example = "0")
        Long version,
    @Schema(description = "Data de criação", example = "2026-05-04T21:30:00")
        LocalDateTime createdAt,
    @Schema(description = "Data da última atualização", example = "2026-05-04T21:30:00")
        LocalDateTime updatedAt,
    @Schema(description = "ACTIVE para não vista, INACTIVE para vista", example = "ACTIVE")
        EntityStatus entityStatus,
    @Schema(
            description = "Data do soft delete, quando houver",
            nullable = true,
            example = "2026-05-04T21:40:00")
        LocalDateTime deletedAt) {}
