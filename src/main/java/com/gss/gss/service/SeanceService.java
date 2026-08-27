package com.gss.gss.service;

import com.gss.gss.dao.Impl.SeanceDAOImpl;
import com.gss.gss.dao.SeanceDAO;

public class SeanceService {

    private final SeanceDAO seanceDAO;

    public SeanceService() {
        this.seanceDAO = new SeanceDAOImpl();
    }

    public long countAll(){
        return seanceDAO.findAll().size();
    }
}
