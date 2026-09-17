-- =============================================================
-- GSS - Données de test
-- =============================================================
-- À exécuter après database.sql.
-- Les INSERT sont protégés pour éviter les doublons lors d'une
-- seconde exécution.
--
-- Comptes de test (mot de passe pour les trois comptes : password) :
-- admin.test / password
-- accueil.test / password
-- coach.test / password
-- =============================================================

USE gestion_salle_sport;

SET @bcrypt_password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy';

INSERT INTO utilisateurs (username, password, type, statut)
SELECT 'admin.test', @bcrypt_password, 'ADMINISTRATEUR', 'ACTIF'
WHERE NOT EXISTS (
    SELECT 1 FROM utilisateurs WHERE username = 'admin.test'
);

INSERT INTO utilisateurs (username, password, type, statut)
SELECT 'accueil.test', @bcrypt_password, 'RECEPTIONNISTE', 'ACTIF'
WHERE NOT EXISTS (
    SELECT 1 FROM utilisateurs WHERE username = 'accueil.test'
);

INSERT INTO utilisateurs (username, password, type, statut)
SELECT 'coach.test', @bcrypt_password, 'COACH', 'ACTIF'
WHERE NOT EXISTS (
    SELECT 1 FROM utilisateurs WHERE username = 'coach.test'
);

SET @admin_id = (
    SELECT id FROM utilisateurs WHERE username = 'admin.test' LIMIT 1
);
SET @receptionniste_id = (
    SELECT id FROM utilisateurs WHERE username = 'accueil.test' LIMIT 1
);
SET @coach_id = (
    SELECT id FROM utilisateurs WHERE username = 'coach.test' LIMIT 1
);

INSERT INTO administrateurs (id, niveau_acces)
SELECT @admin_id, 'SUPER_ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM administrateurs WHERE id = @admin_id
);

INSERT INTO receptionnistes (id, horaire_travail)
SELECT @receptionniste_id, '08:00-17:00'
WHERE NOT EXISTS (
    SELECT 1 FROM receptionnistes WHERE id = @receptionniste_id
);

INSERT INTO coachs
    (id, nom, prenom, telephone, email, specialite, salaire, disponibilite)
SELECT @coach_id, 'DIOP', 'Mamadou', '770000003', 'coach.test@gss.local',
       'Musculation', 250000, 'DISPONIBLE'
WHERE NOT EXISTS (
    SELECT 1 FROM coachs WHERE id = @coach_id
);

INSERT INTO membres
    (nom, prenom, sexe, date_naissance, telephone, email, adresse,
     date_inscription, statut)
SELECT 'NDIAYE', 'Awa', 'F', '1998-04-12', '770000001',
       'awa.test@gss.local', 'Dakar', CURDATE(), 'ACTIF'
WHERE NOT EXISTS (
    SELECT 1 FROM membres WHERE telephone = '770000001'
);

INSERT INTO membres
    (nom, prenom, sexe, date_naissance, telephone, email, adresse,
     date_inscription, statut)
SELECT 'FALL', 'Ibrahima', 'M', '1992-11-03', '770000002',
       'ibrahima.test@gss.local', 'Thies', CURDATE(), 'ACTIF'
WHERE NOT EXISTS (
    SELECT 1 FROM membres WHERE telephone = '770000002'
);

INSERT INTO membres
    (nom, prenom, sexe, date_naissance, telephone, email, adresse,
     date_inscription, statut)
SELECT 'BA', 'Fatou', 'F', '1987-08-25', '770000004',
       'fatou.test@gss.local', 'Dakar', CURDATE(), 'INACTIF'
WHERE NOT EXISTS (
    SELECT 1 FROM membres WHERE telephone = '770000004'
);

SET @membre_awa_id = (
    SELECT id FROM membres WHERE telephone = '770000001' LIMIT 1
);
SET @membre_ibrahima_id = (
    SELECT id FROM membres WHERE telephone = '770000002' LIMIT 1
);
SET @membre_fatou_id = (
    SELECT id FROM membres WHERE telephone = '770000004' LIMIT 1
);

INSERT INTO abonnements
    (membre_id, type, prix, date_debut, date_fin, statut)
SELECT @membre_awa_id, 'MENSUEL', 15000,
       DATE_SUB(CURDATE(), INTERVAL 10 DAY),
       DATE_ADD(CURDATE(), INTERVAL 20 DAY), 'ACTIF'
WHERE NOT EXISTS (
    SELECT 1 FROM abonnements
    WHERE membre_id = @membre_awa_id
      AND type = 'MENSUEL'
      AND date_debut = DATE_SUB(CURDATE(), INTERVAL 10 DAY)
);

INSERT INTO abonnements
    (membre_id, type, prix, date_debut, date_fin, statut)
SELECT @membre_ibrahima_id, 'TRIMESTRIEL', 40000,
       DATE_SUB(CURDATE(), INTERVAL 120 DAY),
       DATE_SUB(CURDATE(), INTERVAL 30 DAY), 'EXPIRE'
WHERE NOT EXISTS (
    SELECT 1 FROM abonnements
    WHERE membre_id = @membre_ibrahima_id
      AND type = 'TRIMESTRIEL'
      AND statut = 'EXPIRE'
);

INSERT INTO abonnements
    (membre_id, type, prix, date_debut, date_fin, statut)
SELECT @membre_fatou_id, 'HEBDOMADAIRE', 5000,
       DATE_SUB(CURDATE(), INTERVAL 2 DAY),
       DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'ACTIF'
WHERE NOT EXISTS (
    SELECT 1 FROM abonnements
    WHERE membre_id = @membre_fatou_id
      AND type = 'HEBDOMADAIRE'
);

SET @abonnement_awa_id = (
    SELECT id FROM abonnements
    WHERE membre_id = @membre_awa_id AND statut = 'ACTIF'
    ORDER BY id DESC LIMIT 1
);
SET @abonnement_ibrahima_id = (
    SELECT id FROM abonnements
    WHERE membre_id = @membre_ibrahima_id AND statut = 'EXPIRE'
    ORDER BY id DESC LIMIT 1
);

INSERT INTO seances
    (coach_id, nom, date_seance, heure_debut, heure_fin, salle, capacite)
SELECT @coach_id, 'Musculation débutant', CURDATE(), '09:00', '10:00',
       'Salle A', 10
WHERE NOT EXISTS (
    SELECT 1 FROM seances
    WHERE coach_id = @coach_id
      AND nom = 'Musculation débutant'
      AND date_seance = CURDATE()
);

INSERT INTO seances
    (coach_id, nom, date_seance, heure_debut, heure_fin, salle, capacite)
SELECT @coach_id, 'Renforcement musculaire', DATE_ADD(CURDATE(), INTERVAL 1 DAY),
       '18:00', '19:00', 'Salle B', 15
WHERE NOT EXISTS (
    SELECT 1 FROM seances
    WHERE coach_id = @coach_id
      AND nom = 'Renforcement musculaire'
      AND date_seance = DATE_ADD(CURDATE(), INTERVAL 1 DAY)
);

SET @seance_aujourdhui_id = (
    SELECT id FROM seances
    WHERE coach_id = @coach_id
      AND nom = 'Musculation débutant'
      AND date_seance = CURDATE()
    LIMIT 1
);

INSERT INTO inscriptions_seances
    (membre_id, seance_id, date_inscription, statut)
SELECT @membre_awa_id, @seance_aujourdhui_id, NOW(), 'CONFIRMEE'
WHERE NOT EXISTS (
    SELECT 1 FROM inscriptions_seances
    WHERE membre_id = @membre_awa_id
      AND seance_id = @seance_aujourdhui_id
);

INSERT INTO paiements
    (membre_id, abonnement_id, montant, date_paiement,
     mode_paiement, statut, reference)
SELECT @membre_awa_id, @abonnement_awa_id, 15000, NOW(),
       'WAVE', 'VALIDE', 'TEST-PAY-001'
WHERE NOT EXISTS (
    SELECT 1 FROM paiements WHERE reference = 'TEST-PAY-001'
);

INSERT INTO paiements
    (membre_id, abonnement_id, montant, date_paiement,
     mode_paiement, statut, reference)
SELECT @membre_ibrahima_id, @abonnement_ibrahima_id, 40000,
       DATE_SUB(NOW(), INTERVAL 45 DAY),
       'ESPECES', 'ANNULE', 'TEST-PAY-002'
WHERE NOT EXISTS (
    SELECT 1 FROM paiements WHERE reference = 'TEST-PAY-002'
);

-- Contrôles rapides après exécution.
SELECT id, username, type, statut
FROM utilisateurs
WHERE username IN ('admin.test', 'accueil.test', 'coach.test');

SELECT id, nom, prenom, telephone, statut
FROM membres
WHERE telephone IN ('770000001', '770000002', '770000004');

SELECT id, membre_id, type, date_debut, date_fin, statut
FROM abonnements
WHERE membre_id IN (@membre_awa_id, @membre_ibrahima_id, @membre_fatou_id)
ORDER BY id;
