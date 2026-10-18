package com.gss.gss.service;

import com.gss.gss.dao.Impl.UtilisateurDAOImpl;
import com.gss.gss.dao.UtilisateurDAO;
import com.gss.gss.model.Utilisateur;
import com.gss.gss.security.PasswordHasher;
import com.gss.gss.security.SessionManager;

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

    public void changerMonMotDePasse(String motDePasseActuel,
                                     String nouveauMotDePasse,
                                     String confirmation) {
        Utilisateur session = SessionManager.getCurrentUser();
        if (session == null) {
            throw new SecurityException("Vous devez être connecté pour changer votre mot de passe.");
        }
        if (motDePasseActuel == null || motDePasseActuel.isBlank()) {
            throw new IllegalArgumentException("Saisissez votre mot de passe actuel.");
        }
        if (nouveauMotDePasse == null || nouveauMotDePasse.length() < 8) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 8 caractères.");
        }
        if (!nouveauMotDePasse.equals(confirmation)) {
            throw new IllegalArgumentException("La confirmation du nouveau mot de passe ne correspond pas.");
        }

        Utilisateur current = utilisateurDAO.findById(session.getId())
                .orElseThrow(() -> new SecurityException("Votre compte n'existe plus."));
        if (!"ACTIF".equalsIgnoreCase(current.getStatut())) {
            SessionManager.logout();
            throw new SecurityException("Votre compte est désactivé. Veuillez vous reconnecter.");
        }
        if (!PasswordHasher.verify(motDePasseActuel, current.getPassword())) {
            throw new IllegalArgumentException("Le mot de passe actuel est incorrect.");
        }
        if (PasswordHasher.verify(nouveauMotDePasse, current.getPassword())) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit être différent de l'actuel.");
        }
        if (!utilisateurDAO.changePassword(current.getId(), current.getPassword(),
                PasswordHasher.hash(nouveauMotDePasse))) {
            throw new IllegalStateException("Le mot de passe n'a pas été modifié. Réessayez.");
        }
        session.setPassword(null);
    }

    public boolean delete(int id) {
        return utilisateurDAO.delete(id);
    }

    public Optional<Utilisateur> findById(int id) {return utilisateurDAO.findById(id);}

    public List<Utilisateur> findAll() {return utilisateurDAO.findAll();}

    public long countAll() {return utilisateurDAO.findAll().size();}

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