package com.gss.gss.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Seance {

    private int id;
    private int coachId;
    private String nom;
    private LocalDate dateSeance;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String salle;
    private int capacite;

    public Seance() {}

    public Seance(
            int id,
            int coachId,
            String nom,
            LocalDate dateSeance,
            LocalTime heureDebut,
            LocalTime heureFin,
            String salle,
            int capacite
    ) {
        this.id = id;
        this.coachId = coachId;
        this.nom = nom;
        this.dateSeance = dateSeance;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.salle = salle;
        this.capacite = capacite;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getCoachId() {
        return coachId;
    }
    public void setCoachId(int coachId) {
        this.coachId = coachId;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public LocalDate getDateSeance() {
        return dateSeance;
    }
    public void setDateSeance(LocalDate dateSeance) {
        this.dateSeance = dateSeance;
    }

    public LocalTime getHeureDebut() {
        return heureDebut;
    }
    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalTime getHeureFin() {
        return heureFin;
    }
    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
    }

    public String getSalle() {
        return salle;
    }
    public void setSalle(String salle) {
        this.salle = salle;
    }

    public int getCapacite() {
        return capacite;
    }
    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    @Override
    public String toString() {
        return "Seance{" +
                "id=" + id +
                ", coachId=" + coachId +
                ", nom='" + nom + '\'' +
                ", dateSeance=" + dateSeance +
                ", heureDebut=" + heureDebut +
                ", heureFin=" + heureFin +
                ", salle='" + salle + '\'' +
                ", capacite=" + capacite +
                '}';
    }
}