package com.gss.gss.dao.Impl;

import com.gss.gss.dao.AbonnementDAO;
import com.gss.gss.database.DatabaseConnection;
import com.gss.gss.model.Abonnement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AbonnementsDAOImpl implements AbonnementDAO {

    @Override
    public List<Abonnement> findAll() {

        List<Abonnement> abonnement = new ArrayList<>();

        String sql = "SELECT * FROM abonnements ORDER BY id DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                abonnement.add(convertir(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return abonnement;
    }
    private Abonnement convertir(ResultSet rs) throws SQLException {

        Abonnement abonnement = new Abonnement();

        abonnement.setId(rs.getInt("id"));
        abonnement.setMembreId(rs.getInt("membre_id"));
        abonnement.setPrix(rs.getDouble("prix"));
        abonnement.setType(rs.getString("type"));

        Date dateDebut = rs.getDate("date_debut");
        if (dateDebut != null) {
            abonnement.setDateFin(dateDebut.toLocalDate());
        }
        Date dateFin = rs.getDate("date_fin");
        if (dateFin != null) {
            abonnement.setDateFin(dateFin.toLocalDate());
        }

        abonnement.setStatut(rs.getString("statut"));

        return abonnement;
    }
}
