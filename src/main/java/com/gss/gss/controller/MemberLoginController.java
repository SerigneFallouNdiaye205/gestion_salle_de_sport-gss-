package com.gss.gss.controller;

import com.gss.gss.security.MembreSessionManager;
import com.gss.gss.service.EspaceMembreService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class MemberLoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final EspaceMembreService service = new EspaceMembreService();

    @FXML
    private void handleLogin() {
        try {
            EspaceMembreService.CompteMembre compte = service
                    .authentifier(usernameField.getText(), passwordField.getText())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Identifiants incorrects ou compte inactif."));
            MembreSessionManager.login(compte.membreId(), compte.nomComplet());
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/gss/gss/fxml/member-space.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("GSS - Espace membre");
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (Exception e) {
            MembreSessionManager.logout();
            errorLabel.setText(e.getMessage() == null
                    ? "Connexion impossible. Vérifiez que la migration de l'espace membre est installée."
                    : e.getMessage());
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/gss/gss/fxml/login.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("GSS - Connexion");
            stage.setResizable(false);
            stage.centerOnScreen();
        } catch (IOException e) {
            errorLabel.setText("Impossible de revenir à la connexion du personnel.");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }
}
