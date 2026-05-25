CREATE TABLE subscriptions (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id           UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id       BIGINT       REFERENCES categories(id),
    name              VARCHAR(100) NOT NULL,
    amount            NUMERIC(10,2) NOT NULL,
    currency          VARCHAR(3)   NOT NULL DEFAULT 'CHF',
    billing_cycle     VARCHAR(20)  NOT NULL,
    next_renewal_date DATE         NOT NULL,
    active            BOOLEAN      NOT NULL DEFAULT true,
    in_trial          BOOLEAN      NOT NULL DEFAULT false,
    trial_ends_at     DATE,
    notes             TEXT,
    catalogue_entry_id INTEGER,
    created_at        TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX idx_subscriptions_user_id
    ON subscriptions(user_id);

CREATE INDEX idx_subscriptions_next_renewal
    ON subscriptions(next_renewal_date)
    WHERE active = true;