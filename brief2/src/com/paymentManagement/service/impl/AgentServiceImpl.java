package com.paymentManagement.service.impl;

import com.paymentManagement.model.entity.Agent;
import com.paymentManagement.model.entity.Departement;
import com.paymentManagement.model.enums.TypeAgent;
import com.paymentManagement.repository.imp.AgentRepositoryImpl;
import com.paymentManagement.repository.imp.DepartementRepositoryImpl;
import com.paymentManagement.repository.interfaces.AgentRepository;
import com.paymentManagement.repository.interfaces.DepartementRepository;
import com.paymentManagement.service.interfaces.AgentService;

import java.util.List;
import java.util.stream.Collectors;

public class AgentServiceImpl implements AgentService {
    private final AgentRepository agentRepository;
    private final DepartementRepository departementRepository;

    public AgentServiceImpl() {
        this.agentRepository = new AgentRepositoryImpl();
        this.departementRepository = new DepartementRepositoryImpl();
    }

    @Override
    public Agent createAgent(String nom, String prenom, String email, String motDePasse, TypeAgent typeAgent) {
        validateAgentData(nom, prenom, email, motDePasse, typeAgent);

        if (!isEmailAvailable(email)) {
            throw new IllegalArgumentException("Cette adresse email est déjà utilisée: " + email);
        }

        Agent agent = new Agent(
                nom.trim(),
                prenom.trim(),
                email.trim().toLowerCase(),
                motDePasse,
                typeAgent
        );

        agentRepository.save(agent);
        System.out.println("✓ Agent créé: " + agent.getNomComplet() + " (" + typeAgent + ")");
        return agent;
    }

    @Override
    public Agent findAgentById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID de l'agent doit être positif");
        }

        return agentRepository.findById(id);
    }

    @Override
    public Agent findAgentByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        return agentRepository.findByEmail(email.trim().toLowerCase());
    }

    @Override
    public List<Agent> getAllAgents() {
        return agentRepository.findAll();
    }

    @Override
    public void updateAgent(Agent agent) {
        // Business Rule 1: Validate agent exists
        if (agent == null || agent.getIdAgent() == null) {
            throw new IllegalArgumentException("Agent invalide");
        }

        Agent existing = agentRepository.findById(agent.getIdAgent());
        if (existing == null) {
            throw new IllegalArgumentException("Agent non trouvé avec l'ID: " + agent.getIdAgent());
        }

        validateAgentData(agent.getNom(), agent.getPrenom(), agent.getEmail(),
                agent.getMotDePasse(), agent.getTypeAgent());

        if (!existing.getEmail().equals(agent.getEmail())) {
            if (!isEmailAvailable(agent.getEmail())) {
                throw new IllegalArgumentException("Cette adresse email est déjà utilisée: " + agent.getEmail());
            }
        }

        agent.setNom(agent.getNom().trim());
        agent.setPrenom(agent.getPrenom().trim());
        agent.setEmail(agent.getEmail().trim().toLowerCase());

        agentRepository.update(agent);
        System.out.println("✓ Agent mis à jour: " + agent.getNomComplet());
    }


    @Override
    public Agent authenticate(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email est requis");
        }

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe est requis");
        }

        Agent agent = findAgentByEmail(email);
        if (agent == null) {
            return null; // Agent not found
        }

        if (agent.getMotDePasse().equals(password)) {
            System.out.println("✓ Authentification réussie: " + agent.getNomComplet());
            return agent;
        }

        return null; // Wrong password
    }



    @Override
    public boolean assignToDepartment(Integer agentId, Integer departmentId) {
        if (agentId == null || departmentId == null) {
            throw new IllegalArgumentException("L'ID de l'agent et du département sont requis");
        }

        Agent agent = agentRepository.findById(agentId);
        if (agent == null) {
            throw new IllegalArgumentException("Agent non trouvé avec l'ID: " + agentId);
        }

        Departement departement = departementRepository.findById(departmentId);
        if (departement == null) {
            throw new IllegalArgumentException("Département non trouvé avec l'ID: " + departmentId);
        }
        agent.setIdDepartement(departmentId);
        agentRepository.update(agent);

        System.out.println("✓ " + agent.getNomComplet() + " assigné au département " + departement.getNom());
        return true;
    }



    @Override
    public List<Agent> getAgentsByDepartment(Integer departmentId) {
        if (departmentId == null) {
            throw new IllegalArgumentException("L'ID du département est requis");
        }

        return agentRepository.findByDepartementId(departmentId);
    }


    @Override
    public List<Agent> getAgentsByType(TypeAgent type) {
        if (type == null) {
            throw new IllegalArgumentException("Le type d'agent est requis");
        }

        return agentRepository.findByTypeAgent(type);
    }

    @Override
    public List<Agent> getManagers() {
        List<Agent> managers = agentRepository.findByTypeAgent(TypeAgent.RESPONSABLE_DEPARTEMENT);
        managers.addAll(agentRepository.findByTypeAgent(TypeAgent.DIRECTEUR));
        return managers;
    }

    @Override
    public List<Agent> getBonusEligibleAgents() {
        return agentRepository.findAll().stream()
                .filter(Agent::isEligibleForBonus)
                .collect(Collectors.toList());
    }

    @Override
    public List<Agent> getIndemnityEligibleAgents() {
        return agentRepository.findAll().stream()
                .filter(Agent::isEligibleForIndemnity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isEmailAvailable(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        Agent existing = agentRepository.findByEmail(email.trim().toLowerCase());
        return existing == null;
    }

    @Override
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    @Override
    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    private void validateAgentData(String nom, String prenom, String email, String motDePasse, TypeAgent typeAgent) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom est requis");
        }

        if (prenom == null || prenom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom est requis");
        }

        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Format d'email invalide: " + email);
        }

        if (!isValidPassword(motDePasse)) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères");
        }

        if (typeAgent == null) {
            throw new IllegalArgumentException("Le type d'agent est requis");
        }
    }
}
