package com.gss.gss.controller;

import com.gss.gss.model.Utilisateur;
import com.gss.gss.security.SessionManager;
import com.gss.gss.service.MembreService;
import com.gss.gss.service.AbonnementService;
import com.gss.gss.service.PaiementService;
import com.gss.gss.service.SeanceService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HomeController {

    @FXML
    private Label membersCountLabel;
    @FXML
    private Label activeSubscriptionsLabel;
    @FXML
    private Label paymentsCountLabel;
    @FXML
    private Label sessionsCountLabel;
    @FXML
    private HBox countHBox;
    @FXML
    private Label welcomeLabel;
    @FXML
    private StackPane contentPane;
    @FXML
    private Label localDateTime;
    @FXML
    private Label username;

    private final MembreService membreService =
            new MembreService();

    private final AbonnementService abonnementService =
            new AbonnementService();

    private final PaiementService paiementService =
            new PaiementService();

    private final SeanceService seanceService =
            new SeanceService();

    @FXML
    public void initialize() {

        // Vérifier qu'un utilisateur est connecté
        if (!SessionManager.isLoggedIn()) {
            return;
        }

        // Récupérer l'utilisateur connecté
        Utilisateur utilisateur =
                SessionManager.getCurrentUser();

        if (utilisateur == null) {
            return;
        }

        username.setText(
                utilisateur.getUsername()
        );
        welcomeLabel.setText("Bienvenue, "+utilisateur.getUsername());

        afficherDateEtHeure();
        chargerStatistiques();
    }

    private void afficherDateEtHeure() {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm"
                );

        localDateTime.setText(
                LocalDateTime.now().format(formatter)
        );
    }
    private void chargerStatistiques() {

        try {
            long nombreMembres =
                    membreService.countAll();

            membersCountLabel.setText(
                    String.valueOf(nombreMembres)
            );

            long abonnementsActifs =
                    abonnementService.countActive();

            activeSubscriptionsLabel.setText(
                    String.valueOf(abonnementsActifs)
            );

            long nombrePaiements =
                    paiementService.countAll();

            paymentsCountLabel.setText(
                    String.valueOf(nombrePaiements)
            );

            long nombreSeances =
                    seanceService.countAll();

            sessionsCountLabel.setText(
                    String.valueOf(nombreSeances)
            );


        } catch (Exception e) {

            e.printStackTrace();

            membersCountLabel.setText("0");
            activeSubscriptionsLabel.setText("0");
            paymentsCountLabel.setText("0");
            sessionsCountLabel.setText("0");
        }
    }
}