package com.paymentManagement.view.menu;

import com.paymentManagement.model.entity.Agent;
import com.paymentManagement.model.entity.Departement;
import com.paymentManagement.model.entity.Paiement;
import com.paymentManagement.service.impl.AgentServiceImpl;
import com.paymentManagement.service.impl.DepartementServiceImpl;
import com.paymentManagement.service.impl.PaiementServiceImpl;
import com.paymentManagement.view.InputHandler;

import java.math.BigDecimal;
import java.util.List;

public class AgentMenu {

    private final Agent loggedInAgent;
    private final AgentServiceImpl agentService;
    private final DepartementServiceImpl departementService;
    private final PaiementServiceImpl paiementService;
    private final InputHandler inputHandler;

    public AgentMenu(Agent loggedInAgent) {
        this.loggedInAgent = loggedInAgent;
        this.agentService = new AgentServiceImpl();
        this.departementService = new DepartementServiceImpl();
        this.paiementService = new PaiementServiceImpl();
        this.inputHandler = new InputHandler();
    }

    public void show() {
        boolean running = true;
        while (running) {
            displayAgentMenu();
            int choice = inputHandler.getIntInput("Votre choix: ");
            switch (choice) {
                case 1:
                    showMyProfile();
                    break;

                case 2:
                    showMyDepartement(loggedInAgent);
                    break;

                case 3:
                    showMyPaiements();
                    break;

                case 4:
                    showStatistics();
                    break;

                case 5:
                    if (loggedInAgent.getTypeAgent().name().contains("RESPONSABLE") ||
                            loggedInAgent.getTypeAgent().name().contains("DIRECTEUR")) {
                        showAllAgents();
                    } else {
                        System.out.println("Accès refusé! Vous n'avez pas les droits d'afficher les Agents.");
                    }
                    break;

                case 6:
                    if (loggedInAgent.getTypeAgent().name().contains("RESPONSABLE") ||
                            loggedInAgent.getTypeAgent().name().contains("DIRECTEUR")) {
                        showManagerOptions();
                    } else {
                        System.out.println("Accès refusé! Vous n'avez pas les droits de gestion.");
                    }
                    break;

                case 0:
                    running = false;
                    System.out.println("Déconnexion réussie!");
                    break;

                default:
                    System.out.println("Choix invalide");
            }
        }
    }

    private void displayAgentMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Menu Agent - " + loggedInAgent.getNom());
        System.out.println("Fonction: " + loggedInAgent.getTypeAgent());
        System.out.println("=".repeat(50));
        System.out.println("1. Mon profil");
        System.out.println("2. Mon département");
        System.out.println("3. Mes paiements");
        System.out.println("4. Filtres et statistiques");

        if (loggedInAgent.getTypeAgent().name().contains("RESPONSABLE") ||
                loggedInAgent.getTypeAgent().name().contains("DIRECTEUR")) {
            System.out.println("5. Voir tous les agents");
            System.out.println("6. Options de Gestion");
        }

        System.out.println("0. Déconnexion");
        System.out.println("=".repeat(50));
    }

    private void showMyProfile() {
        System.out.println("\n--- Informations personnelles ---");
        System.out.println("Nom: " + loggedInAgent.getNomComplet());
        System.out.println("Fonction: " + loggedInAgent.getTypeAgent());
        System.out.println("Email: " + loggedInAgent.getEmail());

        System.out.println("\nDroits et privilèges:");
        System.out.println("   • Éligible aux bonus: " + (loggedInAgent.isEligibleForBonus() ? "Oui" : "Non"));
        System.out.println("   • Éligible aux indemnités: " + (loggedInAgent.isEligibleForIndemnity() ? "Oui" : "Non"));

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }

    private void showMyDepartement(Agent loggedInAgent) {
        try {
            if (loggedInAgent.getIdDepartement() == null) {
                System.out.println("\nVous n'êtes pas assigné à un département.");
                inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
                return;
            }

            Departement departement = departementService.findDepartementById(loggedInAgent.getIdDepartement());

            if (departement == null) {
                System.out.println("\nDépartement non trouvé.");
                inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
                return;
            }

            System.out.println("\n--- Mon Département ---");
            System.out.println("Département: " + departement.getNom());

            if (departement.getResponsableId() != null) {
                Agent responsable = agentService.findAgentById(departement.getResponsableId());
                if (responsable != null) {
                    System.out.println("Responsable: " + responsable.getNomComplet());
                } else {
                    System.out.println("Responsable: Aucun");
                }
            } else {
                System.out.println("Responsable: Aucun");
            }

            int agentCount = departementService.getAgentCount(departement.getIdDepartement());
            System.out.println("Nombre d'agents: " + agentCount);

            inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");

        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération du département: " + e.getMessage());
            inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
        }
    }

    private void showMyPaiements() {
        System.out.println("\n--- Mes Paiements ---");

        try {
            List<Paiement> paiements = paiementService.getPaiementsByAgent(loggedInAgent.getIdAgent());

            if (paiements.isEmpty()) {
                System.out.println("\nVous n'avez aucun paiement enregistré.");
            } else {
                System.out.println("\nTotal: " + paiements.size() + " paiement(s)\n");

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
                System.out.println("TOTAL DE VOS PAIEMENTS: " + total + " DH");
                System.out.println("=".repeat(50));
            }

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }

    private void showStatistics() {
        AgentStatisticsMenu statisticsMenu = new AgentStatisticsMenu(loggedInAgent);
        statisticsMenu.show();
    }

    private void showAllAgents() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("              TOUS LES AGENTS");
        System.out.println("-".repeat(50));

        try {
            List<Agent> agents = agentService.getAllAgents();
            if (agents.isEmpty()) {
                System.out.println("Aucun agent trouvé dans le système.");
                inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
                return;
            }

            List<Agent> agentsByCurrentDepartement = agents.stream()
                    .filter(agent -> agent.getDepartementId() != null &&
                            agent.getDepartementId().equals(loggedInAgent.getIdDepartement()))
                    .toList();

            if (agentsByCurrentDepartement.isEmpty()) {
                System.out.println("Aucun agent trouvé dans votre département.");
                inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
                return;
            }

            System.out.println("Agents de votre département:\n");
            for (Agent agent : agentsByCurrentDepartement) {
                System.out.println("ID: " + agent.getIdAgent() + " | " + agent.getNomComplet());
                System.out.println("  Email: " + agent.getEmail());
                System.out.println("  Type: " + agent.getTypeAgent());
                System.out.println();
            }

        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des agents: " + e.getMessage());
        }

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }

    private void showManagerOptions() {
        System.out.println("\nRedirection vers les options de gestion...");

        ManagementMenu managementMenu = new ManagementMenu(loggedInAgent);
        managementMenu.show();
    }
}