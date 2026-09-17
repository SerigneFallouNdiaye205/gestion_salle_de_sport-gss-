package com.gss.gss.controller;

import com.gss.gss.model.Utilisateur;
import com.gss.gss.service.UtilisateurService;
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
import java.text.Normalizer;
import java.util.List;

public class UtilisateursController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TableView<Utilisateur> utilisateursTable;

    @FXML
    private TableColumn<Utilisateur, Number> idColumn;

    @FXML
    private TableColumn<Utilisateur, String> usernameColumn;

    @FXML
    private TableColumn<Utilisateur, String> typeColumn;

    @FXML
    private TableColumn<Utilisateur, String> dateCreationColumn;

    @FXML
    private TableColumn<Utilisateur, String> statutColumn;

    @FXML
    private Label coachsActifsLabel;

    @FXML
    private Label receptionnistesActifsLabel;

    @FXML
    private Label administrateursActifsLabel;

    private final UtilisateurService utilisateurService =
            new UtilisateurService();

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

        typeComboBox.setItems(
                FXCollections.observableArrayList(
                        "Tous",
                        "ADMINISTRATEUR",
                        "RECEPTIONNISTE",
                        "COACH"
                )
        );

        chargerUtilisateurs();

        statusComboBox.setOnAction(event -> handleSearch());
        typeComboBox.setOnAction(event -> handleSearch());
    }

    private void configurerColonnes() {

        idColumn.setCellValueFactory(
                cell ->
                        new javafx.beans.property.SimpleIntegerProperty(
                                cell.getValue().getId()
                        )
        );

        usernameColumn.setCellValueFactory(
                cell ->
                        new javafx.beans.property.SimpleStringProperty(
                                cell.getValue().getUsername()
                        )
        );

        typeColumn.setCellValueFactory(
                cell ->
                        new javafx.beans.property.SimpleStringProperty(
                                cell.getValue().getType()
                        )
        );

        dateCreationColumn.setCellValueFactory(
                cell ->
                        new javafx.beans.property.SimpleStringProperty(
                                cell.getValue().getDateCreation() != null
                                        ? cell.getValue()
                                        .getDateCreation()
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

    private void chargerUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurService.findAll();
        utilisateursTable.setItems(
                FXCollections.observableArrayList(utilisateurs)
        );
        mettreAJourStatistiques(utilisateurs);
    }

    @FXML
    private void handleRefresh() {

        searchField.clear();

        statusComboBox.setValue("Tous");
        typeComboBox.setValue("Tous");

        chargerUtilisateurs();
    }

    @FXML
    private void handleSearch() {

        String recherche = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase();

        String statut = statusComboBox.getValue();
        String type = typeComboBox.getValue();

        List<Utilisateur> utilisateurs = utilisateurService.findAll();
        var resultats = utilisateurs.stream()
                .filter(u -> recherche.isEmpty()
                        || String.valueOf(u.getId()).contains(recherche)
                        || (u.getUsername() != null && u.getUsername().toLowerCase().contains(recherche))
                        || (u.getType() != null && u.getType().toLowerCase().contains(recherche))
                        || (u.getStatut() != null && u.getStatut().toLowerCase().contains(recherche)))
                .filter(u -> statut == null || statut.equals("Tous")
                        || statut.equals(u.getStatut()))
                .filter(u -> type == null || type.equals("Tous")
                        || type.equals(u.getType()))
                .toList();

        utilisateursTable.setItems(
                FXCollections.observableArrayList(resultats)
        );
        mettreAJourStatistiques(utilisateurs);
    }

    private void mettreAJourStatistiques(List<Utilisateur> utilisateurs) {
        coachsActifsLabel.setText(String.valueOf(compterActifsParType(utilisateurs, "COACH")));
        receptionnistesActifsLabel.setText(
                String.valueOf(compterActifsParType(utilisateurs, "RECEPTIONNISTE"))
        );
        administrateursActifsLabel.setText(
                String.valueOf(compterActifsParType(utilisateurs, "ADMINISTRATEUR"))
        );
    }

    private long compterActifsParType(List<Utilisateur> utilisateurs, String type) {
        return utilisateurs.stream()
                .filter(utilisateur -> "ACTIF".equals(normaliser(utilisateur.getStatut())))
                .filter(utilisateur -> type.equals(normaliser(utilisateur.getType())))
                .count();
    }

    private String normaliser(String valeur) {
        if (valeur == null) {
            return "";
        }

        return Normalizer.normalize(valeur, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toUpperCase();
    }

    @FXML
    private void handleAdd() {
        if (!PermissionManager.canManageUsers()) {
            afficherAvertissement("La gestion des utilisateurs est réservée à l'administrateur.");
            return;
        }
        ouvrirFormulaire(null);
    }

    @FXML
    private void handleEdit() {
        if (!PermissionManager.canManageUsers()) {
            afficherAvertissement("La gestion des utilisateurs est réservée à l'administrateur.");
            return;
        }

        Utilisateur utilisateur =
                utilisateursTable
                        .getSelectionModel()
                        .getSelectedItem();

        if (utilisateur == null) {
            afficherAvertissement("Veuillez sélectionner un utilisateur.");

            return;
        }

        ouvrirFormulaire(utilisateur);
    }

    @FXML
    private void handleDelete() {
        if (!PermissionManager.canManageUsers()) {
            afficherAvertissement("La gestion des utilisateurs est réservée à l'administrateur.");
            return;
        }
        Utilisateur utilisateur =
                utilisateursTable
                        .getSelectionModel()
                        .getSelectedItem();

        if (utilisateur == null) {
            afficherAvertissement("Veuillez sélectionner un utilisateur.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);

        confirmation.setTitle("Supprimer l'utilisateur");

        confirmation.setHeaderText(
                "Suppression de "
                        + utilisateur.getUsername()
                        + " "
                        + utilisateur.getId()
        );

        confirmation.setContentText(
                "Voulez-vous vraiment supprimer cet utilisateur ?"
        );

        confirmation.showAndWait()
                .ifPresent(reponse -> {

                    if (reponse == ButtonType.OK) {

                        boolean resultat =
                                utilisateurService.delete(
                                        utilisateur.getId()
                                );

                        if (resultat) {

                            chargerUtilisateurs();

                            afficherInformation(
                                    "L'utilisateur a été supprimé."
                            );

                        } else {

                            afficherErreur(
                                    "Impossible de supprimer l'utilisateur."
                            );
                        }
                    }
                });
    }


    private void ouvrirFormulaire(Utilisateur utilisateur) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/com/gss/gss/fxml/utilisateurs-form.fxml"
                            )
                    );

            Parent root = loader.load();

            UtilisateursFormController controller =
                    loader.getController();

            if (utilisateur != null) {
                controller.setUtilisateur(utilisateur);
            }

            Stage stage = new Stage();

            stage.setTitle(
                    utilisateur == null
                            ? "Nouvel utilisateur"
                            : "Modifier utilisateur"
            );

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(
                    new Scene(root, 500, 560)
            );
            stage.showAndWait();

            chargerUtilisateurs();

        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("Impossible d'ouvrir le formulaire.");
        }
    }

    private void afficherAvertissement(String message) {

        Alert alert = new Alert(Alert.AlertType.WARNING);

        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void afficherInformation(String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void afficherErreur(String message) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
