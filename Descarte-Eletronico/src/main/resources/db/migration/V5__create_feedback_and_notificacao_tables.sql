CREATE TABLE IF NOT EXISTS relato_problema (
                                               id UUID PRIMARY KEY,
                                               version BIGINT,
                                               created_at TIMESTAMP NOT NULL,
                                               updated_at TIMESTAMP NOT NULL,
                                               entity_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    deleted_at TIMESTAMP,
    ponto_coleta_id UUID NOT NULL,
    tipo_relato VARCHAR(50) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    observacao VARCHAR(1000),

    CONSTRAINT ck_relato_problema_entity_status
    CHECK (entity_status IN ('ACTIVE', 'INACTIVE', 'DELETED')),

    CONSTRAINT ck_relato_problema_tipo_relato
    CHECK (tipo_relato IN (
           'PONTO_NAO_EXISTE',
           'LIXEIRA_DANIFICADA',
           'LIXEIRA_CHEIA',
           'HORARIO_INCORRETO',
           'MATERIAIS_RECUSADOS',
           'OUTRO'
                          )),

    CONSTRAINT fk_relato_problema_ponto_coleta
    FOREIGN KEY (ponto_coleta_id) REFERENCES ponto_coleta (id)
);

CREATE INDEX IF NOT EXISTS idx_relato_problema_entity_status
    ON relato_problema (entity_status);

CREATE INDEX IF NOT EXISTS idx_relato_problema_id_entity_status
    ON relato_problema (id, entity_status);

CREATE INDEX IF NOT EXISTS idx_relato_problema_ponto_coleta_id
    ON relato_problema (ponto_coleta_id);

CREATE TABLE IF NOT EXISTS notificacao (
                                           id UUID PRIMARY KEY,
                                           version BIGINT,
                                           created_at TIMESTAMP NOT NULL,
                                           updated_at TIMESTAMP NOT NULL,
                                           entity_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    deleted_at TIMESTAMP,
    titulo VARCHAR(150) NOT NULL,
    mensagem VARCHAR(500) NOT NULL,
    ponto_coleta_id UUID NOT NULL,
    relato_problema_id UUID NOT NULL,

    CONSTRAINT ck_notificacao_entity_status
    CHECK (entity_status IN ('ACTIVE', 'INACTIVE', 'DELETED')),

    CONSTRAINT fk_notificacao_ponto_coleta
    FOREIGN KEY (ponto_coleta_id) REFERENCES ponto_coleta (id),

    CONSTRAINT fk_notificacao_relato_problema
    FOREIGN KEY (relato_problema_id) REFERENCES relato_problema (id)
);

CREATE INDEX IF NOT EXISTS idx_notificacao_entity_status
    ON notificacao (entity_status);

CREATE INDEX IF NOT EXISTS idx_notificacao_id_entity_status
    ON notificacao (id, entity_status);

CREATE INDEX IF NOT EXISTS idx_notificacao_ponto_coleta_id
    ON notificacao (ponto_coleta_id);

CREATE INDEX IF NOT EXISTS idx_notificacao_relato_problema_id_status
    ON notificacao (relato_problema_id, entity_status);