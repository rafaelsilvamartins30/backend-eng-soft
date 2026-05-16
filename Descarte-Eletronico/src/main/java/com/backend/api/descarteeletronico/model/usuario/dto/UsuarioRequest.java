package com.backend.api.descarteeletronico.model.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criação ou atualização de um usuário administrador")
public record UsuarioRequest(
    @Schema(description = "Nome do usuário administrador", example = "Maria Silva")
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,
    @Schema(description = "E-mail único do usuário administrador", example = "maria@descarte.com")
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Informe um e-mail válido")
        @Size(max = 100, message = "O e-mail deve ter no máximo 100 caracteres")
        String email,
    @Schema(description = "Senha de acesso", example = "SenhaForte123")
        @NotBlank(message = "A senha é obrigatória")
        @Size(max = 255, message = "A senha deve ter no máximo 255 caracteres")
        String senha) {}
