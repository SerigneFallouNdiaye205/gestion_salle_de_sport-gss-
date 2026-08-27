package com.gss.gss.service;

import com.gss.gss.dao.AbonnementDAO;
import com.gss.gss.dao.Impl.AbonnementsDAOImpl;
import com.gss.gss.dao.SeanceDAO;

public class AbonnementService {

    private final AbonnementDAO abonnementDAO;

    public AbonnementService() {
        this.abonnementDAO = new AbonnementsDAOImpl();
    }

    public long countActive(){
        return abonnementDAO.findAll().size();
    }

}
