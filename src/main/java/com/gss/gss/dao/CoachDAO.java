package com.gss.gss.dao;

import com.gss.gss.model.Coach;

import java.util.List;

public interface CoachDAO {

    List<Coach> findAll();

    Coach findById(int id);

    List<Coach> search(String recherche);

    boolean save(Coach coach);

    boolean update(Coach coach);

    boolean delete(int id);
}