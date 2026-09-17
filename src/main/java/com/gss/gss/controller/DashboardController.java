package com.gss.gss.controller;

import com.gss.gss.model.Utilisateur;
import com.gss.gss.security.SessionManager;
import com.gss.gss.security.PermissionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {
    @FXML private BorderPane container;
    @FXML private Label roleLabel;
    @FXML private Label welcomeLabel;
    @FXML private StackPane contentPane;
    @FXML private Button homeButton;
    @FXML private Button membersButton;
    @FXML private Button paymentsButton;
    @FXML private Button subscriptionsButton;
    @FXML private Button sessionsButton;
    @FXML private Button inscriptionsButton;
    @FXML private Button coachesButton;
    @FXML private Button usersButton;
    @FXML private Button reportsButton;
    @FXML private Button logoutButton;


    @FXML
    public void initialize() {
        // Vérifier qu'une session existe
        if (!SessionManager.isLoggedIn()) {
            return;
        }
        Utilisateur utilisateur = SessionManager.getCurrentUser();

        // Afficher les informations de l'utilisateur
        welcomeLabel.setText("Bienvenue, " + utilisateur.getUsername());
        roleLabel.setText(utilisateur.getType());
        // Adapter le menu au rôle
        configureMenu(utilisateur.getType());

    }

    // Configure les boutons selon le rôle.

    private void configureMenu(String role) {
        setVisible(homeButton, PermissionManager.canViewDashboard());
        setVisible(membersButton, PermissionManager.canViewMembers());
        setVisible(paymentsButton, PermissionManager.canViewPayments());
        setVisible(subscriptionsButton, PermissionManager.canViewSubscriptions());
        setVisible(sessionsButton, PermissionManager.canViewSessions());
        setVisible(inscriptionsButton, PermissionManager.canViewInscriptions());
        setVisible(coachesButton, PermissionManager.canManageCoaches());
        setVisible(usersButton, PermissionManager.canManageUsers());
        setVisible(reportsButton, PermissionManager.canViewReports());

        if (PermissionManager.canViewDashboard()) {
                try {

                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/com/gss/gss/fxml/home.fxml")
                    );

                    Parent homeView = loader.load();

                    contentPane.getChildren().clear();
                    contentPane.getChildren().add(homeView);
                    container.setCenter(contentPane);

                } catch (IOException e) {

                    e.printStackTrace();
                }
        } else if (PermissionManager.canViewMembers()) {
                try {

                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/com/gss/gss/fxml/membres.fxml")
                    );

                    Parent membresView = loader.load();

                    contentPane.getChildren().clear();
                    contentPane.getChildren().add(membresView);
                    container.setCenter(contentPane);

                } catch (IOException e) {

                    e.printStackTrace();
                }
        }
    }

    private void setVisible(Button button, boolean visible) {
        button.setVisible(visible);
        button.setManaged(visible);
    }


    @FXML
    private void handleHome() {
        if (!PermissionManager.canViewDashboard()) return;
        // Tableau de bord
        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gss/gss/fxml/home.fxml")
            );

            Parent homeView = loader.load();

            contentPane.getChildren().clear();
            contentPane.getChildren().add(homeView);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @FXML
    private void handleMembers() {
        if (!PermissionManager.canViewMembers()) return;
        //Gestion des membres
        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gss/gss/fxml/membres.fxml")
            );

            Parent membresView = loader.load();

            contentPane.getChildren().clear();
            contentPane.getChildren().add(membresView);
            container.setCenter(contentPane);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    @FXML
    private void handlePaiements() {
        if (!PermissionManager.canViewPayments()) return;
        // Gestion des paiements
        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gss/gss/fxml/paiements.fxml")
            );

            Parent paiementsView = loader.load();

            contentPane.getChildren().clear();
            contentPane.getChildren().add(paiementsView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleAbonnements() {
        if (!PermissionManager.canViewSubscriptions()) return;
        // Gestion des abonnements
        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gss/gss/fxml/abonnements.fxml")
            );

            Parent abonnementsView = loader.load();

            contentPane.getChildren().clear();
            contentPane.getChildren().add(abonnementsView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleSeances() {
        if (!PermissionManager.canViewSessions()) return;
        // Gestion des seances
        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gss/gss/fxml/seances.fxml")
            );

            Parent seancesView = loader.load();

            contentPane.getChildren().clear();
            contentPane.getChildren().add(seancesView);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    @FXML
    private void handleInscriptions() {
        if (!PermissionManager.canViewInscriptions()) return;
        // Gestion des inscriptions aux seances
        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gss/gss/fxml/inscriptions-seances.fxml")
            );

            Parent inscriptionsView = loader.load();

            contentPane.getChildren().clear();
            contentPane.getChildren().add(inscriptionsView);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    @FXML
    private void handleCoachs() {
        if (!PermissionManager.canManageCoaches()) return;
        // Gestion des paiements
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gss/gss/fxml/coachs.fxml")
            );

            Parent coachsView = loader.load();

            contentPane.getChildren().clear();
            contentPane.getChildren().add(coachsView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleUtilisateurs() {
        if (!PermissionManager.canManageUsers()) return;
    // Gestion des utilisateurs
        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gss/gss/fxml/utilisateurs.fxml")
            );

            Parent utilisateursView = loader.load();

            contentPane.getChildren().clear();
            contentPane.getChildren().add(utilisateursView);
            container.setCenter(contentPane);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRapports() {
        if (!PermissionManager.canViewReports()) return;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gss/gss/fxml/rapports.fxml"));
            Parent rapportsView = loader.load();
            contentPane.getChildren().clear();
            contentPane.getChildren().add(rapportsView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Déconnexion.
    @FXML
    private void handleLogout() {

        // Supprimer la session
        SessionManager.logout();

        try {
            // Retour vers le login
            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource("/com/gss/gss/fxml/login.fxml")
                    );

            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("GSS - Connexion");
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}