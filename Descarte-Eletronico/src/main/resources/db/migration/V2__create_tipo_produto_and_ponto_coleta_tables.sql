CREATE TABLE IF NOT EXISTS tipo_produto (
    id UUID PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    entity_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    deleted_at TIMESTAMP,
    nome VARCHAR(100) NOT NULL,
    descricao_exemplos VARCHAR(500) NOT NULL,
    CONSTRAINT ck_tipo_produto_entity_status
        CHECK (entity_status IN ('ACTIVE', 'INACTIVE', 'DELETED'))
);

CREATE INDEX IF NOT EXISTS idx_tipo_produto_entity_status
    ON tipo_produto (entity_status);

CREATE INDEX IF NOT EXISTS idx_tipo_produto_id_entity_status
    ON tipo_produto (id, entity_status);

CREATE TABLE IF NOT EXISTS ponto_coleta (
    id UUID PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    entity_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    deleted_at TIMESTAMP,
    nome VARCHAR(150) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    descricao VARCHAR(500) NOT NULL,
    latitude NUMERIC(10, 7) NOT NULL,
    longitude NUMERIC(10, 7) NOT NULL,
    CONSTRAINT ck_ponto_coleta_entity_status
        CHECK (entity_status IN ('ACTIVE', 'INACTIVE', 'DELETED')),
    CONSTRAINT ck_ponto_coleta_latitude
        CHECK (latitude >= -90 AND latitude <= 90),
    CONSTRAINT ck_ponto_coleta_longitude
        CHECK (longitude >= -180 AND longitude <= 180)
);

CREATE INDEX IF NOT EXISTS idx_ponto_coleta_entity_status
    ON ponto_coleta (entity_status);

CREATE INDEX IF NOT EXISTS idx_ponto_coleta_id_entity_status
    ON ponto_coleta (id, entity_status);

CREATE TABLE IF NOT EXISTS ponto_coleta_tipo_produto (
    ponto_coleta_id UUID NOT NULL,
    tipo_produto_id UUID NOT NULL,
    PRIMARY KEY (ponto_coleta_id, tipo_produto_id),
    CONSTRAINT fk_ponto_coleta_tipo_produto_ponto_coleta
        FOREIGN KEY (ponto_coleta_id) REFERENCES ponto_coleta (id),
    CONSTRAINT fk_ponto_coleta_tipo_produto_tipo_produto
        FOREIGN KEY (tipo_produto_id) REFERENCES tipo_produto (id)
);

CREATE INDEX IF NOT EXISTS idx_ponto_coleta_tipo_produto_tipo_produto_id
    ON ponto_coleta_tipo_produto (tipo_produto_id);
