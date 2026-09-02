package com.gss.gss.controller;

import com.gss.gss.model.Membre;
import com.gss.gss.service.MembreService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MembreController {

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private TableView<Membre> membersTable;
    @FXML
    private TableColumn<Membre, Number> idColumn;
    @FXML
    private TableColumn<Membre, String> nomColumn;
    @FXML
    private TableColumn<Membre, String> prenomColumn;
    @FXML
    private TableColumn<Membre, String> telephoneColumn;
    @FXML
    private TableColumn<Membre, String> emailColumn;
    @FXML
    private TableColumn<Membre, String> dateInscriptionColumn;
    @FXML
    private TableColumn<Membre, String> statutColumn;

    private final MembreService membreService =
            new MembreService();

    @FXML
    public void initialize() {

        configurerColonnes();

        statusComboBox.setItems(
                FXCollections.observableArrayList(
                        "Tous",
                        "ACTIF",
                        "INACTIF"
                )
        );

        statusComboBox.setValue("Tous");

        chargerMembres();
    }

    private void configurerColonnes() {

        idColumn.setCellValueFactory(
                cell ->
                        new javafx.beans.property.SimpleIntegerProperty(
                                cell.getValue().getId()
                        )
        );

        nomColumn.setCellValueFactory(
                cell ->
                        new javafx.beans.property.SimpleStringProperty(
                                cell.getValue().getNom()
                        )
        );

        prenomColumn.setCellValueFactory(
                cell ->
                        new javafx.beans.property.SimpleStringProperty(
                                cell.getValue().getPrenom()
                        )
        );

        telephoneColumn.setCellValueFactory(
                cell ->
                        new javafx.beans.property.SimpleStringProperty(
                                cell.getValue().getTelephone()
                        )
        );

        emailColumn.setCellValueFactory(
                cell ->
                        new javafx.beans.property.SimpleStringProperty(
                                cell.getValue().getEmail()
                        )
        );

        dateInscriptionColumn.setCellValueFactory(
                cell ->
                        new javafx.beans.property.SimpleStringProperty(
                                cell.getValue().getDateInscription() != null
                                        ? cell.getValue()
                                        .getDateInscription()
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
    }

    private void chargerMembres() {

        membersTable.setItems(
                FXCollections.observableArrayList(
                        membreService.findAll()
                )
        );
    }

    @FXML
    private void handleRefresh() {

        searchField.clear();

        statusComboBox.setValue("Tous");

        chargerMembres();
    }

    @FXML
    private void handleSearch() {

        String recherche = searchField.getText().trim();
        String statut = statusComboBox.getValue();

        if (!recherche.isEmpty()) {

            membersTable.setItems(
                    FXCollections.observableArrayList(
                            membreService.rechercher(
                                    recherche
                            )
                    )
            );

            return;
        }

        if (statut != null &&
                !statut.equals("Tous")) {

            membersTable.setItems(
                    FXCollections.observableArrayList(
                            membreService.findByStatut(statut)
                    )
            );

            return;
        }

        chargerMembres();
    }

    @FXML
    private void handleAdd() {

        ouvrirFormulaire(null);
    }

    @FXML
    private void handleEdit() {
        Membre membre =
                membersTable.getSelectionModel().getSelectedItem();

        if (membre == null) {
            afficherAvertissement("Veuillez sélectionner un membre.");
            return;
        }

        ouvrirFormulaire(membre);
    }

    @FXML
    private void handleDelete() {

        Membre membre =
                membersTable.getSelectionModel().getSelectedItem();

        if (membre == null) {
            afficherAvertissement("Veuillez sélectionner un membre.");
            return;
        }

        Alert confirmation =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmation.setTitle("Supprimer le membre");

        confirmation.setHeaderText(
                "Suppression de "
                        + membre.getPrenom()
                        + " "
                        + membre.getNom()
        );

        confirmation.setContentText(
                "Voulez-vous vraiment supprimer ce membre ?"
        );

        confirmation.showAndWait()
                .ifPresent(reponse -> {

                    if (reponse == ButtonType.OK) {

                        boolean resultat =
                                membreService.delete(
                                        membre.getId()
                                );

                        if (resultat) {

                            chargerMembres();

                            afficherInformation(
                                    "Le membre a été supprimé."
                            );

                        } else {

                            afficherErreur(
                                    "Impossible de supprimer le membre."
                            );
                        }
                    }
                });
    }

    private void ouvrirFormulaire(Membre membre) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/com/gss/gss/fxml/membres-form.fxml"
                            )
                    );

            Parent root = loader.load();

            MembresFormController controller =
                    loader.getController();

            if (membre != null) {
                controller.setMembre(membre);
            }

            Stage stage = new Stage();

            stage.setTitle(
                    membre == null
                            ? "Nouveau membre"
                            : "Modifier membre"
            );

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setResizable(false);

            stage.setScene(
                    new Scene(root, 500, 560)
            );

            stage.showAndWait();

            chargerMembres();

        } catch (IOException e) {

            e.printStackTrace();

            afficherErreur(
                    "Impossible d'ouvrir le formulaire."
            );
        }
    }

    private void afficherAvertissement(
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.WARNING
                );

        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void afficherInformation(
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void afficherErreur(
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}