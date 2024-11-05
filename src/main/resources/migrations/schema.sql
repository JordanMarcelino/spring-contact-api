DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS contacts CASCADE;
DROP TABLE IF EXISTS addresses CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id               BIGSERIAL PRIMARY KEY,
    username         VARCHAR(100) UNIQUE NOT NULL,
    name             VARCHAR(100)        NOT NULL,
    hash_password    VARCHAR(255)        NOT NULL,
    token            VARCHAR(255) UNIQUE NOT NULL,
    token_expired_at BIGINT,
    created_at       TIMESTAMP           NOT NULL,
    updated_at       TIMESTAMP           NOT NULL
);

CREATE TABLE IF NOT EXISTS contacts
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT REFERENCES users (id) NOT NULL,
    first_name VARCHAR(255)                 NOT NULL,
    last_name  VARCHAR(255),
    email      VARCHAR(255),
    phone      VARCHAR(255),
    created_at TIMESTAMP                    NOT NULL,
    updated_at TIMESTAMP                    NOT NULL
);

CREATE TABLE IF NOT EXISTS addresses
(
    id          BIGSERIAL PRIMARY KEY,
    contact_id  BIGINT REFERENCES contacts (id) NOT NULL,
    country     VARCHAR(100)                    NOT NULL,
    province    VARCHAR(100),
    city        VARCHAR(100),
    street      VARCHAR(100),
    postal_code VARCHAR(100),
    created_at  TIMESTAMP                       NOT NULL,
    updated_at  TIMESTAMP                       NOT NULL
);