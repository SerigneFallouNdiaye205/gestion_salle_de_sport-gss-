package com.gss.gss.model;

import java.time.LocalDate;

public class Utilisateur {

    private int id;
    private String username;
    private String password;
    private String type;
    private String statut;
    private LocalDate dateCreation;

    public Utilisateur() {}

    public Utilisateur(String username, String password, String type, String statut) {
        this.username = username;
        this.password = password;
        this.type = type;
        this.statut = statut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }
}