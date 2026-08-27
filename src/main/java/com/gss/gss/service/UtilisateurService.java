package com.gss.gss.service;

import com.gss.gss.dao.Impl.UtilisateurDAOImpl;
import com.gss.gss.dao.UtilisateurDAO;
import com.gss.gss.model.Utilisateur;

import java.util.List;
import java.util.Optional;

public class UtilisateurService {

    private final UtilisateurDAO utilisateurDAO;

    public UtilisateurService() {
        this.utilisateurDAO = new UtilisateurDAOImpl();
    }

    public boolean save(Utilisateur utilisateur) {
        return utilisateurDAO.save(utilisateur);
    }

    public boolean update(Utilisateur utilisateur) {
        return utilisateurDAO.update(utilisateur);
    }

    public boolean delete(int id) {
        return utilisateurDAO.delete(id);
    }

    public Optional<Utilisateur> findById(int id) {return utilisateurDAO.findById(id);}

    public List<Utilisateur> findAll() {
        return utilisateurDAO.findAll();
    }

    public List<Utilisateur> search(String recherche) {
        return utilisateurDAO.search(recherche);
    }

    public List<Utilisateur> findByStatut(String statut) {
        return utilisateurDAO.findByStatut(statut);
    }

    public List<Utilisateur> findByType(String type) {
        return utilisateurDAO.findByType(type);
    }

}