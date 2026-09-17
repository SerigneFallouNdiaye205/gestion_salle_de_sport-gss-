package com.gss.gss.dao.Impl;

import com.gss.gss.dao.SeanceDAO;
import com.gss.gss.database.DatabaseConnection;
import com.gss.gss.model.Seance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeanceDAOImpl implements SeanceDAO {

    @Override
    public List<Seance> findAll() {

        List<Seance> seances = new ArrayList<>();

        String sql = """
                SELECT *
                FROM seances
                ORDER BY date_seance DESC, heure_debut DESC
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                seances.add(convertir(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return seances;
    }

    @Override
    public Seance findById(int id) {

        String sql = "SELECT * FROM seances WHERE id = ?";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return convertir(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean save(Seance seance) {

        String sql = """
                INSERT INTO seances
                (coach_id, nom, date_seance, heure_debut, heure_fin, salle, capacite)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setInt(1, seance.getCoachId());
            ps.setString(2, seance.getNom());

            ps.setDate(
                    3,
                    Date.valueOf(seance.getDateSeance())
            );

            ps.setTime(
                    4,
                    Time.valueOf(seance.getHeureDebut())
            );

            ps.setTime(
                    5,
                    Time.valueOf(seance.getHeureFin())
            );

            ps.setString(6, seance.getSalle());
            ps.setInt(7, seance.getCapacite());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Seance seance) {

        String sql = """
                UPDATE seances
                SET coach_id = ?,
                    nom = ?,
                    date_seance = ?,
                    heure_debut = ?,
                    heure_fin = ?,
                    salle = ?,
                    capacite = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setInt(1, seance.getCoachId());
            ps.setString(2, seance.getNom());

            ps.setDate(
                    3,
                    Date.valueOf(seance.getDateSeance())
            );

            ps.setTime(
                    4,
                    Time.valueOf(seance.getHeureDebut())
            );

            ps.setTime(
                    5,
                    Time.valueOf(seance.getHeureFin())
            );

            ps.setString(6, seance.getSalle());
            ps.setInt(7, seance.getCapacite());
            ps.setInt(8, seance.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {

        String sql = "DELETE FROM seances WHERE id = ?";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Seance> findByCoachId(int coachId) {
        List<Seance> seances = new ArrayList<>();
        String sql = """
                SELECT *
                FROM seances
                WHERE coach_id = ?
                ORDER BY date_seance DESC, heure_debut DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, coachId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seances.add(convertir(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seances;
    }

    private Seance convertir(ResultSet rs) throws SQLException {

        Seance seance = new Seance();

        seance.setId(rs.getInt("id"));

        seance.setCoachId(
                rs.getInt("coach_id")
        );

        seance.setNom(
                rs.getString("nom")
        );

        Date dateSeance = rs.getDate("date_seance");

        if (dateSeance != null) {
            seance.setDateSeance(
                    dateSeance.toLocalDate()
            );
        }

        Time heureDebut = rs.getTime("heure_debut");

        if (heureDebut != null) {
            seance.setHeureDebut(
                    heureDebut.toLocalTime()
            );
        }

        Time heureFin = rs.getTime("heure_fin");

        if (heureFin != null) {
            seance.setHeureFin(
                    heureFin.toLocalTime()
            );
        }

        seance.setSalle(
                rs.getString("salle")
        );

        seance.setCapacite(
                rs.getInt("capacite")
        );

        return seance;
    }
}