package com.paymentManagement.repository.interfaces;

import java.math.BigDecimal;

public interface PaimentInterface {
    void save(Paiement paiement);

    Paiement findById(Integer id);

    List<Paiement> findAll();

    List<Paiement> findByAgentId(Integer agentId);

    List<Paiement> findByTypePaiement(TypePaiement typePaiement);

    void update(Paiement paiement);

    boolean deleteById(Integer id);

    boolean existsById(Integer id);

}