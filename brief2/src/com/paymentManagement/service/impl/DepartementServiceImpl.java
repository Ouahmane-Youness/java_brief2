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


}
