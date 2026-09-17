package com.gss.gss.dao;

import com.gss.gss.model.Seance;

import java.util.List;

public interface SeanceDAO {

    List<Seance> findAll();

    Seance findById(int id);

    boolean save(Seance seance);

    boolean update(Seance seance);

    boolean delete(int id);

    List<Seance> findByCoachId(int coachId);
}