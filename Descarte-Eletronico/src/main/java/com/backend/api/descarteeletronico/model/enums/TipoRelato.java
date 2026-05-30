package com.backend.api.descarteeletronico.model.enums;

public enum TipoRelato {
    PONTO_NAO_EXISTE("O ponto de coleta não existe aqui"),
    LIXEIRA_DANIFICADA("Lixeira danificada ou vandalizada"),
    LIXEIRA_CHEIA("Lixeira reportada como cheia"),
    HORARIO_INCORRETO("O horário de funcionamento está incorreto"),
    MATERIAIS_RECUSADOS("Eles não aceitaram os materiais listados"),
    OUTRO("Outro problema");

    private final String descricao;

    TipoRelato(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}