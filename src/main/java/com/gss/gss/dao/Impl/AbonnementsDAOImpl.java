package com.gss.gss.dao.Impl;

import com.gss.gss.dao.AbonnementDAO;
import com.gss.gss.database.DatabaseConnection;
import com.gss.gss.model.Abonnement;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AbonnementsDAOImpl implements AbonnementDAO {

    @Override
    public List<Abonnement> findAll() {

        List<Abonnement> abonnements = new ArrayList<>();

        String sql = """
                SELECT id, membre_id, type, prix,
                       date_debut, date_fin, statut
                FROM abonnements
                ORDER BY id DESC
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                abonnements.add(convertir(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la récupération des abonnements.",
                    e
            );
        }

        return abonnements;
    }

    @Override
    public Optional<Abonnement> findById(int id) {

        String sql = """
                SELECT id, membre_id, type, prix,
                       date_debut, date_fin, statut
                FROM abonnements
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(convertir(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la recherche de l'abonnement.",
                    e
            );
        }

        return Optional.empty();
    }

    @Override
    public List<Abonnement> findByMembreId(int membreId) {

        List<Abonnement> abonnements = new ArrayList<>();

        String sql = """
                SELECT id, membre_id, type, prix,
                       date_debut, date_fin, statut
                FROM abonnements
                WHERE membre_id = ?
                ORDER BY id DESC
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setInt(1, membreId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    abonnements.add(convertir(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la recherche des abonnements du membre.",
                    e
            );
        }

        return abonnements;
    }

    @Override
    public List<Abonnement> findByStatut(String statut) {

        List<Abonnement> abonnements = new ArrayList<>();

        String sql = """
                SELECT id, membre_id, type, prix,
                       date_debut, date_fin, statut
                FROM abonnements
                WHERE statut = ?
                ORDER BY id DESC
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setString(1, statut);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    abonnements.add(convertir(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la recherche par statut.",
                    e
            );
        }

        return abonnements;
    }

    @Override
    public List<Abonnement> findByType(String type) {

        List<Abonnement> abonnements = new ArrayList<>();

        String sql = """
                SELECT id, membre_id, type, prix,
                       date_debut, date_fin, statut
                FROM abonnements
                WHERE type = ?
                ORDER BY id DESC
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setString(1, type);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    abonnements.add(convertir(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la recherche par type.",
                    e
            );
        }

        return abonnements;
    }

    @Override
    public List<Abonnement> findActive() {

        return findByStatut("ACTIF");
    }

    @Override
    public List<Abonnement> findExpired() {

        List<Abonnement> abonnements = new ArrayList<>();

        String sql = """
                SELECT id, membre_id, type, prix,
                       date_debut, date_fin, statut
                FROM abonnements
                WHERE date_fin < CURDATE()
                AND statut = 'ACTIF'
                ORDER BY date_fin ASC
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                abonnements.add(convertir(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la recherche des abonnements expirés.",
                    e
            );
        }

        return abonnements;
    }

    @Override
    public List<Abonnement> findExpiringSoon(int jours) {

        List<Abonnement> abonnements = new ArrayList<>();

        String sql = """
                SELECT id, membre_id, type, prix,
                       date_debut, date_fin, statut
                FROM abonnements
                WHERE statut = 'ACTIF'
                AND date_fin BETWEEN CURDATE()
                AND DATE_ADD(CURDATE(), INTERVAL ? DAY)
                ORDER BY date_fin ASC
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setInt(1, jours);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    abonnements.add(convertir(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la recherche des abonnements arrivant à expiration.",
                    e
            );
        }

        return abonnements;
    }

    @Override
    public List<Abonnement> search(String recherche) {

        List<Abonnement> abonnements = new ArrayList<>();

        String sql = """
                SELECT id, membre_id, type, prix,
                       date_debut, date_fin, statut
                FROM abonnements
                WHERE CAST(id AS CHAR) LIKE ?
                   OR CAST(membre_id AS CHAR) LIKE ?
                   OR type LIKE ?
                   OR statut LIKE ?
                   OR CAST(prix AS CHAR) LIKE ?
                ORDER BY id DESC
                """;

        String valeur = "%" + recherche + "%";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setString(1, valeur);
            ps.setString(2, valeur);
            ps.setString(3, valeur);
            ps.setString(4, valeur);
            ps.setString(5, valeur);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    abonnements.add(convertir(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la recherche des abonnements.",
                    e
            );
        }

        return abonnements;
    }

    @Override
    public boolean save(Abonnement abonnement) {

        String sql = """
                INSERT INTO abonnements
                (membre_id, type, prix, date_debut, date_fin, statut)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {

            ps.setInt(1, abonnement.getMembreId());
            ps.setString(2, abonnement.getType());
            ps.setDouble(3, abonnement.getPrix());
            ps.setDate(4, Date.valueOf(abonnement.getDateDebut()));
            ps.setDate(5, Date.valueOf(abonnement.getDateFin()));
            ps.setString(6, abonnement.getStatut());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {

                if (keys.next()) {
                    abonnement.setId(keys.getInt(1));
                }
            }

            return true;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de l'ajout de l'abonnement.",
                    e
            );
        }
    }

    @Override
    public boolean update(Abonnement abonnement) {

        String sql = """
                UPDATE abonnements
                SET membre_id = ?,
                    type = ?,
                    prix = ?,
                    date_debut = ?,
                    date_fin = ?,
                    statut = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setInt(1, abonnement.getMembreId());
            ps.setString(2, abonnement.getType());
            ps.setDouble(3, abonnement.getPrix());
            ps.setDate(4, Date.valueOf(abonnement.getDateDebut()));
            ps.setDate(5, Date.valueOf(abonnement.getDateFin()));
            ps.setString(6, abonnement.getStatut());
            ps.setInt(7, abonnement.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la modification de l'abonnement.",
                    e
            );
        }
    }

    @Override
    public boolean delete(int id) {

        String sql = "DELETE FROM abonnements WHERE id = ?";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la suppression de l'abonnement.",
                    e
            );
        }
    }

    @Override
    public boolean renouveler(
            int id,
            LocalDate nouvelleDateFin,
            double nouveauPrix
    ) {

        String sql = """
                UPDATE abonnements
                SET date_fin = ?,
                    prix = ?,
                    statut = 'ACTIF'
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setDate(1, Date.valueOf(nouvelleDateFin));
            ps.setDouble(2, nouveauPrix);
            ps.setInt(3, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors du renouvellement de l'abonnement.",
                    e
            );
        }
    }

    @Override
    public int countActive() {

        String sql = """
                SELECT COUNT(*)
                FROM abonnements
                WHERE statut = 'ACTIF'
                  AND date_fin >= CURDATE()
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors du comptage des abonnements actifs.",
                    e
            );
        }

        return 0;
    }

    @Override
    public int countExpired() {

        String sql = """
                SELECT COUNT(*)
                FROM abonnements
                WHERE date_fin < CURDATE()
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors du comptage des abonnements expirés.",
                    e
            );
        }

        return 0;
    }

    @Override
    public int countTotal() {

        String sql = "SELECT COUNT(*) FROM abonnements";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors du comptage des abonnements.",
                    e
            );
        }

        return 0;
    }

    @Override
    public double totalRevenue() {

        String sql = """
                SELECT COALESCE(SUM(prix), 0)
                FROM abonnements
                WHERE statut = 'ACTIF'
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors du calcul des revenus.",
                    e
            );
        }

        return 0;
    }

    /**
     * Transforme une ligne SQL en objet Abonnement.
     */
    private Abonnement convertir(ResultSet rs) throws SQLException {

        Abonnement abonnement = new Abonnement();

        abonnement.setId(rs.getInt("id"));
        abonnement.setMembreId(rs.getInt("membre_id"));
        abonnement.setType(rs.getString("type"));
        abonnement.setPrix(rs.getDouble("prix"));

        Date dateDebut = rs.getDate("date_debut");

        if (dateDebut != null) {
            abonnement.setDateDebut(
                    dateDebut.toLocalDate()
            );
        }

        Date dateFin = rs.getDate("date_fin");

        if (dateFin != null) {
            abonnement.setDateFin(
                    dateFin.toLocalDate()
            );
        }

        abonnement.setStatut(
                rs.getString("statut")
        );

        return abonnement;
    }
}