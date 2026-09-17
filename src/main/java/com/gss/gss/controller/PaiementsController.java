package com.gss.gss.controller;

import com.gss.gss.model.Paiement;
import com.gss.gss.model.Membre;
import com.gss.gss.service.MembreService;
import com.gss.gss.service.PaiementService;
import com.gss.gss.security.PermissionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PaiementsController {

    @FXML private TableView<Paiement> paiementTable;
    @FXML private TableColumn<Paiement, Number> idColumn;
    @FXML private TableColumn<Paiement, String> membreIdColumn;
    @FXML private TableColumn<Paiement, Number> abonnementIdColumn;
    @FXML private TableColumn<Paiement, Number> montantColumn;
    @FXML private TableColumn<Paiement, LocalDateTime> dateColumn;
    @FXML private TableColumn<Paiement, String> modeColumn;
    @FXML private TableColumn<Paiement, String> statutColumn;
    @FXML private TableColumn<Paiement, String> referenceColumn;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statutComboBox;
    @FXML private ComboBox<String> modeComboBox;
    @FXML private DatePicker datePicker;
    @FXML private Label totalPaiementsLabel;
    @FXML private Label totalRevenusLabel;
    @FXML private Button nouveauButton;
    @FXML private Button modifierButton;
    @FXML private Button supprimerButton;

    private final PaiementService paiementService =
            new PaiementService();
    private final MembreService membreService = new MembreService();

    private final ObservableList<Paiement> paiementList =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        configurerColonnes();
        boolean canManage = PermissionManager.canManagePayments();
        nouveauButton.setVisible(canManage);
        nouveauButton.setManaged(canManage);
        modifierButton.setVisible(canManage);
        modifierButton.setManaged(canManage);
        supprimerButton.setVisible(canManage);
        supprimerButton.setManaged(canManage);

        statutComboBox.setItems(
                FXCollections.observableArrayList(
                        "TOUS",
                        "VALIDE",
                        "EN_ATTENTE",
                        "ANNULE"
                )
        );

        modeComboBox.setItems(
                FXCollections.observableArrayList(
                        "TOUS",
                        "ESPECES",
                        "WAVE",
                        "ORANGE_MONEY",
                        "CARTE_BANCAIRE"
                )
        );

        statutComboBox.setValue("TOUS");
        modeComboBox.setValue("TOUS");

        chargerPaiements();

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) ->
                        rechercher()
        );
    }

    private void configurerColonnes() {

        idColumn.setCellValueFactory(
                cellData ->
                        new javafx.beans.property.SimpleIntegerProperty(
                                cellData.getValue().getId()
                        )
        );

        membreIdColumn.setCellValueFactory(
                cellData -> {
                    Membre membre = membreService.findById(
                            cellData.getValue().getMembreId()
                    );
                    return new javafx.beans.property.SimpleStringProperty(
                            membre == null
                                    ? "Membre introuvable"
                                    : membre.getPrenom() + " " + membre.getNom()
                    );
                }
        );

        abonnementIdColumn.setCellValueFactory(
                cellData ->
                        new javafx.beans.property.SimpleIntegerProperty(
                                cellData.getValue().getAbonnementId()
                        )
        );

        montantColumn.setCellValueFactory(
                cellData ->
                        new javafx.beans.property.SimpleDoubleProperty(
                                cellData.getValue().getMontant()
                        )
        );

        dateColumn.setCellValueFactory(
                cellData ->
                        new javafx.beans.property.SimpleObjectProperty<>(
                                cellData.getValue().getDatePaiement()
                        )
        );

        modeColumn.setCellValueFactory(
                cellData ->
                        new javafx.beans.property.SimpleStringProperty(
                                cellData.getValue().getModePaiement()
                        )
        );

        statutColumn.setCellValueFactory(
                cellData ->
                        new javafx.beans.property.SimpleStringProperty(
                                cellData.getValue().getStatut()
                        )
        );

        referenceColumn.setCellValueFactory(
                cellData ->
                        new javafx.beans.property.SimpleStringProperty(
                                cellData.getValue().getReference()
                        )
        );
    }

    private void chargerPaiements() {

        paiementList.setAll(
                paiementService.findAll()
        );

        paiementTable.setItems(paiementList);

        mettreAJourStatistiques();
    }

    @FXML
    private void handleNouveau() {
        if (!PermissionManager.canManagePayments()) {
            afficherAlerte("Accès refusé", "Vous n'avez pas la permission de gérer les paiements.");
            return;
        }
        ouvrirFormulaire(null);
    }

    @FXML
    private void handleModifier() {
        if (!PermissionManager.canManagePayments()) {
            afficherAlerte("Accès refusé", "Vous n'avez pas la permission de modifier les paiements.");
            return;
        }
        Paiement paiement =
                paiementTable.getSelectionModel()
                        .getSelectedItem();

        if (paiement == null) {

            afficherAlerte(
                    "Sélection",
                    "Veuillez sélectionner un paiement."
            );

            return;
        }

        ouvrirFormulaire(paiement);
    }

    @FXML
    private void handleSupprimer() {
        if (!PermissionManager.canManagePayments()) {
            afficherAlerte("Accès refusé", "Vous n'avez pas la permission de supprimer les paiements.");
            return;
        }
        Paiement paiement =
                paiementTable.getSelectionModel()
                        .getSelectedItem();

        if (paiement == null) {

            afficherAlerte(
                    "Sélection",
                    "Veuillez sélectionner un paiement."
            );

            return;
        }

        Alert confirmation =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmation.setTitle("Suppression");

        confirmation.setHeaderText(
                "Supprimer le paiement ?"
        );

        confirmation.setContentText(
                "Référence : " +
                        paiement.getReference()
        );

        confirmation.showAndWait()
                .ifPresent(response -> {

                    if (response == ButtonType.OK) {

                        boolean resultat =
                                paiementService.delete(
                                        paiement.getId()
                                );

                        if (resultat) {

                            chargerPaiements();

                            afficherInformation(
                                    "Succès",
                                    "Paiement supprimé."
                            );

                        } else {

                            afficherAlerte(
                                    "Erreur",
                                    "Impossible de supprimer le paiement."
                            );
                        }
                    }
                });
    }

    @FXML
    private void handleRechercher() {

        rechercher();
    }

    private void rechercher() {

        String keyword =
                searchField.getText();

        if (keyword == null ||
                keyword.isBlank()) {

            appliquerFiltres();

            return;
        }

        paiementList.setAll(
                paiementService.search(keyword)
        );

        mettreAJourStatistiques();
    }

    @FXML
    private void handleFiltrer() {

        appliquerFiltres();
    }

    private void appliquerFiltres() {

        String statut =
                statutComboBox.getValue();

        String mode =
                modeComboBox.getValue();

        /*
         * DatePicker retourne LocalDate
         */
        LocalDate date =
                datePicker.getValue();

        ObservableList<Paiement> resultats =
                FXCollections.observableArrayList(
                        paiementService.findAll()
                );

        if (statut != null &&
                !statut.equals("TOUS")) {

            resultats.removeIf(
                    paiement ->
                            !statut.equals(
                                    paiement.getStatut()
                            )
            );
        }

        if (mode != null &&
                !mode.equals("TOUS")) {

            resultats.removeIf(
                    paiement ->
                            !mode.equals(
                                    paiement.getModePaiement()
                            )
            );
        }

        /*
         * Comparaison :
         *
         * LocalDate
         *      ↓
         * LocalDateTime.toLocalDate()
         */
        if (date != null) {

            resultats.removeIf(
                    paiement ->
                            paiement.getDatePaiement() == null ||
                                    !date.equals(
                                            paiement.getDatePaiement()
                                                    .toLocalDate()
                                    )
            );
        }

        paiementList.setAll(resultats);

        paiementTable.setItems(paiementList);

        mettreAJourStatistiques();
    }

    @FXML
    private void handleActualiser() {

        searchField.clear();

        datePicker.setValue(null);

        statutComboBox.setValue("TOUS");

        modeComboBox.setValue("TOUS");

        chargerPaiements();
    }

    private void mettreAJourStatistiques() {

        int nombre =
                paiementList.size();

        double total =
                paiementList.stream()
                        .filter(p ->
                                "VALIDE".equals(
                                        p.getStatut()
                                )
                        )
                        .mapToDouble(
                                Paiement::getMontant
                        )
                        .sum();

        totalPaiementsLabel.setText(
                String.valueOf(nombre)
        );

        totalRevenusLabel.setText(
                String.format(
                        "%.2f FCFA",
                        total
                )
        );
    }

    private void ouvrirFormulaire(
            Paiement paiement) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/com/gss/gss/fxml/paiement-form.fxml"
                            )
                    );

            Parent root = loader.load();

            PaiementFormController controller =
                    loader.getController();

            controller.setPaiementsController(this);

            if (paiement != null) {

                controller.setPaiement(paiement);
            }

            Stage stage = new Stage();

            stage.setTitle(
                    paiement == null
                            ? "Nouveau paiement"
                            : "Modifier paiement"
            );

            stage.setScene(
                    new Scene(root)
            );

            stage.showAndWait();

            chargerPaiements();

        } catch (IOException e) {

            e.printStackTrace();

            afficherAlerte(
                    "Erreur",
                    "Impossible d'ouvrir le formulaire."
            );
        }
    }

    private void afficherAlerte(
            String titre,
            String message) {

        Alert alert =
                new Alert(Alert.AlertType.ERROR);

        alert.setTitle(titre);

        alert.setHeaderText(null);

        alert.setContentText(message);

        alert.showAndWait();
    }

    private void afficherInformation(
            String titre,
            String message) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(titre);

        alert.setHeaderText(null);

        alert.setContentText(message);

        alert.showAndWait();
    }
}