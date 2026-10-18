package com.gss.gss.security;

public final class MembreSessionManager {

    private static int membreId;
    private static String nomComplet;

    private MembreSessionManager() {}

    public static void login(int id, String nom) {
        if (id <= 0 || nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("Le compte membre est invalide.");
        }
        membreId = id;
        nomComplet = nom;
    }

    public static boolean isLoggedIn() {
        return membreId > 0;
    }

    public static int getMembreId() {
        if (!isLoggedIn()) {
            throw new IllegalStateException("Aucun membre n'est connecté.");
        }
        return membreId;
    }

    public static String getNomComplet() {
        return nomComplet;
    }

    public static void logout() {
        membreId = 0;
        nomComplet = null;
    }
}
