package com.gss.gss.security;

import com.gss.gss.model.Utilisateur;

public final class SessionManager {

    private static Utilisateur currentUser;

    private SessionManager() {}// Empêche l'instanciation

    //Enregistre l'utilisateur actuellement connecté.

    public static void login(Utilisateur utilisateur) {
        currentUser = utilisateur;
    }

    // Retourne l'utilisateur actuellement connecté.

    public static Utilisateur getCurrentUser() {
        return currentUser;
    }

    // Vérifie si un utilisateur est actuellement connecté.

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    // Déconnecte l'utilisateur actuel.

    public static void logout() {currentUser = null;}

    // Vérifie si l'utilisateur connecté possède le rôle demandé.

    public static boolean hasRole(String role) {
        if (!isLoggedIn()) {return false;}
        return role.equals(currentUser.getType());
    }
}