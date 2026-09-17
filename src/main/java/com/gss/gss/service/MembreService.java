package com.gss.gss.service;

import com.gss.gss.dao.MembreDAO;
import com.gss.gss.dao.Impl.MembreDAOImpl;
import com.gss.gss.model.Membre;

import java.util.List;

public class MembreService {

    private final MembreDAO membreDAO;

    public MembreService() {
        this.membreDAO = new MembreDAOImpl();
    }

    public boolean save(Membre membre) {
        return membreDAO.save(membre);
    }

    public boolean update(Membre membre) {
        return membreDAO.update(membre);
    }

    public boolean delete(int id) {
        return membreDAO.delete(id);
    }

    public Membre findById(int id) {
        return membreDAO.findById(id);
    }

    public List<Membre> findAll() {
        return membreDAO.findAll();
    }

    public List<Membre> rechercher(String recherche) {
        return membreDAO.rechercher(recherche);
    }

    public long countAll(){
        return membreDAO.findAll().size();
    }

    public List<Membre> findByStatut(String statut) {
        return membreDAO.findByStatut(statut);
    }

    public List<Membre> findByCoachId(int coachId) {
        return membreDAO.findByCoachId(coachId);
    }

    public boolean isManagedByCoach(int membreId, int coachId) {
        return findByCoachId(coachId).stream()
                .anyMatch(membre -> membre.getId() == membreId);
    }
}