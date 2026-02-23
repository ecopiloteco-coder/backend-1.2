-- Database: ecopilot_users
CREATE DATABASE ecopilot_users;
\c ecopilot_users;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    keycloak_id VARCHAR(255) UNIQUE NOT NULL,
    nom_utilisateur VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    titre_poste VARCHAR(255) NOT NULL,
    date_creation_compte TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_admin BOOLEAN DEFAULT FALSE
);

-- Database: ecopilot_articles
-- Contient aussi: niveaux (1-6)
CREATE DATABASE ecopilot_articles;
\c ecopilot_articles;

-- Database: ecopilot_projects
-- Contient aussi: clients
CREATE DATABASE ecopilot_projects;
\c ecopilot_projects;

-- Database: ecopilot_fournisseurs
CREATE DATABASE ecopilot_fournisseurs;
\c ecopilot_fournisseurs;

-- Database: keycloak
CREATE DATABASE keycloak;
\c keycloak;
