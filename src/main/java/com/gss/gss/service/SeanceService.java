package com.gss.gss.service;

import com.gss.gss.dao.Impl.SeanceDAOImpl;
import com.gss.gss.dao.SeanceDAO;
import com.gss.gss.model.Seance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class SeanceService {

    private final SeanceDAO seanceDAO;

    public SeanceService() {
        this.seanceDAO = new SeanceDAOImpl();
    }

    public List<Seance> findAll() {
        return seanceDAO.findAll();
    }

    public Seance findById(int id) {
        return seanceDAO.findById(id);
    }

    public long countAll() {
        return seanceDAO.findAll().size();
    }

    public boolean ajouter(Seance seance) {

        if (!valider(seance)) {
            return false;
        }

        return seanceDAO.save(seance);
    }

    public boolean modifier(Seance seance) {

        if (!valider(seance)) {
            return false;
        }

        return seanceDAO.update(seance);
    }

    public boolean supprimer(int id) {

        return seanceDAO.delete(id);
    }

    private boolean valider(Seance seance) {

        if (seance == null) {
            return false;
        }

        if (seance.getCoachId() <= 0) {
            return false;
        }

        if (seance.getNom() == null ||
                seance.getNom().trim().isEmpty()) {
            return false;
        }

        if (seance.getDateSeance() == null) {
            return false;
        }

        if (seance.getHeureDebut() == null ||
                seance.getHeureFin() == null) {
            return false;
        }

        if (!seance.getHeureFin().isAfter(
                seance.getHeureDebut())) {
            return false;
        }

        if (seance.getSalle() == null ||
                seance.getSalle().trim().isEmpty()) {
            return false;
        }

        if (seance.getCapacite() <= 0) {
            return false;
        }

        return true;
    }
}