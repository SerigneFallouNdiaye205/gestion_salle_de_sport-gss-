package com.gss.gss.model;

import java.time.LocalDate;

public class Membre {

    private int id;
    private String nom;
    private String prenom;
    private String sexe;
    private String telephone;
    private String email;
    private String adresse;
    private LocalDate dateNaissance;
    private LocalDate dateInscription;
    private String statut;

    public Membre() {}

    public Membre(String nom,
                  String prenom,
                  String telephone,
                  String email,
                  String adresse,
                  LocalDate dateNaissance,
                  LocalDate dateInscription,
                  String statut) {

        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
        this.dateNaissance = dateNaissance;
        this.dateInscription = dateInscription;
        this.statut = statut;
    }

    public Membre(int id,
                  String nom,
                  String prenom,
                  String telephone,
                  String email,
                  String adresse,
                  LocalDate dateNaissance,
                  LocalDate dateInscription,
                  String statut) {

        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
        this.dateNaissance = dateNaissance;
        this.dateInscription = dateInscription;
        this.statut = statut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return prenom + " " + nom;
    }
}
