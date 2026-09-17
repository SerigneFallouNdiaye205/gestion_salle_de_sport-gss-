package com.gss.gss.dao;

import com.gss.gss.model.Abonnement;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AbonnementDAO {

    List<Abonnement> findAll();

    Optional<Abonnement> findById(int id);

    List<Abonnement> findByMembreId(int membreId);

    List<Abonnement> findByStatut(String statut);

    List<Abonnement> findByType(String type);

    List<Abonnement> findActive();

    List<Abonnement> findExpired();

    List<Abonnement> findExpiringSoon(int jours);

    List<Abonnement> search(String recherche);

    boolean save(Abonnement abonnement);

    boolean update(Abonnement abonnement);

    boolean delete(int id);

    boolean renouveler(
            int id,
            LocalDate nouvelleDateFin,
            double nouveauPrix
    );

    int countActive();

    int countExpired();

    int countTotal();

    double totalRevenue();
}