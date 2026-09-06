package com.gss.gss.util;

import com.gss.gss.model.Paiement;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Font;
import com.lowagie.text.Element;

import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class RecuPaiement {

    private RecuPaiement() {
    }

    public static File generer(Paiement paiement)
            throws IOException {

        String nomFichier =
                "recu_" +
                        paiement.getReference() +
                        ".pdf";

        File fichier = new File(nomFichier);

        Document document = new Document();

        try {

            PdfWriter.getInstance(
                    document,
                    new FileOutputStream(fichier)
            );

            document.open();

            Font titreFont =
                    new Font(
                            Font.HELVETICA,
                            18,
                            Font.BOLD
                    );

            Paragraph titre =
                    new Paragraph(
                            "SALLE DE SPORT",
                            titreFont
                    );

            titre.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(titre);

            Paragraph recu =
                    new Paragraph(
                            "REÇU DE PAIEMENT"
                    );

            recu.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(recu);

            document.add(
                    new Paragraph(
                            "------------------------------------------"
                    )
            );

            document.add(
                    new Paragraph(
                            "Référence : " +
                                    paiement.getReference()
                    )
            );

            document.add(
                    new Paragraph(
                            "Membre ID : " +
                                    paiement.getMembreId()
                    )
            );

            document.add(
                    new Paragraph(
                            "Abonnement ID : " +
                                    paiement.getAbonnementId()
                    )
            );

            document.add(
                    new Paragraph(
                            "Date : " +
                                    paiement.getDatePaiement()
                    )
            );

            document.add(
                    new Paragraph(
                            "Mode de paiement : " +
                                    paiement.getModePaiement()
                    )
            );

            document.add(
                    new Paragraph(
                            "Statut : " +
                                    paiement.getStatut()
                    )
            );

            document.add(
                    new Paragraph(
                            "------------------------------------------"
                    )
            );

            Font montantFont =
                    new Font(
                            Font.HELVETICA,
                            16,
                            Font.BOLD
                    );

            Paragraph montant =
                    new Paragraph(
                            "Montant : " +
                                    String.format(
                                            "%.2f FCFA",
                                            paiement.getMontant()
                                    ),
                            montantFont
                    );

            montant.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(montant);

            document.add(
                    new Paragraph(
                            "------------------------------------------"
                    )
            );

            Paragraph merci =
                    new Paragraph(
                            "Merci pour votre confiance."
                    );

            merci.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(merci);

        } finally {

            document.close();
        }

        return fichier;
    }
}