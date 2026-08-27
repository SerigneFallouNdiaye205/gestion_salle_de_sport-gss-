package com.gss.gss.model;

import java.time.LocalDate;

public class InscriptionSeance {
    private int id;
    private int membre_id;
    private int seance_id;
    private LocalDate date_inscription;
    private String statut;

    public InscriptionSeance(){}

    public InscriptionSeance(
            int id,
            int membre_id,
            int seance_id,
            LocalDate date_inscription,
            String statut
    ) {
        this.id = id;
        this.membre_id = membre_id;
        this.seance_id = seance_id;
        this.date_inscription = date_inscription;
        this.statut = statut;
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

    public int getSeance_id() {
        return seance_id;
    }
    public void setSeance_id(int seance_id) {
        this.seance_id = seance_id;
    }

    public LocalDate getDate_inscription() {
        return date_inscription;
    }
    public void setDate_inscription(LocalDate date_inscription) {
        this.date_inscription = date_inscription;
    }

    public String getStatut() {
        return statut;
    }
    public void setStatut(String statut) {
        this.statut = statut;
    }

}
