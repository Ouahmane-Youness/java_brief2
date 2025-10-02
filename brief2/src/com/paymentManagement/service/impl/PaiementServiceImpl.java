package com.paymentManagement.service.impl;

import com.paymentManagement.model.entity.Agent;
import com.paymentManagement.model.entity.Paiement;
import com.paymentManagement.model.enums.TypeAgent;
import com.paymentManagement.model.enums.TypePaiement;
import com.paymentManagement.repository.imp.AgentRepositoryImpl;
import com.paymentManagement.repository.imp.PaiementRepositoryImpl;
import com.paymentManagement.repository.interfaces.AgentRepository;
import com.paymentManagement.repository.interfaces.PaiementRepository;
import com.paymentManagement.service.interfaces.PaiementService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PaiementServiceImpl implements PaiementService {

    private final PaiementRepository paiementRepository;
    private final AgentRepository agentRepository;

    public PaiementServiceImpl() {
        this.paiementRepository = new PaiementRepositoryImpl();
        this.agentRepository = new AgentRepositoryImpl();
    }

    @Override
    public Paiement createPaiement(Integer agentId, TypePaiement typePaiement, BigDecimal montant,
                                   LocalDate datePaiement, String motif, String evenement) {

        if (agentId == null) {
            throw new IllegalArgumentException("L'ID de l'agent est requis");
        }

        Agent agent = agentRepository.findById(agentId);
        if (agent == null) {
            throw new IllegalArgumentException("Agent non trouvé avec l'ID: " + agentId);
        }

        if (typePaiement == null) {
            throw new IllegalArgumentException("Le type de paiement est requis");
        }

        validatePaiementAmount(montant);

        if (datePaiement == null) {
            datePaiement = LocalDate.now();
        }

        boolean conditionValidee = false;

        if (typePaiement == TypePaiement.BONUS) {
            if (!isAgentEligibleForBonus(agent)) {
                throw new IllegalArgumentException("L'agent " + agent.getNomComplet() +
                        " n'est pas éligible pour recevoir un bonus. Seuls les Responsables et Directeurs peuvent recevoir des bonus.");
            }

            if (evenement == null || evenement.trim().isEmpty()) {
                throw new IllegalArgumentException("Un événement doit être spécifié pour un bonus");
            }

            conditionValidee = true;
        }

        if (typePaiement == TypePaiement.INDEMNITE) {
            if (!isAgentEligibleForIndemnite(agent)) {
                throw new IllegalArgumentException("L'agent " + agent.getNomComplet() +
                        " n'est pas éligible pour recevoir une indemnité. Seuls les Responsables et Directeurs peuvent recevoir des indemnités.");
            }

            if (motif == null || motif.trim().isEmpty()) {
                throw new IllegalArgumentException("Un motif doit être spécifié pour une indemnité");
            }

            conditionValidee = true;
        }

        Paiement paiement = new Paiement(null, typePaiement, montant, datePaiement,
                motif, evenement, conditionValidee, agentId);

        paiementRepository.save(paiement);

        System.out.println("Paiement créé: " + typePaiement + " de " + montant + " pour " + agent.getNomComplet());

        return paiement;
    }

    @Override
    public Paiement findPaiementById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID du paiement doit être positif");
        }

        return paiementRepository.findById(id);
    }

    @Override
    public List<Paiement> getAllPaiements() {
        return paiementRepository.findAll();
    }

    @Override
    public List<Paiement> getPaiementsByAgent(Integer agentId) {
        if (agentId == null) {
            throw new IllegalArgumentException("L'ID de l'agent est requis");
        }

        return paiementRepository.findByAgentId(agentId);
    }

    @Override
    public List<Paiement> getPaiementsByType(TypePaiement typePaiement) {
        if (typePaiement == null) {
            throw new IllegalArgumentException("Le type de paiement est requis");
        }

        return paiementRepository.findByTypePaiement(typePaiement);
    }

    @Override
    public void updatePaiement(Paiement paiement) {
        if (paiement == null || paiement.getIdPaiement() == null) {
            throw new IllegalArgumentException("Paiement invalide");
        }

        Paiement existing = paiementRepository.findById(paiement.getIdPaiement());
        if (existing == null) {
            throw new IllegalArgumentException("Paiement non trouvé avec l'ID: " + paiement.getIdPaiement());
        }

        validatePaiementAmount(paiement.getMontant());

        if (paiement.getTypePaiement() == TypePaiement.BONUS ||
                paiement.getTypePaiement() == TypePaiement.INDEMNITE) {

            Agent agent = agentRepository.findById(paiement.getIdAgent());
            if (agent == null) {
                throw new IllegalArgumentException("Agent non trouvé");
            }

            if (paiement.getTypePaiement() == TypePaiement.BONUS && !isAgentEligibleForBonus(agent)) {
                throw new IllegalArgumentException("Agent non éligible pour bonus");
            }

            if (paiement.getTypePaiement() == TypePaiement.INDEMNITE && !isAgentEligibleForIndemnite(agent)) {
                throw new IllegalArgumentException("Agent non éligible pour indemnité");
            }
        }

        paiementRepository.update(paiement);
        System.out.println("Paiement mis à jour avec succès");
    }

    @Override
    public boolean deletePaiement(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID du paiement doit être positif");
        }

        Paiement paiement = paiementRepository.findById(id);
        if (paiement == null) {
            System.out.println("Paiement non trouvé avec l'ID: " + id);
            return false;
        }

        boolean deleted = paiementRepository.deleteById(id);
        if (deleted) {
            System.out.println("Paiement supprimé avec succès");
        }
        return deleted;
    }

    @Override
    public boolean isAgentEligibleForBonus(Agent agent) {
        if (agent == null) {
            return false;
        }
        return agent.getTypeAgent() == TypeAgent.RESPONSABLE_DEPARTEMENT ||
                agent.getTypeAgent() == TypeAgent.DIRECTEUR;
    }

    @Override
    public boolean isAgentEligibleForIndemnite(Agent agent) {
        if (agent == null) {
            return false;
        }
        return agent.getTypeAgent() == TypeAgent.RESPONSABLE_DEPARTEMENT ||
                agent.getTypeAgent() == TypeAgent.DIRECTEUR;
    }

    @Override
    public void validatePaiementAmount(BigDecimal montant) {
        if (montant == null) {
            throw new IllegalArgumentException("Le montant est requis");
        }

        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif. Montant reçu: " + montant);
        }

        if (montant.compareTo(new BigDecimal("1000000")) > 0) {
            throw new IllegalArgumentException("Le montant est trop élevé. Maximum: 1,000,000");
        }
    }

    @Override
    public List<Paiement> filterPaiementsByDateRange(Integer agentId, LocalDate startDate, LocalDate endDate) {
        if (agentId == null) {
            throw new IllegalArgumentException("L'ID de l'agent est requis");
        }

        List<Paiement> paiements = paiementRepository.findByAgentId(agentId);

        return paiements.stream()
                .filter(p -> {
                    if (startDate != null && p.getDatePaiement().isBefore(startDate)) {
                        return false;
                    }
                    if (endDate != null && p.getDatePaiement().isAfter(endDate)) {
                        return false;
                    }
                    return true;
                })
                .toList();
    }

    @Override
    public List<Paiement> filterPaiementsByAmountRange(Integer agentId, BigDecimal minAmount, BigDecimal maxAmount) {
        if (agentId == null) {
            throw new IllegalArgumentException("L'ID de l'agent est requis");
        }

        List<Paiement> paiements = paiementRepository.findByAgentId(agentId);

        return paiements.stream()
                .filter(p -> {
                    if (minAmount != null && p.getMontant().compareTo(minAmount) < 0) {
                        return false;
                    }
                    if (maxAmount != null && p.getMontant().compareTo(maxAmount) > 0) {
                        return false;
                    }
                    return true;
                })
                .toList();
    }

    @Override
    public List<Paiement> sortPaiementsByDate(List<Paiement> paiements, boolean ascending) {
        if (paiements == null) {
            return List.of();
        }

        return paiements.stream()
                .sorted((p1, p2) -> ascending ?
                        p1.getDatePaiement().compareTo(p2.getDatePaiement()) :
                        p2.getDatePaiement().compareTo(p1.getDatePaiement()))
                .toList();
    }

    @Override
    public List<Paiement> sortPaiementsByAmount(List<Paiement> paiements, boolean ascending) {
        if (paiements == null) {
            return List.of();
        }

        return paiements.stream()
                .sorted((p1, p2) -> ascending ?
                        p1.getMontant().compareTo(p2.getMontant()) :
                        p2.getMontant().compareTo(p1.getMontant()))
                .toList();
    }

    @Override
    public BigDecimal calculateTotalPaiements(Integer agentId) {
        if (agentId == null) {
            throw new IllegalArgumentException("L'ID de l'agent est requis");
        }

        List<Paiement> paiements = paiementRepository.findByAgentId(agentId);

        return paiements.stream()
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateTotalPaiementsByType(Integer agentId, TypePaiement typePaiement) {
        if (agentId == null) {
            throw new IllegalArgumentException("L'ID de l'agent est requis");
        }

        if (typePaiement == null) {
            throw new IllegalArgumentException("Le type de paiement est requis");
        }

        List<Paiement> paiements = paiementRepository.findByAgentId(agentId);

        return paiements.stream()
                .filter(p -> p.getTypePaiement() == typePaiement)
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public long countPaiementsByType(Integer agentId, TypePaiement typePaiement) {
        if (agentId == null) {
            throw new IllegalArgumentException("L'ID de l'agent est requis");
        }

        if (typePaiement == null) {
            throw new IllegalArgumentException("Le type de paiement est requis");
        }

        List<Paiement> paiements = paiementRepository.findByAgentId(agentId);

        return paiements.stream()
                .filter(p -> p.getTypePaiement() == typePaiement)
                .count();
    }

    @Override
    public Paiement getHighestPaiement(Integer agentId) {
        if (agentId == null) {
            throw new IllegalArgumentException("L'ID de l'agent est requis");
        }

        List<Paiement> paiements = paiementRepository.findByAgentId(agentId);

        return paiements.stream()
                .max((p1, p2) -> p1.getMontant().compareTo(p2.getMontant()))
                .orElse(null);
    }

    @Override
    public Paiement getLowestPaiement(Integer agentId) {
        if (agentId == null) {
            throw new IllegalArgumentException("L'ID de l'agent est requis");
        }

        List<Paiement> paiements = paiementRepository.findByAgentId(agentId);

        return paiements.stream()
                .min((p1, p2) -> p1.getMontant().compareTo(p2.getMontant()))
                .orElse(null);
    }

    @Override
    public BigDecimal calculateAnnualSalary(Integer agentId) {
        if (agentId == null) {
            throw new IllegalArgumentException("L'ID de l'agent est requis");
        }

        List<Paiement> paiements = paiementRepository.findByAgentId(agentId);

        LocalDate oneYearAgo = LocalDate.now().minusYears(1);

        return paiements.stream()
                .filter(p -> p.getTypePaiement() == TypePaiement.SALAIRE)
                .filter(p -> p.getDatePaiement().isAfter(oneYearAgo))
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}