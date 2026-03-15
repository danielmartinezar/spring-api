-- =============================================
-- Script de creación de base de datos
-- Aplicación: spring-test (User Registration API)
-- Base de datos: H2 (in-memory)
-- =============================================

CREATE TABLE IF NOT EXISTS app_user (
    id          UUID            NOT NULL,
    name        VARCHAR(255)    NOT NULL,
    email       VARCHAR(255)    NOT NULL,
    password    VARCHAR(255)    NOT NULL,
    is_active   BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP       NOT NULL,
    modified_at TIMESTAMP       NOT NULL,
    CONSTRAINT pk_app_user PRIMARY KEY (id),
    CONSTRAINT uq_app_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS phone (
    user_id      UUID            NOT NULL,
    number       VARCHAR(50),
    city_code    VARCHAR(10),
    country_code VARCHAR(10),
    CONSTRAINT fk_phone_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS auth (
    id          UUID            NOT NULL,
    user_id     UUID            NOT NULL,
    token       VARCHAR(512)    NOT NULL,
    last_login  TIMESTAMP       NOT NULL,
    CONSTRAINT pk_auth PRIMARY KEY (id),
    CONSTRAINT uq_auth_user UNIQUE (user_id),
    CONSTRAINT fk_auth_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);
