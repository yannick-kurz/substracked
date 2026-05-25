CREATE TABLE import_suggestions (
                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    user_id UUID NOT NULL REFERENCES users(id),
                                    merchant_name VARCHAR(100),
                                    amount NUMERIC(10,2),
                                    currency VARCHAR(3),
                                    detected_cycle VARCHAR(20),
                                    estimated_next_renewal DATE,
                                    occurrence_count INTEGER,
                                    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, CONFIRMED, DISMISSED
                                    created_at TIMESTAMP DEFAULT now()
);