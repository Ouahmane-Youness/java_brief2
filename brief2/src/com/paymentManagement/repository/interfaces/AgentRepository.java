package com.paymentManagement.repository.interfaces;

import com.paymentManagement.model.enums.TypeAgent;

public interface AgentRepository {
    void save(Agent agent);
    Agent findById(Integer id);
    List<Agent> findAll();
    Agent findByEmail(String email);
    List<Agent> findByDepartementId(Integer departmentId);
    List<Agent> findByTypeAgent(TypeAgent typeAgent);
    void update(Agent agent);
}
