CREATE TABLE categories(
  id        BIGSERIAL PRIMARY KEY,
  name      VARCHAR(100) NOT NULL,
  type      VARCHAR(50) NOT NULL
);