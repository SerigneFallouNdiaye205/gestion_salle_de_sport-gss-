package com.gss.gss.dao.Impl;

import com.gss.gss.dao.PaiementDAO;
import com.gss.gss.database.DatabaseConnection;
import com.gss.gss.model.Paiement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaiementDAOImpl implements PaiementDAO {
    @Override
    public List<Paiement> findAll() {

        List<Paiement> paiement = new ArrayList<>();

        String sql = "SELECT * FROM paiements ORDER BY id DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                paiement.add(convertir(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return paiement;
    }
    private Paiement convertir(ResultSet rs) throws SQLException {

        Paiement paiement = new Paiement();

        paiement.setId(rs.getInt("id"));
        paiement.setMembre_id(rs.getInt("membre_id"));
        paiement.setAbonnement_id(rs.getInt("abonnement_id"));
        paiement.setMontant(rs.getDouble("montant"));

        Date datePaiement = rs.getDate("date_paiement");
        if (datePaiement != null) {
            paiement.setDate_paiement(datePaiement.toLocalDate());
        }

        paiement.setMode_paiement(rs.getString("mode_paiement"));
        paiement.setStatut(rs.getString("statut"));
        paiement.setReference(rs.getString("reference"));


        return paiement;
    }
}
