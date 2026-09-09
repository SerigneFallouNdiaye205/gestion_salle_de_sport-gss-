package com.gss.gss.controller;

import com.gss.gss.model.Membre;
import com.gss.gss.service.MembreService;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class MembresFormController {

    @FXML
    private Label titleLabel;

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private ComboBox<String> sexeComboBox;

    @FXML
    private TextField telephoneField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField adresseField;

    @FXML
    private DatePicker dateNaissancePicker;

    @FXML
    private DatePicker dateInscriptionPicker;

    @FXML
    private ComboBox<String> statutComboBox;

    @FXML
    private Label errorLabel;

    @FXML
    private Button saveButton;

    private final MembreService membreService = new MembreService();

    private Membre membre;

    @FXML
    public void initialize() {

        sexeComboBox.getItems().addAll(
                "M",
                "F"
        );
        sexeComboBox.setValue("M");

        statutComboBox.getItems().addAll(
                "ACTIF",
                "INACTIF"
        );

        statutComboBox.setValue("ACTIF");

        dateInscriptionPicker.setValue(
                LocalDate.now()
        );
    }

    public void setMembre(Membre membre) {

        this.membre = membre;

        titleLabel.setText("Modifier le membre");
        saveButton.setText("Modifier");

        nomField.setText(membre.getNom());
        prenomField.setText(membre.getPrenom());
        sexeComboBox.setValue(
                membre.getSexe() == null ? "M" : membre.getSexe()
        );
        telephoneField.setText(membre.getTelephone());
        emailField.setText(membre.getEmail());
        adresseField.setText(membre.getAdresse());

        dateNaissancePicker.setValue(
                membre.getDateNaissance()
        );

        dateInscriptionPicker.setValue(
                membre.getDateInscription()
        );

        statutComboBox.setValue(
                membre.getStatut()
        );
    }

    @FXML
    private void handleSave() {

        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        if (!valider()) {
            return;
        }

        if (membre == null) {

            Membre nouveauMembre = new Membre();

            remplirMembre(nouveauMembre);

            if (membreService.save(nouveauMembre)) {

                afficherSucces(
                        "Succès",
                        "Le membre a été ajouté avec succès."
                );

                fermer();

            } else {

                afficherErreur(
                        "Impossible d'ajouter le membre."
                );
            }

        } else {

            remplirMembre(membre);

            if (membreService.update(membre)) {

                afficherSucces(
                        "Succès",
                        "Le membre a été modifié avec succès."
                );

                fermer();

            } else {

                afficherErreur(
                        "Impossible de modifier le membre."
                );
            }
        }
    }

    private void remplirMembre(Membre membre) {

        membre.setNom(nomField.getText().trim());
        membre.setPrenom(prenomField.getText().trim());
        membre.setSexe(sexeComboBox.getValue());
        membre.setTelephone(telephoneField.getText().trim());
        membre.setEmail(emailField.getText().trim());
        membre.setAdresse(adresseField.getText().trim());

        membre.setDateNaissance(
                dateNaissancePicker.getValue()
        );

        membre.setDateInscription(
                dateInscriptionPicker.getValue()
        );

        membre.setStatut(
                statutComboBox.getValue()
        );
    }

    private boolean valider() {

        if (nomField.getText().trim().isEmpty()) {
            afficherErreur("Le nom est obligatoire.");
            return false;
        }

        if (prenomField.getText().trim().isEmpty()) {
            afficherErreur("Le prénom est obligatoire.");
            return false;
        }

        if (sexeComboBox.getValue() == null) {
            afficherErreur("Veuillez sélectionner le sexe.");
            return false;
        }

        if (telephoneField.getText().trim().isEmpty()) {
            afficherErreur("Le téléphone est obligatoire.");
            return false;
        }

        if (dateInscriptionPicker.getValue() == null) {
            afficherErreur("La date d'inscription est obligatoire.");
            return false;
        }

        if (statutComboBox.getValue() == null) {
            afficherErreur("Veuillez sélectionner un statut.");
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