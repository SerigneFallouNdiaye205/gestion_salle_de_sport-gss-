package com.gss.gss.security;

import com.gss.gss.model.Utilisateur;

public final class SessionManager {

    private static Utilisateur currentUser;

    private SessionManager() {}// Empêche l'instanciation

    public static void login(Utilisateur utilisateur) {
        currentUser = utilisateur;
    }

    public static Utilisateur getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {currentUser = null;}

    public static boolean hasRole(String role) {
        if (!isLoggedIn()) {return false;}
        return role != null && role.equalsIgnoreCase(currentUser.getType());
    }
}