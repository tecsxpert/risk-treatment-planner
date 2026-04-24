CREATE TABLE IF NOT EXISTS risks (
    id             BIGSERIAL PRIMARY KEY,
    title          VARCHAR(255) NOT NULL,
    description    TEXT,
    category       VARCHAR(100) NOT NULL DEFAULT 'GENERAL',
    likelihood     INTEGER NOT NULL DEFAULT 1,
    impact         INTEGER NOT NULL DEFAULT 1,
    risk_score     INTEGER GENERATED ALWAYS AS (likelihood * impact) STORED,
    status         VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    owner          VARCHAR(255),
    treatment_plan TEXT,
    due_date       DATE,
    ai_description TEXT,
    is_deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at     TIMESTAMP,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_risks_status   ON risks(status);
CREATE INDEX idx_risks_category ON risks(category);
CREATE INDEX idx_risks_due_date ON risks(due_date);
CREATE INDEX idx_risks_deleted  ON risks(is_deleted);