package com.gss.gss.controller;

import com.gss.gss.model.Utilisateur;
import com.gss.gss.security.SessionManager;
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
    @FXML
    private BorderPane container;
    @FXML
    private Label roleLabel;
    @FXML
    private Label welcomeLabel;
    @FXML
    private StackPane contentPane;
    @FXML
    private Button homeButton;
    @FXML
    private Button membersButton;
    @FXML
    private Button paymentsButton;
    @FXML
    private Button subscriptionsButton;
    @FXML
    private Button sessionsButton;
    @FXML
    private Button coachesButton;
    @FXML
    private Button usersButton;
    @FXML
    private Button logoutButton;


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
        switch (role) {
            case "ADMINISTRATEUR":
                // L'administrateur a accès à tout
                homeButton.setVisible(true);
                homeButton.setManaged(true);

                membersButton.setVisible(true);
                membersButton.setManaged(true);

                paymentsButton.setVisible(true);
                paymentsButton.setManaged(true);

                subscriptionsButton.setVisible(true);
                subscriptionsButton.setManaged(true);

                sessionsButton.setVisible(true);
                sessionsButton.setManaged(true);

                coachesButton.setVisible(true);
                coachesButton.setManaged(true);

                usersButton.setVisible(true);
                usersButton.setManaged(true);
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
                break;


            case "RECEPTIONNISTE":

                // Le réceptionniste gère les membres,
                // abonnements et paiements.
                membersButton.setVisible(true);
                membersButton.setManaged(true);

                paymentsButton.setVisible(true);
                paymentsButton.setManaged(true);

                subscriptionsButton.setVisible(true);
                subscriptionsButton.setManaged(true);

                sessionsButton.setVisible(true);
                sessionsButton.setManaged(true);

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

                homeButton.setVisible(false);
                homeButton.setManaged(false);

                coachesButton.setVisible(false);
                coachesButton.setManaged(false);

                usersButton.setVisible(false);
                usersButton.setManaged(false);
                break;

            case "COACH":
                // Le coach travaille principalement
                // avec les séances et les membres.
                membersButton.setVisible(true);
                membersButton.setManaged(true);

                sessionsButton.setVisible(true);
                sessionsButton.setManaged(true);
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

                homeButton.setVisible(false);
                homeButton.setManaged(false);

                paymentsButton.setVisible(false);
                paymentsButton.setManaged(false);

                subscriptionsButton.setVisible(false);
                subscriptionsButton.setManaged(false);

                coachesButton.setVisible(false);
                coachesButton.setManaged(false);

                usersButton.setVisible(false);
                usersButton.setManaged(false);
                break;

            default:
                // Si le rôle est inconnu,
                // on masque les menus sensibles.
                membersButton.setVisible(false);
                membersButton.setManaged(false);

                paymentsButton.setVisible(false);
                paymentsButton.setManaged(false);

                subscriptionsButton.setVisible(false);
                subscriptionsButton.setManaged(false);

                sessionsButton.setVisible(false);
                sessionsButton.setManaged(false);

                coachesButton.setVisible(false);
                coachesButton.setManaged(false);

                usersButton.setVisible(false);
                usersButton.setManaged(false);
        }
    }


    @FXML
    private void handleHome() {
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
    private void handleCoachs() {
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