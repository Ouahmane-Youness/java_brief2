package com.paymentManagement.view.menu;

import com.paymentManagement.model.entity.Agent;
import com.paymentManagement.model.entity.Departement;
import com.paymentManagement.model.entity.Paiement;
import com.paymentManagement.service.impl.AgentServiceImpl;
import com.paymentManagement.service.impl.DepartementServiceImpl;
import com.paymentManagement.service.impl.DepartmentStatisticsService;
import com.paymentManagement.view.InputHandler;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ManagerStatisticsMenu {

    private final Agent loggedInManager;
    private final DepartmentStatisticsService statsService;
    private final AgentServiceImpl agentService;
    private final DepartementServiceImpl departementService;
    private final InputHandler inputHandler;

    public ManagerStatisticsMenu(Agent loggedInManager) {
        this.loggedInManager = loggedInManager;
        this.statsService = new DepartmentStatisticsService();
        this.agentService = new AgentServiceImpl();
        this.departementService = new DepartementServiceImpl();
        this.inputHandler = new InputHandler();
    }

    public void show() {
        boolean running = true;

        while (running) {
            displayStatsMenu();

            int choice = inputHandler.getIntInput("Votre choix: ");

            switch (choice) {
                case 1:
                    showDepartmentStatistics();
                    break;
                case 2:
                    showAgentRanking();
                    break;
                case 3:
                    showCompanyWideStatistics();
                    break;
                case 4:
                    showAnomalies();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        }
    }

    private void displayStatsMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    STATISTIQUES AVANCÉES");
        System.out.println("=".repeat(50));
        System.out.println("1. Statistiques de mon département");
        System.out.println("2. Classement des agents");
        System.out.println("3. Statistiques globales (entreprise)");
        System.out.println("4. Détecter les anomalies");
        System.out.println("0. Retour");
        System.out.println("=".repeat(50));
    }

    private void showDepartmentStatistics() {
        System.out.println("\n--- Statistiques du Département ---");

        if (loggedInManager.getIdDepartement() == null) {
            System.out.println("Vous n'êtes pas assigné à un département.");
            inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
            return;
        }

        try {
            Departement dept = departementService.findDepartementById(loggedInManager.getIdDepartement());

            if (dept == null) {
                System.out.println("Département non trouvé.");
                inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
                return;
            }

            System.out.println("Département: " + dept.getNom());
            System.out.println();

            BigDecimal totalPayments = statsService.calculateTotalPaiementsByDepartment(dept.getIdDepartement());
            System.out.println("Total des paiements: " + totalPayments + " DH");

            BigDecimal averageSalary = statsService.calculateAverageSalaryByDepartment(dept.getIdDepartement());
            System.out.println("Montant moyen par agent: " + averageSalary + " DH");

            int agentCount = departementService.getAgentCount(dept.getIdDepartement());
            System.out.println("Nombre d'agents: " + agentCount);

            if (agentCount > 0 && totalPayments.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal avgPerAgent = totalPayments.divide(
                        new BigDecimal(agentCount), 2, BigDecimal.ROUND_HALF_UP);
                System.out.println("Paiement moyen par agent: " + avgPerAgent + " DH");
            }

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }

    private void showAgentRanking() {
        System.out.println("\n--- Classement des Agents par Paiements ---");

        if (loggedInManager.getIdDepartement() == null) {
            System.out.println("Vous n'êtes pas assigné à un département.");
            inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
            return;
        }

        try {
            List<DepartmentStatisticsService.AgentPaymentSummary> ranking =
                    statsService.rankAgentsByTotalPayment(loggedInManager.getIdDepartement());

            if (ranking.isEmpty()) {
                System.out.println("Aucun agent avec des paiements trouvé.");
                inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
                return;
            }

            System.out.println("Classement (du plus payé au moins payé):\n");

            int rank = 1;
            for (DepartmentStatisticsService.AgentPaymentSummary summary : ranking) {
                Agent agent = summary.getAgent();
                System.out.println(rank + ". " + agent.getNomComplet());
                System.out.println("   Type: " + agent.getTypeAgent());
                System.out.println("   Total des paiements: " + summary.getTotalAmount() + " DH");
                System.out.println("   Nombre de paiements: " + summary.getPaymentCount());
                System.out.println();
                rank++;
            }

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }

    private void showCompanyWideStatistics() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    STATISTIQUES GLOBALES (ENTREPRISE)");
        System.out.println("=".repeat(50));

        try {
            Map<String, Object> stats = statsService.getCompanyWideStatistics();

            System.out.println("\n--- Vue d'ensemble ---");
            System.out.println("Total agents: " + stats.get("totalAgents"));
            System.out.println("Total départements: " + stats.get("totalDepartments"));
            System.out.println("Montant total des paiements: " + stats.get("totalAmount") + " DH");

            System.out.println("\n--- Répartition par type de paiement ---");

            @SuppressWarnings("unchecked")
            Map<String, Long> distribution = (Map<String, Long>) stats.get("paymentTypeDistribution");

            @SuppressWarnings("unchecked")
            Map<String, BigDecimal> amounts = (Map<String, BigDecimal>) stats.get("paymentTypeAmounts");

            @SuppressWarnings("unchecked")
            Map<String, Double> percentages = (Map<String, Double>) stats.get("paymentTypePercentages");

            for (String type : distribution.keySet()) {
                System.out.println(type + ":");
                System.out.println("  Nombre: " + distribution.get(type));
                System.out.println("  Montant: " + amounts.get(type) + " DH");
                System.out.println("  Pourcentage: " + String.format("%.2f", percentages.get(type)) + "%");
            }

            if (stats.containsKey("topAgent")) {
                Agent topAgent = (Agent) stats.get("topAgent");
                BigDecimal topTotal = (BigDecimal) stats.get("topAgentTotal");

                System.out.println("\n--- Agent le mieux payé ---");
                System.out.println("Nom: " + topAgent.getNomComplet());
                System.out.println("Type: " + topAgent.getTypeAgent());
                System.out.println("Total: " + topTotal + " DH");
            }

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }

    private void showAnomalies() {
        System.out.println("\n--- Détection des Anomalies ---");
        System.out.println("Paiements inhabituellement élevés (> 3x la moyenne)\n");

        try {
            List<Paiement> anomalies = statsService.detectAnomalies();

            if (anomalies.isEmpty()) {
                System.out.println("Aucune anomalie détectée.");
            } else {
                System.out.println("Total: " + anomalies.size() + " paiement(s) inhabituel(s)\n");

                for (Paiement p : anomalies) {
                    Agent agent = agentService.findAgentById(p.getIdAgent());

                    System.out.println("ID Paiement: " + p.getIdPaiement());
                    System.out.println("  Agent: " + (agent != null ? agent.getNomComplet() : "Inconnu"));
                    System.out.println("  Type: " + p.getTypePaiement());
                    System.out.println("  Montant: " + p.getMontant() + " DH (INHABITUEL)");
                    System.out.println("  Date: " + p.getDatePaiement());

                    if (p.getMotif() != null && !p.getMotif().isEmpty()) {
                        System.out.println("  Motif: " + p.getMotif());
                    }

                    System.out.println();
                }
            }

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }
}