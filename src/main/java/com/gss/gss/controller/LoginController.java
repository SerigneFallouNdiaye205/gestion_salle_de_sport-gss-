package com.gss.gss.controller;

import com.gss.gss.model.Utilisateur;
import com.gss.gss.security.SessionManager;
import com.gss.gss.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    private final AuthService authService;

    public LoginController() {
        this.authService = new AuthService();
    }

    @FXML
    private void handleLogin() {

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        try {
            Utilisateur utilisateur = authService.login(username, password);
            SessionManager.login(utilisateur);
            openDashboard(utilisateur);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void openDashboard(Utilisateur utilisateur)
            throws IOException {

        String fxml;

        if (
                utilisateur.getType().equals("ADMINISTRATEUR") ||
                utilisateur.getType().equals("RECEPTIONNISTE") ||
                utilisateur.getType().equals("COACH")
        ){
            fxml = "/com/gss/gss/fxml/dashboard.fxml";
        }else{
            throw new IllegalStateException("Rôle utilisateur inconnu : " + utilisateur.getType());
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) loginButton.getScene().getWindow();

        stage.setScene(scene);
        stage.setTitle("GSS - Dashboard");
        stage.centerOnScreen();
        stage.setResizable(true);
        stage.show();
    }


    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    @FXML
    private void handleExit() {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.close();
    }
}