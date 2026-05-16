CREATE TABLE IF NOT EXISTS roles (
    id UUID PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    entity_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    deleted_at TIMESTAMP,
    nome VARCHAR(50) NOT NULL UNIQUE,
    CONSTRAINT ck_roles_entity_status
        CHECK (entity_status IN ('ACTIVE', 'INACTIVE', 'DELETED')),
    CONSTRAINT ck_roles_nome
        CHECK (nome IN ('ADMIN'))
);

CREATE INDEX IF NOT EXISTS idx_roles_entity_status
    ON roles (entity_status);

CREATE INDEX IF NOT EXISTS idx_roles_id_entity_status
    ON roles (id, entity_status);

INSERT INTO roles (
    id,
    version,
    created_at,
    updated_at,
    entity_status,
    deleted_at,
    nome
)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE',
    NULL,
    'ADMIN'
)
ON CONFLICT (nome) DO NOTHING;

CREATE TABLE IF NOT EXISTS usuarios_admin_roles (
    usuario_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (usuario_id, role_id),
    CONSTRAINT fk_usuarios_admin_roles_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios_admin (id),
    CONSTRAINT fk_usuarios_admin_roles_role
        FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE INDEX IF NOT EXISTS idx_usuarios_admin_roles_role_id
    ON usuarios_admin_roles (role_id);

INSERT INTO usuarios_admin_roles (usuario_id, role_id)
SELECT id, '00000000-0000-0000-0000-000000000001'
FROM usuarios_admin
WHERE entity_status = 'ACTIVE'
ON CONFLICT DO NOTHING;
