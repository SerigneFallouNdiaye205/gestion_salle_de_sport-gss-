-- =============================================================
-- GSS - Base de données
-- =============================================================
-- Compatible MySQL / MariaDB.
-- Le script respecte l'ordre des dépendances des clés étrangères.
-- =============================================================

CREATE DATABASE IF NOT EXISTS gestion_salle_sport
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gestion_salle_sport;

-- =============================================================
-- UTILISATEURS
-- =============================================================

CREATE TABLE IF NOT EXISTS utilisateurs (
    id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    type ENUM('ADMINISTRATEUR','RECEPTIONNISTE','COACH') NOT NULL,
    statut ENUM('ACTIF','INACTIF') NOT NULL DEFAULT 'ACTIF',
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_utilisateur_username (username)
) ENGINE=InnoDB;

-- =============================================================
-- ADMINISTRATEURS
-- =============================================================

CREATE TABLE IF NOT EXISTS administrateurs (
    id INT NOT NULL,
    niveau_acces ENUM('STANDARD','SUPER_ADMIN') NOT NULL DEFAULT 'STANDARD',
    PRIMARY KEY (id),
    CONSTRAINT fk_administrateur_utilisateur
        FOREIGN KEY (id) REFERENCES utilisateurs(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =============================================================
-- RECEPTIONNISTES
-- =============================================================

CREATE TABLE IF NOT EXISTS receptionnistes (
    id INT NOT NULL,
    horaire_travail VARCHAR(50) DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_receptionniste_utilisateur
        FOREIGN KEY (id) REFERENCES utilisateurs(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =============================================================
-- COACHS
-- L'id du coach correspond à l'id de son compte utilisateur.
-- =============================================================

CREATE TABLE IF NOT EXISTS coachs (
    id INT NOT NULL,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    telephone VARCHAR(20) NOT NULL,
    email VARCHAR(100) DEFAULT NULL,
    specialite VARCHAR(100) DEFAULT NULL,
    salaire DECIMAL(10,2) DEFAULT NULL,
    disponibilite ENUM('DISPONIBLE','INDISPONIBLE') NOT NULL DEFAULT 'DISPONIBLE',
    PRIMARY KEY (id),
    CONSTRAINT fk_coach_utilisateur
        FOREIGN KEY (id) REFERENCES utilisateurs(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =============================================================
-- MEMBRES
-- =============================================================

CREATE TABLE IF NOT EXISTS membres (
    id INT NOT NULL AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    sexe ENUM('M','F') NOT NULL DEFAULT 'M',
    date_naissance DATE DEFAULT NULL,
    telephone VARCHAR(20) NOT NULL,
    email VARCHAR(100) DEFAULT NULL,
    adresse VARCHAR(150) DEFAULT NULL,
    date_inscription DATE NOT NULL DEFAULT (CURDATE()),
    statut ENUM('ACTIF','INACTIF') NOT NULL DEFAULT 'ACTIF',
    PRIMARY KEY (id),
    KEY idx_membre_nom (nom),
    KEY idx_membre_telephone (telephone)
) ENGINE=InnoDB;

-- =============================================================
-- ABONNEMENTS
-- =============================================================

CREATE TABLE IF NOT EXISTS abonnements (
    id INT NOT NULL AUTO_INCREMENT,
    membre_id INT NOT NULL,
    type ENUM('JOURNALIER','HEBDOMADAIRE','MENSUEL','TRIMESTRIEL','SEMESTRIEL','ANNUEL') NOT NULL,
    prix DECIMAL(10,2) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    statut ENUM('ACTIF','EXPIRE','SUSPENDU') NOT NULL DEFAULT 'ACTIF',
    PRIMARY KEY (id),
    KEY idx_abonnement_membre (membre_id),
    CONSTRAINT fk_abonnement_membre
        FOREIGN KEY (membre_id) REFERENCES membres(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =============================================================
-- SEANCES
-- =============================================================

CREATE TABLE IF NOT EXISTS seances (
    id INT NOT NULL AUTO_INCREMENT,
    coach_id INT NOT NULL,
    nom VARCHAR(100) NOT NULL,
    date_seance DATE NOT NULL,
    heure_debut TIME NOT NULL,
    heure_fin TIME NOT NULL,
    salle VARCHAR(50) NOT NULL,
    capacite INT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_seance_coach (coach_id),
    KEY idx_seance_date (date_seance),
    CONSTRAINT fk_seance_coach
        FOREIGN KEY (coach_id) REFERENCES coachs(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =============================================================
-- INSCRIPTIONS AUX SEANCES
-- =============================================================

CREATE TABLE IF NOT EXISTS inscriptions_seances (
    id INT NOT NULL AUTO_INCREMENT,
    membre_id INT NOT NULL,
    seance_id INT NOT NULL,
    date_inscription DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    statut ENUM('CONFIRMEE','ANNULEE','EN_ATTENTE') NOT NULL DEFAULT 'CONFIRMEE',
    PRIMARY KEY (id),
    KEY idx_inscription_membre (membre_id),
    KEY idx_inscription_seance (seance_id),
    CONSTRAINT fk_inscription_membre
        FOREIGN KEY (membre_id) REFERENCES membres(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_inscription_seance
        FOREIGN KEY (seance_id) REFERENCES seances(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =============================================================
-- PAIEMENTS
-- =============================================================

CREATE TABLE IF NOT EXISTS paiements (
    id INT NOT NULL AUTO_INCREMENT,
    membre_id INT NOT NULL,
    abonnement_id INT NOT NULL,
    montant DECIMAL(10,2) NOT NULL,
    date_paiement DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    mode_paiement ENUM('ESPECES','WAVE','ORANGE_MONEY','CARTE_BANCAIRE') NOT NULL,
    statut ENUM('VALIDE','EN_ATTENTE','ANNULE') NOT NULL DEFAULT 'VALIDE',
    reference VARCHAR(50) DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_paiement_membre (membre_id),
    KEY idx_paiement_abonnement (abonnement_id),
    UNIQUE KEY uk_paiement_reference (reference),
    CONSTRAINT fk_paiement_membre
        FOREIGN KEY (membre_id) REFERENCES membres(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_paiement_abonnement
        FOREIGN KEY (abonnement_id) REFERENCES abonnements(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =============================================================
-- COMPTE ADMINISTRATEUR INITIAL
-- =============================================================
-- Le mot de passe doit être un hash BCrypt généré par l'application.
-- Décommente et remplace le hash si nécessaire.
-- INSERT INTO utilisateurs(username, password, type, statut)
-- VALUES ('admin', '<HASH_BCRYPT>', 'ADMINISTRATEUR', 'ACTIF');
