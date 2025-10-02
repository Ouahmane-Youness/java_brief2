package com.paymentManagement.service.interfaces;

import com.paymentManagement.model.entity.Agent;
import com.paymentManagement.model.entity.Paiement;
import com.paymentManagement.model.enums.TypePaiement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PaiementService {

    Paiement createPaiement(Integer agentId, TypePaiement typePaiement, BigDecimal montant,
                            LocalDate datePaiement, String motif, String evenement);

    Paiement findPaiementById(Integer id);

    List<Paiement> getAllPaiements();

    List<Paiement> getPaiementsByAgent(Integer agentId);

    List<Paiement> getPaiementsByType(TypePaiement typePaiement);

    void updatePaiement(Paiement paiement);

    boolean deletePaiement(Integer id);

    boolean isAgentEligibleForBonus(Agent agent);

    boolean isAgentEligibleForIndemnite(Agent agent);

    void validatePaiementAmount(BigDecimal montant);

    List<Paiement> filterPaiementsByDateRange(Integer agentId, LocalDate startDate, LocalDate endDate);

    List<Paiement> filterPaiementsByAmountRange(Integer agentId, BigDecimal minAmount, BigDecimal maxAmount);

    List<Paiement> sortPaiementsByDate(List<Paiement> paiements, boolean ascending);

    List<Paiement> sortPaiementsByAmount(List<Paiement> paiements, boolean ascending);

    BigDecimal calculateTotalPaiements(Integer agentId);

    BigDecimal calculateTotalPaiementsByType(Integer agentId, TypePaiement typePaiement);

    long countPaiementsByType(Integer agentId, TypePaiement typePaiement);

    Paiement getHighestPaiement(Integer agentId);

    Paiement getLowestPaiement(Integer agentId);

    BigDecimal calculateAnnualSalary(Integer agentId);
}