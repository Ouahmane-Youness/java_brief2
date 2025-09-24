package com.paymentManagement.repository.interfaces;

import com.paymentManagement.model.entity.Departement;
import java.util.List;

public interface DepartementRepository {

    void save(Departement departement);
    Departement findById(Integer id);
    List<Departement> findAll();
    Departement findByNom(String nom);
    void update(Departement departement);
    boolean deleteById(Integer id);
    boolean existsById(Integer id);

}
