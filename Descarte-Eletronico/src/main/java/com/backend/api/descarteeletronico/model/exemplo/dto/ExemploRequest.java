package com.backend.api.descarteeletronico.model.exemplo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criação ou atualização de um exemplo")
public record ExemploRequest(
    @Schema(description = "Nome do exemplo", example = "Coleta de notebook")
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
        String nome,
    @Schema(description = "Descrição do exemplo", example = "Equipamento antigo para descarte")
        @NotBlank(message = "A descricao é obrigatória")
        @Size(max = 255, message = "A descricao deve ter no máximo 255 caracteres")
        String descricao) {}
