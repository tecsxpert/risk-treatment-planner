<<<<<<< HEAD
-- Flyway V1 migration created by Keerthana YN

CREATE TABLE risk (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    title VARCHAR(255) NOT NULL,
    description TEXT,

    risk_level VARCHAR(20) NOT NULL,
    impact VARCHAR(20),
    likelihood VARCHAR(20),

    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',

    mitigation_plan TEXT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Indexes for fast lookup

CREATE INDEX idx_risk_status ON risk(status);
CREATE INDEX idx_risk_level ON risk(risk_level);
CREATE INDEX idx_risk_created_at ON risk(created_at);
=======
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
>>>>>>> keerthanayn
