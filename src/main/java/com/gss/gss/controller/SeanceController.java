package com.gss.gss.controller;

import com.gss.gss.model.Seance;
import com.gss.gss.service.SeanceService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalTime;

public class SeanceController {

    // ==============================
    // TABLE
    // ==============================

    @FXML
    private TableView<Seance> tableSeances;

    @FXML
    private TableColumn<Seance, Integer> colId;

    @FXML
    private TableColumn<Seance, Integer> colCoach;

    @FXML
    private TableColumn<Seance, String> colNom;

    @FXML
    private TableColumn<Seance, String> colDate;

    @FXML
    private TableColumn<Seance, String> colHeureDebut;

    @FXML
    private TableColumn<Seance, String> colHeureFin;

    @FXML
    private TableColumn<Seance, String> colSalle;

    @FXML
    private TableColumn<Seance, Integer> colCapacite;


    // ==============================
    // FORMULAIRE
    // ==============================

    @FXML
    private TextField txtCoachId;

    @FXML
    private TextField txtNom;

    @FXML
    private DatePicker dateSeance;

    @FXML
    private TextField txtHeureDebut;

    @FXML
    private TextField txtHeureFin;

    @FXML
    private TextField txtSalle;

    @FXML
    private TextField txtCapacite;


    // ==============================
    // BOUTONS
    // ==============================

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnSupprimer;

    @FXML
    private Button btnAnnuler;


    private final SeanceService seanceService =
            new SeanceService();

    private final ObservableList<Seance> listeSeances =
            FXCollections.observableArrayList();

    private Seance seanceSelectionnee;


    // ==============================
    // INITIALISATION
    // ==============================

    @FXML
    public void initialize() {

        configurerColonnes();

        chargerSeances();

        tableSeances
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, ancienne, nouvelle) -> {

                            if (nouvelle != null) {
                                remplirFormulaire(nouvelle);
                            }
                        }
                );
    }


    // ==============================
    // COLONNES
    // ==============================

    private void configurerColonnes() {

        colId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colCoach.setCellValueFactory(
                new PropertyValueFactory<>("coachId")
        );

        colNom.setCellValueFactory(
                new PropertyValueFactory<>("nom")
        );

        colDate.setCellValueFactory(
                cellData -> {

                    if (cellData.getValue().getDateSeance() == null) {
                        return new javafx.beans.property.SimpleStringProperty("");
                    }

                    return new javafx.beans.property.SimpleStringProperty(
                            cellData.getValue()
                                    .getDateSeance()
                                    .toString()
                    );
                }
        );

        colHeureDebut.setCellValueFactory(
                cellData -> {

                    LocalTime heure =
                            cellData.getValue()
                                    .getHeureDebut();

                    return new javafx.beans.property.SimpleStringProperty(
                            heure != null
                                    ? heure.toString()
                                    : ""
                    );
                }
        );

        colHeureFin.setCellValueFactory(
                cellData -> {

                    LocalTime heure =
                            cellData.getValue()
                                    .getHeureFin();

                    return new javafx.beans.property.SimpleStringProperty(
                            heure != null
                                    ? heure.toString()
                                    : ""
                    );
                }
        );

        colSalle.setCellValueFactory(
                new PropertyValueFactory<>("salle")
        );

        colCapacite.setCellValueFactory(
                new PropertyValueFactory<>("capacite")
        );
    }


    // ==============================
    // CHARGER
    // ==============================

    private void chargerSeances() {

        listeSeances.clear();

        listeSeances.addAll(
                seanceService.findAll()
        );

        tableSeances.setItems(listeSeances);
    }


    // ==============================
    // AJOUTER
    // ==============================

    @FXML
    private void ajouter() {

        try {

            Seance seance =
                    recupererFormulaire();

            boolean resultat =
                    seanceService.ajouter(seance);

            if (resultat) {

                afficherInformation(
                        "Succès",
                        "La séance a été ajoutée avec succès."
                );

                chargerSeances();

                viderFormulaire();

            } else {

                afficherErreur(
                        "Erreur",
                        "Impossible d'ajouter la séance."
                );
            }

        } catch (Exception e) {

            afficherErreur(
                    "Erreur",
                    e.getMessage()
            );
        }
    }


    // ==============================
    // MODIFIER
    // ==============================

    @FXML
    private void modifier() {

        if (seanceSelectionnee == null) {

            afficherErreur(
                    "Attention",
                    "Veuillez sélectionner une séance."
            );

            return;
        }

        try {

            Seance seance =
                    recupererFormulaire();

            seance.setId(
                    seanceSelectionnee.getId()
            );

            boolean resultat =
                    seanceService.modifier(seance);

            if (resultat) {

                afficherInformation(
                        "Succès",
                        "La séance a été modifiée avec succès."
                );

                chargerSeances();

                viderFormulaire();

            } else {

                afficherErreur(
                        "Erreur",
                        "Impossible de modifier la séance."
                );
            }

        } catch (Exception e) {

            afficherErreur(
                    "Erreur",
                    e.getMessage()
            );
        }
    }


    // ==============================
    // SUPPRIMER
    // ==============================

    @FXML
    private void supprimer() {

        if (seanceSelectionnee == null) {

            afficherErreur(
                    "Attention",
                    "Veuillez sélectionner une séance."
            );

            return;
        }

        Alert confirmation =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText(
                "Voulez-vous vraiment supprimer cette séance ?"
        );

        confirmation.showAndWait()
                .ifPresent(response -> {

                    if (response == ButtonType.OK) {

                        boolean resultat =
                                seanceService.supprimer(
                                        seanceSelectionnee.getId()
                                );

                        if (resultat) {

                            afficherInformation(
                                    "Succès",
                                    "La séance a été supprimée."
                            );

                            chargerSeances();

                            viderFormulaire();

                        } else {

                            afficherErreur(
                                    "Erreur",
                                    "Impossible de supprimer la séance."
                            );
                        }
                    }
                });
    }


    // ==============================
    // RECUPERER FORMULAIRE
    // ==============================

    private Seance recupererFormulaire() {

        int coachId =
                Integer.parseInt(
                        txtCoachId.getText().trim()
                );

        String nom =
                txtNom.getText().trim();

        java.time.LocalDate date =
                dateSeance.getValue();

        LocalTime heureDebut =
                LocalTime.parse(
                        txtHeureDebut.getText().trim()
                );

        LocalTime heureFin =
                LocalTime.parse(
                        txtHeureFin.getText().trim()
                );

        String salle =
                txtSalle.getText().trim();

        int capacite =
                Integer.parseInt(
                        txtCapacite.getText().trim()
                );

        return new Seance(
                0,
                coachId,
                nom,
                date,
                heureDebut,
                heureFin,
                salle,
                capacite
        );
    }


    // ==============================
    // REMPLIR FORMULAIRE
    // ==============================

    private void remplirFormulaire(Seance seance) {

        seanceSelectionnee = seance;

        txtCoachId.setText(
                String.valueOf(
                        seance.getCoachId()
                )
        );

        txtNom.setText(
                seance.getNom()
        );

        dateSeance.setValue(
                seance.getDateSeance()
        );

        txtHeureDebut.setText(
                seance.getHeureDebut() != null
                        ? seance.getHeureDebut().toString()
                        : ""
        );

        txtHeureFin.setText(
                seance.getHeureFin() != null
                        ? seance.getHeureFin().toString()
                        : ""
        );

        txtSalle.setText(
                seance.getSalle()
        );

        txtCapacite.setText(
                String.valueOf(
                        seance.getCapacite()
                )
        );
    }


    // ==============================
    // VIDER
    // ==============================

    @FXML
    private void viderFormulaire() {

        seanceSelectionnee = null;

        txtCoachId.clear();
        txtNom.clear();
        dateSeance.setValue(null);
        txtHeureDebut.clear();
        txtHeureFin.clear();
        txtSalle.clear();
        txtCapacite.clear();

        tableSeances
                .getSelectionModel()
                .clearSelection();
    }


    // ==============================
    // ALERTES
    // ==============================

    private void afficherInformation(
            String titre,
            String message
    ) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }


    private void afficherErreur(
            String titre,
            String message
    ) {

        Alert alert =
                new Alert(Alert.AlertType.ERROR);

        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}