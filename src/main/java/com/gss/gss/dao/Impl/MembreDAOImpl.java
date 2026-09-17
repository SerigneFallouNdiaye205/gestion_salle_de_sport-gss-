package com.gss.gss.dao.Impl;

import com.gss.gss.dao.MembreDAO;
import com.gss.gss.database.DatabaseConnection;
import com.gss.gss.model.Membre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MembreDAOImpl implements MembreDAO {

    @Override
    public boolean save(Membre membre) {

        String sql = """
                INSERT INTO membres
                (nom, prenom, sexe, telephone, email, adresse,
                 date_naissance, date_inscription, statut)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, membre.getNom());
            ps.setString(2, membre.getPrenom());
            ps.setString(3, membre.getSexe() == null ? "M" : membre.getSexe());
            ps.setString(4, membre.getTelephone());
            ps.setString(5, membre.getEmail());
            ps.setString(6, membre.getAdresse());

            if (membre.getDateNaissance() != null) {
                ps.setDate(7, Date.valueOf(membre.getDateNaissance()));
            } else {
                ps.setNull(7, Types.DATE);
            }

            ps.setDate(8, Date.valueOf(membre.getDateInscription()));
            ps.setString(9, membre.getStatut());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Membre membre) {

        String sql = """
                UPDATE membres
                SET nom = ?,
                    prenom = ?,
                    sexe = ?,
                    telephone = ?,
                    email = ?,
                    adresse = ?,
                    date_naissance = ?,
                    date_inscription = ?,
                    statut = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, membre.getNom());
            ps.setString(2, membre.getPrenom());
            ps.setString(3, membre.getSexe() == null ? "M" : membre.getSexe());
            ps.setString(4, membre.getTelephone());
            ps.setString(5, membre.getEmail());
            ps.setString(6, membre.getAdresse());

            if (membre.getDateNaissance() != null) {
                ps.setDate(7, Date.valueOf(membre.getDateNaissance()));
            } else {
                ps.setNull(7, Types.DATE);
            }

            ps.setDate(8, Date.valueOf(membre.getDateInscription()));
            ps.setString(9, membre.getStatut());
            ps.setInt(10, membre.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {

        String sql = "DELETE FROM membres WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Membre findById(int id) {

        String sql = "SELECT * FROM membres WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

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
    public List<Membre> findAll() {

        List<Membre> membres = new ArrayList<>();

        String sql = "SELECT * FROM membres ORDER BY id DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                membres.add(convertir(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return membres;
    }

    @Override
    public List<Membre> rechercher(String recherche) {

        List<Membre> membres = new ArrayList<>();

        String sql = """
                SELECT * FROM membres
                WHERE CAST(id AS CHAR) LIKE ?
                   OR nom LIKE ?
                   OR prenom LIKE ?
                   OR telephone LIKE ?
                   OR email LIKE ?
                ORDER BY id DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            String valeur = "%" + recherche + "%";

            ps.setString(1, valeur);
            ps.setString(2, valeur);
            ps.setString(3, valeur);
            ps.setString(4, valeur);
            ps.setString(5, valeur);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    membres.add(convertir(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return membres;
    }

    @Override
    public List<Membre> findByStatut(String statut) {

        List<Membre> membres = new ArrayList<>();

        String sql = """
                SELECT * FROM membres
                WHERE statut = ?
                ORDER BY id DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, statut);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    membres.add(convertir(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return membres;
    }

    @Override
    public List<Membre> findByCoachId(int coachId) {
        List<Membre> membres = new ArrayList<>();

        String sql = """
                SELECT DISTINCT m.*
                FROM membres m
                JOIN inscriptions_seances i ON i.membre_id = m.id
                JOIN seances s ON s.id = i.seance_id
                WHERE s.coach_id = ?
                ORDER BY m.id DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, coachId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    membres.add(convertir(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return membres;
    }

    private Membre convertir(ResultSet rs) throws SQLException {

        Membre membre = new Membre();

        membre.setId(rs.getInt("id"));
        membre.setNom(rs.getString("nom"));
        membre.setPrenom(rs.getString("prenom"));
        membre.setSexe(rs.getString("sexe"));
        membre.setTelephone(rs.getString("telephone"));
        membre.setEmail(rs.getString("email"));
        membre.setAdresse(rs.getString("adresse"));

        Date naissance = rs.getDate("date_naissance");

        if (naissance != null) {
            membre.setDateNaissance(naissance.toLocalDate());
        }

        Date inscription = rs.getDate("date_inscription");

        if (inscription != null) {
            membre.setDateInscription(inscription.toLocalDate());
        }

        membre.setStatut(rs.getString("statut"));

        return membre;
    }
}
