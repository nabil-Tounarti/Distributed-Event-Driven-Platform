CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       first_name VARCHAR(255),
                       last_name VARCHAR(255),
                       role VARCHAR(50) NOT NULL,
                       created_at TIMESTAMP WITH TIME ZONE,
                       updated_at TIMESTAMP WITH TIME ZONE,
                       version BIGINT
);

CREATE INDEX idx_users_email ON users(email);
