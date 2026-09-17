package com.gss.gss.service;

import com.gss.gss.dao.Impl.InscriptionSeanceDAOImpl;
import com.gss.gss.model.Abonnement;
import com.gss.gss.model.InscriptionSeance;
import com.gss.gss.model.Seance;

import java.time.LocalDate;

/**
 * Règles métier communes aux inscriptions, quel que soit le rôle appelant.
 */
public class InscriptionSeanceService {

    private final InscriptionSeanceDAOImpl dao = new InscriptionSeanceDAOImpl();
    private final AbonnementService abonnementService = new AbonnementService();
    private final SeanceService seanceService = new SeanceService();

    public boolean save(InscriptionSeance inscription) {
        valider(inscription, 0);
        return dao.save(inscription);
    }

    public boolean update(InscriptionSeance inscription) {
        if (inscription == null || inscription.getId() <= 0) {
            throw new IllegalArgumentException("L'inscription à modifier est invalide.");
        }
        valider(inscription, inscription.getId());
        return dao.update(inscription);
    }

    public boolean delete(int id) {
        return id > 0 && dao.delete(id);
    }

    private void valider(InscriptionSeance inscription, int excludedId) {
        if (inscription == null || inscription.getMembre_id() <= 0 || inscription.getSeance_id() <= 0) {
            throw new IllegalArgumentException("Le membre et la séance sont obligatoires.");
        }
        if (inscription.getDate_inscription() == null) {
            throw new IllegalArgumentException("La date d'inscription est obligatoire.");
        }

        LocalDate today = LocalDate.now();
        boolean abonnementValide = abonnementService.findByMembreId(inscription.getMembre_id()).stream()
                .anyMatch(a -> "ACTIF".equalsIgnoreCase(a.getStatut())
                        && a.getDateDebut() != null && a.getDateFin() != null
                        && !today.isBefore(a.getDateDebut())
                        && !today.isAfter(a.getDateFin()));
        if (!abonnementValide) {
            throw new IllegalArgumentException(
                    "L'inscription est impossible : le membre ne possède pas d'abonnement valide.");
        }

        Seance seance = seanceService.findById(inscription.getSeance_id());
        if (seance == null) {
            throw new IllegalArgumentException("La séance sélectionnée n'existe plus.");
        }
        if (dao.existsForMemberAndSession(inscription.getMembre_id(), inscription.getSeance_id(), excludedId)) {
            throw new IllegalArgumentException("Ce membre est déjà inscrit à cette séance.");
        }
        if (dao.countOccupants(inscription.getSeance_id(), excludedId) >= seance.getCapacite()
                && !"ANNULEE".equalsIgnoreCase(inscription.getStatut())) {
            throw new IllegalArgumentException("La capacité maximale de cette séance est atteinte.");
        }
    }
}
