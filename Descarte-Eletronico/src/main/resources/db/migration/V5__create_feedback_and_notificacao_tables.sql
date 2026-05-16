CREATE TABLE IF NOT EXISTS feedback (
    id UUID PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    entity_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    deleted_at TIMESTAMP,
    ponto_coleta_id UUID NOT NULL,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    mensagem VARCHAR(1000) NOT NULL,
    CONSTRAINT ck_feedback_entity_status
        CHECK (entity_status IN ('ACTIVE', 'INACTIVE', 'DELETED')),
    CONSTRAINT fk_feedback_ponto_coleta
        FOREIGN KEY (ponto_coleta_id) REFERENCES ponto_coleta (id)
);

CREATE INDEX IF NOT EXISTS idx_feedback_entity_status
    ON feedback (entity_status);

CREATE INDEX IF NOT EXISTS idx_feedback_id_entity_status
    ON feedback (id, entity_status);

CREATE INDEX IF NOT EXISTS idx_feedback_ponto_coleta_id
    ON feedback (ponto_coleta_id);

CREATE TABLE IF NOT EXISTS notificacao (
    id UUID PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    entity_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    deleted_at TIMESTAMP,
    tipo VARCHAR(50) NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    mensagem VARCHAR(500) NOT NULL,
    ponto_coleta_id UUID,
    feedback_id UUID,
    CONSTRAINT ck_notificacao_entity_status
        CHECK (entity_status IN ('ACTIVE', 'INACTIVE', 'DELETED')),
    CONSTRAINT ck_notificacao_tipo
        CHECK (tipo IN ('FEEDBACK_RECEBIDO', 'PONTO_COLETA_CHEIO')),
    CONSTRAINT fk_notificacao_ponto_coleta
        FOREIGN KEY (ponto_coleta_id) REFERENCES ponto_coleta (id),
    CONSTRAINT fk_notificacao_feedback
        FOREIGN KEY (feedback_id) REFERENCES feedback (id)
);

CREATE INDEX IF NOT EXISTS idx_notificacao_entity_status
    ON notificacao (entity_status);

CREATE INDEX IF NOT EXISTS idx_notificacao_id_entity_status
    ON notificacao (id, entity_status);

CREATE INDEX IF NOT EXISTS idx_notificacao_ponto_coleta_id
    ON notificacao (ponto_coleta_id);

CREATE INDEX IF NOT EXISTS idx_notificacao_feedback_id_entity_status
    ON notificacao (feedback_id, entity_status);
