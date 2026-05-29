CREATE TABLE payments (
   id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   user_id           UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
   subscription_id   UUID REFERENCES subscriptions(id) ON DELETE SET NULL,
   subscription_name VARCHAR(100) NOT NULL,
   amount            NUMERIC(10,2) NOT NULL,
   currency          VARCHAR(3) NOT NULL DEFAULT 'CHF',
   paid_on           DATE NOT NULL,
   created_at        TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_payments_user_id ON payments(user_id);
CREATE INDEX idx_payments_paid_on ON payments(paid_on);
