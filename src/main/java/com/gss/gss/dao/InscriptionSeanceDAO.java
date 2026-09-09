package com.gss.gss.dao;

import com.gss.gss.model.InscriptionSeance;
import java.util.List;

public interface InscriptionSeanceDAO {
    boolean save(InscriptionSeance inscription);
    boolean update(InscriptionSeance inscription);
    boolean delete(int id);
    InscriptionSeance getById(int id);
    List<InscriptionSeance> getAll();
}
