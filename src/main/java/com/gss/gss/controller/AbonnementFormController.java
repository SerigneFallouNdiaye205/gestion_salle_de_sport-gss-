package com.gss.gss.controller;

import com.gss.gss.model.Abonnement;
import com.gss.gss.model.Membre;
import com.gss.gss.service.AbonnementService;
import com.gss.gss.service.MembreService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AbonnementFormController {

    @FXML
    private ComboBox<Membre> membreComboBox;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private TextField prixField;
    @FXML
    private DatePicker dateDebutPicker;
    @FXML
    private DatePicker dateFinPicker;
    @FXML
    private ComboBox<String> statutComboBox;
    @FXML
    private Label errorLabel;

    private final AbonnementService abonnementService =
            new AbonnementService();
    private final MembreService membreService = new MembreService();

    private Abonnement abonnement;

    @FXML
    public void initialize() {
        membreComboBox.setItems(FXCollections.observableArrayList(membreService.findAll()));
        membreComboBox.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Membre membre, boolean empty) {
                super.updateItem(membre, empty);
                setText(empty || membre == null ? null
                        : membre.getPrenom() + " " + membre.getNom());
            }
        });
        membreComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Membre membre, boolean empty) {
                super.updateItem(membre, empty);
                setText(empty || membre == null ? null
                        : membre.getPrenom() + " " + membre.getNom());
            }
        });

        typeComboBox.setItems(
                FXCollections.observableArrayList(
                        "JOURNALIER",
                        "HEBDOMADAIRE",
                        "MENSUEL",
                        "TRIMESTRIEL",
                        "SEMESTRIEL",
                        "ANNUEL"
                )
        );

        statutComboBox.setItems(
                FXCollections.observableArrayList(
                        "ACTIF",
                        "EXPIRE"
                )
        );

        statutComboBox.setValue("ACTIF");

        dateDebutPicker.setValue(
                LocalDate.now()
        );
    }

    public void setAbonnement(
            Abonnement abonnement
    ) {

        this.abonnement = abonnement;

        membreComboBox.setValue(membreService.findById(abonnement.getMembreId()));

        typeComboBox.setValue(
                abonnement.getType()
        );

        prixField.setText(
                String.valueOf(
                        abonnement.getPrix()
                )
        );

        dateDebutPicker.setValue(
                abonnement.getDateDebut()
        );

        dateFinPicker.setValue(
                abonnement.getDateFin()
        );

        statutComboBox.setValue(
                abonnement.getStatut()
        );
    }

    @FXML
    private void handleSave() {

        try {

            Membre membre = membreComboBox.getValue();
            if (membre == null) {
                throw new IllegalArgumentException("Veuillez sélectionner un membre.");
            }

            String type =
                    typeComboBox.getValue();

            double prix =
                    Double.parseDouble(
                            prixField.getText().trim()
                    );

            LocalDate dateDebut = dateDebutPicker.getValue();

            LocalDate dateFin = dateFinPicker.getValue();

            String statut = statutComboBox.getValue();

            if (abonnement == null) {

                Abonnement nouveau =
                        new Abonnement();

                nouveau.setMembreId(membre.getId());
                nouveau.setType(type);
                nouveau.setPrix(prix);
                nouveau.setDateDebut(dateDebut);
                nouveau.setDateFin(dateFin);
                nouveau.setStatut(statut);

                abonnementService.save(nouveau);

            } else {

                abonnement.setMembreId(membre.getId());
                abonnement.setType(type);
                abonnement.setPrix(prix);
                abonnement.setDateDebut(dateDebut);
                abonnement.setDateFin(dateFin);
                abonnement.setStatut(statut);

                abonnementService.update(
                        abonnement
                );
            }

            fermer();

        } catch (NumberFormatException e) {
            afficherErreur(
                    "Le membre et le prix doivent être valides."
            );

        } catch (IllegalArgumentException e) {
            afficherErreur(e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {

        fermer();
    }

    private void fermer() {

        Stage stage =
                (Stage) membreComboBox.getScene().getWindow();
        stage.close();
    }

    private void afficherErreur(
            String message
    ) {

        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}