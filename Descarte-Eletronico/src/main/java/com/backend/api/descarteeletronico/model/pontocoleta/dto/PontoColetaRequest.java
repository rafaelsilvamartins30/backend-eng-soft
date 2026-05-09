package com.backend.api.descarteeletronico.model.pontocoleta.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Dados para criação ou atualização de um ponto de coleta")
public record PontoColetaRequest(
    @Schema(description = "Nome do ponto de coleta", example = "EcoPonto Centro")
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String nome,
    @Schema(description = "Endereço do ponto de coleta", example = "Rua das Flores, 123")
        @NotBlank(message = "O endereço é obrigatório")
        @Size(max = 255, message = "O endereço deve ter no máximo 255 caracteres")
        String endereco,
    @Schema(description = "Descrição do ponto de coleta", example = "Recebe eletrônicos de pequeno porte")
        @NotBlank(message = "A descricao é obrigatória")
        @Size(max = 500, message = "A descricao deve ter no máximo 500 caracteres")
        String descricao,
    @Schema(description = "Latitude do ponto de coleta", example = "-23.5505200")
        @NotNull(message = "A latitude é obrigatória")
        @DecimalMin(value = "-90.0", message = "A latitude mínima é -90")
        @DecimalMax(value = "90.0", message = "A latitude máxima é 90")
        BigDecimal latitude,
    @Schema(description = "Longitude do ponto de coleta", example = "-46.6333080")
        @NotNull(message = "A longitude é obrigatória")
        @DecimalMin(value = "-180.0", message = "A longitude mínima é -180")
        @DecimalMax(value = "180.0", message = "A longitude máxima é 180")
        BigDecimal longitude,
    @Schema(description = "IDs dos tipos de produto aceitos")
        @NotEmpty(message = "Informe ao menos um tipo de produto aceito")
        @Size(max = 50, message = "Informe no máximo 50 tipos de produto aceitos")
        Set<UUID> tipoProdutoIds) {}
