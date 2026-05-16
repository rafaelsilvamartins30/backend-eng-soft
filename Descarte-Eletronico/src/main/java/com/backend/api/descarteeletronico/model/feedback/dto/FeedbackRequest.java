package com.backend.api.descarteeletronico.model.feedback.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para envio de feedback público sobre um ponto de coleta")
public record FeedbackRequest(
    @Schema(description = "Nome de quem enviou o feedback", example = "Maria Silva")
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,
    @Schema(description = "E-mail para possível resposta", example = "maria@email.com")
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Informe um e-mail válido")
        @Size(max = 100, message = "O e-mail deve ter no máximo 100 caracteres")
        String email,
    @Schema(
            description = "Mensagem enviada para ajudar na melhoria do ponto de coleta",
            example = "O atendimento foi muito bom.")
        @NotBlank(message = "A mensagem é obrigatória")
        @Size(max = 1000, message = "A mensagem deve ter no máximo 1000 caracteres")
        String mensagem) {}
