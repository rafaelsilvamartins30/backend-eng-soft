package com.backend.api.descarteeletronico.model.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais para autenticação do usuário administrador")
public record LoginRequest(
    @Schema(description = "E-mail do usuário administrador", example = "admin@descarte.local")
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Informe um e-mail válido")
        String email,
    @Schema(description = "Senha do usuário administrador", example = "Admin@123")
        @NotBlank(message = "A senha é obrigatória")
        String senha) {}
