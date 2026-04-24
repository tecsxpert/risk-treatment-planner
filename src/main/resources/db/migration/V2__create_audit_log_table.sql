<<<<<<< HEAD
CREATE TABLE audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT NOT NULL,

    action VARCHAR(50) NOT NULL,   -- CREATE, UPDATE, DELETE
    changed_by VARCHAR(100),

    old_value TEXT,
    new_value TEXT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Composite index (IMPORTANT for lookup)
CREATE INDEX idx_audit_entity_type_id
ON audit_log (entity_type, entity_id);

-- Index on action (for filtering)
CREATE INDEX idx_audit_action
ON audit_log (action);

-- Index on created_at (for date range queries)
CREATE INDEX idx_audit_created_at
ON audit_log (created_at);

-- Index on changed_by (for user-based filtering)
CREATE INDEX idx_audit_changed_by
ON audit_log (changed_by);
=======
CREATE TABLE IF NOT EXISTS audit_log (
    id          BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(100) NOT NULL,
    entity_id   BIGINT NOT NULL,
    action      VARCHAR(50) NOT NULL,
    changed_by  VARCHAR(100),
    old_value   TEXT,
    new_value   TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_entity     ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_created_at ON audit_log(created_at);
CREATE INDEX idx_audit_changed_by ON audit_log(changed_by);
>>>>>>> keerthanayn
