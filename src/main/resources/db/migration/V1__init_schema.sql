-- V1__init_schema.sql
-- Esquema inicial completo. ddl-auto=validate: Hibernate NO crea tablas,
-- solo valida que coincidan con las entidades JPA.

CREATE TABLE region (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE type (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE pokemon (
    id              BIGSERIAL PRIMARY KEY,
    national_number INTEGER      NOT NULL UNIQUE,
    name            VARCHAR(100) NOT NULL,
    image_url       VARCHAR(500),
    generation      INTEGER      NOT NULL,
    has_mega        BOOLEAN      NOT NULL DEFAULT FALSE,
    region_id       BIGINT REFERENCES region (id),
    created_at      TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX idx_pokemon_number ON pokemon (national_number);

CREATE TABLE pokemon_stats (
    id               BIGSERIAL PRIMARY KEY,
    hp               INTEGER NOT NULL,
    attack           INTEGER NOT NULL,
    defense          INTEGER NOT NULL,
    special_attack   INTEGER NOT NULL,
    special_defense  INTEGER NOT NULL,
    speed            INTEGER NOT NULL,
    pokemon_id       BIGINT  NOT NULL UNIQUE REFERENCES pokemon (id)
);

CREATE TABLE pokemon_type (
    pokemon_id BIGINT NOT NULL REFERENCES pokemon (id),
    type_id    BIGINT NOT NULL REFERENCES type (id),
    PRIMARY KEY (pokemon_id, type_id)
);

CREATE TABLE app_user (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(150) NOT NULL UNIQUE,
    username      VARCHAR(30)  NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL,
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE team (
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT      NOT NULL REFERENCES app_user (id),
    name    VARCHAR(50) NOT NULL
);

CREATE TABLE team_pokemon (
    team_id    BIGINT NOT NULL REFERENCES team (id),
    pokemon_id BIGINT NOT NULL
);

CREATE TABLE favorite (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT    NOT NULL REFERENCES app_user (id),
    pokemon_id BIGINT    NOT NULL,
    added_at   TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uk_favorite_user_pokemon UNIQUE (user_id, pokemon_id)
);
