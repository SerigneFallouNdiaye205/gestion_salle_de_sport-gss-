package com.gss.gss.model;

public class Administrateur {
    private int id;
    private String niveau_acces;

    public Administrateur(){}

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getNiveau_acces() {
        return niveau_acces;
    }
    public void setNiveau_acces(String niveau_acces) {
        this.niveau_acces = niveau_acces;
    }
}
