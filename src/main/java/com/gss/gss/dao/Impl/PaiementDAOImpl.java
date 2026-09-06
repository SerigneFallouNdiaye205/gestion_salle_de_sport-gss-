package com.gss.gss.dao.Impl;

import com.gss.gss.dao.PaiementDAO;
import com.gss.gss.model.Paiement;
import com.gss.gss.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaiementDAOImpl implements PaiementDAO {

    @Override
    public boolean save(Paiement paiement) {

        String sql = """
                INSERT INTO paiements
                (membre_id, abonnement_id, montant, date_paiement,
                 mode_paiement, statut, reference)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, paiement.getMembreId());
            statement.setInt(2, paiement.getAbonnementId());
            statement.setDouble(3, paiement.getMontant());

            statement.setTimestamp(
                    4,
                    Timestamp.valueOf(
                            paiement.getDatePaiement()
                    )
            );

            statement.setString(5, paiement.getModePaiement());
            statement.setString(6, paiement.getStatut());
            statement.setString(7, paiement.getReference());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    paiement.setId(generatedKeys.getInt(1));
                }
            }

            return true;

        } catch (SQLException e) {

            System.err.println(
                    "Erreur lors de l'enregistrement du paiement : "
                            + e.getMessage()
            );

            return false;
        }
    }

    @Override
    public boolean update(Paiement paiement) {

        String sql = """
                UPDATE paiements
                SET membre_id = ?,
                    abonnement_id = ?,
                    montant = ?,
                    date_paiement = ?,
                    mode_paiement = ?,
                    statut = ?,
                    reference = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, paiement.getMembreId());
            statement.setInt(2, paiement.getAbonnementId());
            statement.setDouble(3, paiement.getMontant());

            statement.setTimestamp(
                    4,
                    Timestamp.valueOf(
                            paiement.getDatePaiement()
                    )
            );

            statement.setString(5, paiement.getModePaiement());
            statement.setString(6, paiement.getStatut());
            statement.setString(7, paiement.getReference());
            statement.setInt(8, paiement.getId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            System.err.println(
                    "Erreur lors de la modification du paiement : "
                            + e.getMessage()
            );

            return false;
        }
    }

    @Override
    public boolean delete(int id) {

        String sql = "DELETE FROM paiements WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            System.err.println(
                    "Erreur lors de la suppression du paiement : "
                            + e.getMessage()
            );

            return false;
        }
    }

    @Override
    public Optional<Paiement> findById(int id) {

        String sql = "SELECT * FROM paiements WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapResultSet(resultSet));
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Erreur lors de la recherche du paiement : "
                            + e.getMessage()
            );
        }

        return Optional.empty();
    }

    @Override
    public List<Paiement> findAll() {

        List<Paiement> paiements = new ArrayList<>();

        String sql = """
                SELECT *
                FROM paiements
                ORDER BY date_paiement DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                paiements.add(mapResultSet(resultSet));
            }

        } catch (SQLException e) {

            System.err.println(
                    "Erreur lors de la récupération des paiements : "
                            + e.getMessage()
            );
        }

        return paiements;
    }

    @Override
    public List<Paiement> findByMembreId(int membreId) {

        List<Paiement> paiements = new ArrayList<>();

        String sql = """
                SELECT *
                FROM paiements
                WHERE membre_id = ?
                ORDER BY date_paiement DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, membreId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    paiements.add(mapResultSet(resultSet));
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Erreur lors de la récupération des paiements du membre : "
                            + e.getMessage()
            );
        }

        return paiements;
    }

    @Override
    public List<Paiement> findByAbonnementId(int abonnementId) {

        List<Paiement> paiements = new ArrayList<>();

        String sql = """
                SELECT *
                FROM paiements
                WHERE abonnement_id = ?
                ORDER BY date_paiement DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, abonnementId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    paiements.add(mapResultSet(resultSet));
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Erreur lors de la récupération des paiements de l'abonnement : "
                            + e.getMessage()
            );
        }

        return paiements;
    }

    @Override
    public List<Paiement> search(String keyword) {

        List<Paiement> paiements = new ArrayList<>();

        String sql = """
                SELECT *
                FROM paiements
                WHERE CAST(id AS CHAR) LIKE ?
                   OR CAST(membre_id AS CHAR) LIKE ?
                   OR CAST(abonnement_id AS CHAR) LIKE ?
                   OR reference LIKE ?
                ORDER BY date_paiement DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String search = "%" + keyword + "%";

            statement.setString(1, search);
            statement.setString(2, search);
            statement.setString(3, search);
            statement.setString(4, search);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    paiements.add(mapResultSet(resultSet));
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Erreur lors de la recherche : "
                            + e.getMessage()
            );
        }

        return paiements;
    }

    @Override
    public List<Paiement> findByStatut(String statut) {

        List<Paiement> paiements = new ArrayList<>();

        String sql = """
                SELECT *
                FROM paiements
                WHERE statut = ?
                ORDER BY date_paiement DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, statut);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    paiements.add(mapResultSet(resultSet));
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Erreur lors du filtrage par statut : "
                            + e.getMessage()
            );
        }

        return paiements;
    }

    @Override
    public List<Paiement> findByModePaiement(String modePaiement) {

        List<Paiement> paiements = new ArrayList<>();

        String sql = """
                SELECT *
                FROM paiements
                WHERE mode_paiement = ?
                ORDER BY date_paiement DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, modePaiement);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    paiements.add(mapResultSet(resultSet));
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Erreur lors du filtrage par mode de paiement : "
                            + e.getMessage()
            );
        }

        return paiements;
    }

    @Override
    public List<Paiement> findByDate(LocalDateTime date) {

        List<Paiement> paiements = new ArrayList<>();

        String sql = """
                SELECT *
                FROM paiements
                WHERE DATE(date_paiement) = ?
                ORDER BY date_paiement DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, Date.valueOf(date.toLocalDate()));

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    paiements.add(mapResultSet(resultSet));
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Erreur lors de la recherche par date : "
                            + e.getMessage()
            );
        }

        return paiements;
    }

    private Paiement mapResultSet(ResultSet resultSet)
            throws SQLException {

        Paiement paiement = new Paiement();

        paiement.setId(resultSet.getInt("id"));
        paiement.setMembreId(resultSet.getInt("membre_id"));
        paiement.setAbonnementId(resultSet.getInt("abonnement_id"));
        paiement.setMontant(resultSet.getDouble("montant"));

        Timestamp timestamp =
                resultSet.getTimestamp("date_paiement");

        if (timestamp != null) {
            paiement.setDatePaiement(
                    timestamp.toLocalDateTime()
            );
        }

        paiement.setModePaiement(
                resultSet.getString("mode_paiement")
        );

        paiement.setStatut(
                resultSet.getString("statut")
        );

        paiement.setReference(
                resultSet.getString("reference")
        );

        return paiement;
    }
}