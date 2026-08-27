package com.gss.gss.service;

import com.gss.gss.dao.UtilisateurDAO;
import com.gss.gss.dao.Impl.UtilisateurDAOImpl;
import com.gss.gss.model.Utilisateur;

import java.util.Optional;

public class AuthService {

    private final UtilisateurDAO utilisateurDAO;

    public AuthService() {
        this.utilisateurDAO = new UtilisateurDAOImpl();
    }

    /**
     * Authentifie un utilisateur à partir de son username et de son mot de passe.
     *
     * @param username nom d'utilisateur
     * @param password mot de passe
     * @return l'utilisateur authentifié
     * @throws Exception si les identifiants sont incorrects
     */

    public Utilisateur login(String username, String password) throws Exception {

        // Vérification des champs
        if (username == null || username.isBlank()) {
            throw new Exception("Veuillez saisir votre nom d'utilisateur.");
        }

        if (password == null || password.isBlank()) {
            throw new Exception("Veuillez saisir votre mot de passe.");
        }

        // Recherche de l'utilisateur
        Optional<Utilisateur> optionalUtilisateur =
                utilisateurDAO.findByUsername(username);

        // Utilisateur inexistant
        if (optionalUtilisateur.isEmpty()) {
            throw new Exception("Nom d'utilisateur ou mot de passe incorrect.");
        }

        Utilisateur utilisateur = optionalUtilisateur.get();

        // Vérification du statut
        if (!"ACTIF".equals(utilisateur.getStatut())) {
            throw new Exception("Ce compte est désactivé.");
        }

        // Vérification du mot de passe
        if (!password.equals(utilisateur.getPassword())) {
            throw new Exception("Nom d'utilisateur ou mot de passe incorrect.");
        }

        return utilisateur;
    }
}