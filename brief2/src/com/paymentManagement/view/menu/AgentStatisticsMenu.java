package com.paymentManagement.view.menu;

import com.paymentManagement.model.entity.Agent;
import com.paymentManagement.model.entity.Paiement;
import com.paymentManagement.model.enums.TypePaiement;
import com.paymentManagement.service.impl.AgentServiceImpl;
import com.paymentManagement.service.impl.PaiementServiceImpl;
import com.paymentManagement.view.InputHandler;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AgentStatisticsMenu {

    private final Agent loggedInAgent;
    private final PaiementServiceImpl paiementService;
    private final AgentServiceImpl agentService;
    private final InputHandler inputHandler;

    public AgentStatisticsMenu(Agent loggedInAgent) {
        this.loggedInAgent = loggedInAgent;
        this.paiementService = new PaiementServiceImpl();
        this.agentService = new AgentServiceImpl();
        this.inputHandler = new InputHandler();
    }

    public void show() {
        boolean running = true;

        while (running) {
            displayStatisticsMenu();

            int choice = inputHandler.getIntInput("Votre choix: ");

            switch (choice) {
                case 1:
                    filterByType();
                    break;
                case 2:
                    filterByDateRange();
                    break;
                case 3:
                    filterByAmountRange();
                    break;
                case 4:
                    sortPaiements();
                    break;
                case 5:
                    showMyStatistics();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        }
    }

    private void displayStatisticsMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    MES PAIEMENTS - FILTRES ET STATISTIQUES");
        System.out.println("=".repeat(50));
        System.out.println("1. Filtrer par type");
        System.out.println("2. Filtrer par période");
        System.out.println("3. Filtrer par montant");
        System.out.println("4. Trier mes paiements");
        System.out.println("5. Voir mes statistiques complètes");
        System.out.println("0. Retour");
        System.out.println("=".repeat(50));
    }

    private void filterByType() {
        System.out.println("\n--- Filtrer par Type ---");
        System.out.println("1. SALAIRE");
        System.out.println("2. PRIME");
        System.out.println("3. BONUS");
        System.out.println("4. INDEMNITE");

        int typeChoice = inputHandler.getIntInput("Type (1-4): ");

        TypePaiement typePaiement = switch (typeChoice) {
            case 1 -> TypePaiement.SALAIRE;
            case 2 -> TypePaiement.PRIME;
            case 3 -> TypePaiement.BONUS;
            case 4 -> TypePaiement.INDEMNITE;
            default -> null;
        };

        if (typePaiement == null) {
            System.out.println("Type invalide!");
            inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
            return;
        }

        try {
            List<Paiement> allPaiements = paiementService.getPaiementsByAgent(loggedInAgent.getIdAgent());
            List<Paiement> filtered = allPaiements.stream()
                    .filter(p -> p.getTypePaiement() == typePaiement)
                    .toList();

            displayPaiementsList(filtered, "Paiements de type " + typePaiement);

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }

    private void filterByDateRange() {
        System.out.println("\n--- Filtrer par Période ---");

        try {
            System.out.print("Date de début (yyyy-MM-dd) ou Entrée pour ignorer: ");
            String startDateStr = inputHandler.getStringInput("");
            LocalDate startDate = null;

            if (!startDateStr.isEmpty()) {
                try {
                    startDate = LocalDate.parse(startDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException e) {
                    System.out.println("Format de date invalide!");
                    inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
                    return;
                }
            }

            System.out.print("Date de fin (yyyy-MM-dd) ou Entrée pour ignorer: ");
            String endDateStr = inputHandler.getStringInput("");
            LocalDate endDate = null;

            if (!endDateStr.isEmpty()) {
                try {
                    endDate = LocalDate.parse(endDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException e) {
                    System.out.println("Format de date invalide!");
                    inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
                    return;
                }
            }

            List<Paiement> filtered = paiementService.filterPaiementsByDateRange(
                    loggedInAgent.getIdAgent(), startDate, endDate);

            String dateRangeInfo = "Paiements";
            if (startDate != null) dateRangeInfo += " à partir du " + startDate;
            if (endDate != null) dateRangeInfo += " jusqu'au " + endDate;

            displayPaiementsList(filtered, dateRangeInfo);

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }

    private void filterByAmountRange() {
        System.out.println("\n--- Filtrer par Montant ---");

        try {
            System.out.print("Montant minimum ou Entrée pour ignorer: ");
            String minStr = inputHandler.getStringInput("");
            BigDecimal minAmount = null;

            if (!minStr.isEmpty()) {
                minAmount = new BigDecimal(minStr);
            }

            System.out.print("Montant maximum ou Entrée pour ignorer: ");
            String maxStr = inputHandler.getStringInput("");
            BigDecimal maxAmount = null;

            if (!maxStr.isEmpty()) {
                maxAmount = new BigDecimal(maxStr);
            }

            List<Paiement> filtered = paiementService.filterPaiementsByAmountRange(
                    loggedInAgent.getIdAgent(), minAmount, maxAmount);

            String amountRangeInfo = "Paiements";
            if (minAmount != null) amountRangeInfo += " >= " + minAmount + " DH";
            if (maxAmount != null) amountRangeInfo += " <= " + maxAmount + " DH";

            displayPaiementsList(filtered, amountRangeInfo);

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }

    private void sortPaiements() {
        System.out.println("\n--- Trier mes Paiements ---");
        System.out.println("1. Trier par date (plus récent)");
        System.out.println("2. Trier par date (plus ancien)");
        System.out.println("3. Trier par montant (plus élevé)");
        System.out.println("4. Trier par montant (plus faible)");

        int sortChoice = inputHandler.getIntInput("Choix: ");

        try {
            List<Paiement> paiements = paiementService.getPaiementsByAgent(loggedInAgent.getIdAgent());
            List<Paiement> sorted;
            String sortInfo;

            switch (sortChoice) {
                case 1:
                    sorted = paiementService.sortPaiementsByDate(paiements, false);
                    sortInfo = "Paiements triés par date (plus récent)";
                    break;
                case 2:
                    sorted = paiementService.sortPaiementsByDate(paiements, true);
                    sortInfo = "Paiements triés par date (plus ancien)";
                    break;
                case 3:
                    sorted = paiementService.sortPaiementsByAmount(paiements, false);
                    sortInfo = "Paiements triés par montant (plus élevé)";
                    break;
                case 4:
                    sorted = paiementService.sortPaiementsByAmount(paiements, true);
                    sortInfo = "Paiements triés par montant (plus faible)";
                    break;
                default:
                    System.out.println("Choix invalide!");
                    inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
                    return;
            }

            displayPaiementsList(sorted, sortInfo);

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }

    private void showMyStatistics() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    MES STATISTIQUES DE PAIEMENTS");
        System.out.println("=".repeat(50));

        try {
            BigDecimal totalGeneral = paiementService.calculateTotalPaiements(loggedInAgent.getIdAgent());

            System.out.println("\nTOTAL GÉNÉRAL: " + totalGeneral + " DH");

            System.out.println("\n--- Par Type ---");
            for (TypePaiement type : TypePaiement.values()) {
                long count = paiementService.countPaiementsByType(loggedInAgent.getIdAgent(), type);
                BigDecimal total = paiementService.calculateTotalPaiementsByType(loggedInAgent.getIdAgent(), type);
                System.out.println(type + ": " + count + " paiement(s) | Total: " + total + " DH");
            }

            BigDecimal annualSalary = paiementService.calculateAnnualSalary(loggedInAgent.getIdAgent());
            System.out.println("\n--- Salaire Annuel ---");
            System.out.println("Salaire des 12 derniers mois: " + annualSalary + " DH");

            Paiement highest = paiementService.getHighestPaiement(loggedInAgent.getIdAgent());
            Paiement lowest = paiementService.getLowestPaiement(loggedInAgent.getIdAgent());

            System.out.println("\n--- Extrêmes ---");
            if (highest != null) {
                System.out.println("Paiement le plus élevé: " + highest.getMontant() + " DH (" +
                        highest.getTypePaiement() + " - " + highest.getDatePaiement() + ")");
            }

            if (lowest != null) {
                System.out.println("Paiement le plus faible: " + lowest.getMontant() + " DH (" +
                        lowest.getTypePaiement() + " - " + lowest.getDatePaiement() + ")");
            }

            List<Paiement> allPaiements = paiementService.getPaiementsByAgent(loggedInAgent.getIdAgent());
            if (!allPaiements.isEmpty()) {
                BigDecimal average = totalGeneral.divide(
                        new BigDecimal(allPaiements.size()), 2, BigDecimal.ROUND_HALF_UP);
                System.out.println("\nMoyenne par paiement: " + average + " DH");
            }

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }

    private void displayPaiementsList(List<Paiement> paiements, String title) {
        System.out.println("\n--- " + title + " ---");

        if (paiements.isEmpty()) {
            System.out.println("Aucun paiement trouvé.");
            return;
        }

        System.out.println("Total: " + paiements.size() + " paiement(s)\n");

        for (Paiement p : paiements) {
            System.out.println("ID: " + p.getIdPaiement() + " | Type: " + p.getTypePaiement());
            System.out.println("  Montant: " + p.getMontant() + " DH");
            System.out.println("  Date: " + p.getDatePaiement());

            if (p.getMotif() != null && !p.getMotif().isEmpty()) {
                System.out.println("  Motif: " + p.getMotif());
            }

            if (p.getEvenement() != null && !p.getEvenement().isEmpty()) {
                System.out.println("  Événement: " + p.getEvenement());
            }

            System.out.println();
        }

        BigDecimal total = paiements.stream()
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println("=".repeat(50));
        System.out.println("TOTAL: " + total + " DH");
        System.out.println("=".repeat(50));
    }
}