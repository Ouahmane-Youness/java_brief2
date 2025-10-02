package com.paymentManagement.repository.interfaces;

import com.paymentManagement.model.entity.Paiement;
import com.paymentManagement.model.enums.TypePaiement;

import java.util.List;

public interface PaiementRepository {

    void save(Paiement paiement);

    Paiement findById(Integer id);

    List<Paiement> findAll();

    List<Paiement> findByAgentId(Integer agentId);

    List<Paiement> findByTypePaiement(TypePaiement typePaiement);

    void update(Paiement paiement);

    boolean deleteById(Integer id);
}