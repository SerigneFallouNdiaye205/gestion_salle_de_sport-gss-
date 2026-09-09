package com.gss.gss.service;

import com.gss.gss.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Données utilisées exclusivement par le tableau de bord.
 * Toutes les informations affichées dans Home.fxml viennent de MySQL.
 */
public class DashboardService {

    public record PlanningItem(int id, String nom, LocalTime debut, LocalTime fin,
                               String salle, String coach, int capacite) {}

    public record RecentMember(int id, String nomComplet, String telephone,
                                LocalDate dateInscription, String statut) {}

    public record RecentSubscription(int id, int membreId, String membre,
                                     String type, LocalDate debut,
                                     LocalDate fin, String statut, double prix) {}

    public record RecentPayment(int id, int membreId, String membre,
                                int abonnementId, double montant,
                                LocalDateTime date, String mode, String statut,
                                String reference) {}

    public record NotificationItem(String niveau, String titre, String detail,
                                   LocalDateTime date) {
        public NotificationItem(String niveau, String titre, String detail) {
            this(niveau, titre, detail, LocalDateTime.MIN);
        }
    }

    /** Fréquentation confirmée des 7 derniers jours. */
    public Map<LocalDate, Integer> getAttendanceLast7Days() {
        Map<LocalDate, Integer> result = new LinkedHashMap<>();
        LocalDate firstDay = LocalDate.now().minusDays(6);
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        for (int i = 0; i < 7; i++) {
            result.put(firstDay.plusDays(i), 0);
        }

        String sql = """
            SELECT DATE(date_inscription) AS jour, COUNT(*) AS total
            FROM inscriptions_seances
            WHERE date_inscription >= ?
              AND date_inscription < ?
              AND statut = 'CONFIRMEE'
            GROUP BY DATE(date_inscription)
            ORDER BY jour
            """;

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(firstDay.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(tomorrow.atStartOfDay()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getDate("jour").toLocalDate(), rs.getInt("total"));
                }
            }
            return result;
        } catch (SQLException e) {
            throw databaseError("la fréquentation", e);
        }
    }

    /** Planning réel des séances du jour. */
    public List<PlanningItem> getPlanningToday() {
        String sql = """
            SELECT s.id, s.nom, s.heure_debut, s.heure_fin, s.salle,
                   CONCAT(c.prenom, ' ', c.nom) AS coach, s.capacite
            FROM seances s
            LEFT JOIN coachs c ON c.id = s.coach_id
            WHERE s.date_seance = CURDATE()
            ORDER BY s.heure_debut
            """;

        List<PlanningItem> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Time debut = rs.getTime("heure_debut");
                Time fin = rs.getTime("heure_fin");
                result.add(new PlanningItem(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        debut == null ? null : debut.toLocalTime(),
                        fin == null ? null : fin.toLocalTime(),
                        rs.getString("salle"),
                        rs.getString("coach"),
                        rs.getInt("capacite")
                ));
            }
            return result;
        } catch (SQLException e) {
            throw databaseError("le planning d'aujourd'hui", e);
        }
    }

    /** Les membres inscrits le plus récemment. */
    public List<RecentMember> getRecentMembers(int limit) {
        String sql = """
            SELECT id, CONCAT(prenom, ' ', nom) AS nom_complet,
                   telephone, date_inscription, statut
            FROM membres
            ORDER BY date_inscription DESC, id DESC
            LIMIT ?
            """;

        List<RecentMember> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, Math.max(1, limit));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date d = rs.getDate("date_inscription");
                    result.add(new RecentMember(
                            rs.getInt("id"),
                            rs.getString("nom_complet"),
                            rs.getString("telephone"),
                            d == null ? null : d.toLocalDate(),
                            rs.getString("statut")
                    ));
                }
            }
            return result;
        } catch (SQLException e) {
            throw databaseError("les membres récents", e);
        }
    }

    /** Les abonnements créés le plus récemment. */
    public List<RecentSubscription> getRecentSubscriptions(int limit) {
        String sql = """
            SELECT a.id, a.membre_id,
                   CONCAT(m.prenom, ' ', m.nom) AS membre,
                   a.type, a.date_debut, a.date_fin, a.statut, a.prix
            FROM abonnements a
            INNER JOIN membres m ON m.id = a.membre_id
            ORDER BY a.id DESC
            LIMIT ?
            """;

        List<RecentSubscription> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, Math.max(1, limit));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new RecentSubscription(
                            rs.getInt("id"),
                            rs.getInt("membre_id"),
                            rs.getString("membre"),
                            rs.getString("type"),
                            toLocalDate(rs.getDate("date_debut")),
                            toLocalDate(rs.getDate("date_fin")),
                            rs.getString("statut"),
                            rs.getDouble("prix")
                    ));
                }
            }
            return result;
        } catch (SQLException e) {
            throw databaseError("les abonnements récents", e);
        }
    }

    /** Les paiements les plus récents. */
    public List<RecentPayment> getRecentPayments(int limit) {
        String sql = """
            SELECT p.id, p.membre_id,
                   CONCAT(m.prenom, ' ', m.nom) AS membre,
                   p.abonnement_id, p.montant, p.date_paiement,
                   p.mode_paiement, p.statut, p.reference
            FROM paiements p
            INNER JOIN membres m ON m.id = p.membre_id
            ORDER BY p.date_paiement DESC, p.id DESC
            LIMIT ?
            """;

        List<RecentPayment> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, Math.max(1, limit));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("date_paiement");
                    result.add(new RecentPayment(
                            rs.getInt("id"),
                            rs.getInt("membre_id"),
                            rs.getString("membre"),
                            rs.getInt("abonnement_id"),
                            rs.getDouble("montant"),
                            ts == null ? null : ts.toLocalDateTime(),
                            rs.getString("mode_paiement"),
                            rs.getString("statut"),
                            rs.getString("reference")
                    ));
                }
            }
            return result;
        } catch (SQLException e) {
            throw databaseError("les paiements récents", e);
        }
    }

    /**
     * Notifications calculées depuis les données MySQL.
     * Les alertes métier sont complétées par les dernières activités
     * enregistrées afin que le tableau de bord reflète les actions récentes.
     */
    public List<NotificationItem> getNotifications(int days) {
        List<NotificationItem> result = new ArrayList<>();

        String activitiesSql = """
            SELECT niveau, titre, detail, date_evenement
            FROM (
                SELECT
                    'ACTIVITE' AS niveau,
                    'Nouveau membre' AS titre,
                    CONCAT('Membre : ', prenom, ' ', nom) AS detail,
                    CAST(date_inscription AS DATETIME) AS date_evenement,
                    id AS ordre_id
                FROM membres

                UNION ALL

                SELECT
                    'ACTIVITE',
                    'Nouvel abonnement',
                    CONCAT(
                        'Membre : ', m.prenom, ' ', m.nom,
                        ' - Formule : ', a.type,
                        ' - Montant : ', FORMAT(a.prix, 0), ' FCFA'
                    ),
                    CAST(a.date_debut AS DATETIME),
                    a.id
                FROM abonnements a
                INNER JOIN membres m ON m.id = a.membre_id

                UNION ALL

                SELECT
                    'ACTIVITE',
                    'Paiement enregistré',
                    CONCAT(
                        'Membre : ', m.prenom, ' ', m.nom,
                        ' - Montant : ', FORMAT(p.montant, 0), ' FCFA'
                    ),
                    p.date_paiement,
                    p.id
                FROM paiements p
                INNER JOIN membres m ON m.id = p.membre_id

                UNION ALL

                SELECT
                    'ACTIVITE',
                    'Inscription à une séance',
                    CONCAT(
                        'Membre : ', m.prenom, ' ', m.nom,
                        ' - Séance : ', s.nom,
                        ' - Statut : ', i.statut
                    ),
                    CAST(i.date_inscription AS DATETIME),
                    i.id
                FROM inscriptions_seances i
                INNER JOIN membres m ON m.id = i.membre_id
                INNER JOIN seances s ON s.id = i.seance_id
            ) activites
            ORDER BY date_evenement DESC, ordre_id DESC
            LIMIT 7
            """;

        try (Connection c = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(activitiesSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp timestamp = rs.getTimestamp("date_evenement");
                    result.add(new NotificationItem(
                            rs.getString("niveau"),
                            rs.getString("titre"),
                            rs.getString("detail"),
                            timestamp == null ? LocalDateTime.MIN : timestamp.toLocalDateTime()
                    ));
                }
            }

            if (result.isEmpty()) {
                result.add(new NotificationItem(
                        "OK",
                        "Aucune activité récente",
                        "Les dernières actions apparaîtront ici."
                ));
            }
            return result;
        } catch (SQLException e) {
            throw databaseError("les notifications", e);
        }
    }

    private int count(Connection c, String sql) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private String firstValue(Connection c, String sql) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getString(1) : null;
        }
    }

    private String[] firstValues(Connection c, String sql, int columns) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                return new String[0];
            }

            String[] values = new String[columns];
            for (int i = 0; i < columns; i++) {
                values[i] = rs.getString(i + 1);
            }
            return values;
        }
    }

    private LocalDateTime parseDate(String[] values, int index) {
        if (values.length <= index || values[index] == null) {
            return LocalDateTime.MIN;
        }
        String value = values[index];
        try {
            return LocalDateTime.parse(value.replace(' ', 'T'));
        } catch (java.time.format.DateTimeParseException ignored) {
            try {
                return LocalDate.parse(value).atStartOfDay();
            } catch (java.time.format.DateTimeParseException ignoredDate) {
                return LocalDateTime.MIN;
            }
        }
    }

    private LocalDate toLocalDate(java.sql.Date date) {
        return date == null ? null : date.toLocalDate();
    }

    private RuntimeException databaseError(String what, SQLException e) {
        return new RuntimeException("Erreur lors du chargement de " + what + ".", e);
    }
}
