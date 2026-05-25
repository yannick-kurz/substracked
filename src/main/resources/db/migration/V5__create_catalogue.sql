CREATE TABLE catalogue_entries (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(100)  NOT NULL,
    provider       VARCHAR(100),
    typical_amount NUMERIC(10,2),
    currency       VARCHAR(3)    DEFAULT 'CHF',
    billing_cycle  VARCHAR(20),
    category_id    BIGINT        REFERENCES categories(id),
    logo_url       VARCHAR(255),
    website        VARCHAR(255),
    active         BOOLEAN NOT NULL DEFAULT true
);

CREATE INDEX idx_catalogue_search
    ON catalogue_entries
    USING gin(to_tsvector('simple', name || ' ' || COALESCE(provider, '')));