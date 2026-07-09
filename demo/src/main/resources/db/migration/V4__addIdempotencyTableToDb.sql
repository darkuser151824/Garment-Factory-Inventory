CREATE TABLE IF NOT EXISTS idempotency_keys (
    idempotency_key     VARCHAR(64)     NOT NULL,
    request_hash        VARCHAR(64)     NOT NULL,
    response_body       TEXT,
    http_status         INTEGER,
    status              VARCHAR(20)     NOT NULL DEFAULT 'PROCESSING'
                            CHECK (status IN ('PROCESSING', 'COMPLETED', 'FAILED')),
    resource_id         BIGINT,
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_idempotency_keys PRIMARY KEY (idempotency_key)
);

CREATE INDEX idx_idempotency_keys_created_at
    ON idempotency_keys(created_at);