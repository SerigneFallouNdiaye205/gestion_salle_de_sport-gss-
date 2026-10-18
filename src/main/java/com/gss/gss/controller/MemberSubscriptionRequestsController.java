package com.gss.gss.controller;

import com.gss.gss.security.PermissionManager;
import com.gss.gss.security.SessionManager;
import com.gss.gss.service.EspaceMembreService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class MemberSubscriptionRequestsController {

    @FXML private TableView<EspaceMembreService.DemandeAbonnement> requestsTable;
    @FXML private TableColumn<EspaceMembreService.DemandeAbonnement, Number> idColumn;
    @FXML private TableColumn<EspaceMembreService.DemandeAbonnement, String> memberColumn;
    @FXML private TableColumn<EspaceMembreService.DemandeAbonnement, String> typeColumn;
    @FXML private TableColumn<EspaceMembreService.DemandeAbonnement, String> dateColumn;
    @FXML private Label messageLabel;

    private final EspaceMembreService service = new EspaceMembreService();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleIntegerProperty(cell.getValue().id()));
        memberColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().membre()));
        typeColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().type()));
        dateColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().dateDemande() == null ? ""
                                : DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                                .format(cell.getValue().dateDemande())));
        actualiser();
    }

    @FXML
    private void handleRefresh() {
        actualiser();
    }

    @FXML
    private void handleApprove() {
        if (!PermissionManager.canManageSubscriptions()) {
            afficherErreur("Vous n'avez pas la permission de traiter cette demande.");
            return;
        }
        EspaceMembreService.DemandeAbonnement demande =
                requestsTable.getSelectionModel().getSelectedItem();
        if (demande == null) {
            afficherErreur("Sélectionnez une demande.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Confirmez uniquement si le règlement de cette formule a bien été reçu.",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setTitle("Confirmer le règlement");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        TextInputDialog priceDialog = new TextInputDialog();
        priceDialog.setTitle("Montant encaissé");
        priceDialog.setHeaderText("Demande #" + demande.id() + " — " + demande.type());
        priceDialog.setContentText("Montant réellement reçu (FCFA) :");
        Optional<String> priceText = priceDialog.showAndWait();
        if (priceText.isEmpty()) {
            return;
        }
        double price;
        try {
            price = Double.parseDouble(priceText.get().trim().replace(',', '.'));
            if (!Double.isFinite(price) || price <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            afficherErreur("Saisissez un montant positif valide.");
            return;
        }
        List<String> modes = List.of("ESPECES", "WAVE", "ORANGE_MONEY", "CARTE_BANCAIRE");
        ChoiceDialog<String> modeDialog = new ChoiceDialog<>(modes.get(0), modes);
        modeDialog.setTitle("Mode de règlement");
        modeDialog.setHeaderText("Sélectionnez le mode réellement utilisé.");
        modeDialog.setContentText("Mode :");
        Optional<String> mode = modeDialog.showAndWait();
        if (mode.isEmpty()) {
            return;
        }
        try {
            service.approuverDemande(demande.id(), price, mode.get(),
                    SessionManager.getCurrentUser().getId());
            actualiser();
            afficherInformation("Le paiement a été enregistré et l'abonnement activé.");
        } catch (RuntimeException e) {
            afficherErreur(e.getMessage());
        }
    }

    @FXML
    private void handleReject() {
        if (!PermissionManager.canManageSubscriptions()) {
            afficherErreur("Vous n'avez pas la permission de traiter cette demande.");
            return;
        }
        EspaceMembreService.DemandeAbonnement demande =
                requestsTable.getSelectionModel().getSelectedItem();
        if (demande == null) {
            afficherErreur("Sélectionnez une demande.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Refuser la demande d'abonnement sélectionnée ?",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setTitle("Refuser la demande");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                service.refuserDemande(demande.id(), SessionManager.getCurrentUser().getId());
                actualiser();
            } catch (RuntimeException e) {
                afficherErreur(e.getMessage());
            }
        }
    }

    @FXML
    private void handleClose() {
        ((Stage) requestsTable.getScene().getWindow()).close();
    }

    private void actualiser() {
        try {
            requestsTable.setItems(FXCollections.observableArrayList(
                    service.getDemandesEnAttente()));
            messageLabel.setText(requestsTable.getItems().isEmpty()
                    ? "Aucune demande en attente." : "");
        } catch (RuntimeException e) {
            afficherErreur(e.getMessage());
        }
    }

    private void afficherInformation(String message) {
        new Alert(Alert.AlertType.INFORMATION, message).showAndWait();
    }

    private void afficherErreur(String message) {
        new Alert(Alert.AlertType.ERROR,
                message == null ? "Une erreur est survenue." : message).showAndWait();
    }
}
