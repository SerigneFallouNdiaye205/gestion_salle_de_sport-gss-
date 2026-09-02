CREATE DATABASE IF NOT EXISTS gestion_salle_sport;
USE gestion_salle_sport;

CREATE TABLE abonnements (
        id INT(11) NOT NULL AUTO_INCREMENT,
        membre_id INT(11) NOT NULL,
        type ENUM('JOURNALIER','HEBDOMADAIRE','MENSUEL','TRIMESTRIEL','SEMESTRIEL','ANNUEL') NOT NULL,
        prix DECIMAL(10,2) NOT NULL,
        date_debut DATE NOT NULL,
        date_fin DATE NOT NULL,
        statut ENUM('ACTIF','EXPIRE','SUSPENDU') NOT NULL DEFAULT 'ACTIF',
        PRIMARY KEY (id),
        KEY idx_membre_id (membre_id),
        CONSTRAINT fk_abonnement_membre
        FOREIGN KEY (membre_id)
        REFERENCES membres(id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
);

CREATE TABLE administrateurs (
        id INT(11) NOT NULL,
        niveau_acces ENUM('STANDARD', 'SUPER_ADMIN') NOT NULL DEFAULT 'STANDARD',

        PRIMARY KEY (id),
        CONSTRAINT fk_administrateur_utilisateur FOREIGN KEY (id)
            REFERENCES utilisateurs(id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
);

CREATE TABLE coachs (
        id INT(11) NOT NULL,
        nom VARCHAR(50) NOT NULL,
        prenom VARCHAR(50) NOT NULL,
        telephone VARCHAR(20) NOT NULL,
        email VARCHAR(100) DEFAULT NULL,
        specialite VARCHAR(100) DEFAULT NULL,
        salaire DECIMAL(10,2) DEFAULT NULL,
        disponibilite ENUM('DISPONIBLE', 'INDISPONIBLE') NOT NULL DEFAULT 'DISPONIBLE',

        PRIMARY KEY (id),
        CONSTRAINT fk_coach_utilisateur FOREIGN KEY (id)
        REFERENCES utilisateurs(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE inscriptions_seances (
        id INT(11) NOT NULL AUTO_INCREMENT,
        membre_id INT(11) NOT NULL,
        seance_id INT(11) NOT NULL,
        date_inscription DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        statut ENUM('CONFIRMEE', 'ANNULEE', 'EN_ATTENTE') NOT NULL DEFAULT 'CONFIRMEE',

        PRIMARY KEY (id),
        KEY idx_inscription_membre (membre_id),
        KEY idx_inscription_seance (seance_id),
        CONSTRAINT fk_inscription_membre
            FOREIGN KEY (membre_id)
                REFERENCES membres(id)
                ON DELETE CASCADE
                ON UPDATE CASCADE,
        CONSTRAINT fk_inscription_seance
            FOREIGN KEY (seance_id)
                REFERENCES seances(id)
                ON DELETE CASCADE
                ON UPDATE CASCADE
);

CREATE TABLE membres (
        id INT(11) NOT NULL AUTO_INCREMENT,
        nom VARCHAR(50) NOT NULL,
        prenom VARCHAR(50) NOT NULL,
        sexe ENUM('M', 'F') NOT NULL,
        date_naissance DATE DEFAULT NULL,
        telephone VARCHAR(20) NOT NULL,
        email VARCHAR(100) DEFAULT NULL,
        adresse VARCHAR(150) DEFAULT NULL,
        date_inscription DATE NOT NULL DEFAULT (CURDATE()),
        statut ENUM('ACTIF', 'INACTIF') NOT NULL DEFAULT 'ACTIF',

        PRIMARY KEY (id),
        KEY idx_membre_nom (nom),
        KEY idx_membre_telephone (telephone)
);

CREATE TABLE paiements (
        id INT(11) NOT NULL AUTO_INCREMENT,
        membre_id INT(11) NOT NULL,
        abonnement_id INT(11) NOT NULL,
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
            FOREIGN KEY (membre_id)
                REFERENCES membres(id)
                ON DELETE CASCADE
                ON UPDATE CASCADE,
        CONSTRAINT fk_paiement_abonnement
            FOREIGN KEY (abonnement_id)
                REFERENCES abonnements(id)
                ON DELETE CASCADE
                ON UPDATE CASCADE
);

CREATE TABLE utilisateurs (
        id INT(11) NOT NULL AUTO_INCREMENT,
        coach_id INT(11) DEFAULT NULL,
        username VARCHAR(50) NOT NULL,
        password VARCHAR(255) NOT NULL,
        type ENUM('ADMINISTRATEUR','RECEPTIONNISTE','COACH') NOT NULL,
                              statut ENUM(
                                  'ACTIF',
                                  'INACTIF'
                                  ) NOT NULL DEFAULT 'ACTIF',
                              date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                              PRIMARY KEY (id),
                              UNIQUE KEY uk_utilisateur_username (username),
                              KEY idx_utilisateur_coach (coach_id),

                              CONSTRAINT fk_utilisateur_coach
                                  FOREIGN KEY (coach_id)
                                      REFERENCES coachs(id)
                                      ON DELETE SET NULL
                                      ON UPDATE CASCADE
);

CREATE TABLE receptionnistes (
        id INT(11) NOT NULL,
        horaire_travail VARCHAR(50) DEFAULT NULL,
        PRIMARY KEY (id),

        CONSTRAINT fk_receptionniste_utilisateur
            FOREIGN KEY (id)
                REFERENCES utilisateurs(id)
                ON DELETE CASCADE
                ON UPDATE CASCADE
);

CREATE TABLE seances (
        id INT(11) NOT NULL AUTO_INCREMENT,
        coach_id INT(11) NOT NULL,
        nom VARCHAR(100) NOT NULL,
        date_seance DATE NOT NULL,
        heure_debut TIME NOT NULL,
        heure_fin TIME NOT NULL,
        salle VARCHAR(50) NOT NULL,
        capacite INT(11) NOT NULL,

        PRIMARY KEY (id),
        KEY idx_seance_coach (coach_id),
        KEY idx_seance_date (date_seance),

        CONSTRAINT fk_seance_coach
            FOREIGN KEY (coach_id)
                REFERENCES coachs(id)
                ON DELETE CASCADE
                ON UPDATE CASCADE
);