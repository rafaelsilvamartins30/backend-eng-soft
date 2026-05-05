package com.backend.api.descarteeletronico.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Resposta padrão para erros da API")
public record ErrorResponseDTO(
    @Schema(description = "Data e hora em que o erro ocorreu", example = "2026-05-04T21:30:00")
        LocalDateTime timestamp,
    @Schema(description = "Código HTTP numérico", example = "400") int status,
    @Schema(description = "Descrição do status HTTP", example = "Bad Request") String error,
    @Schema(description = "Mensagem principal do erro", example = "Dados de entrada inválidos")
        String message,
    @Schema(description = "Caminho da requisição", example = "/api/exemplos") String path,
    @Schema(
            description = "Detalhes adicionais do erro",
            example = "[\"nome: O nome é obrigatório\"]")
        Set<String> details) {}
