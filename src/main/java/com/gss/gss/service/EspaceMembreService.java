package com.gss.gss.service;

import com.gss.gss.database.DatabaseConnection;
import com.gss.gss.model.Abonnement;
import com.gss.gss.model.InscriptionSeance;
import com.gss.gss.model.Paiement;
import com.gss.gss.model.Seance;
import com.gss.gss.security.MembreSessionManager;
import com.gss.gss.security.PasswordHasher;
import com.gss.gss.util.PaiementReferenceGenerator;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EspaceMembreService {

    private static final List<String> TYPES_ABONNEMENT = List.of(
            "JOURNALIER", "HEBDOMADAIRE", "MENSUEL",
            "TRIMESTRIEL", "SEMESTRIEL", "ANNUEL"
    );

    public record CompteMembre(int membreId, String nomComplet) {}
    public record SeanceDisponible(Seance seance, int placesRestantes) {}
    public record InscriptionMembre(int id, int seanceId, String seance,
                                     LocalDate date, String horaires, String salle,
                                     String statut) {}
    public record DemandeAbonnement(int id, int membreId, String membre,
                                     String type, LocalDateTime dateDemande,
                                     String statut) {}

    public void creerCompte(int membreId, String username, String password) {
        if (membreId <= 0) {
            throw new IllegalArgumentException("Le membre sélectionné est invalide.");
        }
        String login = username == null ? "" : username.trim();
        if (login.length() < 3 || login.length() > 50) {
            throw new IllegalArgumentException("Le nom d'utilisateur doit contenir de 3 à 50 caractères.");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Le mot de passe initial doit contenir au moins 8 caractères.");
        }

        String sql = """
                INSERT INTO comptes_membres(membre_id, username, password_hash)
                SELECT id, ?, ?
                FROM membres
                WHERE id = ? AND statut = 'ACTIF'
                ON DUPLICATE KEY UPDATE
                    username = VALUES(username),
                    password_hash = VALUES(password_hash),
                    statut = 'ACTIF'
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);
            statement.setString(2, PasswordHasher.hash(password));
            statement.setInt(3, membreId);
            if (statement.executeUpdate() < 1) {
                throw new IllegalArgumentException("Le membre est introuvable ou inactif.");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new IllegalArgumentException("Ce nom d'utilisateur ou ce compte membre existe déjà.", e);
            }
            throw databaseError("la création du compte membre", e);
        }
    }

    public Optional<CompteMembre> authentifier(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return Optional.empty();
        }
        String sql = """
                SELECT cm.membre_id, cm.password_hash,
                       CONCAT(m.prenom, ' ', m.nom) AS nom_complet
                FROM comptes_membres cm
                INNER JOIN membres m ON m.id = cm.membre_id
                WHERE cm.username = ? AND cm.statut = 'ACTIF' AND m.statut = 'ACTIF'
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username.trim());
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next() || !PasswordHasher.verify(password, result.getString("password_hash"))) {
                    return Optional.empty();
                }
                return Optional.of(new CompteMembre(
                        result.getInt("membre_id"), result.getString("nom_complet")));
            }
        } catch (SQLException e) {
            throw databaseError("la connexion à l'espace membre", e);
        }
    }

    public List<Abonnement> getAbonnements(int membreId) {
        verifierMembreConnecte(membreId);
        String sql = """
                SELECT id, membre_id, type, prix, date_debut, date_fin, statut
                FROM abonnements
                WHERE membre_id = ?
                ORDER BY date_debut DESC, id DESC
                """;
        List<Abonnement> result = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, membreId);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    result.add(new Abonnement(
                            rows.getInt("id"), rows.getInt("membre_id"),
                            rows.getString("type"), rows.getDouble("prix"),
                            toLocalDate(rows.getDate("date_debut")),
                            toLocalDate(rows.getDate("date_fin")),
                            rows.getString("statut")));
                }
                return result;
            }
        } catch (SQLException e) {
            throw databaseError("les abonnements du membre", e);
        }
    }

    public List<Paiement> getPaiements(int membreId) {
        verifierMembreConnecte(membreId);
        String sql = """
                SELECT id, membre_id, abonnement_id, montant, date_paiement,
                       mode_paiement, statut, reference
                FROM paiements
                WHERE membre_id = ?
                ORDER BY date_paiement DESC, id DESC
                """;
        List<Paiement> result = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, membreId);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    Timestamp timestamp = rows.getTimestamp("date_paiement");
                    result.add(new Paiement(
                            rows.getInt("id"), rows.getInt("membre_id"),
                            rows.getInt("abonnement_id"), rows.getDouble("montant"),
                            timestamp == null ? null : timestamp.toLocalDateTime(),
                            rows.getString("mode_paiement"), rows.getString("statut"),
                            rows.getString("reference")));
                }
                return result;
            }
        } catch (SQLException e) {
            throw databaseError("les paiements du membre", e);
        }
    }

    public void changerMotDePasse(int membreId, String ancien, String nouveau, String confirmation) {
        verifierMembreConnecte(membreId);
        if (nouveau == null || nouveau.length() < 8) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 8 caractères.");
        }
        if (!nouveau.equals(confirmation)) {
            throw new IllegalArgumentException("La confirmation du nouveau mot de passe ne correspond pas.");
        }

        String currentHash;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT password_hash FROM comptes_membres
                     WHERE membre_id = ? AND statut = 'ACTIF'
                     """)) {
            statement.setInt(1, membreId);
            try (ResultSet row = statement.executeQuery()) {
                if (!row.next()) {
                    throw new IllegalStateException("Le compte membre est désactivé.");
                }
                currentHash = row.getString("password_hash");
            }
        } catch (SQLException e) {
            throw databaseError("la vérification du mot de passe", e);
        }
        if (!PasswordHasher.verify(ancien, currentHash)) {
            throw new IllegalArgumentException("Le mot de passe actuel est incorrect.");
        }
        if (PasswordHasher.verify(nouveau, currentHash)) {
            throw new IllegalArgumentException("Choisissez un mot de passe différent de l'actuel.");
        }
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE comptes_membres
                     SET password_hash = ?
                     WHERE membre_id = ? AND statut = 'ACTIF'
                     """)) {
            statement.setString(1, PasswordHasher.hash(nouveau));
            statement.setInt(2, membreId);
            if (statement.executeUpdate() != 1) {
                throw new IllegalStateException("Le compte membre a été désactivé.");
            }
        } catch (SQLException e) {
            throw databaseError("la modification du mot de passe", e);
        }
    }

    public List<SeanceDisponible> getSeancesDisponibles() {
        String sql = """
                SELECT s.*, s.capacite - COUNT(i.id) AS places_restantes
                FROM seances s
                LEFT JOIN inscriptions_seances i
                  ON i.seance_id = s.id AND i.statut <> 'ANNULEE'
                WHERE s.date_seance > CURDATE()
                   OR (s.date_seance = CURDATE() AND s.heure_fin > CURTIME())
                GROUP BY s.id
                HAVING places_restantes > 0
                ORDER BY s.date_seance, s.heure_debut
                """;
        List<SeanceDisponible> result = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) {
                Seance seance = new Seance();
                seance.setId(rows.getInt("id"));
                seance.setCoachId(rows.getInt("coach_id"));
                seance.setNom(rows.getString("nom"));
                seance.setDateSeance(toLocalDate(rows.getDate("date_seance")));
                Time debut = rows.getTime("heure_debut");
                Time fin = rows.getTime("heure_fin");
                seance.setHeureDebut(debut == null ? null : debut.toLocalTime());
                seance.setHeureFin(fin == null ? null : fin.toLocalTime());
                seance.setSalle(rows.getString("salle"));
                seance.setCapacite(rows.getInt("capacite"));
                result.add(new SeanceDisponible(seance, rows.getInt("places_restantes")));
            }
            return result;
        } catch (SQLException e) {
            throw databaseError("les séances disponibles", e);
        }
    }

    public List<InscriptionMembre> getInscriptions(int membreId) {
        verifierMembreConnecte(membreId);
        String sql = """
                SELECT i.id, s.id AS seance_id, s.nom, s.date_seance,
                       s.heure_debut, s.heure_fin, s.salle, i.statut
                FROM inscriptions_seances i
                INNER JOIN seances s ON s.id = i.seance_id
                WHERE i.membre_id = ?
                ORDER BY s.date_seance DESC, s.heure_debut DESC
                """;
        List<InscriptionMembre> result = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, membreId);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    Time start = rows.getTime("heure_debut");
                    Time end = rows.getTime("heure_fin");
                    result.add(new InscriptionMembre(
                            rows.getInt("id"),
                            rows.getInt("seance_id"),
                            rows.getString("nom"),
                            toLocalDate(rows.getDate("date_seance")),
                            (start == null ? "" : start.toLocalTime().toString())
                                    + " - "
                                    + (end == null ? "" : end.toLocalTime().toString()),
                            rows.getString("salle"),
                            rows.getString("statut")));
                }
                return result;
            }
        } catch (SQLException e) {
            throw databaseError("les inscriptions du membre", e);
        }
    }

    public void annulerInscription(int membreId, int inscriptionId) {
        verifierMembreConnecte(membreId);
        String sql = """
                UPDATE inscriptions_seances i
                INNER JOIN seances s ON s.id = i.seance_id
                SET i.statut = 'ANNULEE'
                WHERE i.id = ? AND i.membre_id = ?
                  AND i.statut <> 'ANNULEE'
                  AND (s.date_seance > CURDATE()
                       OR (s.date_seance = CURDATE() AND s.heure_debut > CURTIME()))
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, inscriptionId);
            statement.setInt(2, membreId);
            if (statement.executeUpdate() != 1) {
                throw new IllegalArgumentException(
                        "Cette inscription ne vous appartient pas, est déjà annulée ou la séance a commencé.");
            }
        } catch (SQLException e) {
            throw databaseError("l'annulation de l'inscription", e);
        }
    }

    public void inscrireASeance(int membreId, int seanceId) {
        verifierMembreConnecte(membreId);
        if (membreId <= 0 || seanceId <= 0) {
            throw new IllegalArgumentException("Le membre et la séance sont obligatoires.");
        }
        Seance seance = new SeanceService().findById(seanceId);
        if (seance == null || seance.getDateSeance() == null
                || seance.getHeureFin() == null
                || seance.getDateSeance().isBefore(LocalDate.now())
                || (seance.getDateSeance().isEqual(LocalDate.now())
                && !seance.getHeureFin().isAfter(java.time.LocalTime.now()))) {
            throw new IllegalArgumentException("Cette séance n'est plus disponible.");
        }
        InscriptionSeance inscription = new InscriptionSeance();
        inscription.setMembre_id(membreId);
        inscription.setSeance_id(seanceId);
        inscription.setDate_inscription(LocalDate.now());
        inscription.setStatut("CONFIRMEE");
        if (!new InscriptionSeanceService().save(inscription)) {
            throw new IllegalStateException("L'inscription à cette séance n'a pas pu être enregistrée.");
        }
    }

    public void demanderAbonnement(int membreId, String type) {
        verifierMembreConnecte(membreId);
        if (!TYPES_ABONNEMENT.contains(type)) {
            throw new IllegalArgumentException("Le type d'abonnement sélectionné est invalide.");
        }
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                verifierCompteActif(connection, membreId);
                verifierAucunAbonnementValide(connection, membreId);
                try (PreparedStatement pending = connection.prepareStatement("""
                        SELECT 1 FROM demandes_abonnement
                        WHERE membre_id = ? AND statut = 'EN_ATTENTE'
                        LIMIT 1 FOR UPDATE
                        """)) {
                    pending.setInt(1, membreId);
                    try (ResultSet row = pending.executeQuery()) {
                        if (row.next()) {
                            throw new IllegalArgumentException(
                                    "Vous avez déjà une demande d'abonnement en attente.");
                        }
                    }
                }
                try (PreparedStatement insert = connection.prepareStatement("""
                        INSERT INTO demandes_abonnement(membre_id, type)
                        VALUES (?, ?)
                        """)) {
                    insert.setInt(1, membreId);
                    insert.setString(2, type);
                    insert.executeUpdate();
                }
                connection.commit();
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw databaseError("la demande d'abonnement", e);
        }
    }

    public List<DemandeAbonnement> getDemandesAbonnement(int membreId) {
        verifierMembreConnecte(membreId);
        String sql = """
                SELECT id, membre_id, type, statut, date_demande
                FROM demandes_abonnement
                WHERE membre_id = ?
                ORDER BY date_demande DESC, id DESC
                """;
        List<DemandeAbonnement> result = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, membreId);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    Timestamp date = rows.getTimestamp("date_demande");
                    result.add(new DemandeAbonnement(
                            rows.getInt("id"), membreId, null, rows.getString("type"),
                            date == null ? null : date.toLocalDateTime(), rows.getString("statut")));
                }
                return result;
            }
        } catch (SQLException e) {
            throw databaseError("les demandes d'abonnement", e);
        }
    }

    public List<DemandeAbonnement> getDemandesEnAttente() {
        String sql = """
                SELECT d.id, d.membre_id, CONCAT(m.prenom, ' ', m.nom) AS membre,
                       d.type, d.date_demande, d.statut
                FROM demandes_abonnement d
                INNER JOIN membres m ON m.id = d.membre_id
                WHERE d.statut = 'EN_ATTENTE'
                ORDER BY d.date_demande, d.id
                """;
        List<DemandeAbonnement> result = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) {
                Timestamp date = rows.getTimestamp("date_demande");
                result.add(new DemandeAbonnement(
                        rows.getInt("id"), rows.getInt("membre_id"),
                        rows.getString("membre"), rows.getString("type"),
                        date == null ? null : date.toLocalDateTime(), rows.getString("statut")));
            }
            return result;
        } catch (SQLException e) {
            throw databaseError("les demandes d'abonnement en attente", e);
        }
    }

    public void approuverDemande(int demandeId, double prix, String modePaiement, int personnelId) {
        if (demandeId <= 0 || prix <= 0 || !List.of(
                "ESPECES", "WAVE", "ORANGE_MONEY", "CARTE_BANCAIRE").contains(modePaiement)) {
            throw new IllegalArgumentException("Les informations de paiement ne sont pas valides.");
        }
        String requestSql = """
                SELECT membre_id, type
                FROM demandes_abonnement
                WHERE id = ? AND statut = 'EN_ATTENTE'
                FOR UPDATE
                """;
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int membreId;
                String type;
                try (PreparedStatement request = connection.prepareStatement(requestSql)) {
                    request.setInt(1, demandeId);
                    try (ResultSet row = request.executeQuery()) {
                        if (!row.next()) {
                            throw new IllegalArgumentException("Cette demande a déjà été traitée ou n'existe plus.");
                        }
                        membreId = row.getInt("membre_id");
                        type = row.getString("type");
                    }
                }
                verifierCompteActif(connection, membreId);
                verifierAucunAbonnementValide(connection, membreId);
                LocalDate start = LocalDate.now();
                LocalDate end = switch (type) {
                    case "JOURNALIER" -> start;
                    case "HEBDOMADAIRE" -> start.plusWeeks(1).minusDays(1);
                    case "MENSUEL" -> start.plusMonths(1).minusDays(1);
                    case "TRIMESTRIEL" -> start.plusMonths(3).minusDays(1);
                    case "SEMESTRIEL" -> start.plusMonths(6).minusDays(1);
                    case "ANNUEL" -> start.plusYears(1).minusDays(1);
                    default -> throw new IllegalArgumentException("Type d'abonnement invalide.");
                };
                int abonnementId;
                try (PreparedStatement insert = connection.prepareStatement("""
                        INSERT INTO abonnements(membre_id, type, prix, date_debut, date_fin, statut)
                        VALUES (?, ?, ?, ?, ?, 'ACTIF')
                        """, Statement.RETURN_GENERATED_KEYS)) {
                    insert.setInt(1, membreId);
                    insert.setString(2, type);
                    insert.setDouble(3, prix);
                    insert.setDate(4, Date.valueOf(start));
                    insert.setDate(5, Date.valueOf(end));
                    insert.executeUpdate();
                    try (ResultSet keys = insert.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("L'identifiant de l'abonnement n'a pas été généré.");
                        }
                        abonnementId = keys.getInt(1);
                    }
                }
                try (PreparedStatement payment = connection.prepareStatement("""
                        INSERT INTO paiements
                          (membre_id, abonnement_id, montant, date_paiement,
                           mode_paiement, statut, reference)
                        VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, 'VALIDE', ?)
                        """)) {
                    payment.setInt(1, membreId);
                    payment.setInt(2, abonnementId);
                    payment.setDouble(3, prix);
                    payment.setString(4, modePaiement);
                    payment.setString(5, PaiementReferenceGenerator.generate());
                    payment.executeUpdate();
                }
                try (PreparedStatement update = connection.prepareStatement("""
                        UPDATE demandes_abonnement
                        SET statut = 'APPROUVEE', date_traitement = CURRENT_TIMESTAMP,
                            traite_par = ?, abonnement_id = ?
                        WHERE id = ? AND statut = 'EN_ATTENTE'
                        """)) {
                    update.setInt(1, personnelId);
                    update.setInt(2, abonnementId);
                    update.setInt(3, demandeId);
                    if (update.executeUpdate() != 1) {
                        throw new SQLException("La demande n'a pas pu être mise à jour.");
                    }
                }
                connection.commit();
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw databaseError("la validation de la demande d'abonnement", e);
        }
    }

    public void refuserDemande(int demandeId, int personnelId) {
        String sql = """
                UPDATE demandes_abonnement
                SET statut = 'REFUSEE', date_traitement = CURRENT_TIMESTAMP, traite_par = ?
                WHERE id = ? AND statut = 'EN_ATTENTE'
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, personnelId);
            statement.setInt(2, demandeId);
            if (statement.executeUpdate() != 1) {
                throw new IllegalArgumentException("Cette demande a déjà été traitée ou n'existe plus.");
            }
        } catch (SQLException e) {
            throw databaseError("le refus de la demande d'abonnement", e);
        }
    }

    private void verifierCompteActif(Connection connection, int membreId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT m.statut
                FROM comptes_membres cm
                INNER JOIN membres m ON m.id = cm.membre_id
                WHERE cm.membre_id = ? AND cm.statut = 'ACTIF'
                FOR UPDATE
                """)) {
            statement.setInt(1, membreId);
            try (ResultSet row = statement.executeQuery()) {
                if (!row.next() || !"ACTIF".equals(row.getString("statut"))) {
                    throw new IllegalArgumentException("Le compte membre est désactivé.");
                }
            }
        }
    }

    private void verifierMembreConnecte(int membreId) {
        if (!MembreSessionManager.isLoggedIn()
                || MembreSessionManager.getMembreId() != membreId) {
            throw new SecurityException("Accès refusé : cet espace appartient à un autre membre.");
        }
        try (Connection connection = DatabaseConnection.getConnection()) {
            verifierCompteActif(connection, membreId);
        } catch (SQLException e) {
            throw databaseError("la vérification du compte membre", e);
        }
    }

    private void verifierAucunAbonnementValide(Connection connection, int membreId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT 1 FROM abonnements
                WHERE membre_id = ? AND statut = 'ACTIF'
                  AND date_debut <= CURDATE() AND date_fin >= CURDATE()
                LIMIT 1 FOR UPDATE
                """)) {
            statement.setInt(1, membreId);
            try (ResultSet row = statement.executeQuery()) {
                if (row.next()) {
                    throw new IllegalArgumentException(
                            "Le membre possède déjà un abonnement valide.");
                }
            }
        }
    }

    private LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }

    private RuntimeException databaseError(String action, SQLException cause) {
        if ("42S02".equals(cause.getSQLState()) || cause.getErrorCode() == 1146) {
            return new IllegalStateException(
                    "Les tables de l'espace membre ne sont pas installées. Appliquez member-portal-migration.sql.",
                    cause);
        }
        return new IllegalStateException("Erreur lors de " + action + ".", cause);
    }
}
