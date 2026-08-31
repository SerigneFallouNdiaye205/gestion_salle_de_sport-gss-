package com.gss.gss.controller;

import com.gss.gss.model.Abonnement;
import com.gss.gss.service.AbonnementService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AbonnementController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private ComboBox<String> statutComboBox;

    @FXML
    private TableView<Abonnement> abonnementsTable;

    @FXML
    private TableColumn<Abonnement, Number> idColumn;

    @FXML
    private TableColumn<Abonnement, Number> membreIdColumn;

    @FXML
    private TableColumn<Abonnement, String> typeColumn;

    @FXML
    private TableColumn<Abonnement, Number> prixColumn;

    @FXML
    private TableColumn<Abonnement, String> dateDebutColumn;

    @FXML
    private TableColumn<Abonnement, String> dateFinColumn;

    @FXML
    private TableColumn<Abonnement, String> statutColumn;

    @FXML
    private TableColumn<Abonnement, String> joursRestantsColumn;

    private final AbonnementService abonnementService =
            new AbonnementService();

    @FXML
    public void initialize() {

        configurerColonnes();

        typeComboBox.setItems(
                FXCollections.observableArrayList(
                        "Tous",
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
                        "Tous",
                        "ACTIF",
                        "EXPIRE",
                        "SUSPENDU"
                )
        );

        typeComboBox.setValue("Tous");
        statutComboBox.setValue("Tous");

        abonnementService.mettreAJourStatutsExpires();

        chargerAbonnements();
    }

    private void configurerColonnes() {

        idColumn.setCellValueFactory(
                cell ->
                        new javafx.beans.property.SimpleIntegerProperty(
                                cell.getValue().getId()
                        )
        );

        membreIdColumn.setCellValueFactory(
                cell ->
                        new javafx.beans.property.SimpleIntegerProperty(
                                cell.getValue().getMembreId()
                        )
        );

        typeColumn.setCellValueFactory(
                cell ->
                        new javafx.beans.property.SimpleStringProperty(
                                cell.getValue().getType()
                        )
        );

        prixColumn.setCellValueFactory(
                cell ->
                        new javafx.beans.property.SimpleDoubleProperty(
                                cell.getValue().getPrix()
                        )
        );

        dateDebutColumn.setCellValueFactory(
                cell ->
                        new javafx.beans.property.SimpleStringProperty(
                                cell.getValue().getDateDebut() != null
                                        ? cell.getValue()
                                        .getDateDebut()
                                        .toString()
                                        : ""
                        )
        );

        dateFinColumn.setCellValueFactory(
                cell ->
                        new javafx.beans.property.SimpleStringProperty(
                                cell.getValue().getDateFin() != null
                                        ? cell.getValue()
                                        .getDateFin()
                                        .toString()
                                        : ""
                        )
        );

        statutColumn.setCellValueFactory(
                cell ->
                        new javafx.beans.property.SimpleStringProperty(
                                cell.getValue().getStatut()
                        )
        );

        joursRestantsColumn.setCellValueFactory(
                cell ->
                        new javafx.beans.property.SimpleStringProperty(
                                String.valueOf(
                                        cell.getValue()
                                                .getJoursRestants()
                                )
                        )
        );
    }

    private void chargerAbonnements() {

        abonnementsTable.setItems(
                FXCollections.observableArrayList(
                        abonnementService.findAll()
                )
        );
    }

    @FXML
    private void handleSearch() {

        String recherche =
                searchField.getText()
                        .trim();

        if (!recherche.isEmpty()) {

            abonnementsTable.setItems(
                    FXCollections.observableArrayList(
                            abonnementService.search(recherche)
                    )
            );

            return;
        }

        filtrer();
    }

    @FXML
    private void handleFilter() {

        filtrer();
    }

    private void filtrer() {

        String type = typeComboBox.getValue();
        String statut = statutComboBox.getValue();

        if (type != null &&
                !type.equals("Tous")) {

            abonnementsTable.setItems(
                    FXCollections.observableArrayList(
                            abonnementService.findByType(type)
                    )
            );

            return;
        }

        if (statut != null &&
                !statut.equals("Tous")) {

            abonnementsTable.setItems(
                    FXCollections.observableArrayList(
                            abonnementService.findByStatut(statut)
                    )
            );

            return;
        }

        chargerAbonnements();
    }

    @FXML
    private void handleRefresh() {

        searchField.clear();

        typeComboBox.setValue("Tous");
        statutComboBox.setValue("Tous");

        abonnementService.mettreAJourStatutsExpires();

        chargerAbonnements();
    }

    @FXML
    private void handleAdd() {

        ouvrirFormulaire(null);
    }

    @FXML
    private void handleEdit() {

        Abonnement abonnement =
                abonnementsTable
                        .getSelectionModel()
                        .getSelectedItem();

        if (abonnement == null) {

            afficherAvertissement(
                    "Veuillez sélectionner un abonnement."
            );

            return;
        }

        ouvrirFormulaire(abonnement);
    }

    @FXML
    private void handleDelete() {

        Abonnement abonnement =
                abonnementsTable
                        .getSelectionModel()
                        .getSelectedItem();

        if (abonnement == null) {

            afficherAvertissement(
                    "Veuillez sélectionner un abonnement."
            );

            return;
        }

        Alert confirmation =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmation.setTitle(
                "Supprimer l'abonnement"
        );

        confirmation.setHeaderText(
                "Abonnement #" +
                        abonnement.getId()
        );

        confirmation.setContentText(
                "Voulez-vous vraiment supprimer cet abonnement ?"
        );

        confirmation.showAndWait()
                .ifPresent(reponse -> {

                    if (reponse == ButtonType.OK) {

                        boolean resultat =
                                abonnementService.delete(
                                        abonnement.getId()
                                );

                        if (resultat) {

                            chargerAbonnements();

                            afficherInformation(
                                    "Abonnement supprimé avec succès."
                            );

                        } else {

                            afficherErreur(
                                    "Impossible de supprimer l'abonnement."
                            );
                        }
                    }
                });
    }

    @FXML
    private void handleSuspend() {

        Abonnement abonnement =
                abonnementsTable
                        .getSelectionModel()
                        .getSelectedItem();

        if (abonnement == null) {

            afficherAvertissement(
                    "Veuillez sélectionner un abonnement."
            );

            return;
        }

        boolean resultat =
                abonnementService.suspendre(
                        abonnement.getId()
                );

        if (resultat) {

            chargerAbonnements();

            afficherInformation(
                    "L'abonnement a été suspendu."
            );

        } else {

            afficherErreur(
                    "Impossible de suspendre l'abonnement."
            );
        }
    }

    @FXML
    private void handleRenew() {

        Abonnement abonnement =
                abonnementsTable
                        .getSelectionModel()
                        .getSelectedItem();

        if (abonnement == null) {

            afficherAvertissement(
                    "Veuillez sélectionner un abonnement."
            );

            return;
        }

        ouvrirFormulaire(abonnement);
    }

    private void ouvrirFormulaire(
            Abonnement abonnement
    ) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/com/gss/gss/fxml/abonnement-form.fxml"
                            )
                    );

            Parent root = loader.load();

            AbonnementFormController controller =
                    loader.getController();

            if (abonnement != null) {

                controller.setAbonnement(
                        abonnement
                );
            }

            Stage stage = new Stage();

            stage.setTitle(
                    abonnement == null
                            ? "Nouvel abonnement"
                            : "Modifier abonnement"
            );

            stage.initModality(
                    Modality.APPLICATION_MODAL
            );

            stage.setResizable(false);

            stage.setScene(
                    new Scene(root)
            );

            stage.showAndWait();

            chargerAbonnements();

        } catch (IOException e) {

            e.printStackTrace();

            afficherErreur(
                    "Impossible d'ouvrir le formulaire."
            );
        }
    }

    private void afficherAvertissement(
            String message
    ) {

        Alert alert =
                new Alert(Alert.AlertType.WARNING);

        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void afficherInformation(
            String message
    ) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void afficherErreur(
            String message
    ) {

        Alert alert =
                new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}