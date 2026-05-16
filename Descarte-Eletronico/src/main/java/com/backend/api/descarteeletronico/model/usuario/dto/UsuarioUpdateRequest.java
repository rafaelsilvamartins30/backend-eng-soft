package com.backend.api.descarteeletronico.model.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para atualização do único usuário administrador")
public record UsuarioUpdateRequest(
    @Schema(description = "Nome do usuário administrador", example = "Administrador")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,
    @Schema(description = "E-mail do usuário administrador", example = "admin@descarte.local")
        @Email(message = "Informe um e-mail válido")
        @Size(max = 100, message = "O e-mail deve ter no máximo 100 caracteres")
        String email,
    @Schema(description = "Nova senha de acesso", example = "Admin@456")
        @Size(min = 8, max = 255, message = "A senha deve ter entre 8 e 255 caracteres")
        String senha) {}
