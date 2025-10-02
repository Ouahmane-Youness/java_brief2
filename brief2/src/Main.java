import com.paymentManagement.model.entity.Agent;
import com.paymentManagement.model.entity.Departement;
import com.paymentManagement.model.enums.TypeAgent;
import com.paymentManagement.service.impl.AgentServiceImpl;
import com.paymentManagement.service.impl.DepartementServiceImpl;
import com.paymentManagement.view.menu.AgentMenu;

import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final AgentServiceImpl agentService = new AgentServiceImpl();
    private static final DepartementServiceImpl departementService = new DepartementServiceImpl();

    public static void main(String[] args) {
        printWelcomeBanner();

        try {
            if (isFirstRun()) {
                runFirstTimeSetup();
            }

            runMainApplication();

        } catch (Exception e) {
            System.err.println("\nErreur critique: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }

        printGoodbyeMessage();
    }

    private static void printWelcomeBanner() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("          SYSTEME DE GESTION DES PAIEMENTS");
        System.out.println("          Payment Management System");
        System.out.println("=".repeat(60));
        System.out.println();
    }

    private static boolean isFirstRun() {
        try {
            return agentService.getAllAgents().isEmpty() &&
                    departementService.getAllDepartements().isEmpty();
        } catch (Exception e) {
            return true;
        }
    }

    private static void runFirstTimeSetup() {
        System.out.println("=".repeat(60));
        System.out.println("  PREMIERE UTILISATION - CONFIGURATION INITIALE");
        System.out.println("=".repeat(60));
        System.out.println("\nLe systeme est vide. Configuration du premier compte administrateur...\n");

        try {
            System.out.println("ETAPE 1: Creation du premier departement");
            System.out.println("-".repeat(60));
            System.out.print("Nom du departement: ");
            String deptName = scanner.nextLine().trim();

            Departement firstDept = departementService.createDepartement(deptName);
            System.out.println("Departement cree avec ID: " + firstDept.getIdDepartement());

            System.out.println("\nETAPE 2: Creation du compte Directeur");
            System.out.println("-".repeat(60));
            System.out.print("Votre nom: ");
            String nom = scanner.nextLine().trim();

            System.out.print("Votre prenom: ");
            String prenom = scanner.nextLine().trim();

            System.out.print("Votre email: ");
            String email = scanner.nextLine().trim();

            System.out.print("Votre mot de passe (min 6 caracteres): ");
            String password = scanner.nextLine().trim();

            Agent director = agentService.createAgent(nom, prenom, email, password, TypeAgent.DIRECTEUR);
            agentService.assignToDepartment(director.getIdAgent(), firstDept.getIdDepartement());
            departementService.assignManager(firstDept.getIdDepartement(), director.getIdAgent());

            System.out.println("\n" + "=".repeat(60));
            System.out.println("  CONFIGURATION TERMINEE AVEC SUCCES!");
            System.out.println("=".repeat(60));
            System.out.println("\nVos identifiants:");
            System.out.println("  Email: " + email);
            System.out.println("  Type: DIRECTEUR");
            System.out.println("  Departement: " + firstDept.getNom());
            System.out.println("\nVous pouvez maintenant vous connecter.");
            System.out.println();

            waitForEnter();

        } catch (Exception e) {
            System.err.println("\nErreur lors de la configuration: " + e.getMessage());
            System.err.println("L'application va se fermer. Veuillez reessayer.");
            System.exit(1);
        }
    }

    private static void runMainApplication() {
        boolean running = true;

        while (running) {
            displayMainMenu();

            int choice = getIntInput("Votre choix: ");

            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    displaySystemInfo();
                    break;
                case 3:
                    displayAbout();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("\nChoix invalide! Veuillez choisir entre 0 et 3.");
                    waitForEnter();
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                    MENU PRINCIPAL");
        System.out.println("=".repeat(60));
        System.out.println("  1. Connexion");
        System.out.println("  2. Informations Systeme");
        System.out.println("  3. A Propos");
        System.out.println("  0. Quitter");
        System.out.println("=".repeat(60));
    }

    private static void handleLogin() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                     CONNEXION");
        System.out.println("=".repeat(60));

        int attempts = 0;
        final int MAX_ATTEMPTS = 3;
        boolean authenticated = false;

        while (attempts < MAX_ATTEMPTS && !authenticated) {
            System.out.print("\nEmail: ");
            String email = scanner.nextLine().trim();

            System.out.print("Mot de passe: ");
            String password = scanner.nextLine().trim();

            System.out.println("\nVerification des identifiants...");

            try {
                Agent authenticatedAgent = agentService.authenticate(email, password);

                if (authenticatedAgent != null) {
                    authenticated = true;
                    displayLoginSuccess(authenticatedAgent);

                    waitForEnter();

                    AgentMenu agentMenu = new AgentMenu(authenticatedAgent);
                    agentMenu.show();

                } else {
                    attempts++;
                    System.out.println("\n" + "=".repeat(60));
                    System.out.println("  ECHEC DE LA CONNEXION");
                    System.out.println("=".repeat(60));
                    System.out.println("\nEmail ou mot de passe incorrect!");

                    if (attempts < MAX_ATTEMPTS) {
                        System.out.println("Tentatives restantes: " + (MAX_ATTEMPTS - attempts));

                        System.out.print("\nVoulez-vous reessayer? (oui/non): ");
                        String retry = scanner.nextLine().trim().toLowerCase();

                        if (!retry.equals("oui") && !retry.equals("o") && !retry.equals("yes") && !retry.equals("y")) {
                            break;
                        }
                    } else {
                        System.out.println("\nNombre maximum de tentatives atteint.");
                        waitForEnter();
                    }
                }

            } catch (Exception e) {
                System.out.println("\nErreur lors de la connexion: " + e.getMessage());
                waitForEnter();
                break;
            }
        }
    }

    private static void displayLoginSuccess(Agent agent) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  AUTHENTIFICATION REUSSIE");
        System.out.println("=".repeat(60));
        System.out.println("\nBienvenue, " + agent.getNomComplet());
        System.out.println("Type: " + agent.getTypeAgent());

        if (agent.getIdDepartement() != null) {
            try {
                Departement dept = departementService.findDepartementById(agent.getIdDepartement());
                if (dept != null) {
                    System.out.println("Departement: " + dept.getNom());
                }
            } catch (Exception e) {
                // Silent fail
            }
        }

        System.out.println("\nAcces autorise au systeme.");
        System.out.println();
    }

    private static void displaySystemInfo() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              INFORMATIONS SYSTEME");
        System.out.println("=".repeat(60));

        try {
            var allAgents = agentService.getAllAgents();
            var allDepartments = departementService.getAllDepartements();

            System.out.println("\nSTATISTIQUES GLOBALES:");
            System.out.println("  Nombre total d'agents: " + allAgents.size());
            System.out.println("  Nombre de departements: " + allDepartments.size());

            if (!allDepartments.isEmpty()) {
                System.out.println("\nDEPARTEMENTS:");
                for (Departement dept : allDepartments) {
                    int count = departementService.getAgentCount(dept.getIdDepartement());
                    System.out.println("  - " + dept.getNom() + ": " + count + " agent(s)");
                }
            }

            if (!allAgents.isEmpty()) {
                System.out.println("\nREPARTITION PAR TYPE:");
                System.out.println("  - Ouvriers: " + agentService.getAgentsByType(TypeAgent.OUVRIER).size());
                System.out.println("  - Responsables: " +
                        agentService.getAgentsByType(TypeAgent.RESPONSABLE_DEPARTEMENT).size());
                System.out.println("  - Directeurs: " + agentService.getAgentsByType(TypeAgent.DIRECTEUR).size());
                System.out.println("  - Stagiaires: " + agentService.getAgentsByType(TypeAgent.STAGIAIRE).size());
            }

            System.out.println("\nINFORMATION:");
            System.out.println("Pour acceder au systeme complet, connectez-vous");
            System.out.println("avec votre email et mot de passe.");

        } catch (Exception e) {
            System.out.println("\nErreur lors de la recuperation des informations: " + e.getMessage());
        }

        waitForEnter();
    }

    private static void displayAbout() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                    A PROPOS");
        System.out.println("=".repeat(60));
        System.out.println("\nSYSTEME DE GESTION DES PAIEMENTS");
        System.out.println("Version: 1.0");
        System.out.println();
        System.out.println("Description:");
        System.out.println("  Application de gestion des agents, departements et paiements.");
        System.out.println("  Permet le suivi complet des paiements avec statistiques");
        System.out.println("  detaillees et detection d'anomalies.");
        System.out.println();
        System.out.println("Fonctionnalites principales:");
        System.out.println("  - Gestion des agents et departements");
        System.out.println("  - Gestion des paiements (Salaire, Prime, Bonus, Indemnite)");
        System.out.println("  - Filtrage et tri des paiements");
        System.out.println("  - Statistiques par agent et departement");
        System.out.println("  - Statistiques globales de l'entreprise");
        System.out.println("  - Detection des paiements inhabituels");
        System.out.println();
        System.out.println("Technologies:");
        System.out.println("  - Java (POO, Collections, Streams API)");
        System.out.println("  - JDBC pour la persistance");
        System.out.println("  - Architecture MVC");
        System.out.println();
        System.out.println("Developpe par: [Votre Nom]");
        System.out.println("Date: 2025");
        System.out.println();

        waitForEnter();
    }

    private static void printGoodbyeMessage() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  Merci d'avoir utilise le systeme de gestion!");
        System.out.println("  Au revoir!");
        System.out.println("=".repeat(60));
        System.out.println();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre valide!");
            }
        }
    }

    private static void waitForEnter() {
        System.out.print("\nAppuyez sur Entree pour continuer...");
        scanner.nextLine();
    }
}