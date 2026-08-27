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

        List<Seance> seance = new ArrayList<>();

        String sql = "SELECT * FROM seances ORDER BY id DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                seance.add(convertir(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return seance;
    }
    private Seance convertir(ResultSet rs) throws SQLException {

        Seance seance = new Seance();

        seance.setId(rs.getInt("id"));
        seance.setCoachId(rs.getInt("coach_id"));
        seance.setNom(rs.getString("nom"));

        Date dateSeance = rs.getDate("date_seance");
        if (dateSeance != null) {
            seance.setDateSeance(dateSeance.toLocalDate());
        }
        Time HeureDebut = rs.getTime("heure_debut");
        if (HeureDebut != null) {
            seance.setHeureDebut(HeureDebut.toLocalTime());
        }
        Time HeureFin = rs.getTime("heure_fin");
        if (HeureFin != null) {
            seance.setHeureDebut(HeureFin.toLocalTime());
        }

        seance.setSalle(rs.getString("salle"));
        seance.setCapacite(rs.getInt("capacite"));


        return seance;
    }
}
