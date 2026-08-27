package com.gss.gss.service;

import com.gss.gss.dao.Impl.PaiementDAOImpl;
import com.gss.gss.dao.PaiementDAO;

public class PaiementService {

    private final PaiementDAO paiementDAO;

    public PaiementService(){
        this.paiementDAO = new PaiementDAOImpl();
    }
    public long countAll(){
        return paiementDAO.findAll().size();
    }
}
