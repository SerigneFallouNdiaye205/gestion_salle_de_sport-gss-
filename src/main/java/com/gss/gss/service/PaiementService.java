package com.gss.gss.service;

import com.gss.gss.dao.PaiementDAO;
import com.gss.gss.dao.Impl.PaiementDAOImpl;
import com.gss.gss.model.Paiement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PaiementService {

    private final PaiementDAO paiementDAO;

    public PaiementService() {
        this.paiementDAO = new PaiementDAOImpl();
    }

    public boolean save(Paiement paiement) {

        if (paiement == null) {
            return false;
        }

        return paiementDAO.save(paiement);
    }

    public boolean update(Paiement paiement) {

        if (paiement == null || paiement.getId() <= 0) {
            return false;
        }

        return paiementDAO.update(paiement);
    }

    public boolean delete(int id) {

        if (id <= 0) {
            return false;
        }

        return paiementDAO.delete(id);
    }

    public Optional<Paiement> findById(int id) {
        return paiementDAO.findById(id);
    }

    public List<Paiement> findAll() {
        return paiementDAO.findAll();
    }

    public List<Paiement> findByMembreId(int membreId) {
        return paiementDAO.findByMembreId(membreId);
    }

    public List<Paiement> findByAbonnementId(int abonnementId) {
        return paiementDAO.findByAbonnementId(abonnementId);
    }

    public List<Paiement> search(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }

        return paiementDAO.search(keyword.trim());
    }

    public List<Paiement> findByStatut(String statut) {
        return paiementDAO.findByStatut(statut);
    }

    public List<Paiement> findByModePaiement(String modePaiement) {
        return paiementDAO.findByModePaiement(modePaiement);
    }

    public List<Paiement> findByDate(LocalDateTime date) {
        return paiementDAO.findByDate(date);
    }
}