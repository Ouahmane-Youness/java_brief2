package com.paymentManagement.service.interfaces;

import com.paymentManagement.model.entity.Agent;
import com.paymentManagement.model.enums.TypeAgent;

import java.util.List;

public interface AgentService {
    Agent createAgent(String nom, String prenom, String email, String motDePasse, TypeAgent typeAgent);
    Agent findAgentById(Integer id);
    Agent findAgentByEmail(String email);
    List<Agent> getAllAgents();
    void updateAgent(Agent agent);
    //boolean deleteAgent(Integer id);

    // Authentication
    Agent authenticate(String email, String password);
    //boolean changePassword(Integer agentId, String oldPassword, String newPassword);

    // Department operations
    boolean assignToDepartment(Integer agentId, Integer departmentId);
    //boolean removeFromDepartment(Integer agentId);
    List<Agent> getAgentsByDepartment(Integer departmentId);
   // List<Agent> getAgentsWithoutDepartment();

    // Type and role operations
    List<Agent> getAgentsByType(TypeAgent type);
    List<Agent> getManagers();
    List<Agent> getBonusEligibleAgents();
    List<Agent> getIndemnityEligibleAgents();

    // Validation methods
    boolean isEmailAvailable(String email);
    boolean isValidEmail(String email);
    boolean isValidPassword(String password);
}
