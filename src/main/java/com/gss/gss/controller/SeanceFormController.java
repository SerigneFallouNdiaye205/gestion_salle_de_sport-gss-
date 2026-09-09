package com.gss.gss.controller;

import com.gss.gss.model.Coach;
import com.gss.gss.model.Seance;
import com.gss.gss.service.CoachService;
import com.gss.gss.service.SeanceService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SeanceFormController {
    @FXML private Label titleLabel;
    @FXML private ComboBox<Coach> coachCombo;
    @FXML private ComboBox<String> nomCombo;
    @FXML private DatePicker dateSeance;
    @FXML private ComboBox<String> heureDebutCombo;
    @FXML private ComboBox<String> heureFinCombo;
    @FXML private ComboBox<String> salleCombo;
    @FXML private TextField capaciteField;
    @FXML private Label errorLabel;
    @FXML private Button saveButton;

    private final SeanceService seanceService = new SeanceService();
    private final CoachService coachService = new CoachService();
    private Seance seance;
    private Consumer<Void> onSaved;

    @FXML
    public void initialize() {
        configurerCoachs();
        configurerSpecialites();
        configurerHoraires();
        salleCombo.setItems(FXCollections.observableArrayList(
                "Salle A", "Salle B", "Salle C", "Studio", "Extérieur"));
        salleCombo.setEditable(true);
        coachCombo.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && newValue.getSpecialite() != null
                    && !newValue.getSpecialite().isBlank()) {
                nomCombo.setValue(newValue.getSpecialite());
            }
        });
    }

    public void setSeance(Seance seance) {
        this.seance = seance;
        if (seance == null) {
            return;
        }
        titleLabel.setText("Modifier la séance");
        saveButton.setText("Modifier");
        coachCombo.setValue(coachService.findById(seance.getCoachId()));
        nomCombo.setValue(seance.getNom());
        dateSeance.setValue(seance.getDateSeance());
        heureDebutCombo.setValue(seance.getHeureDebut() == null ? null : seance.getHeureDebut().toString());
        heureFinCombo.setValue(seance.getHeureFin() == null ? null : seance.getHeureFin().toString());
        salleCombo.setValue(seance.getSalle());
        capaciteField.setText(String.valueOf(seance.getCapacite()));
    }

    public void setOnSaved(Runnable callback) {
        this.onSaved = ignored -> callback.run();
    }

    private void configurerCoachs() {
        coachCombo.setItems(FXCollections.observableArrayList(coachService.findAll()));
        coachCombo.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(Coach coach, boolean empty) {
                super.updateItem(coach, empty);
                setText(empty || coach == null ? null : coach.getPrenom() + " " + coach.getNom());
            }
        });
        coachCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Coach coach, boolean empty) {
                super.updateItem(coach, empty);
                setText(empty || coach == null ? null : coach.getPrenom() + " " + coach.getNom());
            }
        });
    }

    private void configurerSpecialites() {
        nomCombo.setItems(FXCollections.observableArrayList(
                coachService.findAll().stream()
                        .map(Coach::getSpecialite)
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(value -> !value.isBlank())
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList())));
    }

    private void configurerHoraires() {
        javafx.collections.ObservableList<String> horaires = FXCollections.observableArrayList();
        for (int heure = 6; heure <= 22; heure++) {
            horaires.add(String.format("%02d:00", heure));
            if (heure < 22) horaires.add(String.format("%02d:30", heure));
        }
        heureDebutCombo.setItems(horaires);
        heureFinCombo.setItems(FXCollections.observableArrayList(horaires));
    }

    @FXML
    private void handleSave() {
        try {
            Seance value = construireSeance();
            boolean saved = seance == null
                    ? seanceService.ajouter(value)
                    : seanceService.modifier(value);
            if (!saved) {
                afficherErreur("L'opération n'a pas pu être enregistrée.");
                return;
            }
            if (onSaved != null) onSaved.accept(null);
            fermer();
        } catch (IllegalArgumentException e) {
            afficherErreur(e.getMessage());
        }
    }

    private Seance construireSeance() {
        Coach coach = coachCombo.getValue();
        if (coach == null) throw new IllegalArgumentException("Sélectionnez un coach.");
        String nom = nomCombo.getValue();
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("Sélectionnez le nom de la séance.");
        if (dateSeance.getValue() == null) throw new IllegalArgumentException("Sélectionnez la date.");
        if (heureDebutCombo.getValue() == null || heureFinCombo.getValue() == null) {
            throw new IllegalArgumentException("Sélectionnez les horaires.");
        }
        String salle = salleCombo.getEditor().getText().trim();
        if (salle.isBlank()) throw new IllegalArgumentException("Sélectionnez ou saisissez une salle.");
        int capacite;
        try {
            capacite = Integer.parseInt(capaciteField.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La capacité doit être un nombre entier.");
        }
        if (capacite <= 0) throw new IllegalArgumentException("La capacité doit être supérieure à zéro.");

        Seance value = new Seance(0, coach.getId(), nom, dateSeance.getValue(),
                LocalTime.parse(heureDebutCombo.getValue()),
                LocalTime.parse(heureFinCombo.getValue()), salle, capacite);
        if (seance != null) value.setId(seance.getId());
        return value;
    }

    @FXML
    private void handleCancel() {
        fermer();
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
