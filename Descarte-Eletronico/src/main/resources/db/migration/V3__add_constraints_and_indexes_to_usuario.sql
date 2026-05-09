ALTER TABLE usuarios_admin
    ADD CONSTRAINT ck_usuarios_admin_entity_status
    CHECK (entity_status IN ('ACTIVE', 'INACTIVE', 'DELETED'));

CREATE INDEX IF NOT EXISTS idx_usuarios_admin_entity_status
    ON usuarios_admin (entity_status);

CREATE INDEX IF NOT EXISTS idx_usuarios_admin_id_entity_status
    ON usuarios_admin (id, entity_status);