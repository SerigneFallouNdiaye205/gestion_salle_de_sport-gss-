package com.gss.gss.dao.Impl;

import com.gss.gss.database.DatabaseConnection;
import com.gss.gss.model.InscriptionSeance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InscriptionSeanceDAOImpl {

    public boolean save(InscriptionSeance inscription) {
        String sql = """
            INSERT INTO inscriptions_seances 
            (membre_id, seance_id, date_inscription, statut)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, inscription.getMembre_id());
            ps.setInt(2, inscription.getSeance_id());
            ps.setDate(3, Date.valueOf(inscription.getDate_inscription()));
            ps.setString(4, inscription.getStatut());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(InscriptionSeance inscription) {
        String sql = """
            UPDATE inscriptions_seances 
            SET membre_id = ?, seance_id = ?, date_inscription = ?, statut = ?
            WHERE id = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, inscription.getMembre_id());
            ps.setInt(2, inscription.getSeance_id());
            ps.setDate(3, Date.valueOf(inscription.getDate_inscription()));
            ps.setString(4, inscription.getStatut());
            ps.setInt(5, inscription.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM inscriptions_seances WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public InscriptionSeance getById(int id) {
        String sql = "SELECT * FROM inscriptions_seances WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<InscriptionSeance> getAll() {
        String sql = "SELECT * FROM inscriptions_seances ORDER BY id DESC";
        List<InscriptionSeance> inscriptions = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                inscriptions.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inscriptions;
    }

    public List<InscriptionSeance> getByMembre(int membreId) {
        String sql = "SELECT * FROM inscriptions_seances WHERE membre_id = ? ORDER BY id DESC";
        List<InscriptionSeance> inscriptions = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, membreId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    inscriptions.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inscriptions;
    }

    public List<InscriptionSeance> getBySeance(int seanceId) {
        String sql = "SELECT * FROM inscriptions_seances WHERE seance_id = ? ORDER BY id DESC";
        List<InscriptionSeance> inscriptions = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, seanceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    inscriptions.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inscriptions;
    }

    public int countOccupants(int seanceId, int excludedInscriptionId) {
        String sql = """
                SELECT COUNT(*) FROM inscriptions_seances
                WHERE seance_id = ?
                  AND statut <> 'ANNULEE'
                  AND id <> ?
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, seanceId);
            ps.setInt(2, excludedInscriptionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Impossible de vérifier la capacité de la séance.", e);
        }
    }

    public boolean existsForMemberAndSession(int membreId, int seanceId, int excludedInscriptionId) {
        String sql = """
                SELECT 1 FROM inscriptions_seances
                WHERE membre_id = ? AND seance_id = ?
                  AND statut <> 'ANNULEE' AND id <> ?
                LIMIT 1
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, membreId);
            ps.setInt(2, seanceId);
            ps.setInt(3, excludedInscriptionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Impossible de vérifier l'inscription existante.", e);
        }
    }

    private InscriptionSeance mapRow(ResultSet rs) throws SQLException {
        return new InscriptionSeance(
                rs.getInt("id"),
                rs.getInt("membre_id"),
                rs.getInt("seance_id"),
                rs.getDate("date_inscription").toLocalDate(),
                rs.getString("statut")
        );
    }
}
