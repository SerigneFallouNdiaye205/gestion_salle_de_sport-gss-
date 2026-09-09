package com.gss.gss.controller;

import com.gss.gss.model.Paiement;
import com.gss.gss.model.Abonnement;
import com.gss.gss.model.Membre;
import com.gss.gss.service.AbonnementService;
import com.gss.gss.service.MembreService;
import com.gss.gss.service.PaiementService;
import com.gss.gss.util.PaiementReferenceGenerator;
import com.gss.gss.util.RecuPaiement;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PaiementFormController {

    @FXML
    private ComboBox<Membre> membreComboBox;

    @FXML
    private ComboBox<Abonnement> abonnementComboBox;

    @FXML
    private TextField montantField;

    @FXML
    private DatePicker datePaiementPicker;

    @FXML
    private ComboBox<String> modePaiementComboBox;

    @FXML
    private ComboBox<String> statutComboBox;

    @FXML
    private TextField referenceField;

    @FXML
    private Label errorLabel;

    @FXML
    private Label titleLabel;

    private Paiement paiement;

    private PaiementsController paiementsController;

    private final PaiementService paiementService =
            new PaiementService();
    private final MembreService membreService = new MembreService();
    private final AbonnementService abonnementService = new AbonnementService();

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
        membreComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            abonnementComboBox.setItems(newValue == null
                    ? FXCollections.observableArrayList()
                    : FXCollections.observableArrayList(
                    abonnementService.findByMembreId(newValue.getId())));
        });
        abonnementComboBox.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Abonnement abonnement, boolean empty) {
                super.updateItem(abonnement, empty);
                setText(empty || abonnement == null ? null
                        : abonnement.getType() + " - " + abonnement.getPrix()
                        + " FCFA (" + abonnement.getDateDebut()
                        + " au " + abonnement.getDateFin() + ")");
            }
        });
        abonnementComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Abonnement abonnement, boolean empty) {
                super.updateItem(abonnement, empty);
                setText(empty || abonnement == null ? null
                        : abonnement.getType() + " - " + abonnement.getPrix() + " FCFA");
            }
        });

        // Modes de paiement
        modePaiementComboBox.setItems(
                FXCollections.observableArrayList(
                        "ESPECES",
                        "WAVE",
                        "ORANGE_MONEY",
                        "CARTE_BANCAIRE"
                )
        );

        // Statuts
        statutComboBox.setItems(
                FXCollections.observableArrayList(
                        "VALIDE",
                        "EN_ATTENTE",
                        "ANNULE"
                )
        );

        // Valeurs par défaut
        datePaiementPicker.setValue(LocalDate.now());

        modePaiementComboBox.setValue("ESPECES");

        statutComboBox.setValue("VALIDE");

        referenceField.setText(
                PaiementReferenceGenerator.generate()
        );
    }

    public void setPaiementsController(
            PaiementsController paiementsController) {

        this.paiementsController = paiementsController;
    }

    public void setPaiement(Paiement paiement) {

        this.paiement = paiement;

        titleLabel.setText("Modifier le paiement");

        membreComboBox.setValue(membreService.findById(paiement.getMembreId()));
        abonnementComboBox.setValue(abonnementService.findById(paiement.getAbonnementId()).orElse(null));

        montantField.setText(
                String.valueOf(paiement.getMontant())
        );

        /*
         * Paiement utilise LocalDateTime
         * DatePicker utilise LocalDate
         *
         * On récupère uniquement la date.
         */
        if (paiement.getDatePaiement() != null) {

            datePaiementPicker.setValue(
                    paiement.getDatePaiement().toLocalDate()
            );
        }

        modePaiementComboBox.setValue(
                paiement.getModePaiement()
        );

        statutComboBox.setValue(
                paiement.getStatut()
        );

        referenceField.setText(
                paiement.getReference()
        );
    }

    @FXML
    private void handleSave() {

        if (!valider()) {
            return;
        }

        try {

            if (paiement == null) {

                // Nouveau paiement
                paiement = new Paiement();

                remplirPaiement();

                boolean resultat =
                        paiementService.save(paiement);

                if (resultat) {

                    afficherSucces(
                            "Paiement enregistré avec succès."
                    );

                    File recu =
                            RecuPaiement.generer(paiement);

                    if (java.awt.Desktop.isDesktopSupported()) {
                        java.awt.Desktop.getDesktop().open(recu);
                    }

                    fermer();
                } else {

                    afficherErreur(
                            "Erreur lors de l'enregistrement."
                    );
                }

            } else {

                // Modification
                remplirPaiement();

                boolean resultat =
                        paiementService.update(paiement);

                if (resultat) {

                    afficherSucces(
                            "Paiement modifié avec succès."
                    );

                    fermer();

                } else {

                    afficherErreur(
                            "Erreur lors de la modification."
                    );
                }
            }

        } catch (NumberFormatException e) {

            afficherErreur(
                    "Les identifiants et le montant doivent être numériques."
            );

        } catch (IOException e) {

            e.printStackTrace();

            afficherErreur(
                    "Erreur lors de la génération du reçu."
            );
        }
    }

    private void remplirPaiement() {

        paiement.setMembreId(membreComboBox.getValue().getId());
        paiement.setAbonnementId(abonnementComboBox.getValue().getId());

        paiement.setMontant(
                Double.parseDouble(
                        montantField.getText().trim()
                )
        );

        LocalDate date =
                datePaiementPicker.getValue();

        // Pour un nouveau paiement, on peut conserver l'heure actuelle.

        if (paiement.getDatePaiement() == null) {

            paiement.setDatePaiement(
                    LocalDateTime.now()
            );

        } else {
            paiement.setDatePaiement(
                    LocalDateTime.of(
                            date,
                            paiement.getDatePaiement().toLocalTime()
                    )
            );
        }

        paiement.setModePaiement(
                modePaiementComboBox.getValue()
        );

        paiement.setStatut(
                statutComboBox.getValue()
        );

        paiement.setReference(
                referenceField.getText().trim()
        );
    }

    private boolean valider() {

        errorLabel.setText("");

        if (membreComboBox.getValue() == null) {
            afficherErreur("Veuillez sélectionner le membre.");

            return false;
        }

        if (abonnementComboBox.getValue() == null) {
            afficherErreur("Veuillez sélectionner l'abonnement.");

            return false;
        }

        if (montantField.getText().isBlank()) {

            afficherErreur(
                    "Veuillez saisir le montant."
            );

            return false;
        }

        if (datePaiementPicker.getValue() == null) {

            afficherErreur(
                    "Veuillez sélectionner la date."
            );

            return false;
        }

        if (modePaiementComboBox.getValue() == null) {

            afficherErreur(
                    "Veuillez sélectionner le mode de paiement."
            );

            return false;
        }

        if (statutComboBox.getValue() == null) {

            afficherErreur(
                    "Veuillez sélectionner le statut."
            );

            return false;
        }

        try {

            double montant =
                    Double.parseDouble(
                            montantField.getText().trim()
                    );

            if (montant <= 0) {

                afficherErreur(
                        "Le montant doit être supérieur à zéro."
                );

                return false;
            }

        } catch (NumberFormatException e) {

            afficherErreur(
                    "Le montant doit être numérique."
            );

            return false;
        }

        return true;
    }

    private void afficherErreur(String message) {

        errorLabel.setText(message);
    }

    private void afficherSucces(String message) {

        Alert alert = new Alert(
                Alert.AlertType.INFORMATION
        );

        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleAnnuler() {

        fermer();
    }

    private void fermer() {

        ((javafx.stage.Stage)
                membreComboBox.getScene().getWindow())
                .close();
    }
}