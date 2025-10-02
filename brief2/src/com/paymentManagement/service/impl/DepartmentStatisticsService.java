package com.paymentManagement.service.impl;

import com.paymentManagement.model.entity.Agent;
import com.paymentManagement.model.entity.Paiement;
import com.paymentManagement.repository.imp.AgentRepositoryImpl;
import com.paymentManagement.repository.imp.PaiementRepositoryImpl;
import com.paymentManagement.repository.interfaces.AgentRepository;
import com.paymentManagement.repository.interfaces.PaiementRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class DepartmentStatisticsService {

    private final AgentRepository agentRepository;
    private final PaiementRepository paiementRepository;

    public DepartmentStatisticsService() {
        this.agentRepository = new AgentRepositoryImpl();
        this.paiementRepository = new PaiementRepositoryImpl();
    }

    public BigDecimal calculateTotalPaiementsByDepartment(Integer departmentId) {
        if (departmentId == null) {
            throw new IllegalArgumentException("L'ID du département est requis");
        }

        List<Agent> agents = agentRepository.findByDepartementId(departmentId);

        return agents.stream()
                .flatMap(agent -> paiementRepository.findByAgentId(agent.getIdAgent()).stream())
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateAverageSalaryByDepartment(Integer departmentId) {
        if (departmentId == null) {
            throw new IllegalArgumentException("L'ID du département est requis");
        }

        List<Agent> agents = agentRepository.findByDepartementId(departmentId);

        if (agents.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalSalaries = BigDecimal.ZERO;
        int agentCount = 0;

        for (Agent agent : agents) {
            List<Paiement> paiements = paiementRepository.findByAgentId(agent.getIdAgent());
            BigDecimal agentTotal = paiements.stream()
                    .map(Paiement::getMontant)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (agentTotal.compareTo(BigDecimal.ZERO) > 0) {
                totalSalaries = totalSalaries.add(agentTotal);
                agentCount++;
            }
        }

        if (agentCount == 0) {
            return BigDecimal.ZERO;
        }

        return totalSalaries.divide(new BigDecimal(agentCount), 2, RoundingMode.HALF_UP);
    }

    public List<AgentPaymentSummary> rankAgentsByTotalPayment(Integer departmentId) {
        if (departmentId == null) {
            throw new IllegalArgumentException("L'ID du département est requis");
        }

        List<Agent> agents = agentRepository.findByDepartementId(departmentId);

        return agents.stream()
                .map(agent -> {
                    List<Paiement> paiements = paiementRepository.findByAgentId(agent.getIdAgent());
                    BigDecimal total = paiements.stream()
                            .map(Paiement::getMontant)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new AgentPaymentSummary(agent, total, paiements.size());
                })
                .sorted((a1, a2) -> a2.getTotalAmount().compareTo(a1.getTotalAmount()))
                .toList();
    }

    public Map<String, Object> getCompanyWideStatistics() {
        Map<String, Object> stats = new HashMap<>();

        List<Agent> allAgents = agentRepository.findAll();
        List<Paiement> allPaiements = paiementRepository.findAll();

        stats.put("totalAgents", allAgents.size());

        Set<Integer> uniqueDepartments = allAgents.stream()
                .map(Agent::getIdDepartement)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        stats.put("totalDepartments", uniqueDepartments.size());

        Map<String, Long> paymentTypeDistribution = allPaiements.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getTypePaiement().name(),
                        Collectors.counting()
                ));
        stats.put("paymentTypeDistribution", paymentTypeDistribution);

        Map<String, BigDecimal> paymentTypeAmounts = allPaiements.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getTypePaiement().name(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Paiement::getMontant,
                                BigDecimal::add
                        )
                ));
        stats.put("paymentTypeAmounts", paymentTypeAmounts);

        BigDecimal totalAmount = allPaiements.stream()
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalAmount", totalAmount);

        Map<String, Double> paymentTypePercentages = new HashMap<>();
        if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
            for (Map.Entry<String, BigDecimal> entry : paymentTypeAmounts.entrySet()) {
                double percentage = entry.getValue()
                        .divide(totalAmount, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"))
                        .doubleValue();
                paymentTypePercentages.put(entry.getKey(), percentage);
            }
        }
        stats.put("paymentTypePercentages", paymentTypePercentages);

        Optional<Agent> topAgent = allAgents.stream()
                .max((a1, a2) -> {
                    BigDecimal total1 = paiementRepository.findByAgentId(a1.getIdAgent()).stream()
                            .map(Paiement::getMontant)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal total2 = paiementRepository.findByAgentId(a2.getIdAgent()).stream()
                            .map(Paiement::getMontant)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return total1.compareTo(total2);
                });

        if (topAgent.isPresent()) {
            Agent agent = topAgent.get();
            BigDecimal topTotal = paiementRepository.findByAgentId(agent.getIdAgent()).stream()
                    .map(Paiement::getMontant)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            stats.put("topAgent", agent);
            stats.put("topAgentTotal", topTotal);
        }

        return stats;
    }

    public List<Paiement> detectAnomalies() {
        List<Paiement> allPaiements = paiementRepository.findAll();

        if (allPaiements.isEmpty()) {
            return List.of();
        }

        BigDecimal totalAmount = allPaiements.stream()
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal average = totalAmount.divide(
                new BigDecimal(allPaiements.size()), 2, RoundingMode.HALF_UP);

        BigDecimal threshold = average.multiply(new BigDecimal("3"));

        return allPaiements.stream()
                .filter(p -> p.getMontant().compareTo(threshold) > 0)
                .sorted((p1, p2) -> p2.getMontant().compareTo(p1.getMontant()))
                .toList();
    }

    public static class AgentPaymentSummary {
        private final Agent agent;
        private final BigDecimal totalAmount;
        private final int paymentCount;

        public AgentPaymentSummary(Agent agent, BigDecimal totalAmount, int paymentCount) {
            this.agent = agent;
            this.totalAmount = totalAmount;
            this.paymentCount = paymentCount;
        }

        public Agent getAgent() {
            return agent;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public int getPaymentCount() {
            return paymentCount;
        }
    }
}