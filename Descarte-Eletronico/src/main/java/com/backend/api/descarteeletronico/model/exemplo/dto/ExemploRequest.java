package com.backend.api.descarteeletronico.model.exemplo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para criação ou atualização de um exemplo")
public record ExemploRequest(
    @Schema(description = "Nome do exemplo", example = "Coleta de notebook")
        @NotBlank(message = "O nome é obrigatório")
        String nome,
    @Schema(description = "Descrição do exemplo", example = "Equipamento antigo para descarte")
        @NotBlank(message = "A descricao é obrigatória")
        String descricao) {}
