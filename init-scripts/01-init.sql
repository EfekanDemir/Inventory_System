-- =============================================
-- init-scripts/01-init.sql
-- Campus Management System — PostgreSQL 15 init
-- Otomatik çalışır: docker-entrypoint-initdb.d/
-- =============================================

-- Charset & collation güvence altına alma
SET client_encoding = 'UTF8';

-- ── Veritabanı zaten oluşturuldu (POSTGRES_DB env) ──────────────
-- Bu script envanter_db içinde çalışır.

-- ── Schema ──────────────────────────────────────────────────────
CREATE SCHEMA IF NOT EXISTS envanter;

-- ── Extension'lar ──────────────────────────────────────────────
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm"; -- İleride fuzzy arama için

-- ── USERS tablosu ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id              BIGSERIAL       PRIMARY KEY,
    username        VARCHAR(50)     NOT NULL UNIQUE,
    email           VARCHAR(100)    NOT NULL UNIQUE,
    password_hash   VARCHAR(255)    NOT NULL,
    first_name      VARCHAR(50),
    last_name       VARCHAR(50),
    role            VARCHAR(20)     NOT NULL DEFAULT 'PERSONEL'
                        CHECK (role IN ('ADMIN', 'MANAGER', 'PERSONEL')),
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- ── CATEGORIES tablosu ─────────────────────────────────────────
CREATE TABLE IF NOT EXISTS categories (
    id          BIGSERIAL       PRIMARY KEY,
    name        VARCHAR(100)    NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- ── ITEMS tablosu ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS items (
    id              BIGSERIAL       PRIMARY KEY,
    item_code       VARCHAR(50)     NOT NULL UNIQUE,
    name            VARCHAR(200)    NOT NULL,
    description     TEXT,
    category_id     BIGINT          REFERENCES categories(id) ON DELETE SET NULL,
    quantity        INTEGER         NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    min_stock_level INTEGER         NOT NULL DEFAULT 0 CHECK (min_stock_level >= 0),
    location        VARCHAR(100),
    status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE'
                        CHECK (status IN ('ACTIVE', 'INACTIVE', 'DISCONTINUED')),
    unit_price      DECIMAL(10,2)   DEFAULT 0.00,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- ── STOCK_MOVEMENTS tablosu ────────────────────────────────────
CREATE TABLE IF NOT EXISTS stock_movements (
    id               BIGSERIAL       PRIMARY KEY,
    item_id          BIGINT          NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    movement_type    VARCHAR(3)      NOT NULL CHECK (movement_type IN ('IN', 'OUT')),
    quantity         INTEGER         NOT NULL CHECK (quantity > 0),
    reason           TEXT,
    user_id          BIGINT          REFERENCES users(id) ON DELETE SET NULL,
    movement_date    TIMESTAMP       NOT NULL DEFAULT NOW(),
    reference_number VARCHAR(100)
);

-- ── İndeksler ──────────────────────────────────────────────────
CREATE INDEX IF NOT EXISTS idx_items_category_id    ON items(category_id);
CREATE INDEX IF NOT EXISTS idx_items_status         ON items(status);
CREATE INDEX IF NOT EXISTS idx_items_quantity       ON items(quantity);
CREATE INDEX IF NOT EXISTS idx_stock_movements_item ON stock_movements(item_id);
CREATE INDEX IF NOT EXISTS idx_stock_movements_date ON stock_movements(movement_date DESC);
CREATE INDEX IF NOT EXISTS idx_users_username       ON users(username);

-- ── updated_at otomatik güncelleme trigger ─────────────────────
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE OR REPLACE TRIGGER trg_items_updated_at
    BEFORE UPDATE ON items
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ── Seed Data (sadece geliştirme için) ────────────────────────
-- Admin kullanıcı (şifre: Admin123! — bcrypt hash)
INSERT INTO users (username, email, password_hash, first_name, last_name, role)
VALUES (
    'admin',
    'admin@envanter.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- Admin123!
    'Sistem',
    'Yöneticisi',
    'ADMIN'
) ON CONFLICT (username) DO NOTHING;

-- Temel kategoriler
INSERT INTO categories (name, description) VALUES
    ('Elektronik',     'Bilgisayar, telefon, tablet ve aksesuarlar'),
    ('Ofis Malzemeleri', 'Kırtasiye, yazıcı, kağıt'),
    ('Mobilya',        'Masa, sandalye, dolap'),
    ('Temizlik',       'Temizlik ürünleri ve ekipmanları'),
    ('Diğer',          'Sınıflandırılmamış ürünler')
ON CONFLICT (name) DO NOTHING;

-- ── Tamamlandı ─────────────────────────────────────────────────
SELECT 'envanter_db init tamamlandi — ' || NOW() AS init_status;
