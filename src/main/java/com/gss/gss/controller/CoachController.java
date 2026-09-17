package com.gss.gss.controller;

import com.gss.gss.model.Coach;
import com.gss.gss.service.CoachService;
import com.gss.gss.security.PermissionManager;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class CoachController {

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> specialiteComboBox;
    @FXML
    private ComboBox<String> disponibiliteComboBox;
    @FXML
    private TableView<Coach> coachsTable;
    @FXML
    private TableColumn<Coach, Number> idColumn;
    @FXML
    private TableColumn<Coach, String> nomColumn;
    @FXML
    private TableColumn<Coach, String> prenomColumn;
    @FXML
    private TableColumn<Coach, String> telephoneColumn;
    @FXML
    private TableColumn<Coach, String> emailColumn;
    @FXML
    private TableColumn<Coach, String> specialiteColumn;
    @FXML
    private TableColumn<Coach, Number> salaireColumn;
    @FXML
    private TableColumn<Coach, String> disponibiliteColumn;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Label totalCoachsLabel;
    @FXML
    private Label disponiblesLabel;
    @FXML
    private Label indisponiblesLabel;

    private final CoachService coachService =
            new CoachService();

    @FXML
    public void initialize() {

        configurerColonnes();

        specialiteComboBox.setItems(
                FXCollections.observableArrayList(
                        "Toutes",
                        "Musculation",
                        "Cardio",
                        "Fitness",
                        "CrossFit",
                        "Yoga",
                        "Pilates",
                        "Boxe",
                        "Zumba",
                        "Autre"
                )
        );

        disponibiliteComboBox.setItems(
                FXCollections.observableArrayList(
                        "Toutes",
                        "DISPONIBLE",
                        "INDISPONIBLE"
                )
        );

        specialiteComboBox.setValue("Toutes");
        disponibiliteComboBox.setValue("Toutes");

        disponibiliteColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : value);
                getStyleClass().removeAll("status-available", "status-unavailable");
                if (!empty && value != null) {
                    getStyleClass().add(
                            "DISPONIBLE".equalsIgnoreCase(value)
                                    ? "status-available"
                                    : "status-unavailable"
                    );
                }
            }
        });

        chargerCoachs();
    }

    private void configurerColonnes() {

        idColumn.setCellValueFactory(
                cell ->
                        new SimpleIntegerProperty(
                                cell.getValue().getId()
                        )
        );

        nomColumn.setCellValueFactory(
                cell ->
                        new SimpleStringProperty(
                                cell.getValue().getNom()
                        )
        );

        prenomColumn.setCellValueFactory(
                cell ->
                        new SimpleStringProperty(
                                cell.getValue().getPrenom()
                        )
        );

        telephoneColumn.setCellValueFactory(
                cell ->
                        new SimpleStringProperty(
                                cell.getValue().getTelephone()
                        )
        );

        emailColumn.setCellValueFactory(
                cell ->
                        new SimpleStringProperty(
                                cell.getValue().getEmail() == null
                                        ? ""
                                        : cell.getValue().getEmail()
                        )
        );

        specialiteColumn.setCellValueFactory(
                cell ->
                        new SimpleStringProperty(
                                cell.getValue().getSpecialite() == null
                                        ? ""
                                        : cell.getValue().getSpecialite()
                        )
        );

        salaireColumn.setCellValueFactory(
                cell ->
                        new SimpleDoubleProperty(
                                cell.getValue().getSalaire() == null
                                        ? 0
                                        : cell.getValue().getSalaire()
                        )
        );

        disponibiliteColumn.setCellValueFactory(
                cell ->
                        new SimpleStringProperty(
                                cell.getValue().getDisponibilite()
                        )
        );
    }

    private void chargerCoachs() {
        filtrer();
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
        String specialite =
                specialiteComboBox.getValue();

        String disponibilite =
                disponibiliteComboBox.getValue();

        List<Coach> coachs =
                coachService.findAll();

        String recherche = searchField.getText() == null
                ? "" : searchField.getText().trim().toLowerCase();

        if (!recherche.isEmpty()) {
            coachs = coachs.stream()
                    .filter(coach -> contient(coach, recherche))
                    .toList();
        }

        if (specialite != null &&
                !specialite.equals("Toutes")) {

            coachs =
                    coachs.stream()
                            .filter(c ->
                                    specialite.equalsIgnoreCase(
                                            c.getSpecialite()
                                    )
                            )
                            .toList();
        }

        if (disponibilite != null &&
                !disponibilite.equals("Toutes")) {

            coachs =
                    coachs.stream()
                            .filter(c ->
                                    disponibilite.equalsIgnoreCase(
                                            c.getDisponibilite()
                                    )
                            )
                            .toList();
        }

        coachsTable.setItems(
                FXCollections.observableArrayList(
                        coachs
                )
        );
        mettreAJourStatistiques(coachs);
    }

    @FXML
    private void handleRefresh() {

        searchField.clear();

        specialiteComboBox.setValue(
                "Toutes"
        );

        disponibiliteComboBox.setValue(
                "Toutes"
        );

        chargerCoachs();
    }

    private boolean contient(Coach coach, String recherche) {
        return String.valueOf(coach.getId()).contains(recherche)
                || texte(coach.getNom()).contains(recherche)
                || texte(coach.getPrenom()).contains(recherche)
                || texte(coach.getTelephone()).contains(recherche)
                || texte(coach.getEmail()).contains(recherche)
                || texte(coach.getSpecialite()).contains(recherche);
    }

    private String texte(String valeur) {
        return valeur == null ? "" : valeur.toLowerCase();
    }

    private void mettreAJourStatistiques(List<Coach> coachs) {
        totalCoachsLabel.setText(String.valueOf(coachs.size()));
        disponiblesLabel.setText(String.valueOf(coachs.stream()
                .filter(coach -> "DISPONIBLE".equalsIgnoreCase(coach.getDisponibilite()))
                .count()));
        indisponiblesLabel.setText(String.valueOf(coachs.stream()
                .filter(coach -> "INDISPONIBLE".equalsIgnoreCase(coach.getDisponibilite()))
                .count()));
    }

    @FXML
    private void handleAdd() {
        if (!PermissionManager.canManageCoaches()) {
            afficherAvertissement("La gestion des coachs est réservée à l'administrateur.");
            return;
        }
        ouvrirFormulaire(null);
    }

    @FXML
    private void handleEdit() {
        if (!PermissionManager.canManageCoaches()) {
            afficherAvertissement("La gestion des coachs est réservée à l'administrateur.");
            return;
        }

        Coach coach =
                coachsTable
                        .getSelectionModel()
                        .getSelectedItem();

        if (coach == null) {

            afficherAvertissement(
                    "Veuillez sélectionner un coach."
            );

            return;
        }

        ouvrirFormulaire(coach);
    }

    @FXML
    private void handleDelete() {
        if (!PermissionManager.canManageCoaches()) {
            afficherAvertissement("La gestion des coachs est réservée à l'administrateur.");
            return;
        }

        Coach coach =
                coachsTable
                        .getSelectionModel()
                        .getSelectedItem();

        if (coach == null) {

            afficherAvertissement(
                    "Veuillez sélectionner un coach."
            );

            return;
        }

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Supprimer le coach"
        );

        confirmation.setHeaderText(
                coach.getPrenom()
                        + " "
                        + coach.getNom()
        );

        confirmation.setContentText(
                "Voulez-vous vraiment supprimer ce coach ?\n\n"
                        + "Son compte utilisateur COACH sera également supprimé."
        );

        confirmation.showAndWait()
                .ifPresent(reponse -> {

                    if (reponse == ButtonType.OK) {

                        boolean resultat =
                                coachService.delete(
                                        coach.getId()
                                );

                        if (resultat) {

                            chargerCoachs();

                            afficherInformation(
                                    "Coach et compte utilisateur supprimés avec succès."
                            );

                        } else {

                            afficherErreur(
                                    "Impossible de supprimer le coach."
                            );
                        }
                    }
                });
    }

    private void ouvrirFormulaire(
            Coach coach
    ) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/com/gss/gss/fxml/coach-form.fxml"
                            )
                    );

            Parent root =
                    loader.load();

            CoachFormController controller =
                    loader.getController();

            if (coach != null) {
                controller.setCoach(coach);
            }

            Stage stage =
                    new Stage();

            stage.setTitle(
                    coach == null
                            ? "Nouveau coach"
                            : "Modifier le coach"
            );

            stage.initModality(
                    Modality.APPLICATION_MODAL
            );

            stage.setResizable(false);

            stage.setScene(
                    new Scene(root)
            );

            stage.showAndWait();

            chargerCoachs();

        } catch (IOException e) {

            e.printStackTrace();

            afficherErreur(
                    "Impossible d'ouvrir le formulaire du coach."
            );
        }
    }

    private void afficherAvertissement(
            String message
    ) {

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
            String message
    ) {

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
            String message
    ) {

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