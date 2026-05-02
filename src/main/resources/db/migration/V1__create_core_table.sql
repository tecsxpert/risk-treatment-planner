CREATE TABLE IF NOT EXISTS risk (
    id             BIGSERIAL PRIMARY KEY,
    title          VARCHAR(255) NOT NULL,
    description    TEXT,
    category       VARCHAR(100) NOT NULL DEFAULT 'GENERAL',
    likelihood     INTEGER NOT NULL DEFAULT 1,
    impact         INTEGER NOT NULL DEFAULT 1,
    status         VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    due_date       DATE,
    ai_description TEXT,
    is_deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at     TIMESTAMP,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_risk_status   ON risk(status);
CREATE INDEX idx_risk_category ON risk(category);
CREATE INDEX idx_risk_due_date ON risk(due_date);
CREATE INDEX idx_risk_deleted  ON risk(is_deleted);