package com.backend.api.descarteeletronico.model.tipoproduto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criação ou atualização de um tipo de produto")
public record TipoProdutoRequest(
    @Schema(description = "Nome do tipo de produto", example = "Computadores")
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,
    @Schema(
            description = "Descrição com exemplos de produtos aceitos",
            example = "Notebooks, desktops, monitores e periféricos")
        @NotBlank(message = "A descricao de exemplos é obrigatória")
        @Size(max = 500, message = "A descricao de exemplos deve ter no máximo 500 caracteres")
        String descricaoExemplos) {}
