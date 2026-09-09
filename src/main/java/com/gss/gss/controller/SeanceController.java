package com.gss.gss.controller;

import com.gss.gss.model.Coach;
import com.gss.gss.model.Seance;
import com.gss.gss.service.CoachService;
import com.gss.gss.service.SeanceService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class SeanceController {
    @FXML private TableView<Seance> tableSeances;
    @FXML private TableColumn<Seance, Integer> colId;
    @FXML private TableColumn<Seance, String> colCoach;
    @FXML private TableColumn<Seance, String> colNom;
    @FXML private TableColumn<Seance, String> colDate;
    @FXML private TableColumn<Seance, String> colHeureDebut;
    @FXML private TableColumn<Seance, String> colHeureFin;
    @FXML private TableColumn<Seance, String> colSalle;
    @FXML private TableColumn<Seance, Integer> colCapacite;

    private final SeanceService seanceService = new SeanceService();
    private final CoachService coachService = new CoachService();
    private final ObservableList<Seance> listeSeances = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurerColonnes();
        chargerSeances();
    }

    private void configurerColonnes() {
        colId.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleIntegerProperty(
                        cell.getValue().getId()).asObject()
        );
        colCoach.setCellValueFactory(cell -> {
            Coach coach = coachService.findById(
                    cell.getValue().getCoachId()
            );
            return new SimpleStringProperty(
                    coach == null ? "Coach introuvable"
                    : coach.getPrenom() + " " + coach.getNom());
        });

        colNom.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getNom()));

        colDate.setCellValueFactory(cell ->
                new SimpleStringProperty(
                cell.getValue().getDateSeance()
                        == null ? "" : cell.getValue().getDateSeance().toString())
        );

        colHeureDebut.setCellValueFactory(cell ->
                new SimpleStringProperty(
                cell.getValue().getHeureDebut()
                        == null ? "" : cell.getValue().getHeureDebut().toString())
        );

        colHeureFin.setCellValueFactory(cell ->
                new SimpleStringProperty(
                cell.getValue().getHeureFin()
                        == null ? "" : cell.getValue().getHeureFin().toString())
        );
        colSalle.setCellValueFactory(cell -> new
                SimpleStringProperty(cell.getValue().getSalle())
        );
        colCapacite.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleIntegerProperty(
                cell.getValue().getCapacite()).asObject()
        );
    }

    private void chargerSeances() {
        listeSeances.setAll(seanceService.findAll());
        tableSeances.setItems(listeSeances);
    }

    @FXML
    private void handleAdd() {
        ouvrirFormulaire(null);
    }

    @FXML
    private void handleEdit() {
        Seance selection = tableSeances.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherAvertissement("Veuillez sélectionner une séance.");
            return;
        }
        ouvrirFormulaire(selection);
    }

    @FXML
    private void handleDelete() {
        Seance selection = tableSeances.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherAvertissement("Veuillez sélectionner une séance.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer cette séance ?",
                ButtonType.OK, ButtonType.CANCEL);
        confirmation.setTitle("Supprimer la séance");
        confirmation.setHeaderText(selection.getNom());

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (seanceService.supprimer(selection.getId())) {
                chargerSeances();
            } else {
                afficherErreur("Impossible de supprimer la séance.");
            }
        }
    }

    private void ouvrirFormulaire(Seance seance) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gss/gss/fxml/seance-form.fxml"));
            Parent root = loader.load();
            SeanceFormController controller = loader.getController();
            controller.setSeance(seance);
            controller.setOnSaved(this::chargerSeances);

            Stage stage = new Stage();
            stage.setTitle(seance == null ? "Nouvelle séance" : "Modifier la séance");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            afficherErreur("Impossible d'ouvrir le formulaire de séance.");
        }
    }

    private void afficherAvertissement(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
