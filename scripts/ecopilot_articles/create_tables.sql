-- SQL Scripts to Create Tables and Insert Sample Data
-- Based on DBtables.md schema

-- 1. ARTICLES TABLE (Main articles table with pricing, specifications, and metadata)
CREATE TABLE IF NOT EXISTS public.articles (
    ID SERIAL PRIMARY KEY,
    Date DATE,
    nom_article VARCHAR(255),
    Unite VARCHAR(50),
    Type VARCHAR(100),
    Expertise VARCHAR(100),
    Fourniture VARCHAR(50),
    Cadence VARCHAR(50),
    Accessoires VARCHAR(50),
    Pertes VARCHAR(10),
    PU VARCHAR(50),
    Prix_Cible VARCHAR(50),
    Prix_estime VARCHAR(50),
    Prix_consulte VARCHAR(50),
    Rabais VARCHAR(10),
    Commentaires TEXT,
    "user" VARCHAR(255),
    Indice_de_confiance INTEGER DEFAULT 3,
    files TEXT,
    fournisseur INTEGER,
    id_niv_6 INTEGER
);

-- 2. ARTICLES_SUPPRIME TABLE (Soft-deleted articles with deleted_by field)
CREATE TABLE IF NOT EXISTS public.articles_supprime (
    ID SERIAL PRIMARY KEY,
    Date DATE,
    nom_article VARCHAR(255),
    Unite VARCHAR(50),
    Type VARCHAR(100),
    Expertise VARCHAR(100),
    Fourniture VARCHAR(50),
    Cadence VARCHAR(50),
    Accessoires VARCHAR(50),
    Pertes VARCHAR(10),
    PU VARCHAR(50),
    Prix_Cible VARCHAR(50),
    Prix_estime VARCHAR(50),
    Prix_consulte VARCHAR(50),
    Rabais VARCHAR(10),
    Commentaires TEXT,
    "user" VARCHAR(255),
    Indice_de_confiance INTEGER DEFAULT 3,
    files TEXT,
    deleted_by VARCHAR(255),
    fournisseur INTEGER,
    id_niv_6 INTEGER
);

-- 3. PENDING_ARTICLES TABLE (Articles awaiting approval with status tracking)
CREATE TABLE IF NOT EXISTS public.pending_articles (
    ID SERIAL PRIMARY KEY,
    Date DATE NOT NULL,
    nom_article VARCHAR(255) NOT NULL,
    Unite VARCHAR(50) NOT NULL,
    Type VARCHAR(100) NOT NULL,
    Expertise VARCHAR(100) NOT NULL,
    Fourniture NUMERIC(10,2) DEFAULT 0.00,
    Cadence NUMERIC(10,2) DEFAULT 0.00,
    Accessoires NUMERIC(10,2) DEFAULT 0.00,
    Pertes VARCHAR(10) DEFAULT '0%',
    PU VARCHAR(50) NOT NULL,
    Prix_Cible NUMERIC(10,2) DEFAULT 0.00,
    Prix_estime NUMERIC(10,2) DEFAULT 0.00,
    Prix_consulte NUMERIC(10,2) DEFAULT 0.00,
    Rabais VARCHAR(10) DEFAULT '0%',
    Commentaires TEXT DEFAULT '',
    created_by VARCHAR(255) NOT NULL,
    status VARCHAR(50) DEFAULT 'En attente',
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    accepted_by VARCHAR(255),
    reviewed_at TIMESTAMP,
    Indice_de_confiance INTEGER DEFAULT 3,
    files TEXT,
    rejected_by VARCHAR(255),
    fournisseur INTEGER,
    id_niv_6 INTEGER,
    approved_article_id INTEGER
);

-- 4. HIERARCHICAL CLASSIFICATION TABLES (Niveau 1-6)
-- NIVEAU_1: Top-level category
CREATE TABLE IF NOT EXISTS public.niveau_1 (
    id_niveau_1 SERIAL PRIMARY KEY,
    niveau_1 VARCHAR(255)
);

-- NIVEAU_2: Links to niveau_1
CREATE TABLE IF NOT EXISTS public.niveau_2 (
    id_niveau_2 SERIAL PRIMARY KEY,
    niveau_2 VARCHAR(255),
    id_niv_1 INTEGER REFERENCES public.niveau_1(id_niveau_1)
);

-- NIVEAU_3: Links to niveau_2
CREATE TABLE IF NOT EXISTS public.niveau_3 (
    id_niveau_3 SERIAL PRIMARY KEY,
    niveau_3 VARCHAR(255),
    id_niv_2 INTEGER REFERENCES public.niveau_2(id_niveau_2)
);

-- NIVEAU_4: Links to niveau_3
CREATE TABLE IF NOT EXISTS public.niveau_4 (
    id_niveau_4 SERIAL PRIMARY KEY,
    niveau_4 VARCHAR(255),
    id_niv_3 INTEGER NOT NULL REFERENCES public.niveau_3(id_niveau_3)
);

-- NIVEAU_5: Links to niveau_3 or niveau_4 (flexible structure)
CREATE TABLE IF NOT EXISTS public.niveau_5 (
    id_niveau_5 SERIAL PRIMARY KEY,
    niveau_5 VARCHAR(255),
    id_niv_3 INTEGER NOT NULL REFERENCES public.niveau_3(id_niveau_3),
    id_niv_4 INTEGER REFERENCES public.niveau_4(id_niveau_4)
);

-- NIVEAU_6: Links to niveau_3, niveau_4, or niveau_5 (most flexible)
CREATE TABLE IF NOT EXISTS public.niveau_6 (
    id_niveau_6 SERIAL PRIMARY KEY,
    niveau_6 VARCHAR(255),
    id_niv_3 INTEGER NOT NULL REFERENCES public.niveau_3(id_niveau_3),
    id_niv_4 INTEGER REFERENCES public.niveau_4(id_niveau_4),
    id_niv_5 INTEGER REFERENCES public.niveau_5(id_niveau_5)
);

-- FOREIGN KEY CONSTRAINTS (Only for hierarchical tables - no user/fournisseur FKs)
ALTER TABLE public.articles ADD CONSTRAINT fk_articles_niveau6 FOREIGN KEY (id_niv_6) REFERENCES public.niveau_6(id_niveau_6);

ALTER TABLE public.articles_supprime ADD CONSTRAINT fk_articles_supprime_niveau6 FOREIGN KEY (id_niv_6) REFERENCES public.niveau_6(id_niveau_6);

ALTER TABLE public.pending_articles ADD CONSTRAINT fk_pending_articles_niveau6 FOREIGN KEY (id_niv_6) REFERENCES public.niveau_6(id_niveau_6);
ALTER TABLE public.pending_articles ADD CONSTRAINT fk_pending_articles_approved FOREIGN KEY (approved_article_id) REFERENCES public.articles(ID);
