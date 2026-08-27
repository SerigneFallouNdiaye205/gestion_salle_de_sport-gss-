package com.gss.gss.model;

public class Coach {

    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String specialite;
    private Double salaire;
    private String disponibilite;

    public Coach(){}

    public Coach(
            int id,
            String nom,
            String prenom,
            String telephone,
            String email,
            String specialite,
            Double salaire,
            String disponibilite
    ) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.specialite = specialite;
        this.salaire = salaire;
        this.disponibilite = disponibilite;
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

    public String getSpecialite() {
        return specialite;
    }
    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public Double getSalaire() {
        return salaire;
    }
    public void setSalaire(Double salaire) {
        this.salaire = salaire;
    }

    public String getDisponibilite() {
        return disponibilite;
    }
    public void setDisponibilite(String disponibilite) {
        this.disponibilite = disponibilite;
    }

}
