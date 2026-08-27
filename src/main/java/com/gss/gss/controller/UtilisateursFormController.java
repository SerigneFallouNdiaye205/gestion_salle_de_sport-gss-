package com.gss.gss.controller;

import com.gss.gss.model.Utilisateur;
import com.gss.gss.service.UtilisateurService;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class UtilisateursFormController {

    @FXML
    private Label titleLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private DatePicker dateCreationPicker;

    @FXML
    private ComboBox<String> statutComboBox;

    @FXML
    private Label errorLabel;

    @FXML
    private Button saveButton;

    private final UtilisateurService utilisateurService = new UtilisateurService();

    private Utilisateur utilisateur;

    @FXML
    public void initialize() {

        statutComboBox.getItems().addAll(
                "ACTIF",
                "INACTIF"
        );

        statutComboBox.setValue("ACTIF");

        typeComboBox.getItems().addAll(
                "ADMINISTRATEUR",
                "RÉCEPTIONNISTE",
                "COACH"
        );

        typeComboBox.setValue("COACH");

        dateCreationPicker.setValue(
                LocalDate.now()
        );
    }

    public void setUtilisateur(Utilisateur utilisateur) {

        this.utilisateur = utilisateur;

        titleLabel.setText("Modifier l'utilisateur");
        saveButton.setText("Modifier");

        usernameField.setText(utilisateur.getUsername());
        passwordField.setText(utilisateur.getPassword());
        dateCreationPicker.setValue(utilisateur.getDateCreation());

        statutComboBox.setValue(
                utilisateur.getStatut()
        );
    }

    @FXML
    private void handleSave() {

        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        if (!valider()) {
            return;
        }

        if (utilisateur == null) {

            Utilisateur nouvelUtilisateur = new Utilisateur();

            remplirUtilisateur(nouvelUtilisateur);

            if (utilisateurService.save(nouvelUtilisateur)) {

                afficherSucces(
                        "Succès",
                        "L'utilisateur a été ajouté avec succès."
                );

                fermer();

            } else {

                afficherErreur(
                        "Impossible d'ajouter l'utilisateur."
                );
            }

        } else {

            remplirUtilisateur(utilisateur);

            if (utilisateurService.update(utilisateur)) {

                afficherSucces(
                        "Succès",
                        "L'utilisateur a été modifié avec succès."
                );

                fermer();

            } else {

                afficherErreur(
                        "Impossible de modifier l'utilisateur."
                );
            }
        }
    }

    private void remplirUtilisateur(Utilisateur utilisateur) {

        utilisateur.setUsername(usernameField.getText().trim());
        utilisateur.setPassword(passwordField.getText());

        utilisateur.setType(
                typeComboBox.getValue()
        );
        utilisateur.setDateCreation(
                dateCreationPicker.getValue()
        );
        utilisateur.setStatut(
                statutComboBox.getValue()
        );
    }

    private boolean valider() {

        if (usernameField.getText().trim().isEmpty()) {
            afficherErreur("Le nom d'utilisateur est obligatoire.");
            return false;
        }

        if (passwordField.getText().trim().isEmpty()) {
            afficherErreur("Le mot de passe est obligatoire.");
            return false;
        }

        if (typeComboBox.getValue() == null) {
            afficherErreur("Le type est obligatoire.");
            return false;
        }

        if (statutComboBox.getValue() == null) {
            afficherErreur("Veuillez sélectionner un statut.");
            return false;
        }

        if (dateCreationPicker.getValue() == null) {
            afficherErreur("La date de Creation est obligatoire.");
            return false;
        }

        return true;
    }

    @FXML
    private void handleCancel() {
        fermer();
    }

    private void fermer() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void afficherErreur(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void afficherSucces(String titre, String message) {
        Alert alert = new Alert(
                Alert.AlertType.INFORMATION
        );

        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}