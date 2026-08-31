package com.gss.gss.controller;

import com.gss.gss.model.Abonnement;
import com.gss.gss.service.AbonnementService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AbonnementFormController {

    @FXML
    private TextField membreIdField;

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

    private Abonnement abonnement;

    @FXML
    public void initialize() {

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
                        "EXPIRE",
                        "SUSPENDU"
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

        membreIdField.setText(
                String.valueOf(
                        abonnement.getMembreId()
                )
        );

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

            int membreId =
                    Integer.parseInt(
                            membreIdField
                                    .getText()
                                    .trim()
                    );

            String type =
                    typeComboBox.getValue();

            double prix =
                    Double.parseDouble(
                            prixField
                                    .getText()
                                    .trim()
                    );

            LocalDate dateDebut =
                    dateDebutPicker.getValue();

            LocalDate dateFin =
                    dateFinPicker.getValue();

            String statut =
                    statutComboBox.getValue();

            if (abonnement == null) {

                Abonnement nouveau =
                        new Abonnement();

                nouveau.setMembreId(membreId);
                nouveau.setType(type);
                nouveau.setPrix(prix);
                nouveau.setDateDebut(dateDebut);
                nouveau.setDateFin(dateFin);
                nouveau.setStatut(statut);

                abonnementService.save(nouveau);

            } else {

                abonnement.setMembreId(membreId);
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

            afficherErreur(
                    e.getMessage()
            );
        }
    }

    @FXML
    private void handleCancel() {

        fermer();
    }

    private void fermer() {

        Stage stage =
                (Stage) membreIdField
                        .getScene()
                        .getWindow();

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