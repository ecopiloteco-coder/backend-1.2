-- ======================================================
-- PROJECT MANAGEMENT TABLES
-- Tables: client, projets, projet_lot, projet_article, projet_equipe, ouvrage, bloc, structure
-- ======================================================

-- ======================================================
-- DROP ALL TABLES (reverse dependency order)
-- ======================================================
DROP TABLE IF EXISTS public.projet_article  CASCADE;
DROP TABLE IF EXISTS public.projet_equipe   CASCADE;
DROP TABLE IF EXISTS public.structure        CASCADE;
DROP TABLE IF EXISTS public.bloc             CASCADE;
DROP TABLE IF EXISTS public.ouvrage          CASCADE;
DROP TABLE IF EXISTS public.projet_lot       CASCADE;
DROP TABLE IF EXISTS public.projets          CASCADE;
DROP TABLE IF EXISTS public.client           CASCADE;


-- ======================================================
-- 1. CLIENT TABLE
-- ======================================================
CREATE TABLE IF NOT EXISTS public.client (
    id SERIAL PRIMARY KEY,
    nom_client VARCHAR(255) NOT NULL,
    marge_brut REAL DEFAULT 0.0,
    marge_net REAL DEFAULT 0.0,
    agence VARCHAR(255),
    responsable VARCHAR(255),
    effectif_chantier TEXT
);

-- ======================================================
-- 2. PROJETS TABLE
-- ======================================================
CREATE TABLE IF NOT EXISTS public.projets (
    id SERIAL PRIMARY KEY,
    Nom_Projet VARCHAR(255) NOT NULL,
    Date_Limite DATE,
    Ajouté_par INTEGER NOT NULL,
    Description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    adresse VARCHAR(500),
    Cout REAL DEFAULT 0.0,
    Date_Debut DATE,
    client INTEGER,
    file TEXT,
    prix_vente REAL DEFAULT 0.0,
    etat VARCHAR(100) DEFAULT 'En cours'
);

-- ======================================================
-- 3. PROJET_LOT TABLE
-- ======================================================
CREATE TABLE IF NOT EXISTS public.projet_lot (
    id_projet_lot SERIAL PRIMARY KEY,
    id_projet INTEGER NOT NULL,
    id_lot INTEGER NOT NULL,
    designation_lot VARCHAR(255),
    prix_total REAL DEFAULT 0.0,
    prix_vente REAL DEFAULT 0.0
);

-- ======================================================
-- 4. PROJET_ARTICLE TABLE
-- ======================================================
CREATE TABLE IF NOT EXISTS public.projet_article (
    id SERIAL PRIMARY KEY,
    article INTEGER,
    quantite INTEGER DEFAULT 0,
    prix_total_ht REAL DEFAULT 0.0,
    tva REAL DEFAULT 20.0,
    total_ttc REAL DEFAULT 0.0,
    localisation VARCHAR(255),
    description TEXT,
    nouv_prix REAL,
    designation_article VARCHAR(255),
    structure INTEGER,
    article_import VARCHAR(255),
    unite_import VARCHAR(50)
);

-- ======================================================
-- 5. PROJET_EQUIPE TABLE
-- ======================================================
CREATE TABLE IF NOT EXISTS public.projet_equipe (
    id SERIAL PRIMARY KEY,
    equipe INTEGER NOT NULL,
    projet INTEGER NOT NULL
);

-- ======================================================
-- 6. OUVRAGE TABLE
-- ======================================================
CREATE TABLE IF NOT EXISTS public.ouvrage (
    id SERIAL PRIMARY KEY,
    nom_ouvrage VARCHAR(255) NOT NULL,
    prix_total REAL DEFAULT 0.0,
    designation VARCHAR(500),
    projet_lot INTEGER
);

-- ======================================================
-- 7. BLOC TABLE
-- ======================================================
CREATE TABLE IF NOT EXISTS public.bloc (
    id SERIAL PRIMARY KEY,
    nom_bloc VARCHAR(255) NOT NULL,
    unite VARCHAR(50),
    quantite INTEGER DEFAULT 0,
    pu REAL DEFAULT 0.0,
    pt REAL DEFAULT 0.0,
    designation VARCHAR(500),
    ouvrage INTEGER
);

-- ======================================================
-- 8. STRUCTURE TABLE
-- ======================================================
CREATE TABLE IF NOT EXISTS public.structure (
    id_structure SERIAL PRIMARY KEY,
    ouvrage INTEGER,
    bloc INTEGER,
    action VARCHAR(100)
);

-- ======================================================
-- FOREIGN KEY CONSTRAINTS (INTERNAL ONLY)
-- ======================================================

-- Client constraints (internal to project tables)
ALTER TABLE public.projets 
    ADD CONSTRAINT fk_projets_client 
    FOREIGN KEY (client) REFERENCES public.client(id);

-- Projet_lot constraints (internal to project tables)
ALTER TABLE public.projet_lot 
    ADD CONSTRAINT fk_projet_lot_projet 
    FOREIGN KEY (id_projet) REFERENCES public.projets(id);

-- Projet_article constraints (internal to project tables)
ALTER TABLE public.projet_article 
    ADD CONSTRAINT fk_projet_article_structure 
    FOREIGN KEY (structure) REFERENCES public.structure(id_structure);

-- Projet_equipe constraints (internal to project tables)
ALTER TABLE public.projet_equipe 
    ADD CONSTRAINT fk_projet_equipe_projet 
    FOREIGN KEY (projet) REFERENCES public.projets(id);

-- Ouvrage constraints (internal to project tables)
ALTER TABLE public.ouvrage 
    ADD CONSTRAINT fk_ouvrage_projet_lot 
    FOREIGN KEY (projet_lot) REFERENCES public.projet_lot(id_projet_lot);

-- Bloc constraints (internal to project tables)
ALTER TABLE public.bloc 
    ADD CONSTRAINT fk_bloc_ouvrage 
    FOREIGN KEY (ouvrage) REFERENCES public.ouvrage(id);

-- Structure constraints (internal to project tables)
ALTER TABLE public.structure 
    ADD CONSTRAINT fk_structure_ouvrage 
    FOREIGN KEY (ouvrage) REFERENCES public.ouvrage(id);

ALTER TABLE public.structure 
    ADD CONSTRAINT fk_structure_bloc 
    FOREIGN KEY (bloc) REFERENCES public.bloc(id);

-- ======================================================
-- SAMPLE DATA INSERTION
-- ======================================================

-- Insert sample clients
INSERT INTO public.client (nom_client, marge_brut, marge_net, agence, responsable, effectif_chantier) VALUES 
    ('SARL Construction Plus', 15.5, 12.8, 'Paris', 'Jean Martin', '5 personnes'),
    ('Entreprise Dupont SA', 18.2, 15.0, 'Lyon', 'Marie Dupont', '8 personnes'),
    ('Promoteur Immobilier XYZ', 22.0, 18.5, 'Marseille', 'Pierre Bernard', '12 personnes'),
    ('Société Bâtiment Moderne', 16.8, 14.2, 'Toulouse', 'Sophie Leroy', '6 personnes'),
    ('Construction Innovation', 19.5, 16.8, 'Nice', 'Lucas Petit', '10 personnes');

-- Insert sample projets (no foreign key to users table)
INSERT INTO public.projets (Nom_Projet, Date_Limite, Ajouté_par, Description, adresse, Cout, Date_Debut, client, prix_vente, etat) VALUES 
    ('Résidence Les Jardins', '2024-06-30', 1, 'Construction d''une résidence de 20 logements', '123 Rue des Fleurs, Paris', 2500000.00, '2024-01-15', 1, 3200000.00, 'En cours'),
    ('Centre Commercial Ouest', '2024-09-15', 2, 'Rénovation complète du centre commercial', '456 Avenue du Commerce, Lyon', 1800000.00, '2024-02-01', 2, 2300000.00, 'En cours'),
    ('Bureaux Modernes', '2024-05-20', 3, 'Construction de bureaux écologiques', '789 Boulevard du Progrès, Marseille', 3200000.00, '2024-01-10', 3, 4100000.00, 'Planifié'),
    ('Hôtel de Luxe', '2024-12-31', 4, 'Construction d''un hôtel 4 étoiles', '321 Rue du Parc, Toulouse', 4500000.00, '2024-03-01', 4, 5800000.00, 'En cours'),
    ('École Primaire', '2024-08-10', 5, 'Construction d''une école primaire', '654 Avenue de l''Éducation, Nice', 1500000.00, '2024-02-15', 5, 1950000.00, 'Planifié');

-- Insert sample projet_lot (no foreign key to niveau_2 table)
INSERT INTO public.projet_lot (id_projet, id_lot, designation_lot, prix_total, prix_vente) VALUES 
    (1, 1, 'Gros œuvre - Fondations', 450000.00, 580000.00),
    (1, 2, 'Gros œuvre - Structure', 680000.00, 870000.00),
    (1, 3, 'Second œuvre - Cloisons', 220000.00, 285000.00),
    (2, 4, 'Rénovation - Façade', 320000.00, 410000.00),
    (2, 5, 'Rénovation - Toiture', 280000.00, 360000.00),
    (3, 6, 'Structure Métallique', 850000.00, 1090000.00),
    (3, 7, 'Menuiseries Extérieures', 420000.00, 540000.00),
    (4, 8, 'Travaux de Luxe', 1200000.00, 1540000.00),
    (5, 9, 'Équipements Scolaires', 380000.00, 490000.00),
    (5, 10, 'Espaces Extérieurs', 220000.00, 285000.00);

-- Insert sample ouvrage
INSERT INTO public.ouvrage (nom_ouvrage, prix_total, designation, projet_lot) VALUES 
    ('Fondations Bâtiment A', 150000.00, 'Fondations en béton armé pour bâtiment A', 1),
    ('Fondations Bâtiment B', 120000.00, 'Fondations en béton armé pour bâtiment B', 1),
    ('Poteaux et Poutres', 280000.00, 'Structure porteuse en béton armé', 2),
    ('Dalle 1er Étage', 200000.00, 'Dalle béton du 1er étage', 2),
    ('Dalle 2e Étage', 200000.00, 'Dalle béton du 2e étage', 2),
    ('Façade Principale', 180000.00, 'Rénovation complète de la façade principale', 4),
    ('Façade Latérale', 140000.00, 'Rénovation de la façade latérale', 4),
    ('Toiture Principale', 280000.00, 'Rénovation complète de la toiture', 5),
    ('Charpente Métallique', 450000.00, 'Charpente métallique principale', 6),
    ('Charpente Secondaire', 400000.00, 'Charpente métallique secondaire', 6),
    ('Menuiseries Bureau 1', 120000.00, 'Menuiseries aluminium pour bureau 1', 7),
    ('Menuiseries Bureau 2', 100000.00, 'Menuiseries aluminium pour bureau 2', 7),
    ('Réception Hôtel', 500000.00, 'Travaux de luxe pour la réception', 8),
    ('Suites Hôtel', 700000.00, 'Travaux de luxe pour les suites', 8),
    ('Salles de Classe', 250000.00, 'Aménagement des salles de classe', 9),
    ('Gymnase', 130000.00, 'Construction du gymnase', 9),
    ('Cour de Récréation', 120000.00, 'Aménagement de la cour de récréation', 10),
    ('Parking École', 100000.00, 'Construction du parking', 10);

-- Insert sample bloc
INSERT INTO public.bloc (nom_bloc, unite, quantite, pu, pt, designation, ouvrage) VALUES 
    ('Béton Fondation A', 'm3', 150, 85.50, 12825.00, 'Béton prêt à l''emploi C25/30', 1),
    ('Acier Fondation A', 'kg', 12000, 1.85, 22200.00, 'Acier HA fe 500', 1),
    ('Béton Fondation B', 'm3', 120, 85.50, 10260.00, 'Béton prêt à l''emploi C25/30', 2),
    ('Acier Fondation B', 'kg', 9500, 1.85, 17575.00, 'Acier HA fe 500', 2),
    ('Béton Poteaux', 'm3', 85, 88.00, 7480.00, 'Béton prêt à l''emploi C30/37', 3),
    ('Acier Poteaux', 'kg', 8500, 1.90, 16150.00, 'Acier HA fe 500', 3),
    ('Béton Dalle 1er', 'm3', 120, 87.00, 10440.00, 'Béton prêt à l''emploi C25/30', 4),
    ('Acier Dalle 1er', 'kg', 7200, 1.88, 13536.00, 'Acier HA fe 500', 4),
    ('Béton Dalle 2e', 'm3', 120, 87.00, 10440.00, 'Béton prêt à l''emploi C25/30', 5),
    ('Acier Dalle 2e', 'kg', 7200, 1.88, 13536.00, 'Acier HA fe 500', 5),
    ('Enduit Façade Principale', 'm2', 450, 12.50, 5625.00, 'Enduit traditionnel 15mm', 6),
    ('Peinture Façade Principale', 'm2', 450, 8.75, 3937.50, 'Peinture façade acrylique', 6),
    ('Enduit Façade Latérale', 'm2', 320, 12.50, 4000.00, 'Enduit traditionnel 15mm', 7),
    ('Peinture Façade Latérale', 'm2', 320, 8.75, 2800.00, 'Peinture façade acrylique', 7),
    ('Tuiles', 'u', 2800, 2.85, 7980.00, 'Tuiles mécaniques', 8),
    ('Liteaux', 'ml', 850, 4.20, 3570.00, 'Liteaux 50x50mm', 8),
    ('Poutrelles Métal', 'ml', 280, 95.50, 26740.00, 'Poutrelles IPN 180', 9),
    ('Poteaux Métal', 'ml', 120, 125.00, 15000.00, 'Poteaux HEB 160', 9),
    ('Poutrelles Secondaires', 'ml', 240, 85.00, 20400.00, 'Poutrelles IPN 160', 10),
    ('Poteaux Secondaires', 'ml', 95, 98.00, 9310.00, 'Poteaux HEB 140', 10),
    ('Fenêtres Bureau 1', 'u', 24, 450.00, 10800.00, 'Fenêtres aluminium double vitrage', 11),
    ('Portes Bureau 1', 'u', 8, 380.00, 3040.00, 'Portes aluminium', 11),
    ('Fenêtres Bureau 2', 'u', 18, 450.00, 8100.00, 'Fenêtres aluminium double vitrage', 12),
    ('Portes Bureau 2', 'u', 6, 380.00, 2280.00, 'Portes aluminium', 12),
    ('Marbre Réception', 'm2', 85, 185.00, 15725.00, 'Marbre blanc Carrare', 13),
    ('Lustres Réception', 'u', 8, 1200.00, 9600.00, 'Lustres cristal', 13),
    ('Parquet Suites', 'm2', 320, 65.00, 20800.00, 'Parquet chêne massif', 14),
    ('Papiers Peints', 'm2', 280, 25.50, 7140.00, 'Papiers peints luxe', 14),
    ('Carrelage Salles', 'm2', 450, 22.75, 10237.50, 'Carrelage 40x40 grès', 15),
    ('Peinture Salles', 'm2', 850, 4.20, 3570.00, 'Peinture acrylique', 15),
    ('Parquet Gymnase', 'm2', 280, 38.50, 10780.00, 'Parquet sportif', 16),
    ('Gradins Gymnase', 'u', 150, 85.00, 12750.00, 'Gradins pliables', 16),
    ('Béton Cour', 'm2', 450, 12.50, 5625.00, 'Béton désactivé', 17),
    ('Jeux Cour', 'u', 15, 850.00, 12750.00, 'Jeux d''extérieur', 17),
    ('Béton Parking', 'm2', 380, 8.75, 3325.00, 'Béton compacté', 18),
    ('Marquage Parking', 'ml', 120, 12.50, 1500.00, 'Marquage routier', 18);

-- Insert sample structure
INSERT INTO public.structure (ouvrage, bloc, action) VALUES 
    (1, 1, 'Couler'),
    (1, 2, 'Assembler'),
    (2, 3, 'Couler'),
    (2, 4, 'Assembler'),
    (3, 5, 'Monter'),
    (3, 6, 'Fixer'),
    (4, 7, 'Couler'),
    (4, 8, 'Assembler'),
    (5, 9, 'Couler'),
    (5, 10, 'Assembler'),
    (6, 11, 'Appliquer'),
    (6, 12, 'Peindre'),
    (7, 13, 'Appliquer'),
    (7, 14, 'Peindre'),
    (8, 15, 'Poser'),
    (8, 16, 'Fixer'),
    (9, 17, 'Souder'),
    (9, 18, 'Boulonner'),
    (10, 19, 'Souder'),
    (10, 20, 'Boulonner'),
    (11, 21, 'Poser'),
    (11, 22, 'Régler'),
    (12, 23, 'Poser'),
    (12, 24, 'Régler'),
    (13, 25, 'Poser'),
    (13, 26, 'Installer'),
    (14, 27, 'Poser'),
    (14, 28, 'Coller'),
    (15, 29, 'Poser'),
    (15, 30, 'Peindre'),
    (16, 31, 'Poser'),
    (16, 32, 'Fixer'),
    (17, 33, 'Couler'),
    (17, 34, 'Installer'),
    (18, 35, 'Couler'),
    (18, 36, 'Marquer');

-- Insert sample projet_equipe (no foreign key to users table)
INSERT INTO public.projet_equipe (equipe, projet) VALUES 
    (1, 1), (2, 1), (3, 1),  -- Équipe Résidence Les Jardins
    (2, 2), (4, 2), (5, 2),  -- Équipe Centre Commercial
    (3, 3), (6, 3), (7, 3),  -- Équipe Bureaux Modernes
    (4, 4), (8, 4), (9, 4),  -- Équipe Hôtel de Luxe
    (5, 5), (10, 5), (1, 5); -- Équipe École Primaire

-- Insert sample projet_article (no foreign key to articles table, only internal structure references)
INSERT INTO public.projet_article (article, quantite, prix_total_ht, tva, total_ttc, localisation, description, nouv_prix, designation_article, structure, article_import, unite_import) VALUES 
    (1, 150, 7845.00, 20.0, 9414.00, 'Bâtiment A - Fondations', 'Béton C25/30 pour fondations', 52.30, 'Béton prêt à l''emploi C25/30', 1, 'BETON-C25', 'm3'),
    (2, 12000, 22200.00, 20.0, 26640.00, 'Bâtiment A - Fondations', 'Acier HA fe 500 pour fondations', 1.85, 'Acier HA fe 500', 2, 'ACIER-HA500', 'kg'),
    (3, 85, 7480.00, 20.0, 8976.00, 'Structure - Poteaux', 'Béton C30/37 pour poteaux', 88.00, 'Béton prêt à l''emploi C30/37', 5, 'BETON-C30', 'm3'),
    (4, 120, 10440.00, 20.0, 12528.00, '1er Étage - Dalle', 'Béton C25/30 pour dalle', 87.00, 'Béton prêt à l''emploi C25/30', 7, 'BETON-C25', 'm3'),
    (5, 450, 5625.00, 20.0, 6750.00, 'Façade Principale', 'Enduit traditionnel 15mm', 12.50, 'Enduit traditionnel', 11, 'ENDUIT-TRAD', 'm2'),
    (6, 450, 3937.50, 20.0, 4725.00, 'Façade Principale', 'Peinture façade acrylique', 8.75, 'Peinture façade acrylique', 12, 'PEINT-FACADE', 'm2'),
    (7, 2800, 7980.00, 20.0, 9576.00, 'Toiture', 'Tuiles mécaniques', 2.85, 'Tuiles mécaniques', 15, 'TUILES-MECA', 'u'),
    (8, 280, 26740.00, 20.0, 32088.00, 'Charpente Principale', 'Poutrelles IPN 180', 95.50, 'Poutrelles IPN 180', 17, 'IPN-180', 'ml'),
    (9, 24, 10800.00, 20.0, 12960.00, 'Bureau 1 - Menuiseries', 'Fenêtres aluminium double vitrage', 450.00, 'Fenêtres aluminium', 21, 'FENETRE-ALU', 'u'),
    (10, 320, 20800.00, 20.0, 24960.00, 'Suites Hôtel', 'Parquet chêne massif', 65.00, 'Parquet chêne massif', 27, 'PARQUET-CHENE', 'm2');