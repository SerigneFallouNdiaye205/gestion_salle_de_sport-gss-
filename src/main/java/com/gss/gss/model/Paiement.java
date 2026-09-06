package com.gss.gss.model;

import java.time.LocalDateTime;

public class Paiement {

    private int id;
    private int membreId;
    private int abonnementId;
    private double montant;
    private LocalDateTime datePaiement;
    private String modePaiement;
    private String statut;
    private String reference;

    public Paiement() {
    }

    public Paiement(
            int id,
            int membreId,
            int abonnementId,
            double montant,
            LocalDateTime datePaiement,
            String modePaiement,
            String statut,
            String reference
    ) {
        this.id = id;
        this.membreId = membreId;
        this.abonnementId = abonnementId;
        this.montant = montant;
        this.datePaiement = datePaiement;
        this.modePaiement = modePaiement;
        this.statut = statut;
        this.reference = reference;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMembreId() {
        return membreId;
    }

    public void setMembreId(int membreId) {
        this.membreId = membreId;
    }

    public int getAbonnementId() {
        return abonnementId;
    }

    public void setAbonnementId(int abonnementId) {
        this.abonnementId = abonnementId;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}