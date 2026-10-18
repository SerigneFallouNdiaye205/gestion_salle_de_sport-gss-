-- Additive migration for the member area.
-- Run this script against gestion_salle_sport before enabling member accounts.
-- Existing tables and staff authentication remain unchanged.

USE gestion_salle_sport;

CREATE TABLE IF NOT EXISTS comptes_membres (
    membre_id INT NOT NULL,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    statut ENUM('ACTIF','INACTIF') NOT NULL DEFAULT 'ACTIF',
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (membre_id),
    UNIQUE KEY uk_compte_membre_username (username),
    CONSTRAINT fk_compte_membre
        FOREIGN KEY (membre_id) REFERENCES membres(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS demandes_abonnement (
    id INT NOT NULL AUTO_INCREMENT,
    membre_id INT NOT NULL,
    type ENUM('JOURNALIER','HEBDOMADAIRE','MENSUEL','TRIMESTRIEL','SEMESTRIEL','ANNUEL') NOT NULL,
    statut ENUM('EN_ATTENTE','APPROUVEE','REFUSEE') NOT NULL DEFAULT 'EN_ATTENTE',
    date_demande DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_traitement DATETIME DEFAULT NULL,
    traite_par INT DEFAULT NULL,
    abonnement_id INT DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_demande_membre_statut (membre_id, statut),
    KEY idx_demande_statut_date (statut, date_demande),
    UNIQUE KEY uk_demande_abonnement (abonnement_id),
    CONSTRAINT fk_demande_membre
        FOREIGN KEY (membre_id) REFERENCES membres(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_demande_traitee_par
        FOREIGN KEY (traite_par) REFERENCES utilisateurs(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_demande_abonnement
        FOREIGN KEY (abonnement_id) REFERENCES abonnements(id)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;
