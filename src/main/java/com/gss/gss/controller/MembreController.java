package com.gss.gss.controller;

import com.gss.gss.model.Membre;
import com.gss.gss.service.MembreService;
import com.gss.gss.security.PermissionManager;
import com.gss.gss.security.SessionManager;
import com.gss.gss.service.EspaceMembreService;
import com.gss.gss.util.StatusCellFactory;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class MembreController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TableView<Membre> membersTable;
    @FXML private TableColumn<Membre, Number> idColumn;
    @FXML private TableColumn<Membre, String> nomColumn;
    @FXML private TableColumn<Membre, String> prenomColumn;
    @FXML private TableColumn<Membre, String> telephoneColumn;
    @FXML private TableColumn<Membre, String> emailColumn;
    @FXML private TableColumn<Membre, String> dateInscriptionColumn;
    @FXML private TableColumn<Membre, String> statutColumn;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button memberAccountButton;

    private final MembreService membreService =
            new MembreService();
    private final EspaceMembreService espaceMembreService = new EspaceMembreService();
    private boolean coach;
    private int coachId;

    @FXML
    public void initialize() {

        configurerColonnes();
        coach = PermissionManager.isCoach();
        coachId = coach ? SessionManager.getCurrentUser().getId() : 0;
        if (coach) {
            membersTable.setPlaceholder(new Label("Aucun membre inscrit à vos séances."));
            addButton.setVisible(false);
            addButton.setManaged(false);
            editButton.setVisible(false);
            editButton.setManaged(false);
            deleteButton.setVisible(false);
            deleteButton.setManaged(false);
            memberAccountButton.setVisible(false);
            memberAccountButton.setManaged(false);
        } else if (!PermissionManager.canManageMembers()) {
            addButton.setVisible(false);
            addButton.setManaged(false);
            editButton.setVisible(false);
            editButton.setManaged(false);
            deleteButton.setVisible(false);
            deleteButton.setManaged(false);
            memberAccountButton.setVisible(false);
            memberAccountButton.setManaged(false);
        }

        statusComboBox.setItems(
                FXCollections.observableArrayList(
                        "Tous",
                        "ACTIF",
                        "INACTIF"
                )
        );

        statusComboBox.setValue("Tous");
        StatusCellFactory.colorStatusOptions(statusComboBox);

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
        statutColumn.setCellFactory(StatusCellFactory.redInactiveOrExpired());
    }

    private void chargerMembres() {
        membersTable.setItems(FXCollections.observableArrayList(
                coach ? membreService.findByCoachId(coachId) : membreService.findAll()
        ));
    }

    @FXML
    private void handleRefresh() {

        searchField.clear();

        statusComboBox.setValue("Tous");

        chargerMembres();
    }

    @FXML
    private void handleSearch() {

        String recherche = searchField.getText() == null
                ? "" : searchField.getText().trim();
        String statut = statusComboBox.getValue();
        List<Membre> source = coach
                ? membreService.findByCoachId(coachId)
                : membreService.findAll();

        List<Membre> resultats = source.stream()
                .filter(membre -> recherche.isEmpty() || contient(membre, recherche))
                .filter(membre -> statut == null || "Tous".equals(statut)
                        || statut.equalsIgnoreCase(membre.getStatut()))
                .toList();
        membersTable.setItems(FXCollections.observableArrayList(resultats));
    }

    @FXML
    private void handleAdd() {
        if (coach) {
            afficherAvertissement("Un coach ne peut gérer que les membres déjà inscrits à ses séances.");
            return;
        }
        if (!PermissionManager.canManageMembers()) {
            afficherAvertissement("Vous n'avez pas la permission de modifier les membres.");
            return;
        }
        ouvrirFormulaire(null);
    }

    @FXML
    private void handleEdit() {
        if (!PermissionManager.canManageMembers()) {
            afficherAvertissement("La modification des membres est réservée à l'administration et à la réception.");
            return;
        }
        Membre membre =
                membersTable.getSelectionModel().getSelectedItem();

        if (membre == null) {
            afficherAvertissement("Veuillez sélectionner un membre.");
            return;
        }
        if (coach && !membreService.isManagedByCoach(membre.getId(), coachId)) {
            afficherAvertissement("Ce membre n'est pas rattaché à vos séances.");
            return;
        }

        ouvrirFormulaire(membre);
    }

    @FXML
    private void handleDelete() {

        if (!PermissionManager.canManageMembers()) {
            afficherAvertissement("La suppression des membres est réservée à l'administration et à la réception.");
            return;
        }
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

    @FXML
    private void handleCreateMemberAccount() {
        if (!PermissionManager.canManageMembers()) {
            afficherAvertissement("Seul le personnel autorisé peut activer un espace membre.");
            return;
        }
        Membre membre = membersTable.getSelectionModel().getSelectedItem();
        if (membre == null) {
            afficherAvertissement("Veuillez sélectionner un membre actif.");
            return;
        }
        if (!"ACTIF".equalsIgnoreCase(membre.getStatut())) {
            afficherAvertissement("Un compte ne peut être créé que pour un membre actif.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Activer l'espace membre");
        dialog.setHeaderText("Créer ou réinitialiser l'accès de "
                + membre.getPrenom() + " " + membre.getNom());
        ButtonType createButton = new ButtonType("Enregistrer les identifiants", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButton, ButtonType.CANCEL);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Nom d'utilisateur");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("8 caractères minimum");
        PasswordField confirmationField = new PasswordField();
        confirmationField.setPromptText("Confirmer le mot de passe");
        GridPane fields = new GridPane();
        fields.setHgap(10);
        fields.setVgap(10);
        fields.addRow(0, new Label("Identifiant :"), usernameField);
        fields.addRow(1, new Label("Mot de passe initial :"), passwordField);
        fields.addRow(2, new Label("Confirmation :"), confirmationField);
        dialog.getDialogPane().setContent(fields);

        dialog.showAndWait().filter(createButton::equals).ifPresent(button -> {
            String password = passwordField.getText();
            if (!password.equals(confirmationField.getText())) {
                afficherErreur("Les mots de passe ne correspondent pas.");
                return;
            }
            try {
                espaceMembreService.creerCompte(membre.getId(), usernameField.getText(), password);
                afficherInformation("Les identifiants de l'espace membre sont prêts. Remettez-les au membre de manière confidentielle.");
            } catch (RuntimeException e) {
                afficherErreur(e.getMessage());
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

    private boolean contient(Membre membre, String recherche) {
        String valeur = recherche.toLowerCase();
        return String.valueOf(membre.getId()).contains(recherche)
                || (membre.getNom() != null && membre.getNom().toLowerCase().contains(valeur))
                || (membre.getPrenom() != null && membre.getPrenom().toLowerCase().contains(valeur))
                || (membre.getTelephone() != null && membre.getTelephone().contains(recherche))
                || (membre.getEmail() != null && membre.getEmail().toLowerCase().contains(valeur))
                || String.valueOf(membre.getId()).contains(recherche);
    }
}