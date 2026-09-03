package com.gss.gss.controller;

import com.gss.gss.model.Coach;
import com.gss.gss.service.CoachService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CoachFormController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField telephoneField;
    @FXML
    private TextField emailField;
    @FXML
    private ComboBox<String> specialiteComboBox;
    @FXML
    private TextField salaireField;
    @FXML
    private ComboBox<String> disponibiliteComboBox;

    private final CoachService coachService =
            new CoachService();

    private Coach coach;

    @FXML
    public void initialize() {

        specialiteComboBox.setItems(
                FXCollections.observableArrayList(
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
                        "DISPONIBLE",
                        "INDISPONIBLE"
                )
        );

        disponibiliteComboBox.setValue(
                "DISPONIBLE"
        );
    }

    public void setCoach(Coach coach) {

        this.coach = coach;

        if (coach == null) {
            return;
        }

        nomField.setText(
                coach.getNom()
        );

        prenomField.setText(
                coach.getPrenom()
        );

        telephoneField.setText(
                coach.getTelephone()
        );

        emailField.setText(
                coach.getEmail()
        );

        specialiteComboBox.setValue(
                coach.getSpecialite()
        );

        if (coach.getSalaire() != null) {

            salaireField.setText(
                    String.valueOf(coach.getSalaire())
            );
        }

        disponibiliteComboBox.setValue(coach.getDisponibilite());
    }

    @FXML
    private void handleSave() {

        if (!validerChamps()) {
            return;
        }

        try {

            String nom =
                    nomField.getText().trim();

            String prenom =
                    prenomField.getText().trim();

            String telephone =
                    telephoneField.getText().trim();

            String email =
                    emailField.getText().trim();

            String specialite =
                    specialiteComboBox.getValue();

            String salaireTexte =
                    salaireField.getText().trim();

            String disponibilite =
                    disponibiliteComboBox.getValue();

            Double salaire = null;

            if (!salaireTexte.isEmpty()) {

                salaire =
                        Double.parseDouble(
                                salaireTexte.replace(
                                        ",",
                                        "."
                                )
                        );

                if (salaire < 0) {

                    afficherErreur(
                            "Le salaire ne peut pas être négatif."
                    );

                    return;
                }
            }

            if (coach == null) {

                Coach nouveauCoach =
                        new Coach();

                nouveauCoach.setNom(nom);
                nouveauCoach.setPrenom(prenom);
                nouveauCoach.setTelephone(telephone);
                nouveauCoach.setEmail(
                        email.isEmpty()
                                ? null
                                : email
                );
                nouveauCoach.setSpecialite(specialite);
                nouveauCoach.setSalaire(salaire);
                nouveauCoach.setDisponibilite(disponibilite);

                boolean resultat = coachService.save(nouveauCoach);

                if (resultat) {

                    afficherInformation(
                            "Coach ajouté avec succès.\n\n"
                                    + "Nom utilisateur : coach"
                                    + nouveauCoach.getId()
                                    + "\n"
                                    + "Mot de passe par défaut : gss"
                    );

                    fermerFenetre();

                } else {

                    afficherErreur(
                            "Impossible d'ajouter le coach."
                    );
                }

            } else {

                coach.setNom(nom);
                coach.setPrenom(prenom);
                coach.setTelephone(telephone);
                coach.setEmail(
                        email.isEmpty()
                                ? null
                                : email
                );
                coach.setSpecialite(specialite);
                coach.setSalaire(salaire);
                coach.setDisponibilite(disponibilite);

                boolean resultat =
                        coachService.update(coach);

                if (resultat) {

                    afficherInformation(
                            "Coach modifié avec succès."
                    );

                    fermerFenetre();

                } else {

                    afficherErreur(
                            "Impossible de modifier le coach."
                    );
                }
            }

        } catch (NumberFormatException e) {

            afficherErreur(
                    "Le salaire doit être un nombre valide.\n"
                            + "Exemple : 150000"
            );

        } catch (Exception e) {

            e.printStackTrace();

            afficherErreur(
                    "Une erreur est survenue :\n"
                            + e.getMessage()
            );
        }
    }

    private boolean validerChamps() {

        if (nomField.getText() == null ||
                nomField.getText().trim().isEmpty()) {

            afficherAvertissement("Le nom est obligatoire.");
            return false;
        }

        if (prenomField.getText() == null ||
                prenomField.getText().trim().isEmpty()) {

            afficherAvertissement("Le prénom est obligatoire.");
            return false;
        }

        if (telephoneField.getText() == null ||
                telephoneField.getText().trim().isEmpty()) {

            afficherAvertissement("Le téléphone est obligatoire.");
            return false;
        }

        if (disponibiliteComboBox.getValue() == null) {

            disponibiliteComboBox.setValue("DISPONIBLE");
        }

        return true;
    }

    @FXML
    private void handleCancel() {
        fermerFenetre();
    }

    private void fermerFenetre() {

        Stage stage =
                (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    private void afficherAvertissement(
            String message
    ) {

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