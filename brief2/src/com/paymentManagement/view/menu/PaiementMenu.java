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

public class PaiementMenu {

    private final Agent loggedInManager;
    private final PaiementServiceImpl paiementService;
    private final AgentServiceImpl agentService;
    private final InputHandler inputHandler;

    public PaiementMenu(Agent loggedInManager) {
        this.loggedInManager = loggedInManager;
        this.paiementService = new PaiementServiceImpl();
        this.agentService = new AgentServiceImpl();
        this.inputHandler = new InputHandler();
    }

    public void show() {
        boolean running = true;

        while (running) {
            displayPaiementMenu();

            int choice = inputHandler.getIntInput("Votre choix: ");

            switch (choice) {
                case 1:
                    addPaiement();
                    break;
                case 2:
                    viewAgentPaiements();
                    break;
                case 3:
                    viewAllPaiements();
                    break;
                case 4:
                    deletePaiement();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        }
    }

    private void displayPaiementMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    GESTION DES PAIEMENTS");
        System.out.println("=".repeat(50));
        System.out.println("1. Ajouter un paiement");
        System.out.println("2. Voir les paiements d'un agent");
        System.out.println("3. Voir tous les paiements");
        System.out.println("4. Supprimer un paiement");
        System.out.println("0. Retour");
        System.out.println("=".repeat(50));
    }

    private void addPaiement() {
        System.out.println("\n--- Ajouter un Paiement ---");

        try {
            System.out.print("Email de l'agent: ");
            String email = inputHandler.getStringInput("");

            Agent agent = agentService.findAgentByEmail(email);
            if (agent == null) {
                System.out.println("Agent non trouvé!");
                inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
                return;
            }

            System.out.println("\nAgent: " + agent.getNomComplet() + " (" + agent.getTypeAgent() + ")");

            System.out.println("\nTypes de paiement:");
            System.out.println("1. SALAIRE");
            System.out.println("2. PRIME");
            System.out.println("3. BONUS (Responsables et Directeurs uniquement)");
            System.out.println("4. INDEMNITE (Responsables et Directeurs uniquement)");

            int typeChoice = inputHandler.getIntInput("Type (1-4): ");

            TypePaiement typePaiement = switch (typeChoice) {
                case 1 -> TypePaiement.SALAIRE;
                case 2 -> TypePaiement.PRIME;
                case 3 -> TypePaiement.BONUS;
                case 4 -> TypePaiement.INDEMNITE;
                default -> throw new IllegalArgumentException("Type invalide");
            };

            System.out.print("Montant: ");
            String montantStr = inputHandler.getStringInput("");
            BigDecimal montant = new BigDecimal(montantStr);

            System.out.print("Date (format: yyyy-MM-dd) ou Entrée pour aujourd'hui: ");
            String dateStr = inputHandler.getStringInput("");
            LocalDate datePaiement = LocalDate.now();

            if (!dateStr.isEmpty()) {
                try {
                    datePaiement = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException e) {
                    System.out.println("Format de date invalide, utilisation de la date du jour.");
                }
            }

            String motif = null;
            String evenement = null;

            if (typePaiement == TypePaiement.BONUS) {
                System.out.print("Événement (requis pour bonus): ");
                evenement = inputHandler.getStringInput("");
            }

            if (typePaiement == TypePaiement.INDEMNITE) {
                System.out.print("Motif (requis pour indemnité): ");
                motif = inputHandler.getStringInput("");
            }

            if (typePaiement == TypePaiement.SALAIRE || typePaiement == TypePaiement.PRIME) {
                System.out.print("Motif (optionnel): ");
                motif = inputHandler.getStringInput("");
                if (motif.isEmpty()) motif = null;
            }

            Paiement paiement = paiementService.createPaiement(
                    agent.getIdAgent(), typePaiement, montant, datePaiement, motif, evenement
            );

            System.out.println("\nPaiement créé avec succès!");
            System.out.println("ID: " + paiement.getIdPaiement());
            System.out.println("Type: " + paiement.getTypePaiement());
            System.out.println("Montant: " + paiement.getMontant() + " DH");
            System.out.println("Date: " + paiement.getDatePaiement());

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }

    private void viewAgentPaiements() {
        System.out.println("\n--- Paiements d'un Agent ---");

        try {
            System.out.print("Email de l'agent: ");
            String email = inputHandler.getStringInput("");

            Agent agent = agentService.findAgentByEmail(email);
            if (agent == null) {
                System.out.println("Agent non trouvé!");
                inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
                return;
            }

            List<Paiement> paiements = paiementService.getPaiementsByAgent(agent.getIdAgent());

            if (paiements.isEmpty()) {
                System.out.println("\nAucun paiement trouvé pour " + agent.getNomComplet());
            } else {
                System.out.println("\nPaiements de " + agent.getNomComplet() + ":");
                System.out.println("Total: " + paiements.size() + " paiements\n");

                for (Paiement p : paiements) {
                    displayPaiementDetails(p);
                }
            }

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }

    private void viewAllPaiements() {
        System.out.println("\n--- Tous les Paiements ---");

        try {
            List<Paiement> paiements = paiementService.getAllPaiements();

            if (paiements.isEmpty()) {
                System.out.println("\nAucun paiement trouvé dans le système.");
            } else {
                System.out.println("\nTotal: " + paiements.size() + " paiements\n");

                for (Paiement p : paiements) {
                    Agent agent = agentService.findAgentById(p.getIdAgent());
                    System.out.println("Agent: " + (agent != null ? agent.getNomComplet() : "Inconnu"));
                    displayPaiementDetails(p);
                }
            }

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }

    private void deletePaiement() {
        System.out.println("\n--- Supprimer un Paiement ---");

        try {
            int paiementId = inputHandler.getIntInput("ID du paiement à supprimer: ");

            Paiement paiement = paiementService.findPaiementById(paiementId);
            if (paiement == null) {
                System.out.println("Paiement non trouvé!");
                inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
                return;
            }

            displayPaiementDetails(paiement);

            boolean confirm = inputHandler.getBooleanInput("\nÊtes-vous sûr de vouloir supprimer ce paiement?");

            if (confirm) {
                paiementService.deletePaiement(paiementId);
                System.out.println("Paiement supprimé avec succès!");
            } else {
                System.out.println("Suppression annulée.");
            }

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }

    private void displayPaiementDetails(Paiement p) {
        System.out.println("  ID: " + p.getIdPaiement());
        System.out.println("  Type: " + p.getTypePaiement());
        System.out.println("  Montant: " + p.getMontant() + " DH");
        System.out.println("  Date: " + p.getDatePaiement());

        if (p.getMotif() != null && !p.getMotif().isEmpty()) {
            System.out.println("  Motif: " + p.getMotif());
        }

        if (p.getEvenement() != null && !p.getEvenement().isEmpty()) {
            System.out.println("  Événement: " + p.getEvenement());
        }

        if (p.requiresConditionValidation()) {
            System.out.println("  Condition validée: " + (p.isConditionValidee() ? "Oui" : "Non"));
        }

        System.out.println();
    }
}