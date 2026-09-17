package com.gss.gss.dao;

import com.gss.gss.model.Membre;

import java.util.List;

public interface MembreDAO {

    boolean save(Membre membre);

    boolean update(Membre membre);

    boolean delete(int id);

    Membre findById(int id);

    List<Membre> findAll();

    List<Membre> rechercher(String recherche);

    List<Membre> findByStatut(String statut);

    List<Membre> findByCoachId(int coachId);
}