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
