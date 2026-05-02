package com.backend.api.Descarte_Eletronico.model.enums;

public enum Status {
    ATIVO("Ativo"),
    INATIVO("Inativo");

    private final String descricao;

    Status(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
