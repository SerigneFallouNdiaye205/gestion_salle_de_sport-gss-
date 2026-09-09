package com.gss.gss.controller;

import com.gss.gss.dao.Impl.InscriptionSeanceDAOImpl;
import com.gss.gss.model.InscriptionSeance;
import com.gss.gss.model.Membre;
import com.gss.gss.model.Seance;
import com.gss.gss.service.MembreService;
import com.gss.gss.service.SeanceService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.function.Consumer;

public class InscriptionSeanceFormController {
    @FXML private Label titleLabel;
    @FXML private ComboBox<Membre> membreCombo;
    @FXML private ComboBox<Seance> seanceCombo;
    @FXML private DatePicker dateInscription;
    @FXML private ComboBox<String> statutCombo;
    @FXML private Label errorLabel;
    @FXML private Button saveButton;

    private final InscriptionSeanceDAOImpl dao = new InscriptionSeanceDAOImpl();
    private final MembreService membreService = new MembreService();
    private final SeanceService seanceService = new SeanceService();
    private InscriptionSeance inscription;
    private Runnable onSaved;

    @FXML
    public void initialize() {
        membreCombo.setItems(FXCollections.observableArrayList(membreService.findAll()));
        seanceCombo.setItems(FXCollections.observableArrayList(seanceService.findAll()));
        configurerCellules();
        statutCombo.setItems(FXCollections.observableArrayList("CONFIRMEE", "EN_ATTENTE", "ANNULEE"));
        statutCombo.setValue("CONFIRMEE");
        dateInscription.setValue(LocalDate.now());
    }

    private void configurerCellules() {
        membreCombo.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(Membre value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : value.getPrenom() + " " + value.getNom());
            }
        });
        membreCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Membre value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : value.getPrenom() + " " + value.getNom());
            }
        });
        seanceCombo.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(Seance value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : formatSeance(value));
            }
        });
        seanceCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Seance value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : formatSeance(value));
            }
        });
    }

    public void setInscription(InscriptionSeance inscription) {
        this.inscription = inscription;
        if (inscription == null) return;
        titleLabel.setText("Modifier l'inscription");
        saveButton.setText("Modifier");
        membreCombo.setValue(membreService.findById(inscription.getMembre_id()));
        seanceCombo.setValue(seanceService.findById(inscription.getSeance_id()));
        dateInscription.setValue(inscription.getDate_inscription());
        statutCombo.setValue(inscription.getStatut());
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    @FXML
    private void handleSave() {
        try {
            if (membreCombo.getValue() == null) throw new IllegalArgumentException("Sélectionnez un membre.");
            if (seanceCombo.getValue() == null) throw new IllegalArgumentException("Sélectionnez une séance.");
            if (dateInscription.getValue() == null) throw new IllegalArgumentException("Sélectionnez la date.");
            if (statutCombo.getValue() == null) throw new IllegalArgumentException("Sélectionnez le statut.");

            if (inscription == null) {
                inscription = new InscriptionSeance();
            }
            inscription.setMembre_id(membreCombo.getValue().getId());
            inscription.setSeance_id(seanceCombo.getValue().getId());
            inscription.setDate_inscription(dateInscription.getValue());
            inscription.setStatut(statutCombo.getValue());

            boolean saved = inscription.getId() == 0 ? dao.save(inscription) : dao.update(inscription);
            if (!saved) {
                afficherErreur("L'inscription n'a pas pu être enregistrée.");
                return;
            }
            if (onSaved != null) onSaved.run();
            fermer();
        } catch (IllegalArgumentException e) {
            afficherErreur(e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        fermer();
    }

    private String formatSeance(Seance seance) {
        return seance.getNom() + " - " + seance.getDateSeance()
                + " (" + seance.getHeureDebut() + " à " + seance.getHeureFin() + ")";
    }

    private void afficherErreur(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void fermer() {
        ((Stage) saveButton.getScene().getWindow()).close();
    }
}
