-- SAMPLE DATA INSERTION SCRIPTS
-- Insert sample data for all tables
-- This version clears existing data first to avoid constraint violations

-- Clear existing data in reverse order (to respect foreign key constraints)
DELETE FROM public.articles_supprime;
DELETE FROM public.pending_articles;
DELETE FROM public.articles;
DELETE FROM public.niveau_6;
DELETE FROM public.niveau_5;
DELETE FROM public.niveau_4;
DELETE FROM public.niveau_3;
DELETE FROM public.niveau_2;
DELETE FROM public.niveau_1;

-- Reset sequences
ALTER SEQUENCE public.articles_id_seq RESTART WITH 1;
ALTER SEQUENCE public.articles_supprime_id_seq RESTART WITH 1;
ALTER SEQUENCE public.pending_articles_id_seq RESTART WITH 1;
ALTER SEQUENCE public.niveau_1_id_niveau_1_seq RESTART WITH 1;
ALTER SEQUENCE public.niveau_2_id_niveau_2_seq RESTART WITH 1;
ALTER SEQUENCE public.niveau_3_id_niveau_3_seq RESTART WITH 1;
ALTER SEQUENCE public.niveau_4_id_niveau_4_seq RESTART WITH 1;
ALTER SEQUENCE public.niveau_5_id_niveau_5_seq RESTART WITH 1;
ALTER SEQUENCE public.niveau_6_id_niveau_6_seq RESTART WITH 1;

-- ==============================================
-- 1. HIERARCHICAL CLASSIFICATION DATA (Niveau 1-6)
-- ==============================================

-- NIVEAU 1 (Top Level Categories)
INSERT INTO public.niveau_1 (id_niveau_1, niveau_1) VALUES 
    (1, 'Gros Œuvre'),
    (2, 'Second Œuvre'),
    (3, 'Aménagement Extérieur'),
    (4, 'VRD'),
    (5, 'Équipements Techniques'),
    (6, 'Fondations Spéciales');

-- NIVEAU_2 (Subcategories of niveau_1)
INSERT INTO public.niveau_2 (id_niveau_2, niveau_2, id_niv_1) VALUES 
    (1, 'Structure', 1),
    (2, 'Maçonnerie', 1),
    (3, 'Charpente', 1),
    (4, 'Couverture', 2),
    (5, 'Menuiserie', 2),
    (6, 'Plâtrerie', 2),
    (7, 'Carrelage', 2),
    (8, 'Peinture', 2),
    (9, 'Voirie', 3),
    (10, 'Espaces Verts', 3),
    (11, 'Clôtures', 3),
    (12, 'Canalisation', 4),
    (13, 'Électricité', 5),
    (14, 'Plomberie', 5),
    (15, 'Chauffage', 5),
    (16, 'Climatisation', 5);

-- NIVEAU_3
INSERT INTO public.niveau_3 (id_niveau_3, niveau_3, id_niv_2) VALUES 
    (1, 'Béton Armé', 1),
    (2, 'Précontraint', 1),
    (3, 'Métallique', 1),
    (4, 'Brique', 2),
    (5, 'Parpaing', 2),
    (6, 'Pierre', 2),
    (7, 'Bois Traditionnel', 3),
    (8, 'Métallique', 3),
    (9, 'Tuiles', 4),
    (10, 'Ardoise', 4),
    (11, 'Zinc', 4),
    (12, 'Bois', 5),
    (13, 'PVC', 5),
    (14, 'Aluminium', 5),
    (15, 'Plâtre', 6),
    (16, 'Cloisons', 6),
    (17, 'Faux Plafonds', 6),
    (18, 'Faïence', 7),
    (19, 'Grès', 7),
    (20, 'Marbre', 7),
    (21, 'Peinture Intérieure', 8),
    (22, 'Peinture Extérieure', 8),
    (23, 'Enduits', 8),
    (24, 'Enrobés', 9),
    (25, 'Béton', 9),
    (26, 'Pavés', 9),
    (27, 'Gazon', 10),
    (28, 'Plantation', 10),
    (29, 'Arrosage', 10),
    (30, 'Métalliques', 11),
    (31, 'Bois', 11),
    (32, 'Végétaux', 11),
    (33, 'AEP', 12),
    (34, 'Assainissement', 12),
    (35, 'Gaz', 12);

-- NIVEAU_4
INSERT INTO public.niveau_4 (niveau_4, id_niv_3) VALUES 
    ('Poutrelles', 1),
    ('Poteaux', 1),
    ('Poutres', 1),
    ('Dalles', 1),
    ('Hourdis', 2),
    ('Poutres Précontraintes', 2),
    ('Poteaux Précontraints', 2),
    ('Poutrelles Métalliques', 3),
    ('Poteaux Métalliques', 3),
    ('Poutres Métalliques', 3),
    ('Briques Pleines', 4),
    ('Briques Creuses', 4),
    ('Briques Alvéolées', 4),
    ('Parpaings 20', 5),
    ('Parpaings 15', 5),
    ('Parpaings 10', 5),
    ('Pierre Naturelle', 6),
    ('Pierre Reconstituée', 6),
    ('Pierre Artificielle', 6),
    ('Chevrons', 7),
    ('Fermettes', 7),
    ('Pannes', 7),
    ('Arbalétriers', 7),
    ('Poutres Métalliques', 8),
    ('Poteaux Métalliques', 8),
    ('Poutrelles Métalliques', 8),
    ('Tuiles Terre Cuite', 9),
    ('Tuiles Béton', 9),
    ('Tuiles Acier', 9),
    ('Ardoise Naturelle', 10),
    ('Ardoise Fibrociment', 10),
    ('Zinc', 11),
    ('Cuivre', 11),
    ('Acier', 11),
    ('Portes', 12),
    ('Fenêtres', 12),
    ('Volets', 12),
    ('Portes', 13),
    ('Fenêtres', 13),
    ('Volets', 13),
    ('Portes', 14),
    ('Fenêtres', 14),
    ('Volets', 14);

-- NIVEAU_5 (Flexible - can link to niveau_3 or niveau_4)
INSERT INTO public.niveau_5 (id_niveau_5, niveau_5, id_niv_3, id_niv_4) VALUES 
    (1, 'IPN', 3, NULL),
    (2, 'IPE', 3, NULL),
    (3, 'HEA', 3, NULL),
    (4, 'HEB', 3, NULL),
    (5, 'Plâtres Traditionnels', 6, NULL),
    (6, 'Plâtres Industriels', 6, NULL),
    (7, 'BA13', 6, NULL),
    (8, 'Placo Standard', 16, NULL),
    (9, 'Placo Hydro', 16, NULL),
    (10, 'Placo Phonique', 16, NULL),
    (11, 'Placo Standard', 17, NULL),
    (12, 'Placo Hydro', 17, NULL),
    (13, 'Placo Phonique', 17, NULL),
    (14, 'Placo Standard', 18, NULL),
    (15, 'Placo Hydro', 18, NULL),
    (16, 'Placo Phonique', 18, NULL),
    (17, '30x30', 19, NULL),
    (18, '40x40', 19, NULL),
    (19, '50x50', 19, NULL),
    (20, '30x60', 20, NULL),
    (21, '45x45', 20, NULL),
    (22, '60x60', 20, NULL),
    (23, '40x40', 21, NULL),
    (24, '50x50', 21, NULL),
    (25, '60x60', 21, NULL),
    (26, 'Peinture Acrylique', 22, NULL),
    (27, 'Peinture Glycéro', 22, NULL),
    (28, 'Peinture Époxy', 22, NULL),
    (29, 'Peinture Acrylique', 23, NULL),
    (30, 'Peinture Glycéro', 23, NULL),
    (31, 'Peinture Époxy', 23, NULL),
    (32, 'Enduit Traditionnel', 24, NULL),
    (33, 'Enduit Industriel', 24, NULL),
    (34, 'Enduit Monocouche', 24, NULL),
    -- Exemple lié directement à un niveau_4 (Portes, id_niv_4 = 35)
    (35, 'Portes intérieures bois standard', 12, 35);

-- NIVEAU_6 (Most flexible - can link to niveau_3, niveau_4, or niveau_5)
-- Fixed ID references - they should start from 1, not 37
INSERT INTO public.niveau_6 (niveau_6, id_niv_3, id_niv_4, id_niv_5) VALUES 
    ('IPN 100', 3, NULL, 1),
    ('IPN 120', 3, NULL, 1),
    ('IPN 140', 3, NULL, 1),
    ('IPN 160', 3, NULL, 1),
    ('IPE 100', 3, NULL, 2),
    ('IPE 120', 3, NULL, 2),
    ('IPE 140', 3, NULL, 2),
    ('IPE 160', 3, NULL, 2),
    ('HEA 100', 3, NULL, 3),
    ('HEA 120', 3, NULL, 3),
    ('HEA 140', 3, NULL, 3),
    ('HEA 160', 3, NULL, 3),
    ('HEB 100', 3, NULL, 4),
    ('HEB 120', 3, NULL, 4),
    ('HEB 140', 3, NULL, 4),
    ('HEB 160', 3, NULL, 4),
    ('Placo 9.5mm', 16, NULL, NULL),
    ('Placo 12.5mm', 16, NULL, NULL),
    ('Placo 15mm', 16, NULL, NULL),
    ('Placo 18mm', 16, NULL, NULL),
    ('Placo Hydro 12.5mm', 17, NULL, NULL),
    ('Placo Hydro 15mm', 17, NULL, NULL),
    ('Placo Phonique 12.5mm', 18, NULL, NULL),
    ('Placo Phonique 15mm', 18, NULL, NULL),
    ('Placo Standard BA13', 6, NULL, 7),
    ('Placo Hydro BA13', 16, NULL, 8),
    ('Placo Phonique BA13', 16, NULL, 9),
    ('Carrelage 30x30 Standard', 21, NULL, 25),
    ('Carrelage 40x40 Standard', 22, NULL, 26),
    ('Carrelage 50x50 Standard', 22, NULL, 27),
    ('Carrelage 30x60 Standard', 22, NULL, 28),
    ('Carrelage 45x45 Standard', 23, NULL, 29),
    ('Carrelage 60x60 Standard', 23, NULL, 30),
    ('Peinture Acrylique Blanche', 23, NULL, 31),
    ('Peinture Acrylique Grise', 23, NULL, 31),
    ('Peinture Glycéro Blanche', 24, NULL, 32),
    ('Peinture Glycéro Grise', 24, NULL, 32),
    ('Peinture Époxy Blanche', 24, NULL, 33),
    ('Peinture Époxy Grise', 24, NULL, 33),
    ('Enduit Traditionnel 15mm', 24, NULL, 33),
    ('Enduit Industriel 15mm', 24, NULL, 34),
    ('Enduit Monocouche 15mm', 24, NULL, 34),
    -- Exemples liés directement à un niveau_4 (Tuiles Terre Cuite, id_niv_4 = 27)
    ('Tuiles Terre Cuite 10/m²', 9, 27, NULL),
    ('Tuiles Terre Cuite 12/m²', 9, 27, NULL);

-- ==============================================
-- 2. ARTICLES TABLE (Main articles)
-- ==============================================
INSERT INTO public.articles (Date, nom_article, Unite, Type, Expertise, Fourniture, Cadence, Accessoires, Pertes, PU, Prix_Cible, Prix_estime, Prix_consulte, Rabais, Commentaires, user_id, Indice_de_confiance, files, fournisseur, id_niv_6) VALUES 
    ('2024-01-15', 'IPN 100 Acier S235', 'ml', 'Métallique', 'Structure', '45.50', '2.5', '3.20', '5%', '52.30', '52.00', NULL, NULL, '0%', 'Poutrelle IPN 100 en acier S235, longueur standard 6m', 'user-uuid-1', 4, 'ipn100.jpg', 1, 1),
    ('2024-01-15', 'IPN 120 Acier S235', 'ml', 'Métallique', 'Structure', '58.75', '2.8', '4.10', '5%', '67.20', NULL, NULL, '67.20', '0%', 'Poutrelle IPN 120 en acier S235, longueur standard 6m', 'user-uuid-1', 4, 'ipn120.jpg', 1, 2),
    ('2024-01-20', 'Sample Article N6-3', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 3', 'user-uuid-1', 3, NULL, 1, 3),
    ('2024-01-20', 'Sample Article N6-4', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 4', 'user-uuid-1', 3, NULL, 1, 4),
    ('2024-01-20', 'Sample Article N6-5', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 5', 'user-uuid-1', 3, NULL, 1, 5),
    ('2024-01-20', 'Sample Article N6-6', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 6', 'user-uuid-1', 3, NULL, 1, 6),
    ('2024-01-20', 'Sample Article N6-7', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 7', 'user-uuid-1', 3, NULL, 1, 7),
    ('2024-01-20', 'Sample Article N6-8', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 8', 'user-uuid-1', 3, NULL, 1, 8),
    ('2024-01-20', 'Sample Article N6-9', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 9', 'user-uuid-1', 3, NULL, 1, 9),
    ('2024-01-20', 'Sample Article N6-10', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 10', 'user-uuid-1', 3, NULL, 1, 10),
    ('2024-01-20', 'Sample Article N6-11', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 11', 'user-uuid-1', 3, NULL, 1, 11),
    ('2024-01-20', 'Sample Article N6-12', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 12', 'user-uuid-1', 3, NULL, 1, 12),
    ('2024-01-20', 'Sample Article N6-13', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 13', 'user-uuid-1', 3, NULL, 1, 13),
    ('2024-01-20', 'Sample Article N6-14', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 14', 'user-uuid-1', 3, NULL, 1, 14),
    ('2024-01-20', 'Sample Article N6-15', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 15', 'user-uuid-1', 3, NULL, 1, 15),
    ('2024-01-20', 'Sample Article N6-16', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 16', 'user-uuid-1', 3, NULL, 1, 16),
    ('2024-01-16', 'Placo BA13 Standard', 'm2', 'Plâtre', 'Cloisons', '3.25', '0.8', '0.50', '10%', '4.55', NULL, '4.00', NULL, '5%', 'Plaque de plâtre BA13 standard pour cloison', 'user-uuid-2', 3, 'placo_standard.jpg', 3, 17),
    ('2024-01-16', 'Placo BA13 Hydro', 'm2', 'Plâtre', 'Cloisons', '4.75', '0.8', '0.65', '10%', '6.20', '5.80', NULL, NULL, '5%', 'Plaque de plâtre BA13 hydrofuge pour pièces humides', 'user-uuid-2', 3, 'placo_hydro.jpg', 3, 18),
    ('2024-01-20', 'Sample Article N6-19', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 19', 'user-uuid-1', 3, NULL, 1, 19),
    ('2024-01-20', 'Sample Article N6-20', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 20', 'user-uuid-1', 3, NULL, 1, 20),
    ('2024-01-20', 'Sample Article N6-21', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 21', 'user-uuid-1', 3, NULL, 1, 21),
    ('2024-01-20', 'Sample Article N6-22', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 22', 'user-uuid-1', 3, NULL, 1, 22),
    ('2024-01-20', 'Sample Article N6-23', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 23', 'user-uuid-1', 3, NULL, 1, 23),
    ('2024-01-20', 'Sample Article N6-24', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 24', 'user-uuid-1', 3, NULL, 1, 24),
    ('2024-01-20', 'Sample Article N6-25', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 25', 'user-uuid-1', 3, NULL, 1, 25),
    ('2024-01-20', 'Sample Article N6-26', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 26', 'user-uuid-1', 3, NULL, 1, 26),
    ('2024-01-20', 'Sample Article N6-27', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 27', 'user-uuid-1', 3, NULL, 1, 27),
    ('2024-01-20', 'Sample Article N6-28', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 28', 'user-uuid-1', 3, NULL, 1, 28),
    ('2024-01-20', 'Sample Article N6-29', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 29', 'user-uuid-1', 3, NULL, 1, 29),
    ('2024-01-20', 'Sample Article N6-30', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 30', 'user-uuid-1', 3, NULL, 1, 30),
    ('2024-01-17', 'Carrelage 30x30 Grès Blanc', 'm2', 'Céramique', 'Sol', '18.50', '1.2', '2.10', '8%', '23.70', NULL, '21.00', NULL, '0%', 'Carrelage grès 30x30cm, couleur blanche, épaisseur 8mm', 'user-uuid-3', 3, 'carrelage_30x30.jpg', 2, 31),
    ('2024-01-17', 'Carrelage 40x40 Grès Beige', 'm2', 'Céramique', 'Sol', '22.75', '1.3', '2.50', '8%', '28.55', NULL, NULL, '30.00', '0%', 'Carrelage grès 40x40cm, couleur beige, épaisseur 9mm', 'user-uuid-3', 3, 'carrelage_40x40.jpg', 2, 32),
    ('2024-01-18', 'Peinture Acrylique Blanche 10L', 'L', 'Acrylique', 'Peinture', '2.85', '0.5', '0.30', '15%', '3.70', '3.50', NULL, NULL, '0%', 'Peinture acrylique blanche mate, rendement 8m2/L', 'user-uuid-4', 3, 'peinture_acrylique.jpg', 5, 33),
    ('2024-01-18', 'Peinture Glycéro Blanche 10L', 'L', 'Glycéro', 'Peinture', '4.20', '0.6', '0.45', '15%', '5.55', NULL, '5.00', NULL, '0%', 'Peinture glycéro blanche satinée, rendement 10m2/L', 'user-uuid-4', 3, 'peinture_glycero.jpg', 5, 34),
    ('2024-01-20', 'Sample Article N6-35', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 35', 'user-uuid-1', 3, NULL, 1, 35),
    ('2024-01-20', 'Sample Article N6-36', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 36', 'user-uuid-1', 3, NULL, 1, 36),
    ('2024-01-20', 'Sample Article N6-37', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 37', 'user-uuid-1', 3, NULL, 1, 37),
    ('2024-01-20', 'Sample Article N6-38', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 38', 'user-uuid-1', 3, NULL, 1, 38),
    ('2024-01-20', 'Sample Article N6-39', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 39', 'user-uuid-1', 3, NULL, 1, 39),
    ('2024-01-19', 'Enduit Traditionnel 15mm', 'm2', 'Enduit', 'Façade', '8.75', '1.5', '1.20', '12%', '12.45', NULL, NULL, '13.50', '0%', 'Enduit traditionnel 15mm pour façade extérieure', 'user-uuid-2', 3, 'enduit_traditionnel.jpg', 4, 40),
    ('2024-01-19', 'Enduit Industriel 15mm', 'm2', 'Enduit', 'Façade', '9.50', '1.6', '1.30', '12%', '13.70', NULL, NULL, '14.50', '0%', 'Enduit industriel 15mm pour façade extérieure', 'user-uuid-2', 3, 'enduit_industriel.jpg', 4, 41),
    ('2024-01-19', 'Enduit Monocouche 15mm', 'm2', 'Enduit', 'Façade', '12.25', '1.8', '1.50', '12%', '16.95', '15.80', NULL, NULL, '0%', 'Enduit monocouche 15mm pour façade extérieure', 'user-uuid-2', 3, 'enduit_monocouche.jpg', 4, 42),
    ('2024-01-20', 'Sample Article N6-43', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 43', 'user-uuid-1', 3, NULL, 1, 43),
    ('2024-01-20', 'Sample Article N6-44', 'u', 'Sample', 'Sample', '0.00', '0.0', '0.0', '0%', '0.00', NULL, '0.00', NULL, '0%', 'Auto-generated sample article for niveau_6 id 44', 'user-uuid-1', 3, NULL, 1, 44);

-- ==============================================
-- 3. PENDING_ARTICLES TABLE (Articles awaiting approval)
-- ==============================================
INSERT INTO public.pending_articles (Date, nom_article, Unite, Type, Expertise, Fourniture, Cadence, Accessoires, Pertes, PU, Prix_Cible, Prix_estime, Prix_consulte, Rabais, Commentaires, created_by, status, submitted_at, updated_at, Indice_de_confiance, files, fournisseur, id_niv_6) VALUES 
    ('2024-02-01', 'IPE 180 Acier S355', 'ml', 'Métallique', 'Structure', '95.50', '3.2', '6.80', '5%', '108.50', '105.00', NULL, NULL, '0%', 'Nouvelle demande pour IPE 180 en acier S355', 'user-uuid-1', 'En attente', '2024-02-01 10:30:00', '2024-02-01 10:30:00', 3, 'ipe180_drawing.pdf', 1, 8),
    ('2024-02-02', 'Placo BA13 Phonique', 'm2', 'Plâtre', 'Cloisons', '5.25', '0.9', '0.75', '10%', '6.90', NULL, '6.20', NULL, '5%', 'Plaque de plâtre BA13 phonique pour bureau', 'user-uuid-2', 'En attente', '2024-02-02 14:15:00', '2024-02-02 14:15:00', 3, 'placo_phonique_specs.pdf', 3, 19),
    ('2024-02-03', 'Carrelage 60x60 Grès Noir', 'm2', 'Céramique', 'Sol', '28.75', '1.5', '3.20', '8%', '35.45', NULL, NULL, '37.00', '0%', 'Carrelage grès 60x60cm, couleur noir, épaisseur 10mm', 'user-uuid-3', 'En attente', '2024-02-03 09:45:00', '2024-02-03 09:45:00', 4, 'carrelage_60x60_noir.jpg', 2, 33),
    ('2024-01-25', 'Peinture Époxy Grise 5L', 'L', 'Époxy', 'Peinture', '12.50', '0.8', '1.10', '15%', '15.40', NULL, '14.20', NULL, '0%', 'Peinture époxy grise pour sol industriel', 'user-uuid-4', 'Rejeté', '2024-01-25 16:20:00', '2024-01-26 09:30:00', 2, 'peinture_epoxy_rejected.pdf', 5, 34),
    ('2024-01-28', 'Brique Pleine 20x50x25', 'u', 'Brique', 'Maçonnerie', '0.85', '0.2', '0.05', '5%', '1.10', '1.00', NULL, NULL, '0%', 'Brique pleine 20x50x25cm pour maçonnerie', 'user-uuid-1', 'Approuvé', '2024-01-28 11:00:00', '2024-01-29 14:45:00', 4, 'brique_pleine_approved.pdf', 1, 12);

-- ==============================================
-- 4. ARTICLES_SUPPRIME TABLE (Deleted articles)
-- ==============================================
INSERT INTO public.articles_supprime (Date, nom_article, Unite, Type, Expertise, Fourniture, Cadence, Accessoires, Pertes, PU, Prix_Cible, Prix_estime, Prix_consulte, Rabais, Commentaires, "user", Indice_de_confiance, files, deleted_by, fournisseur, id_niv_6) VALUES 
    ('2024-01-10', 'Vieux Modèle IPN 80', 'ml', 'Métallique', 'Structure', '35.20', '2.0', '2.50', '5%', '40.70', '38.00', NULL, NULL, '0%', 'Ancien modèle obsolète', 'user-uuid-1', 2, 'old_ipn80.jpg', 'user-uuid-2', 1, NULL),
    ('2024-01-12', 'Placo BA10 Standard', 'm2', 'Plâtre', 'Cloisons', '2.85', '0.7', '0.40', '10%', '3.95', NULL, '3.50', NULL, '5%', 'Produit remplacé par BA13', 'user-uuid-2', 2, 'placo_ba10_old.jpg', 'user-uuid-3', 3, 16),
    ('2024-01-14', 'Carrelage 25x25 Rouge', 'm2', 'Céramique', 'Sol', '15.25', '1.0', '1.80', '8%', '19.05', NULL, NULL, '20.00', '0%', 'Format non standard supprimé', 'user-uuid-3', 2, 'carrelage_25x25_old.jpg', 'user-uuid-4', 2, 30);
