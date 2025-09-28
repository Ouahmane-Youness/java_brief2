package com.paymentManagement.service.impl;

import com.paymentManagement.model.entity.Departement;
import com.paymentManagement.repository.interfaces.AgentRepository;
import com.paymentManagement.repository.interfaces.DepartementRepository;
import com.paymentManagement.service.interfaces.DepartementService;

import java.util.List;

public class DepartementServiceImpl implements DepartementService {
    private final DepartementRepository departementRepository;
    private final AgentRepository agentRepository;

    public DepartementServiceImpl(DepartementRepository departementRepository, AgentRepository agentRepository) {
        this.departementRepository = departementRepository;
        this.agentRepository = agentRepository;
    }

    @Override
    public Departement createDepartement(String nom) {
        if(nom == null || nom.trim().isEmpty())
        {
            throw new IllegalArgumentException("Le nom du département ne peut pas être vide");
        }

        if(!isDepartementNameAvailable(nom))
        {
            throw new IllegalArgumentException("Un département avec ce nom existe déjà: " + nom);
        }
        String cleanName = nom.trim();
        if(cleanName.length()<2)
        {
            throw new IllegalArgumentException("Le nom du département doit contenir au moins 2 caractères");

        }

        Departement departement = new Departement(nom);
        this.departementRepository.save(departement);
        System.out.println("✓ Département créé: " + cleanName);
        return departement;

    }

    @Override
    public boolean isDepartementNameAvailable(String nom) {
        if(nom == null || nom.trim().isEmpty())
        {
            return false;
        }
        Departement dep = this.departementRepository.findByNom(nom.trim())
        return dep == null;
    }

    @Override
    public Departement findDepartementById(Integer id) {
        if(id == null || id < 1)
        {
            throw new IllegalArgumentException("L'ID du département doit être positif");
        }
        return this.departementRepository.findById(id);
    }

    @Override
    public Departement findDepartementByNom(String nom) {
        if(nom == null || nom.trim().isEmpty())
        {
            throw new IllegalArgumentException("Le nom du département ne peut pas être vide");

        }
        return this.departementRepository.findByNom(nom);
    }

    @Override
    public List<Departement> getAllDepartements() {
        return departementRepository.findAll();
    }

    @Override
    public void updateDepartement(Departement departement) {
        if (departement == null || departement.getIdDepartement() == null) {
            throw new IllegalArgumentException("Département invalide");
        }

        Departement existing = departementRepository.findById(departement.getIdDepartement());
        if (existing == null) {
            throw new IllegalArgumentException("Département non trouvé avec l'ID: " + departement.getIdDepartement());
        }

        if (!existing.getNom().equals(departement.getNom())) {
            if (!isDepartementNameAvailable(departement.getNom())) {
                throw new IllegalArgumentException("Un autre département utilise déjà ce nom: " + departement.getNom());
            }
        }

        departement.setNom(departement.getNom().trim());

        departementRepository.update(departement);
        System.out.println("✓ Département mis à jour: " + departement.getNom());
    }

    @Override
    public boolean deleteDepartement(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID du département doit être positif");
        }

        Departement departement = departementRepository.findById(id);
        if (departement == null) {
            System.out.println("Département non trouvé avec l'ID: " + id);
            return false;
        }

        List<Agent> agents = agentRepository.findByDepartementId(id);
        if (!agents.isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer le département '" + departement.getNom() +
                    "' car il contient " + agents.size() + " agent(s). Réassignez d'abord les agents.");
        }

        boolean deleted = departementRepository.deleteById(id);
        if (deleted) {
            System.out.println("✓ Département supprimé: " + departement.getNom());
        }
        return deleted;
    }

    @Override
    public boolean assignManager(Integer departementId, Integer managerId) {
        if (departementId == null || managerId == null) {
            throw new IllegalArgumentException("L'ID du département et du manager sont requis");
        }
        Departement departement = departementRepository.findById(departementId);

        Agent agent = agentRepository.findById(managerId);
        if (agent == null) {
            throw new IllegalArgumentException("Agent non trouvé avec l'ID: " + managerId);
        }
        if(!canAgentManageDepartement(agent))
        {
            throw new IllegalArgumentException("L'agent " + agent.getNomComplet() +
                    " (" + agent.getTypeAgent() + ") ne peut pas gérer un département");
        }
        departement.setResponsableId(managerId);
        departementRepository.update(departement);
        System.out.println("✓ " + agent.getNomComplet() + " assigné comme responsable de " + departement.getNom());
        return true;

    }


    @Override
    public boolean canAgentManageDepartement(Agent agent) {
        return agent.getTypeAgent() == TypeAgent.RESPONSABLE_DEPARTEMENT ||
                agent.getTypeAgent() == TypeAgent.DIRECTEUR;

    }

    @Override
    public List<Agent> getAgentsInDepartement(Integer departementId) {
        if (departementId == null) {
            throw new IllegalArgumentException("L'ID du département est requis");
        }

        return agentRepository.findByDepartementId(departementId);
    }

    @Override
    public int getAgentCount(Integer departementId) {
        List<Agent> agents = getAgentsInDepartement(departementId);
        return agents.size();
    }
}
