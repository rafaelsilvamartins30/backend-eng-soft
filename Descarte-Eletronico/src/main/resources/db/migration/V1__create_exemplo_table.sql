CREATE TABLE IF NOT EXISTS exemplo (
    id UUID PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    entity_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    deleted_at TIMESTAMP,
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    CONSTRAINT ck_exemplo_entity_status
        CHECK (entity_status IN ('ACTIVE', 'INACTIVE', 'DELETED'))
);

CREATE INDEX IF NOT EXISTS idx_exemplo_entity_status
    ON exemplo (entity_status);

CREATE INDEX IF NOT EXISTS idx_exemplo_id_entity_status
    ON exemplo (id, entity_status);
