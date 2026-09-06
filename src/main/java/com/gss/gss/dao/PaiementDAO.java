package com.gss.gss.dao;

import com.gss.gss.model.Paiement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaiementDAO {

        boolean save(Paiement paiement);

        boolean update(Paiement paiement);

        boolean delete(int id);

        Optional<Paiement> findById(int id);

        List<Paiement> findAll();

        List<Paiement> findByMembreId(int membreId);

        List<Paiement> findByAbonnementId(int abonnementId);

        List<Paiement> search(String keyword);

        List<Paiement> findByStatut(String statut);

        List<Paiement> findByModePaiement(String modePaiement);

        List<Paiement> findByDate(LocalDateTime date);
}