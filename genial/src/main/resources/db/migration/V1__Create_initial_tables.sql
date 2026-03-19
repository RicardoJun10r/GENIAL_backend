-- Extensão para geração nativa de UUIDs no PostgreSQL (caso não esteja habilitada)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::varchar,
    email VARCHAR(256) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_role CHECK (role = 'USER' OR role = 'ADMIN')
);

CREATE TABLE IF NOT EXISTS storages (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::varchar,
    name VARCHAR(256) NOT NULL,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    id_user VARCHAR(36),
    FOREIGN KEY (id_user) REFERENCES users(id)
);

CREATE INDEX idx_storages_id_user ON storages(id_user);

CREATE TABLE IF NOT EXISTS products (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::varchar,
    name VARCHAR(256) NOT NULL,
    description TEXT,
    sector VARCHAR(256),
    value DECIMAL(19, 4),
    quantidade INTEGER NOT NULL CHECK(quantidade >= 0),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    id_storage VARCHAR(36),
    FOREIGN KEY (id_storage) REFERENCES storages(id)
);

CREATE INDEX idx_products_id_storage ON products(id_storage);
