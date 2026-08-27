package com.gss.gss.model;

import java.time.LocalDate;

public class Paiement {
    private int id;
    private int membre_id;
    private int abonnement_id;
    private double montant;
    private LocalDate date_paiement;
    private String  mode_paiement;
    private String statut;
    private String reference;

    public Paiement(){}

    public Paiement(
            int id,
            int membre_id,
            int abonnement_id,
            double montant,
            LocalDate date_paiement,
            String mode_paiement,
            String statut,
            String reference
    ) {
        this.id = id;
        this.membre_id = membre_id;
        this.abonnement_id = abonnement_id;
        this.montant = montant;
        this.date_paiement = date_paiement;
        this.mode_paiement = mode_paiement;
        this.statut = statut;
        this.reference = reference;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getMembre_id() {
        return membre_id;
    }
    public void setMembre_id(int membre_id) {
        this.membre_id = membre_id;
    }

    public int getAbonnement_id() {
        return abonnement_id;
    }
    public void setAbonnement_id(int abonnement_id) {
        this.abonnement_id = abonnement_id;
    }

    public double getMontant() {
        return montant;
    }
    public void setMontant(double montant) {
        this.montant = montant;
    }

    public LocalDate getDate_paiement() {
        return date_paiement;
    }
    public void setDate_paiement(LocalDate date_paiement) {
        this.date_paiement = date_paiement;
    }

    public String getMode_paiement() {
        return mode_paiement;
    }
    public void setMode_paiement(String mode_paiement) {
        this.mode_paiement = mode_paiement;
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
