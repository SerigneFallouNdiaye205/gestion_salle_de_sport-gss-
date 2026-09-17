package com.gss.gss.controller;

import com.gss.gss.model.Abonnement;
import com.gss.gss.model.Membre;
import com.gss.gss.service.AbonnementService;
import com.gss.gss.service.MembreService;
import com.gss.gss.security.PermissionManager;
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
    private TableColumn<Abonnement, String> membreIdColumn;
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
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button renewButton;
    @FXML private Button deleteButton;

    private final AbonnementService abonnementService =
            new AbonnementService();
    private final MembreService membreService = new MembreService();

    @FXML
    public void initialize() {

        configurerColonnes();
        boolean canManage = PermissionManager.canManageSubscriptions();
        addButton.setVisible(canManage);
        addButton.setManaged(canManage);
        editButton.setVisible(canManage);
        editButton.setManaged(canManage);
        renewButton.setVisible(canManage);
        renewButton.setManaged(canManage);
        deleteButton.setVisible(canManage);
        deleteButton.setManaged(canManage);

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
                        "EXPIRE"
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
                cell -> {
                    Membre membre = membreService.findById(cell.getValue().getMembreId());
                    return new javafx.beans.property.SimpleStringProperty(
                            membre == null
                                    ? "Membre introuvable"
                                    : membre.getPrenom() + " " + membre.getNom()
                    );
                }
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
        filtrer();
    }

    @FXML
    private void handleFilter() {

        filtrer();
    }

    private void filtrer() {

        String type = typeComboBox.getValue();
        String statut = statutComboBox.getValue();
        String recherche = searchField.getText() == null
                ? "" : searchField.getText().trim().toLowerCase();

        var resultats = abonnementService.findAll().stream()
                .filter(a -> type == null || "Tous".equals(type)
                        || type.equalsIgnoreCase(a.getType()))
                .filter(a -> statut == null || "Tous".equals(statut)
                        || statut.equalsIgnoreCase(a.getStatut()))
                .filter(a -> recherche.isEmpty() || abonnementContient(a, recherche))
                .toList();
        abonnementsTable.setItems(FXCollections.observableArrayList(resultats));
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
        if (!PermissionManager.canManageSubscriptions()) {
            afficherAvertissement("Vous n'avez pas la permission de gérer les abonnements.");
            return;
        }
        ouvrirFormulaire(null);
    }

    @FXML
    private void handleEdit() {
        if (!PermissionManager.canManageSubscriptions()) {
            afficherAvertissement("Vous n'avez pas la permission de modifier les abonnements.");
            return;
        }

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
        if (!PermissionManager.canManageSubscriptions()) {
            afficherAvertissement("Vous n'avez pas la permission de supprimer les abonnements.");
            return;
        }

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
    private void handleRenew() {
        if (!PermissionManager.canManageSubscriptions()) {
            afficherAvertissement("Vous n'avez pas la permission de renouveler les abonnements.");
            return;
        }

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

    private boolean abonnementContient(Abonnement abonnement, String recherche) {
        Membre membre = membreService.findById(abonnement.getMembreId());
        String nom = membre == null ? "" : membre.getPrenom() + " " + membre.getNom();
        return String.valueOf(abonnement.getId()).contains(recherche)
                || String.valueOf(abonnement.getMembreId()).contains(recherche)
                || safe(abonnement.getType()).contains(recherche)
                || safe(abonnement.getStatut()).contains(recherche)
                || nom.toLowerCase().contains(recherche);
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase();
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