package com.paymentManagement.service.interfaces;

import com.paymentManagement.model.entity.Agent;
import com.paymentManagement.model.entity.Departement;

import java.util.List;

public interface DepartementService {
    Departement createDepartement(String nom);
    Departement findDepartementById(Integer id);
    Departement findDepartementByNom(String nom);
    List<Departement> getAllDepartements();
    void updateDepartement(Departement departement);
    boolean deleteDepartement(Integer id);

    // Business logic operations
    boolean assignManager(Integer departementId, Integer managerId);
    //boolean removeManager(Integer departementId);
    List<Agent> getAgentsInDepartement(Integer departementId);
    int getAgentCount(Integer departementId);
    boolean isDepartementNameAvailable(String nom);

    boolean canAgentManageDepartement(Agent agent);
}
