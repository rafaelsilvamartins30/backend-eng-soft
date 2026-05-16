CREATE TABLE IF NOT EXISTS usuarios_admin (
    id UUID PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    entity_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    deleted_at TIMESTAMP,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL,

    CONSTRAINT ck_usuarios_admin_entity_status
        CHECK (entity_status IN ('ACTIVE', 'INACTIVE', 'DELETED'))
);

CREATE INDEX IF NOT EXISTS idx_usuarios_admin_entity_status
    ON usuarios_admin (entity_status);

CREATE INDEX IF NOT EXISTS idx_usuarios_admin_id_entity_status
    ON usuarios_admin (id, entity_status);