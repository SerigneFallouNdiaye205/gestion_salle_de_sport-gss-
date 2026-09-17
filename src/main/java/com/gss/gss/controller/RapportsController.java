package com.gss.gss.controller;

import com.gss.gss.model.Abonnement;
import com.gss.gss.model.Membre;
import com.gss.gss.model.Paiement;
import com.gss.gss.model.Seance;
import com.gss.gss.service.AbonnementService;
import com.gss.gss.service.CoachService;
import com.gss.gss.service.MembreService;
import com.gss.gss.service.PaiementService;
import com.gss.gss.service.SeanceService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

public class RapportsController {

    @FXML
    private TextArea reportArea;

    private final MembreService membreService = new MembreService();
    private final AbonnementService abonnementService = new AbonnementService();
    private final PaiementService paiementService = new PaiementService();
    private final CoachService coachService = new CoachService();
    private final SeanceService seanceService = new SeanceService();

    @FXML
    public void initialize() {
        genererRapports();
    }

    @FXML
    private void genererRapports() {
        List<Membre> membres = membreService.findAll();
        List<Abonnement> abonnements = abonnementService.findAll();
        List<Paiement> paiements = paiementService.findAll();
        List<Seance> seances = seanceService.findAll();
        double revenus = paiements.stream()
                .filter(p -> "VALIDE".equalsIgnoreCase(p.getStatut()))
                .mapToDouble(Paiement::getMontant)
                .sum();

        StringBuilder rapport = new StringBuilder();
        rapport.append("RAPPORT DE GESTION - GSS\n")
                .append("Généré le : ").append(LocalDateTime.now()).append("\n\n")
                .append("RAPPORT DES MEMBRES\n")
                .append("Total : ").append(membres.size()).append("\n")
                .append("Actifs : ").append(membres.stream()
                        .filter(m -> "ACTIF".equalsIgnoreCase(m.getStatut())).count()).append("\n")
                .append("Inactifs : ").append(membres.stream()
                        .filter(m -> "INACTIF".equalsIgnoreCase(m.getStatut())).count()).append("\n\n")
                .append("RAPPORT DES ABONNEMENTS\n")
                .append("Total : ").append(abonnements.size()).append("\n")
                .append("Actifs : ").append(abonnements.stream()
                        .filter(a -> "ACTIF".equalsIgnoreCase(a.getStatut())).count()).append("\n")
                .append("Expirés : ").append(abonnements.stream()
                        .filter(a -> "EXPIRE".equalsIgnoreCase(a.getStatut())).count()).append("\n\n")
                .append("RAPPORT FINANCIER\n")
                .append("Paiements enregistrés : ").append(paiements.size()).append("\n")
                .append("Revenus validés : ").append(String.format("%.0f FCFA", revenus)).append("\n\n")
                .append("RAPPORT DES SÉANCES ET DU PLANNING\n")
                .append("Coachs : ").append(coachService.count()).append("\n")
                .append("Séances : ").append(seances.size()).append("\n");

        reportArea.setText(rapport.toString());
    }

    @FXML
    private void exporter() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exporter le rapport");
        chooser.setInitialFileName("rapport-gss.txt");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Rapport texte", "*.txt"));
        File file = chooser.showSaveDialog(reportArea.getScene().getWindow());
        if (file == null) {
            return;
        }
        try {
            Files.writeString(file.toPath(), reportArea.getText(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Impossible d'exporter le rapport : " + e.getMessage());
            alert.showAndWait();
        }
    }
}
