package com.gss.gss.security;

import com.gss.gss.model.Utilisateur;

public class PermissionManager {

    public static final String ADMINISTRATEUR = "ADMINISTRATEUR";
    public static final String RECEPTIONNISTE = "RECEPTIONNISTE";
    public static final String COACH = "COACH";

    private PermissionManager() {}

    public static boolean hasRole(String role) {
        Utilisateur user = SessionManager.getCurrentUser();
        return user != null && role != null && role.equalsIgnoreCase(user.getType());
    }

    public static boolean isAdmin() {
        return hasRole(ADMINISTRATEUR);
    }

    public static boolean isReceptionniste() {
        return hasRole(RECEPTIONNISTE);
    }

    public static boolean isCoach() {
        return hasRole(COACH);
    }

    public static boolean canViewDashboard() {
        return isAdmin();
    }

    public static boolean canViewMembers() {
        return isAdmin() || isReceptionniste() || isCoach();
    }

    public static boolean canManageMembers() {
        return isAdmin() || isReceptionniste();
    }

    public static boolean canViewSubscriptions() {
        return isAdmin() || isReceptionniste();
    }

    public static boolean canManageSubscriptions() {
        return canViewSubscriptions();
    }

    public static boolean canViewPayments() {
        return isAdmin() || isReceptionniste();
    }

    public static boolean canManagePayments() {
        return canViewPayments();
    }

    public static boolean canViewSessions() {
        return isAdmin() || isReceptionniste() || isCoach();
    }

    public static boolean canManageSessions() {
        return isAdmin();
    }

    public static boolean canViewInscriptions() {
        return isAdmin() || isReceptionniste() || isCoach();
    }

    public static boolean canManageInscriptions() {
        return isAdmin() || isReceptionniste();
    }

    public static boolean canManageCoaches() {
        return isAdmin();
    }

    public static boolean canManageUsers() {
        return isAdmin();
    }

    public static boolean canViewReports() {
        return isAdmin();
    }

    public static boolean isAllowed(boolean permission) {
        return SessionManager.isLoggedIn() && permission;
    }
}
