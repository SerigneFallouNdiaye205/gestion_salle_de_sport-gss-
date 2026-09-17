package com.gss.gss.service;

import com.gss.gss.dao.AbonnementDAO;
import com.gss.gss.dao.Impl.AbonnementsDAOImpl;
import com.gss.gss.model.Abonnement;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AbonnementService {

    private final AbonnementDAO abonnementDAO;

    public AbonnementService() {
        this.abonnementDAO = new AbonnementsDAOImpl();
    }

    public List<Abonnement> findAll() {
        return abonnementDAO.findAll();
    }

    public Optional<Abonnement> findById(int id) {
        return abonnementDAO.findById(id);
    }

    public List<Abonnement> findByMembreId(int membreId) {
        return abonnementDAO.findByMembreId(membreId);
    }

    public List<Abonnement> findByStatut(String statut) {
        return abonnementDAO.findByStatut(statut);
    }

    public List<Abonnement> findByType(String type) {
        return abonnementDAO.findByType(type);
    }

    public List<Abonnement> findActive() {
        return abonnementDAO.findActive();
    }

    public List<Abonnement> findExpired() {
        return abonnementDAO.findExpired();
    }

    public List<Abonnement> findExpiringSoon(int jours) {
        return abonnementDAO.findExpiringSoon(jours);
    }

    public List<Abonnement> search(String recherche) {
        return abonnementDAO.search(recherche);
    }

    public boolean save(Abonnement abonnement) {

        valider(abonnement);

        if (abonnement.getStatut() == null ||
                abonnement.getStatut().isBlank()) {

            abonnement.setStatut("ACTIF");
        }

        return abonnementDAO.save(abonnement);
    }

    public boolean update(Abonnement abonnement) {

        valider(abonnement);

        return abonnementDAO.update(abonnement);
    }

    public boolean delete(int id) {

        return abonnementDAO.delete(id);
    }

    public boolean renouveler(
            int id,
            LocalDate nouvelleDateFin,
            double nouveauPrix
    ) {

        if (nouvelleDateFin == null) {
            throw new IllegalArgumentException(
                    "La nouvelle date de fin est obligatoire."
            );
        }

        if (nouvelleDateFin.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La nouvelle date de fin doit être future."
            );
        }

        if (nouveauPrix < 0) {
            throw new IllegalArgumentException(
                    "Le prix ne peut pas être négatif."
            );
        }

        return abonnementDAO.renouveler(
                id,
                nouvelleDateFin,
                nouveauPrix
        );
    }

    /**
     * Met automatiquement les abonnements expirés
     * au statut EXPIRE.
     */
    public void mettreAJourStatutsExpires() {

        List<Abonnement> actifs =
                abonnementDAO.findByStatut("ACTIF");

        LocalDate aujourdHui = LocalDate.now();

        for (Abonnement abonnement : actifs) {

            if (abonnement.getDateFin() != null &&
                    abonnement.getDateFin().isBefore(aujourdHui)) {

                abonnement.setStatut("EXPIRE");

                abonnementDAO.update(abonnement);
            }
        }
    }

    public int countActive() {
        return abonnementDAO.countActive();
    }

    public int countExpired() {
        return abonnementDAO.countExpired();
    }

    public int countTotal() {
        return abonnementDAO.countTotal();
    }

    public double totalRevenue() {
        return abonnementDAO.totalRevenue();
    }

    private void valider(Abonnement abonnement) {

        if (abonnement == null) {
            throw new IllegalArgumentException(
                    "L'abonnement est obligatoire."
            );
        }

        if (abonnement.getMembreId() <= 0) {
            throw new IllegalArgumentException(
                    "Le membre est obligatoire."
            );
        }

        if (abonnement.getType() == null ||
                abonnement.getType().isBlank()) {

            throw new IllegalArgumentException(
                    "Le type d'abonnement est obligatoire."
            );
        }

        if (abonnement.getPrix() == null ||
                abonnement.getPrix() < 0) {

            throw new IllegalArgumentException(
                    "Le prix est invalide."
            );
        }

        if (abonnement.getDateDebut() == null) {
            throw new IllegalArgumentException(
                    "La date de début est obligatoire."
            );
        }

        if (abonnement.getDateFin() == null) {
            throw new IllegalArgumentException(
                    "La date de fin est obligatoire."
            );
        }

        if (abonnement.getDateFin()
                .isBefore(abonnement.getDateDebut())) {

            throw new IllegalArgumentException(
                    "La date de fin doit être après la date de début."
            );
        }

        if (abonnement.getStatut() != null &&
                !abonnement.getStatut().isBlank() &&
                !"ACTIF".equals(abonnement.getStatut()) &&
                !"EXPIRE".equals(abonnement.getStatut())) {

            throw new IllegalArgumentException(
                    "Le statut de l'abonnement est invalide."
            );
        }
    }
}