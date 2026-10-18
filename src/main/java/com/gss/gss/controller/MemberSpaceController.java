package com.gss.gss.controller;

import com.gss.gss.model.Abonnement;
import com.gss.gss.model.Paiement;
import com.gss.gss.model.Seance;
import com.gss.gss.security.MembreSessionManager;
import com.gss.gss.service.EspaceMembreService;
import com.gss.gss.util.StatusCellFactory;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

public class MemberSpaceController {

    @FXML private Label welcomeLabel;
    @FXML private Label activeSubscriptionMetric;
    @FXML private Label bookingMetric;
    @FXML private Label paymentMetric;
    @FXML private Label pendingRequestMetric;
    @FXML private TableView<Abonnement> subscriptionsTable;
    @FXML private TableColumn<Abonnement, Number> subscriptionIdColumn;
    @FXML private TableColumn<Abonnement, String> subscriptionTypeColumn;
    @FXML private TableColumn<Abonnement, String> subscriptionStartColumn;
    @FXML private TableColumn<Abonnement, String> subscriptionEndColumn;
    @FXML private TableColumn<Abonnement, String> subscriptionStatusColumn;
    @FXML private TableView<EspaceMembreService.SeanceDisponible> sessionsTable;
    @FXML private TableColumn<EspaceMembreService.SeanceDisponible, String> sessionNameColumn;
    @FXML private TableColumn<EspaceMembreService.SeanceDisponible, String> sessionDateColumn;
    @FXML private TableColumn<EspaceMembreService.SeanceDisponible, String> sessionTimeColumn;
    @FXML private TableColumn<EspaceMembreService.SeanceDisponible, String> sessionRoomColumn;
    @FXML private TableColumn<EspaceMembreService.SeanceDisponible, Number> sessionPlacesColumn;
    @FXML private TableView<EspaceMembreService.InscriptionMembre> bookingsTable;
    @FXML private TableColumn<EspaceMembreService.InscriptionMembre, String> bookingNameColumn;
    @FXML private TableColumn<EspaceMembreService.InscriptionMembre, String> bookingDateColumn;
    @FXML private TableColumn<EspaceMembreService.InscriptionMembre, String> bookingTimeColumn;
    @FXML private TableColumn<EspaceMembreService.InscriptionMembre, String> bookingRoomColumn;
    @FXML private TableColumn<EspaceMembreService.InscriptionMembre, String> bookingStatusColumn;
    @FXML private TableView<Paiement> paymentsTable;
    @FXML private TableColumn<Paiement, String> paymentDateColumn;
    @FXML private TableColumn<Paiement, Number> paymentAmountColumn;
    @FXML private TableColumn<Paiement, String> paymentModeColumn;
    @FXML private TableColumn<Paiement, String> paymentStatusColumn;
    @FXML private TableColumn<Paiement, String> paymentReferenceColumn;
    @FXML private TableView<EspaceMembreService.DemandeAbonnement> requestsTable;
    @FXML private TableColumn<EspaceMembreService.DemandeAbonnement, String> requestTypeColumn;
    @FXML private TableColumn<EspaceMembreService.DemandeAbonnement, String> requestDateColumn;
    @FXML private TableColumn<EspaceMembreService.DemandeAbonnement, String> requestStatusColumn;
    @FXML private ComboBox<String> subscriptionTypeComboBox;
    @FXML private javafx.scene.control.Button requestSubscriptionButton;
    @FXML private javafx.scene.control.Button bookSessionButton;
    @FXML private javafx.scene.control.Button cancelBookingButton;

    private final EspaceMembreService service = new EspaceMembreService();
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private boolean abonnementValide;

    @FXML
    private void initialize() {
        if (!MembreSessionManager.isLoggedIn()) {
            afficherErreur("La session membre a expiré. Veuillez vous reconnecter.");
            return;
        }
        welcomeLabel.setText("Bienvenue, " + MembreSessionManager.getNomComplet());
        subscriptionTypeComboBox.setItems(FXCollections.observableArrayList(
                "JOURNALIER", "HEBDOMADAIRE", "MENSUEL",
                "TRIMESTRIEL", "SEMESTRIEL", "ANNUEL"));
        configurerColonnes();
        sessionsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) ->
                        bookSessionButton.setDisable(newValue == null || !abonnementValide));
        bookingsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) ->
                        cancelBookingButton.setDisable(newValue == null));
        subscriptionTypeComboBox.valueProperty().addListener(
                (observable, oldValue, newValue) -> mettreAJourDisponibiliteDemande());
        bookSessionButton.setDisable(true);
        cancelBookingButton.setDisable(true);
        actualiser();
    }

    private void configurerColonnes() {
        subscriptionIdColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getId()));
        subscriptionTypeColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getType()));
        subscriptionStartColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        formatDate(cell.getValue().getDateDebut())));
        subscriptionEndColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        formatDate(cell.getValue().getDateFin())));
        subscriptionStatusColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatut()));
        subscriptionStatusColumn.setCellFactory(StatusCellFactory.redInactiveOrExpired());

        sessionNameColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().seance().getNom()));
        sessionDateColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        formatDate(cell.getValue().seance().getDateSeance())));
        sessionTimeColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().seance().getHeureDebut() + " - "
                                + cell.getValue().seance().getHeureFin()));
        sessionRoomColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().seance().getSalle()));
        sessionPlacesColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleIntegerProperty(cell.getValue().placesRestantes()));

        bookingNameColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().seance()));
        bookingDateColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(formatDate(cell.getValue().date())));
        bookingTimeColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().horaires()));
        bookingRoomColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().salle()));
        bookingStatusColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().statut()));
        bookingStatusColumn.setCellFactory(StatusCellFactory.redInactiveOrExpired());

        paymentDateColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getDatePaiement() == null ? ""
                                : DATE_TIME.format(cell.getValue().getDatePaiement())));
        paymentAmountColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getMontant()));
        paymentModeColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getModePaiement()));
        paymentStatusColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatut()));
        paymentStatusColumn.setCellFactory(StatusCellFactory.redInactiveOrExpired());
        paymentReferenceColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getReference()));

        requestTypeColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().type()));
        requestDateColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().dateDemande() == null ? ""
                                : DATE_TIME.format(cell.getValue().dateDemande())));
        requestStatusColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().statut()));
    }

    @FXML
    private void handleBookSession() {
        EspaceMembreService.SeanceDisponible selected =
                sessionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherErreur("Sélectionnez une séance.");
            return;
        }
        executer(() -> service.inscrireASeance(
                MembreSessionManager.getMembreId(), selected.seance().getId()),
                "Inscription confirmée.", true);
    }

    @FXML
    private void handleRequestSubscription() {
        String type = subscriptionTypeComboBox.getValue();
        if (type == null) {
            afficherErreur("Sélectionnez une formule d'abonnement.");
            return;
        }
        executer(() -> service.demanderAbonnement(MembreSessionManager.getMembreId(), type),
                "Votre demande a été envoyée. Elle sera activée après confirmation du règlement par le personnel.",
                true);
    }

    @FXML
    private void handleCancelBooking() {
        EspaceMembreService.InscriptionMembre selected =
                bookingsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherErreur("Sélectionnez une inscription.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Annuler l'inscription à « " + selected.seance() + " » ?",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setTitle("Annuler une inscription");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        executer(() -> service.annulerInscription(MembreSessionManager.getMembreId(), selected.id()),
                "Votre inscription a été annulée.", true);
    }

    @FXML
    private void handleRefresh() {
        actualiser();
    }

    @FXML
    private void handleChangePassword() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier le mot de passe");
        dialog.setHeaderText("Choisissez un mot de passe personnel d'au moins 8 caractères.");
        ButtonType save = new ButtonType("Enregistrer", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        PasswordField oldPassword = new PasswordField();
        PasswordField newPassword = new PasswordField();
        PasswordField confirmation = new PasswordField();
        GridPane fields = new GridPane();
        fields.setHgap(10);
        fields.setVgap(10);
        fields.addRow(0, new Label("Mot de passe actuel :"), oldPassword);
        fields.addRow(1, new Label("Nouveau mot de passe :"), newPassword);
        fields.addRow(2, new Label("Confirmation :"), confirmation);
        dialog.getDialogPane().setContent(fields);
        dialog.showAndWait().filter(save::equals).ifPresent(button -> executer(() ->
                service.changerMotDePasse(MembreSessionManager.getMembreId(),
                        oldPassword.getText(), newPassword.getText(), confirmation.getText()),
                "Mot de passe modifié.", false));
    }

    private void actualiser() {
        try {
            int membreId = MembreSessionManager.getMembreId();
            var subscriptions = service.getAbonnements(membreId);
            var sessions = service.getSeancesDisponibles();
            var bookings = service.getInscriptions(membreId);
            var payments = service.getPaiements(membreId);
            var requests = service.getDemandesAbonnement(membreId);
            subscriptionsTable.setItems(FXCollections.observableArrayList(subscriptions));
            sessionsTable.setItems(FXCollections.observableArrayList(
                    sessions));
            bookingsTable.setItems(FXCollections.observableArrayList(
                    bookings));
            paymentsTable.setItems(FXCollections.observableArrayList(
                    payments));
            requestsTable.setItems(FXCollections.observableArrayList(
                    requests));

            LocalDate today = LocalDate.now();
            abonnementValide = subscriptions.stream().anyMatch(subscription ->
                    "ACTIF".equalsIgnoreCase(subscription.getStatut())
                            && subscription.getDateDebut() != null
                            && subscription.getDateFin() != null
                            && !today.isBefore(subscription.getDateDebut())
                            && !today.isAfter(subscription.getDateFin()));
            long upcomingBookings = bookings.stream()
                    .filter(booking -> "CONFIRMEE".equalsIgnoreCase(booking.statut()))
                    .filter(booking -> booking.date() != null && !booking.date().isBefore(today))
                    .count();
            long pendingRequests = requests.stream()
                    .filter(request -> "EN_ATTENTE".equalsIgnoreCase(request.statut()))
                    .count();

            activeSubscriptionMetric.setText(abonnementValide ? "En cours" : "Aucun");
            bookingMetric.setText(String.valueOf(upcomingBookings));
            paymentMetric.setText(String.valueOf(payments.size()));
            pendingRequestMetric.setText(String.valueOf(pendingRequests));
            bookSessionButton.setDisable(!abonnementValide
                    || sessionsTable.getSelectionModel().getSelectedItem() == null);
            cancelBookingButton.setDisable(
                    bookingsTable.getSelectionModel().getSelectedItem() == null);
            requestSubscriptionButton.setDisable(
                    abonnementValide || pendingRequests > 0
                            || subscriptionTypeComboBox.getValue() == null);
        } catch (RuntimeException e) {
            afficherErreur(e.getMessage());
        }
    }

    private void mettreAJourDisponibiliteDemande() {
        if (pendingRequestMetric == null || activeSubscriptionMetric == null
                || requestSubscriptionButton == null) {
            return;
        }
        boolean hasActiveSubscription = "En cours".equals(activeSubscriptionMetric.getText());
        boolean hasPendingRequest = !"0".equals(pendingRequestMetric.getText());
        requestSubscriptionButton.setDisable(hasActiveSubscription || hasPendingRequest
                || subscriptionTypeComboBox.getValue() == null);
    }

    @FXML
    private void handleLogout() {
        MembreSessionManager.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/gss/gss/fxml/login.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("GSS - Connexion");
            stage.centerOnScreen();
            stage.setResizable(false);
        } catch (IOException e) {
            afficherErreur("Impossible de revenir à la connexion.");
        }
    }

    private void executer(Runnable operation, String success, boolean refresh) {
        try {
            operation.run();
            afficherInformation(success);
            if (refresh) {
                actualiser();
            }
        } catch (RuntimeException e) {
            afficherErreur(e.getMessage());
        }
    }

    private String formatDate(java.time.LocalDate date) {
        return date == null ? "" : date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private void afficherInformation(String message) {
        new Alert(Alert.AlertType.INFORMATION, message).showAndWait();
    }

    private void afficherErreur(String message) {
        new Alert(Alert.AlertType.ERROR,
                message == null ? "Une erreur est survenue." : message).showAndWait();
    }
}
