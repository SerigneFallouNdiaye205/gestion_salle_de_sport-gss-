package com.gss.gss.controller;

import com.gss.gss.dao.Impl.InscriptionSeanceDAOImpl;
import com.gss.gss.model.InscriptionSeance;
import com.gss.gss.model.Membre;
import com.gss.gss.model.Seance;
import com.gss.gss.service.MembreService;
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
import java.util.List;

public class InscriptionSeanceController {
    @FXML private TableView<InscriptionSeanceRow> inscriptionsTable;
    @FXML private TableColumn<InscriptionSeanceRow, Integer> colId;
    @FXML private TableColumn<InscriptionSeanceRow, String> colMembre;
    @FXML private TableColumn<InscriptionSeanceRow, String> colSeance;
    @FXML private TableColumn<InscriptionSeanceRow, String> colDateSeance;
    @FXML private TableColumn<InscriptionSeanceRow, String> colDateInscription;
    @FXML private TableColumn<InscriptionSeanceRow, String> colStatut;

    private final InscriptionSeanceDAOImpl dao = new InscriptionSeanceDAOImpl();
    private final MembreService membreService = new MembreService();
    private final SeanceService seanceService = new SeanceService();
    private final ObservableList<InscriptionSeanceRow> rows = FXCollections.observableArrayList();
    private List<Membre> membres;
    private List<Seance> seances;

    @FXML
    public void initialize() {
        membres = membreService.findAll();
        seances = seanceService.findAll();
        configurerColonnes();
        handleRefresh();
    }

    private void configurerColonnes() {
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(
                cell.getValue().inscription().getId()).asObject());
        colMembre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().membre()));
        colSeance.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().seance()));
        colDateSeance.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().dateSeance()));
        colDateInscription.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().dateInscription()));
        colStatut.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().statut()));
    }

    @FXML
    private void ajouter() {
        ouvrirFormulaire(null);
    }

    @FXML
    private void handleEdit() {
        InscriptionSeanceRow selection = inscriptionsTable.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherAvertissement("Veuillez sélectionner une inscription.");
            return;
        }
        ouvrirFormulaire(selection.inscription());
    }

    @FXML
    private void handleDelete() {
        InscriptionSeanceRow selection = inscriptionsTable.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherAvertissement("Veuillez sélectionner une inscription.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer cette inscription ?",
                ButtonType.OK, ButtonType.CANCEL);
        confirmation.setTitle("Supprimer l'inscription");
        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (dao.delete(selection.inscription().getId())) {
                handleRefresh();
            } else {
                afficherErreur("Impossible de supprimer l'inscription.");
            }
        }
    }

    @FXML
    private void handleRefresh() {
        membres = membreService.findAll();
        seances = seanceService.findAll();
        rows.clear();
        for (InscriptionSeance inscription : dao.getAll()) {
            rows.add(new InscriptionSeanceRow(
                    inscription,
                    trouverMembreNom(inscription.getMembre_id()),
                    trouverSeanceNom(inscription.getSeance_id())
            ));
        }
        inscriptionsTable.setItems(rows);
    }

    private void ouvrirFormulaire(InscriptionSeance inscription) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/gss/gss/fxml/inscription-form.fxml"));
            Parent root = loader.load();
            InscriptionSeanceFormController controller = loader.getController();
            controller.setInscription(inscription);
            controller.setOnSaved(this::handleRefresh);

            Stage stage = new Stage();
            stage.setTitle(inscription == null ? "Nouvelle inscription" : "Modifier l'inscription");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            afficherErreur("Impossible d'ouvrir le formulaire d'inscription.");
        }
    }

    private Membre trouverMembre(int id) {
        return membres.stream().filter(membre -> membre.getId() == id).findFirst().orElse(null);
    }

    private Seance trouverSeance(int id) {
        return seances.stream().filter(seance -> seance.getId() == id).findFirst().orElse(null);
    }

    private void afficherAvertissement(String message) {
        new Alert(Alert.AlertType.WARNING, message, ButtonType.OK).showAndWait();
    }

    private void afficherErreur(String message) {
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
    }

    public record InscriptionSeanceRow(InscriptionSeance inscription,
                                       String membre,
                                       String seance) {
        public String dateSeance() {
            return seance == null ? "-" : seance.substring(seance.indexOf('|') + 1).trim();
        }

        public String dateInscription() {
            return inscription.getDate_inscription() == null
                    ? "-" : inscription.getDate_inscription().toString();
        }

        public String statut() {
            return inscription.getStatut();
        }
    }

    private String trouverMembreNom(int id) {
        Membre membre = trouverMembre(id);
        return membre == null ? "Membre introuvable"
                : membre.getPrenom() + " " + membre.getNom();
    }

    private String trouverSeanceNom(int id) {
        Seance seance = trouverSeance(id);
        return seance == null ? "Séance introuvable"
                : seance.getNom() + " | " + seance.getDateSeance()
                + " (" + seance.getHeureDebut() + " - " + seance.getHeureFin() + ")";
    }
}
