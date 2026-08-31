package com.gss.gss.service;

import com.gss.gss.dao.Impl.UtilisateurDAOImpl;
import com.gss.gss.dao.UtilisateurDAO;
import com.gss.gss.model.Utilisateur;
import com.gss.gss.security.PasswordHasher;

import java.util.Optional;

public class AuthService {

    private final UtilisateurDAO utilisateurDAO;

    public AuthService() {
        this.utilisateurDAO = new UtilisateurDAOImpl();
    }

    /**
     * Authentifie un utilisateur.
     *
     * @param username nom d'utilisateur
     * @param password mot de passe saisi
     * @return utilisateur authentifié
     * @throws Exception si les identifiants sont incorrects
     */
    public Utilisateur login(
            String username,
            String password
    ) throws Exception {

        // ==============================
        // Vérification du username
        // ==============================

        if (username == null || username.isBlank()) {

            throw new Exception(
                    "Veuillez saisir votre nom d'utilisateur."
            );
        }

        // ==============================
        // Vérification du mot de passe
        // ==============================

        if (password == null || password.isBlank()) {

            throw new Exception(
                    "Veuillez saisir votre mot de passe."
            );
        }

        // ==============================
        // Recherche de l'utilisateur
        // ==============================

        Optional<Utilisateur> optionalUtilisateur =
                utilisateurDAO.findByUsername(
                        username.trim()
                );

        if (optionalUtilisateur.isEmpty()) {

            throw new Exception(
                    "Nom d'utilisateur ou mot de passe incorrect."
            );
        }

        Utilisateur utilisateur =
                optionalUtilisateur.get();

        // ==============================
        // Vérification du statut
        // ==============================

        if (!"ACTIF".equalsIgnoreCase(
                utilisateur.getStatut()
        )) {

            throw new Exception(
                    "Ce compte est désactivé."
            );
        }

        // ==============================
        // Vérification BCrypt
        // ==============================

        boolean passwordCorrect =
                PasswordHasher.verify(
                        password,
                        utilisateur.getPassword()
                );

        if (!passwordCorrect) {

            throw new Exception(
                    "Nom d'utilisateur ou mot de passe incorrect."
            );
        }

        // ==============================
        // Authentification réussie
        // ==============================

        return utilisateur;
    }
}