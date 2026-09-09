package com.gss.gss.controller;

import com.gss.gss.model.Paiement;
import com.gss.gss.model.Utilisateur;
import com.gss.gss.security.SessionManager;
import com.gss.gss.service.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class HomeController {

    @FXML private Label membersCountLabel;
    @FXML private Label activeSubscriptionsLabel;
    @FXML private Label paymentsCountLabel;
    @FXML private Label sessionsCountLabel;
    @FXML private Label utilisateursCountLabel;
    @FXML private Label totalPaymentAmountLabel;


    @FXML private Label username;
    @FXML private Label localDateTime;

    @FXML private HBox countHBox;
    @FXML private StackPane contentPane;
    @FXML private LineChart<String, Number> attendanceChart;

    @FXML private VBox planningContainer;
    @FXML private VBox recentMembersContainer;
    @FXML private VBox recentSubscriptionsContainer;
    @FXML private VBox recentPaymentsContainer;
    @FXML private VBox notificationsContainer;

    private final MembreService membreService = new MembreService();
    private final AbonnementService abonnementService = new AbonnementService();
    private final PaiementService paiementService = new PaiementService();
    private final SeanceService seanceService = new SeanceService();
    private final UtilisateurService utilisateurService = new UtilisateurService();
    private final DashboardService dashboardService = new DashboardService();

    private Timeline clock;
    private Timeline notificationsRefresh;

    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter TIME =
            DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        if (!SessionManager.isLoggedIn()) {
            setEmptyDashboard("Aucune session utilisateur.");
            return;
        }

        Utilisateur utilisateur = SessionManager.getCurrentUser();
        if (utilisateur == null) {
            setEmptyDashboard("Utilisateur connecté introuvable.");
            return;
        }

        username.setText(utilisateur.getUsername());

        demarrerHorloge();
        demarrerActualisationNotifications();
        chargerTout();
    }

    /**
     * Recharge toutes les données du dashboard depuis MySQL.
     */
    private void chargerTout() {
        chargerStatistiques();
        chargerGraphique();
        chargerPlanning();
        chargerMembresRecents();
        chargerAbonnementsRecents();
        chargerPaiementsRecents();
        chargerNotifications();
    }

    private void chargerStatistiques() {
        try {
            // Les quatre cartes sont alimentées directement par les services JDBC.
            membersCountLabel.setText(String.valueOf(membreService.countAll()));

            // On corrige les statuts expirés avant de compter les actifs.
            abonnementService.mettreAJourStatutsExpires();
            activeSubscriptionsLabel.setText(
                    String.valueOf(abonnementService.countActive())
            );

            paymentsCountLabel.setText(String.valueOf(paiementService.countAll()));
            sessionsCountLabel.setText(String.valueOf(seanceService.countAll()));
            utilisateursCountLabel.setText(String.valueOf(utilisateurService.countAll()));

        } catch (Exception e) {
            e.printStackTrace();
            membersCountLabel.setText("0");
            activeSubscriptionsLabel.setText("0");
            paymentsCountLabel.setText("0");
            sessionsCountLabel.setText("0");
            utilisateursCountLabel.setText("0");
            totalPaymentAmountLabel.setText("0");
        }
    }

    private void chargerGraphique() {
        attendanceChart.getData().clear();

        try {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Inscriptions confirmées");

            for (Map.Entry<LocalDate, Integer> entry :
                    dashboardService.getAttendanceLast7Days().entrySet()) {

                series.getData().add(new XYChart.Data<>(
                        entry.getKey().format(
                                DateTimeFormatter.ofPattern("dd/MM")
                        ),
                        entry.getValue()
                ));
            }

            attendanceChart.getData().add(series);

        } catch (Exception e) {
            e.printStackTrace();
            Label error = new Label("Fréquentation indisponible.");
            error.setStyle("-fx-text-fill: #64748b;");
        }
    }

    private void chargerPlanning() {
        planningContainer.getChildren().clear();

        try {
            var planning = dashboardService.getPlanningToday();

            if (planning.isEmpty()) {
                planningContainer.getChildren().add(
                        ligneVide("Aucune séance programmée aujourd'hui.")
                );
                return;
            }

            for (DashboardService.PlanningItem item : planning) {
                HBox row = new HBox(15);
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                row.setStyle(styleRow());

                Label heure = label(
                        formatTime(item.debut()) + " - " + formatTime(item.fin()),
                        "#8b5cf6", "13px", true
                );
                heure.setMinWidth(105);

                VBox info = new VBox(3);
                Label nom = label(item.nom(), "#0f172a", "14px", true);
                String coach = item.coach() == null || item.coach().isBlank()
                        ? "Coach non renseigné"
                        : "Coach : " + item.coach();
                Label details = label(
                        coach + "  •  " + safe(item.salle()) +
                                "  •  Capacité : " + item.capacite(),
                        "#64748b", "12px", false
                );
                info.getChildren().addAll(nom, details);

                row.getChildren().addAll(heure, info);
                planningContainer.getChildren().add(row);
            }

        } catch (Exception e) {
            planningContainer.getChildren().add(
                    ligneErreur("Impossible de charger le planning.")
            );
            e.printStackTrace();
        }
    }

    private void chargerMembresRecents() {
        recentMembersContainer.getChildren().clear();

        try {
            var membres = dashboardService.getRecentMembers(5);

            if (membres.isEmpty()) {
                recentMembersContainer.getChildren().add(
                        ligneVide("Aucun membre enregistré.")
                );
                return;
            }

            for (DashboardService.RecentMember m : membres) {
                HBox row = new HBox(12);
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                row.setStyle(styleRow());

                VBox info = new VBox(3);
                info.setPrefWidth(230);
                info.getChildren().addAll(
                        label(m.nomComplet(), "#0f172a", "14px", true),
                        label(
                                safe(m.telephone()) + "  •  " +
                                formatDate(m.dateInscription()),
                                "#64748b", "12px", false
                        )
                );

                Label statut = badge(
                        m.statut(),
                        "ACTIF".equalsIgnoreCase(m.statut())
                );

                row.getChildren().addAll(info, statut);
                recentMembersContainer.getChildren().add(row);
            }

        } catch (Exception e) {
            recentMembersContainer.getChildren().add(
                    ligneErreur("Impossible de charger les membres récents.")
            );
            e.printStackTrace();
        }
    }

    private void chargerAbonnementsRecents() {
        recentSubscriptionsContainer.getChildren().clear();

        try {
            var abonnements = dashboardService.getRecentSubscriptions(5);

            if (abonnements.isEmpty()) {
                recentSubscriptionsContainer.getChildren().add(
                        ligneVide("Aucun abonnement enregistré.")
                );
                return;
            }

            for (DashboardService.RecentSubscription a : abonnements) {
                HBox row = new HBox(12);
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                row.setStyle(styleRow());

                VBox info = new VBox(3);
                info.setPrefWidth(250);
                info.getChildren().addAll(
                        label(
                                safe(a.membre()) + "  •  " + formatType(a.type()),
                                "#0f172a", "14px", true
                        ),
                        label(
                                formatDate(a.debut()) + " → " +
                                formatDate(a.fin()) + "  •  " +
                                formatMoney(a.prix()),
                                "#64748b", "12px", false
                        )
                );

                row.getChildren().addAll(info, badge(
                        a.statut(),
                        "ACTIF".equalsIgnoreCase(a.statut())
                ));

                recentSubscriptionsContainer.getChildren().add(row);
            }

        } catch (Exception e) {
            recentSubscriptionsContainer.getChildren().add(
                    ligneErreur("Impossible de charger les abonnements récents.")
            );
            e.printStackTrace();
        }
    }

    private void chargerPaiementsRecents() {
        recentPaymentsContainer.getChildren().clear();

        try {
            var paiements = dashboardService.getRecentPayments(5);

            if (paiements.isEmpty()) {
                recentPaymentsContainer.getChildren().add(
                        ligneVide("Aucun paiement enregistré.")
                );
                return;
            }

            for (DashboardService.RecentPayment p : paiements) {
                HBox row = new HBox(12);
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                row.setStyle(styleRow());

                VBox info = new VBox(3);
                info.setPrefWidth(260);
                info.getChildren().addAll(
                        label(
                                safe(p.membre()) + "  •  " +
                                formatMoney(p.montant()),
                                "#0f172a", "14px", true
                        ),
                        label(
                                safe(p.mode()) + "  •  " +
                                formatDateTime(p.date()),
                                "#64748b", "12px", false
                        )
                );

                row.getChildren().addAll(
                        info,
                        badge(p.statut(), "VALIDE".equalsIgnoreCase(p.statut()))
                );

                recentPaymentsContainer.getChildren().add(row);
            }

        } catch (Exception e) {
            recentPaymentsContainer.getChildren().add(
                    ligneErreur("Impossible de charger les paiements récents.")
            );
            e.printStackTrace();
        }
    }

    private void chargerNotifications() {
        notificationsContainer.getChildren().clear();

        try {
            var notifications = dashboardService.getNotifications(7);

            for (DashboardService.NotificationItem n : notifications) {
                HBox row = new HBox(12);
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                row.setStyle(
                        "-fx-background-color: #f8fafc;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 12;"
                );

                Label niveau = badge(
                        n.niveau(),
                        "OK".equalsIgnoreCase(n.niveau())
                                || "ACTIVITE".equalsIgnoreCase(n.niveau())
                );

                VBox info = new VBox(3);
                info.getChildren().addAll(
                        label(n.titre(), "#0f172a", "13px", true),
                        label(n.detail(), "#64748b", "12px", false)
                );

                row.getChildren().addAll(niveau, info);
                notificationsContainer.getChildren().add(row);
            }

        } catch (Exception e) {
            notificationsContainer.getChildren().add(
                    ligneErreur("Impossible de charger les notifications.")
            );
            e.printStackTrace();
        }
    }

    private void demarrerHorloge() {
        localDateTime.setText(LocalDateTime.now().format(DATE_TIME));

        clock = new Timeline(new KeyFrame(
                Duration.seconds(1),
                event -> localDateTime.setText(
                        LocalDateTime.now().format(DATE_TIME)
                )
        ));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    private void demarrerActualisationNotifications() {
        notificationsRefresh = new Timeline(new KeyFrame(
                Duration.seconds(30),
                event -> chargerNotifications()
        ));
        notificationsRefresh.setCycleCount(Timeline.INDEFINITE);
        notificationsRefresh.play();
    }

    private Label label(String text, String color, String size, boolean bold) {
        Label label = new Label(safe(text));
        label.setStyle(
                "-fx-text-fill: " + color + ";" +
                "-fx-font-size: " + size + ";" +
                (bold ? "-fx-font-weight: bold;" : "")
        );
        return label;
    }

    private Label badge(String text, boolean positive) {
        Label label = new Label(safe(text));
        label.setStyle(
                "-fx-background-color: " +
                        (positive ? "#dcfce7" : "#fee2e2") + ";" +
                "-fx-text-fill: " +
                        (positive ? "#166534" : "#991b1b") + ";" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 5 10;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;"
        );
        return label;
    }

    private Label ligneVide(String text) {
        return label(text, "#64748b", "13px", false);
    }

    private Label ligneErreur(String text) {
        return label(text, "#b91c1c", "13px", false);
    }

    private String styleRow() {
        return "-fx-background-color: #f8fafc;" +
               "-fx-background-radius: 10;" +
               "-fx-padding: 11;";
    }

    private String formatDate(LocalDate date) {
        return date == null ? "-" : date.format(DATE);
    }

    private String formatDateTime(LocalDateTime date) {
        return date == null ? "-" : date.format(DATE_TIME);
    }

    private String formatTime(LocalTime time) {
        return time == null ? "--:--" : time.format(TIME);
    }

    private String formatMoney(double amount) {
        return String.format(Locale.FRANCE, "%,.0f FCFA", amount)
                .replace('\u202f', ' ');
    }

    private String formatType(String type) {
        if (type == null) return "-";
        return type.replace('_', ' ');
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private void setEmptyDashboard(String message) {
        if (membersCountLabel != null) membersCountLabel.setText("0");
        if (activeSubscriptionsLabel != null) activeSubscriptionsLabel.setText("0");
        if (paymentsCountLabel != null) paymentsCountLabel.setText("0");
        if (sessionsCountLabel != null) sessionsCountLabel.setText("0");
    }

    public void stopClock() {
        if (clock != null) {
            clock.stop();
        }
        if (notificationsRefresh != null) {
            notificationsRefresh.stop();
        }
    }
}
