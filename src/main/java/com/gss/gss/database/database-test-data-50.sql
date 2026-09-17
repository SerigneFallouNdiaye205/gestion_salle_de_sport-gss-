-- =============================================================
-- GSS - 50 membres et 10 coachs de test
-- =============================================================
-- À exécuter après database.sql.
-- Mot de passe de tous les comptes coach : password
-- Les données sont ajoutées uniquement si elles n'existent pas déjà.
-- =============================================================

USE gestion_salle_sport;

SET @bcrypt_password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy';

INSERT INTO utilisateurs (username, password, type, statut)
SELECT username, @bcrypt_password, 'COACH', 'ACTIF'
FROM (
    SELECT 'coach01.test' AS username
    UNION ALL SELECT 'coach02.test'
    UNION ALL SELECT 'coach03.test'
    UNION ALL SELECT 'coach04.test'
    UNION ALL SELECT 'coach05.test'
    UNION ALL SELECT 'coach06.test'
    UNION ALL SELECT 'coach07.test'
    UNION ALL SELECT 'coach08.test'
    UNION ALL SELECT 'coach09.test'
    UNION ALL SELECT 'coach10.test'
) AS comptes
WHERE NOT EXISTS (
    SELECT 1
    FROM utilisateurs u
    WHERE u.username = comptes.username
);

INSERT INTO coachs
    (id, nom, prenom, telephone, email, specialite, salaire, disponibilite)
SELECT u.id, donnees.nom, donnees.prenom, donnees.telephone,
       donnees.email, donnees.specialite, donnees.salaire, 'DISPONIBLE'
FROM utilisateurs u
JOIN (
    SELECT 'coach01.test' AS username, 'DIOP' AS nom, 'Mamadou' AS prenom,
           '771000001' AS telephone, 'coach01@gss.local' AS email,
           'Musculation' AS specialite, 250000.00 AS salaire
    UNION ALL SELECT 'coach02.test', 'FALL', 'Aminata', '771000002',
           'coach02@gss.local', 'Cardio', 230000.00
    UNION ALL SELECT 'coach03.test', 'BA', 'Ibrahima', '771000003',
           'coach03@gss.local', 'Fitness', 220000.00
    UNION ALL SELECT 'coach04.test', 'NDIAYE', 'Fatou', '771000004',
           'coach04@gss.local', 'Yoga', 210000.00
    UNION ALL SELECT 'coach05.test', 'SOW', 'Abdoulaye', '771000005',
           'coach05@gss.local', 'Cross-training', 260000.00
    UNION ALL SELECT 'coach06.test', 'GUEYE', 'Mariama', '771000006',
           'coach06@gss.local', 'Pilates', 215000.00
    UNION ALL SELECT 'coach07.test', 'DIALLO', 'Ousmane', '771000007',
           'coach07@gss.local', 'Boxe', 240000.00
    UNION ALL SELECT 'coach08.test', 'KANE', 'Sokhna', '771000008',
           'coach08@gss.local', 'Zumba', 205000.00
    UNION ALL SELECT 'coach09.test', 'SECK', 'Cheikh', '771000009',
           'coach09@gss.local', 'Athlétisme', 235000.00
    UNION ALL SELECT 'coach10.test', 'THIAM', 'Aïssatou', '771000010',
           'coach10@gss.local', 'Stretching', 200000.00
) AS donnees ON donnees.username = u.username
WHERE u.type = 'COACH'
  AND NOT EXISTS (
      SELECT 1 FROM coachs c WHERE c.id = u.id
  );

CREATE TEMPORARY TABLE donnees_membres_test (
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    sexe ENUM('M','F') NOT NULL,
    date_naissance DATE,
    telephone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    adresse VARCHAR(150),
    statut ENUM('ACTIF','INACTIF') NOT NULL
);

INSERT INTO donnees_membres_test
    (nom, prenom, sexe, date_naissance, telephone, email, adresse, statut)
VALUES
    ('DIOP', 'Awa', 'F', '1995-01-12', '772000001', 'membre01@gss.local', 'Dakar', 'ACTIF'),
    ('FALL', 'Moussa', 'M', '1991-02-23', '772000002', 'membre02@gss.local', 'Thies', 'ACTIF'),
    ('BA', 'Fatou', 'F', '1998-03-14', '772000003', 'membre03@gss.local', 'Dakar', 'ACTIF'),
    ('NDIAYE', 'Ibrahima', 'M', '1989-04-05', '772000004', 'membre04@gss.local', 'Rufisque', 'ACTIF'),
    ('SOW', 'Mariama', 'F', '1996-05-16', '772000005', 'membre05@gss.local', 'Dakar', 'ACTIF'),
    ('GUEYE', 'Ousmane', 'M', '1993-06-27', '772000006', 'membre06@gss.local', 'Pikine', 'ACTIF'),
    ('DIALLO', 'Sokhna', 'F', '2000-07-18', '772000007', 'membre07@gss.local', 'Guediawaye', 'ACTIF'),
    ('KANE', 'Cheikh', 'M', '1987-08-09', '772000008', 'membre08@gss.local', 'Dakar', 'ACTIF'),
    ('SECK', 'Aissatou', 'F', '1994-09-20', '772000009', 'membre09@gss.local', 'Thies', 'ACTIF'),
    ('THIAM', 'Abdoulaye', 'M', '1992-10-11', '772000010', 'membre10@gss.local', 'Dakar', 'ACTIF'),
    ('DIOP', 'Khadija', 'F', '1999-11-22', '772000011', 'membre11@gss.local', 'Dakar', 'ACTIF'),
    ('FALL', 'Lamine', 'M', '1986-12-03', '772000012', 'membre12@gss.local', 'Mbour', 'ACTIF'),
    ('BA', 'Ndeye', 'F', '1997-01-24', '772000013', 'membre13@gss.local', 'Dakar', 'ACTIF'),
    ('NDIAYE', 'Serigne', 'M', '1990-02-15', '772000014', 'membre14@gss.local', 'Thies', 'ACTIF'),
    ('SOW', 'Astou', 'F', '2001-03-26', '772000015', 'membre15@gss.local', 'Dakar', 'ACTIF'),
    ('GUEYE', 'Modou', 'M', '1988-04-17', '772000016', 'membre16@gss.local', 'Dakar', 'ACTIF'),
    ('DIALLO', 'Adama', 'F', '1995-05-28', '772000017', 'membre17@gss.local', 'Rufisque', 'ACTIF'),
    ('KANE', 'Mamadou', 'M', '1993-06-19', '772000018', 'membre18@gss.local', 'Dakar', 'ACTIF'),
    ('SECK', 'Coumba', 'F', '1998-07-10', '772000019', 'membre19@gss.local', 'Pikine', 'ACTIF'),
    ('THIAM', 'Boubacar', 'M', '1985-08-21', '772000020', 'membre20@gss.local', 'Dakar', 'ACTIF'),
    ('DIOP', 'Nafissatou', 'F', '1996-09-12', '772000021', 'membre21@gss.local', 'Dakar', 'ACTIF'),
    ('FALL', 'Malick', 'M', '1991-10-23', '772000022', 'membre22@gss.local', 'Thies', 'ACTIF'),
    ('BA', 'Seynabou', 'F', '1999-11-14', '772000023', 'membre23@gss.local', 'Dakar', 'ACTIF'),
    ('NDIAYE', 'Pape', 'M', '1987-12-05', '772000024', 'membre24@gss.local', 'Mbour', 'ACTIF'),
    ('SOW', 'Rama', 'F', '1994-01-16', '772000025', 'membre25@gss.local', 'Dakar', 'ACTIF'),
    ('GUEYE', 'El Hadji', 'M', '1990-02-27', '772000026', 'membre26@gss.local', 'Dakar', 'INACTIF'),
    ('DIALLO', 'Hawa', 'F', '2000-03-18', '772000027', 'membre27@gss.local', 'Rufisque', 'INACTIF'),
    ('KANE', 'Oumar', 'M', '1989-04-09', '772000028', 'membre28@gss.local', 'Dakar', 'INACTIF'),
    ('SECK', 'Mame', 'F', '1997-05-20', '772000029', 'membre29@gss.local', 'Pikine', 'INACTIF'),
    ('THIAM', 'Samba', 'M', '1992-06-11', '772000030', 'membre30@gss.local', 'Dakar', 'INACTIF'),
    ('DIOP', 'Aminata', 'F', '1995-07-22', '772000031', 'membre31@gss.local', 'Dakar', 'INACTIF'),
    ('FALL', 'Alioune', 'M', '1986-08-13', '772000032', 'membre32@gss.local', 'Thies', 'INACTIF'),
    ('BA', 'Rokhaya', 'F', '1998-09-24', '772000033', 'membre33@gss.local', 'Dakar', 'INACTIF'),
    ('NDIAYE', 'Moussa', 'M', '1993-10-15', '772000034', 'membre34@gss.local', 'Mbour', 'INACTIF'),
    ('SOW', 'Yacine', 'F', '2001-11-26', '772000035', 'membre35@gss.local', 'Dakar', 'INACTIF'),
    ('GUEYE', 'Babacar', 'M', '1988-12-17', '772000036', 'membre36@gss.local', 'Dakar', 'INACTIF'),
    ('DIALLO', 'Fanta', 'F', '1996-01-28', '772000037', 'membre37@gss.local', 'Rufisque', 'INACTIF'),
    ('KANE', 'Moustapha', 'M', '1990-02-19', '772000038', 'membre38@gss.local', 'Dakar', 'INACTIF'),
    ('SECK', 'Dieynaba', 'F', '1999-03-10', '772000039', 'membre39@gss.local', 'Pikine', 'INACTIF'),
    ('THIAM', 'Amadou', 'M', '1985-04-21', '772000040', 'membre40@gss.local', 'Dakar', 'INACTIF'),
    ('DIOP', 'Mame Diarra', 'F', '1994-05-12', '772000041', 'membre41@gss.local', 'Dakar', 'ACTIF'),
    ('FALL', 'Cheikh', 'M', '1991-06-23', '772000042', 'membre42@gss.local', 'Thies', 'ACTIF'),
    ('BA', 'Safiétou', 'F', '1997-07-14', '772000043', 'membre43@gss.local', 'Dakar', 'ACTIF'),
    ('NDIAYE', 'Mamadou', 'M', '1989-08-05', '772000044', 'membre44@gss.local', 'Mbour', 'ACTIF'),
    ('SOW', 'Ndeye Astou', 'F', '2000-09-16', '772000045', 'membre45@gss.local', 'Dakar', 'ACTIF'),
    ('GUEYE', 'Ibrahima', 'M', '1993-10-27', '772000046', 'membre46@gss.local', 'Dakar', 'ACTIF'),
    ('DIALLO', 'Aminata', 'F', '1998-11-18', '772000047', 'membre47@gss.local', 'Rufisque', 'ACTIF'),
    ('KANE', 'Abdou', 'M', '1987-12-09', '772000048', 'membre48@gss.local', 'Dakar', 'ACTIF'),
    ('SECK', 'Mame Fatou', 'F', '1995-01-20', '772000049', 'membre49@gss.local', 'Pikine', 'ACTIF'),
    ('THIAM', 'Ousmane', 'M', '1992-02-11', '772000050', 'membre50@gss.local', 'Dakar', 'ACTIF');

INSERT INTO membres
    (nom, prenom, sexe, date_naissance, telephone, email, adresse,
     date_inscription, statut)
SELECT d.nom, d.prenom, d.sexe, d.date_naissance, d.telephone,
       d.email, d.adresse, CURDATE(), d.statut
FROM donnees_membres_test d
WHERE NOT EXISTS (
    SELECT 1 FROM membres m WHERE m.telephone = d.telephone
);

DROP TEMPORARY TABLE donnees_membres_test;

SELECT COUNT(*) AS coachs_de_test
FROM coachs c
JOIN utilisateurs u ON u.id = c.id
WHERE u.username LIKE 'coach%.test';

SELECT COUNT(*) AS membres_de_test
FROM membres
WHERE telephone LIKE '772000%';
