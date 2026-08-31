package com.gss.gss.service;

import com.gss.gss.dao.CoachDAO;
import com.gss.gss.dao.Impl.CoachDAOImpl;
import com.gss.gss.model.Coach;

import java.util.List;

public class CoachService {

    private final CoachDAO coachDAO;

    public CoachService() {
        this.coachDAO = new CoachDAOImpl();
    }

    public List<Coach> findAll() {
        return coachDAO.findAll();
    }

    public Coach findById(int id) {
        return coachDAO.findById(id);
    }

    public List<Coach> search(String recherche) {

        if (recherche == null ||
                recherche.trim().isEmpty()) {

            return findAll();
        }

        return coachDAO.search(
                recherche.trim()
        );
    }

    public boolean save(Coach coach) {

        if (coach == null) {
            return false;
        }

        return coachDAO.save(coach);
    }

    public boolean update(Coach coach) {

        if (coach == null) {
            return false;
        }

        return coachDAO.update(coach);
    }

    public boolean delete(int id) {
        return coachDAO.delete(id);
    }

    public long count() {
        return coachDAO.findAll().size();
    }
}