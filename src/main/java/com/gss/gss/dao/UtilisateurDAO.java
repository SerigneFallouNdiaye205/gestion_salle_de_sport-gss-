package com.gss.gss.dao;

import com.gss.gss.model.Utilisateur;

import java.util.List;
import java.util.Optional;

public interface UtilisateurDAO {

    Optional<Utilisateur> findByUsername(String username);

    Optional<Utilisateur> findById(int id);

    List<Utilisateur> findAll();

    List<Utilisateur> search(String recherche);

    List<Utilisateur> findByStatut(String statut);

    List<Utilisateur> findByType(String type);

    boolean existsByUsername(String username);

    boolean save(Utilisateur utilisateur);

    boolean update(Utilisateur utilisateur);

    boolean delete(int id);
}