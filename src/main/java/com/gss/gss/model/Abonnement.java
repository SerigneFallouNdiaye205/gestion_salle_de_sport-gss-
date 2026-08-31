package com.gss.gss.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Abonnement {

    private int id;
    private int membreId;
    private String type;
    private Double prix;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String statut;

    public Abonnement() {
    }

    public Abonnement(
            int id,
            int membreId,
            String type,
            Double prix,
            LocalDate dateDebut,
            LocalDate dateFin,
            String statut
    ) {
        this.id = id;
        this.membreId = membreId;
        this.type = type;
        this.prix = prix;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    /**
     * Retourne le nombre de jours restants avant expiration.
     */
    public long getJoursRestants() {

        if (dateFin == null) {
            return 0;
        }

        return ChronoUnit.DAYS.between(
                LocalDate.now(),
                dateFin
        );
    }

    /**
     * Vérifie si l'abonnement est expiré.
     */
    public boolean estExpire() {

        if (dateFin == null) {
            return false;
        }

        return dateFin.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return "Abonnement{" +
                "id=" + id +
                ", membreId=" + membreId +
                ", type='" + type + '\'' +
                ", prix=" + prix +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", statut='" + statut + '\'' +
                '}';
    }
}