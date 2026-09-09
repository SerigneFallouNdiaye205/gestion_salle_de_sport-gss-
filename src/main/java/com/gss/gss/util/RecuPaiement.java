package com.gss.gss.util;

import com.gss.gss.dao.Impl.AbonnementsDAOImpl;
import com.gss.gss.dao.Impl.MembreDAOImpl;
import com.gss.gss.model.Abonnement;
import com.gss.gss.model.Membre;
import com.gss.gss.model.Paiement;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class RecuPaiement {

    private RecuPaiement() {}

    public static File generer(Paiement paiement) throws IOException {

        String reference = paiement.getReference() == null
                || paiement.getReference().isBlank()
                ? "PAY-" + paiement.getId()
                : paiement.getReference();

        File fichier = new File("recu_" + reference + ".pdf");

        Membre membre = null;
        Abonnement abonnement = null;

        try {
            membre = new MembreDAOImpl().findById(paiement.getMembreId());
        } catch (RuntimeException ignored) {
            // Le reçu reste générable même si les informations complémentaires
            // ne peuvent pas être relues depuis la base.
        }

        try {
            abonnement = new AbonnementsDAOImpl()
                    .findById(paiement.getAbonnementId())
                    .orElse(null);
        } catch (RuntimeException ignored) {
            // Même principe : les identifiants restent disponibles.
        }

        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(fichier));
            document.open();

            Font titreFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font montantFont = new Font(Font.HELVETICA, 17, Font.BOLD);

            addCentered(document, "SALLE DE SPORT", titreFont);
            addCentered(document, "REÇU DE PAIEMENT", new Font(Font.HELVETICA, 13, Font.BOLD));
            document.add(new Paragraph(" "));

            addCentered(document, "Référence : " + reference, new Font(Font.HELVETICA, 11, Font.BOLD));
            document.add(new Paragraph(" "));

            String nomMembre = membre != null
                    ? membre.getPrenom() + " " + membre.getNom()
                    : "Membre #" + paiement.getMembreId();

            addCentered(document, "Membre : " + nomMembre);
            addCentered(document, "membre_id : " + paiement.getMembreId());

            if (abonnement != null) {
                addCentered(document, "Abonnement : " + formatType(abonnement.getType()));
                addCentered(document, "Période : "
                        + formatDate(abonnement.getDateDebut())
                        + " → "
                        + formatDate(abonnement.getDateFin()));
            } else {
                addCentered(document, "abonnement_id : " + paiement.getAbonnementId());
            }

            if (paiement.getDatePaiement() != null) {
                addCentered(document, "Date de paiement : "
                        + paiement.getDatePaiement().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }

            addCentered(document, "Mode : " + formatMode(paiement.getModePaiement()));
            addCentered(document, "Statut : " + paiement.getStatut());

            document.add(new Paragraph(" "));
            addCentered(document, "────────────────────────────");
            addCentered(document, "MONTANT PAYÉ", new Font(Font.HELVETICA, 11, Font.BOLD));
            addCentered(document, formatMontant(paiement.getMontant()) + " FCFA", montantFont);
            addCentered(document, "────────────────────────────");
            document.add(new Paragraph(" "));
            addCentered(document, "Merci pour votre confiance.");

        } finally {
            document.close();
        }

        return fichier;
    }

    private static void addCentered(Document document, String text) throws IOException {
        addCentered(document, text, new Font(Font.HELVETICA, 11));
    }

    private static void addCentered(Document document, String text, Font font) throws IOException {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
    }

    private static String formatType(String type) {
        if (type == null) return "";
        return switch (type) {
            case "JOURNALIER" -> "Journalier";
            case "HEBDOMADAIRE" -> "Hebdomadaire";
            case "MENSUEL" -> "Mensuel";
            case "TRIMESTRIEL" -> "Trimestriel";
            case "SEMESTRIEL" -> "Semestriel";
            case "ANNUEL" -> "Annuel";
            default -> type;
        };
    }

    private static String formatMode(String mode) {
        if (mode == null) return "";
        return switch (mode) {
            case "ESPECES" -> "Espèces";
            case "WAVE" -> "WAVE";
            case "ORANGE_MONEY" -> "Orange Money";
            case "CARTE_BANCAIRE" -> "Carte bancaire";
            default -> mode;
        };
    }

    private static String formatDate(LocalDate date) {
        return date == null ? "" : date.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );
    }

    private static String formatMontant(double montant) {
        DecimalFormat format = new DecimalFormat("#,##0.00");
        return format.format(montant);
    }
}

