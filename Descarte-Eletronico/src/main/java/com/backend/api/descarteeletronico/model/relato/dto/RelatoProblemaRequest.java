package com.backend.api.descarteeletronico.model.relato.dto;

import com.backend.api.descarteeletronico.model.enums.TipoRelato;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para envio de relato de problema sobre um ponto de coleta")
public record RelatoProblemaRequest(
        @Schema(description = "Tipo do problema relatado")
        @NotNull(message = "O tipo de relato é obrigatório")
        TipoRelato tipoRelato,

        @Schema(description = "Nome de quem enviou o relato", example = "Maria Silva")
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @Schema(description = "E-mail para possível resposta", example = "maria@email.com")
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Informe um e-mail válido")
        @Size(max = 100, message = "O e-mail deve ter no máximo 100 caracteres")
        String email,

        @Schema(description = "Observação opcional enviada pelo usuário", example = "O portão estava trancado.")
        @Size(max = 1000, message = "A observação deve ter no máximo 1000 caracteres")
        String observacao
) {}