package com.paymentManagement.view.menu;

import com.paymentManagement.model.entity.Agent;
import com.paymentManagement.model.entity.Departement;
import com.paymentManagement.model.enums.TypeAgent;
import com.paymentManagement.service.impl.AgentServiceImpl;
import com.paymentManagement.service.impl.DepartementServiceImpl;
import com.paymentManagement.view.InputHandler;

public class ManagementMenu {

    private final Agent loggedInManager;
    private final AgentServiceImpl agentService;
    private final DepartementServiceImpl departementService;
    private final InputHandler inputHandler;

    public ManagementMenu(Agent loggedInManager) {
        this.loggedInManager = loggedInManager;
        this.agentService = new AgentServiceImpl();
        this.departementService = new DepartementServiceImpl();
        this.inputHandler = new InputHandler();
    }

    public void show() {
        boolean running = true;

        while (running) {
            displayManagementMenu();

            int choice = inputHandler.getIntInput("Votre choix: ");

            switch (choice) {
                case 1:
                    if (isDirector()) {
                        manageAgents();
                    } else {
                        System.out.println("Accès refusé! Seuls les directeurs peuvent gérer les agents.");
                        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
                    }
                    break;
                case 2:
                    if (isDirector()) {
                        manageDepartments();
                    } else {
                        System.out.println("Accès refusé! Seuls les directeurs peuvent gérer les départements.");
                        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
                    }
                    break;
                case 3:
                    showReports();
                    break;
                case 4:
                    viewMyDepartmentTeam();
                    break;
                case 5:
                    managePaiements();
                    break;
                case 6:
                    showAdvancedStatistics();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        }
    }

    private void displayManagementMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    MENU GESTION - " + loggedInManager.getNomComplet());
        System.out.println("    Niveau: " + loggedInManager.getTypeAgent());
        System.out.println("=".repeat(50));

        if (isDirector()) {
            System.out.println("1. Gestion des Agents (DIRECTEUR)");
            System.out.println("2. Gestion des Départements (DIRECTEUR)");
        } else {
            System.out.println("1. Gestion des Agents (DIRECTEUR uniquement)");
            System.out.println("2. Gestion des Départements (DIRECTEUR uniquement)");
        }

        System.out.println("3. Rapports et Statistiques");
        System.out.println("4. Voir Mon Équipe");
        System.out.println("5. Gestion des Paiements");
        System.out.println("6. Statistiques Avancées");
        System.out.println("0. Retour au Menu Agent");
        System.out.println("=".repeat(50));
    }

    private boolean isDirector() {
        return loggedInManager.getTypeAgent() == TypeAgent.DIRECTEUR;
    }

    private void managePaiements() {
        PaiementMenu paiementMenu = new PaiementMenu(loggedInManager);
        paiementMenu.show();
    }

    private void showAdvancedStatistics() {
        ManagerStatisticsMenu statsMenu = new ManagerStatisticsMenu(loggedInManager);
        statsMenu.show();
    }

    private void manageAgents() {
        boolean running = true;

        while (running) {
            System.out.println("\n" + "-".repeat(40));
            System.out.println("         GESTION DES AGENTS");
            System.out.println("         (Réservé aux Directeurs)");
            System.out.println("-".repeat(40));
            System.out.println("1. Créer un Nouvel Agent");
            System.out.println("2. Lister Tous les Agents");
            System.out.println("3. Rechercher un Agent");
            System.out.println("4. Modifier un Agent");
            System.out.println("0. Retour");
            System.out.println("-".repeat(40));

            int choice = inputHandler.getIntInput("Votre choix: ");

            switch (choice) {
                case 1:
                    createAgent();
                    break;
                case 2:
                    listAllAgents();
                    waitForEnter();
                    break;
                case 3:
                    searchAgent();
                    waitForEnter();
                    break;
                case 4:
                    modifyAgent();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        }
    }

    private void createAgent() {
        System.out.println("\nCréation d'un nouvel agent:");
        System.out.println("Tous les agents doivent être assignés à un département dès la création.\n");

        try {
            var departments = departementService.getAllDepartements();

            if (departments.isEmpty()) {
                System.out.println("Aucun département disponible! Créez d'abord un département.");
                inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
                return;
            }

            // Collect agent information
            String nom = inputHandler.getStringInput("Nom: ");
            String prenom = inputHandler.getStringInput("Prénom: ");
            String email = inputHandler.getStringInput("Email: ");
            String password = inputHandler.getStringInput("Mot de passe (min 6 caractères): ");

            System.out.println("\nTypes d'agents disponibles:");
            System.out.println("1. OUVRIER");
            System.out.println("2. RESPONSABLE_DEPARTEMENT");
            System.out.println("3. DIRECTEUR");
            System.out.println("4. STAGIAIRE");

            int typeChoice = inputHandler.getIntInput("Choisir le type (1-4): ");

            TypeAgent typeAgent;
            switch (typeChoice) {
                case 1: typeAgent = TypeAgent.OUVRIER; break;
                case 2: typeAgent = TypeAgent.RESPONSABLE_DEPARTEMENT; break;
                case 3: typeAgent = TypeAgent.DIRECTEUR; break;
                case 4: typeAgent = TypeAgent.STAGIAIRE; break;
                default:
                    System.out.println("Type invalide!");
                    return;
            }

            System.out.println("\nDépartements disponibles:");
            for (Departement dept : departments) {
                System.out.println("   " + dept.getIdDepartement() + ". " + dept.getNom());
            }

            int deptId = inputHandler.getIntInput("Choisir le département: ");

            Departement selectedDept = departementService.findDepartementById(deptId);
            if (selectedDept == null) {
                System.out.println(" Département invalide! L'ID " + deptId + " n'existe pas.");
                inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
                return;
            }

            Agent newAgent = agentService.createAgent(nom, prenom, email, password, typeAgent);

            agentService.assignToDepartment(newAgent.getIdAgent(), deptId);

            System.out.println("\n Agent créé avec succès!");
            System.out.println("   ID: " + newAgent.getIdAgent());
            System.out.println("   Nom: " + newAgent.getNomComplet());
            System.out.println("   Type: " + typeAgent);
            System.out.println("   Département: " + selectedDept.getNom());

        } catch (Exception e) {
            System.out.println(" Erreur: " + e.getMessage());
        }

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }

    private void listAllAgents() {
        System.out.println("\nListe de tous les agents:");

        try {
            var agents = agentService.getAllAgents();

            if (agents.isEmpty()) {
                System.out.println("Aucun agent trouvé.");
                return;
            }

            System.out.println("Total: " + agents.size() + " agents\n");

            for (Agent agent : agents) {
                System.out.println("ID: " + agent.getIdAgent() + " | " + agent.getNomComplet());
                System.out.println("   Email: " + agent.getEmail() + " | Type: " + agent.getTypeAgent());

                if (agent.getIdDepartement() != null) {
                    Departement dept = departementService.findDepartementById(agent.getIdDepartement());
                    System.out.println("   Département: " + (dept != null ? dept.getNom() : "Département inconnu"));
                } else {
                    System.out.println("   PAS DE DÉPARTEMENT (Erreur de données)");
                }
                System.out.println();
            }

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }

    private void searchAgent() {
        System.out.println("\nRecherche d'agent:");
        String email = inputHandler.getStringInput("Email de l'agent: ");

        try {
            Agent agent = agentService.findAgentByEmail(email);

            if (agent != null) {
                System.out.println("Agent trouvé:");
                System.out.println("   ID: " + agent.getIdAgent());
                System.out.println("   Nom: " + agent.getNomComplet());
                System.out.println("   Email: " + agent.getEmail());
                System.out.println("   Type: " + agent.getTypeAgent());

                if (agent.getIdDepartement() != null) {
                    Departement dept = departementService.findDepartementById(agent.getIdDepartement());
                    System.out.println("   Département: " + (dept != null ? dept.getNom() : "Inconnu"));
                }
            } else {
                System.out.println("Aucun agent trouvé avec cet email.");
            }

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }

    private void modifyAgent() {
        System.out.println("\nModification d'un agent:");

        String email = inputHandler.getStringInput("Email de l'agent à modifier: ");

        try {
            Agent agent = agentService.findAgentByEmail(email);

            if (agent == null) {
                System.out.println("Agent non trouvé!");
                inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
                return;
            }

            System.out.println("Agent trouvé: " + agent.getNomComplet());
            System.out.println("\nQue souhaitez-vous modifier?");
            System.out.println("1. Nom et Prénom");
            System.out.println("2. Type d'agent");
            System.out.println("3. Département");
            System.out.println("0. Annuler");

            int modifyChoice = inputHandler.getIntInput("Votre choix: ");

            switch (modifyChoice) {
                case 1:
                    String newNom = inputHandler.getStringInput("Nouveau nom [" + agent.getNom() + "]: ");
                    if (!newNom.isEmpty()) agent.setNom(newNom);

                    String newPrenom = inputHandler.getStringInput("Nouveau prénom [" + agent.getPrenom() + "]: ");
                    if (!newPrenom.isEmpty()) agent.setPrenom(newPrenom);

                    String newEmail = inputHandler.getStringInput("Nouvel email [" + agent.getEmail() + "]: ");
                    if (!newEmail.isEmpty()) {
                        Agent existingAgent = agentService.findAgentByEmail(newEmail);
                        if (existingAgent != null && !existingAgent.getIdAgent().equals(agent.getIdAgent())) {
                            System.out.println(" Cet email est déjà utilisé par un autre agent!");
                            break;
                        }
                        agent.setEmail(newEmail);
                    }

                    agentService.updateAgent(agent);
                    System.out.println(" Agent modifié avec succès!");
                    break;

                case 2:
                    System.out.println("Type actuel: " + agent.getTypeAgent());
                    System.out.println("\nNouveaux types disponibles:");
                    System.out.println("1. OUVRIER");
                    System.out.println("2. RESPONSABLE_DEPARTEMENT");
                    System.out.println("3. DIRECTEUR");
                    System.out.println("4. STAGIAIRE");

                    int typeChoice = inputHandler.getIntInput("Choisir le nouveau type (1-4): ");
                    TypeAgent newType = switch (typeChoice) {
                        case 1 -> TypeAgent.OUVRIER;
                        case 2 -> TypeAgent.RESPONSABLE_DEPARTEMENT;
                        case 3 -> TypeAgent.DIRECTEUR;
                        case 4 -> TypeAgent.STAGIAIRE;
                        default -> null;
                    };

                    if (newType != null) {
                        agent.setTypeAgent(newType);
                        agentService.updateAgent(agent);
                        System.out.println("Type modifié avec succès!");
                    }
                    break;

                case 3:
                    var departments = departementService.getAllDepartements();
                    System.out.println("\nDépartements disponibles:");
                    for (Departement dept : departments) {
                        System.out.println("   " + dept.getIdDepartement() + ". " + dept.getNom());
                    }

                    int newDeptId = inputHandler.getIntInput("Nouveau département: ");
                    agentService.assignToDepartment(agent.getIdAgent(), newDeptId);
                    System.out.println("Département modifié avec succès!");
                    break;

                case 0:
                    System.out.println("Modification annulée.");
                    break;
            }

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }

    private void manageDepartments() {
        boolean running = true;

        while (running) {
            System.out.println("\n" + "-".repeat(40));
            System.out.println("       GESTION DES DÉPARTEMENTS");
            System.out.println("       (Réservé aux Directeurs)");
            System.out.println("-".repeat(40));
            System.out.println("1. Créer un Département");
            System.out.println("2. Lister Tous les Départements");
            System.out.println("3. Assigner un Responsable");
            System.out.println("4. Modifier un Département");
            System.out.println("5. Supprimer un Département");
            System.out.println("0. Retour");
            System.out.println("-".repeat(40));

            int choice = inputHandler.getIntInput("Votre choix: ");

            switch (choice) {
                case 1: createDepartment(); break;
                case 2: listAllDepartments(); waitForEnter(); break;
                case 3: assignManager(); break;
                case 4: modifyDepartment(); break;
                case 5: deleteDepartment(); break;
                case 0: running = false; break;
                default: System.out.println("Choix invalide!");
            }
        }
    }

    private void createDepartment() {
        System.out.println("\nCréation d'un nouveau département:");

        try {
            String nom = inputHandler.getStringInput("Nom du département: ");

            Departement newDept = departementService.createDepartement(nom);
            System.out.println("Département créé avec succès!");
            System.out.println("   ID: " + newDept.getIdDepartement());
            System.out.println("   Nom: " + newDept.getNom());

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }

    private void listAllDepartments() {
        System.out.println("\nListe de tous les départements:");

        try {
            var departments = departementService.getAllDepartements();

            if (departments.isEmpty()) {
                System.out.println("Aucun département trouvé.");
                return;
            }

            System.out.println("Total: " + departments.size() + " départements\n");

            for (Departement dept : departments) {
                System.out.println(dept.getNom() + " (ID: " + dept.getIdDepartement() + ")");

                if (dept.getResponsableId() != null) {
                    Agent manager = agentService.findAgentById(dept.getResponsableId());
                    System.out.println("   Responsable: " + (manager != null ? manager.getNomComplet() : "Inconnu"));
                } else {
                    System.out.println("   Responsable: Aucun assigné");
                }

                int agentCount = departementService.getAgentCount(dept.getIdDepartement());
                System.out.println("   Nombre d'agents: " + agentCount);
                System.out.println();
            }

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }

    private void assignManager() {
        System.out.println("\nAssignation d'un responsable:");

        try {
            listAllDepartments();

            int deptId = inputHandler.getIntInput("\nID du département: ");

            System.out.println("\nManagers disponibles (Responsables et Directeurs):");
            var managers = agentService.getManagers();

            if (managers.isEmpty()) {
                System.out.println("Aucun manager disponible!");
                inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
                return;
            }

            for (Agent manager : managers) {
                System.out.println("   " + manager.getIdAgent() + ". " + manager.getNomComplet() +
                        " (" + manager.getTypeAgent() + ")");
            }

            int managerId = inputHandler.getIntInput("\nID du manager: ");

            departementService.assignManager(deptId, managerId);

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }

    private void modifyDepartment() {
        System.out.println("\nModification d'un département:");

        try {
            listAllDepartments();

            int deptId = inputHandler.getIntInput("\nID du département à modifier: ");

            Departement dept = departementService.findDepartementById(deptId);
            if (dept == null) {
                System.out.println("Département non trouvé!");
                inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
                return;
            }

            System.out.println("Département trouvé: " + dept.getNom());
            String newNom = inputHandler.getStringInput("Nouveau nom [Entrée pour annuler]: ");

            if (!newNom.isEmpty()) {
                dept.setNom(newNom);
                departementService.updateDepartement(dept);
                System.out.println("Département modifié avec succès!");
            } else {
                System.out.println("Aucun changement effectué.");
            }

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }

    private void deleteDepartment() {
        System.out.println("\nSuppression d'un département:");
        System.out.println("Attention: Cette action est irréversible!");

        try {
            listAllDepartments();

            int deptId = inputHandler.getIntInput("\nID du département à supprimer: ");

            Departement dept = departementService.findDepartementById(deptId);
            if (dept == null) {
                System.out.println("Département non trouvé!");
                inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
                return;
            }

            System.out.println("Département: " + dept.getNom());
            boolean confirm = inputHandler.getBooleanInput("Êtes-vous sûr?");

            if (confirm) {
                departementService.deleteDepartement(deptId);
            } else {
                System.out.println("Suppression annulée.");
            }

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }

    private void showReports() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("         RAPPORTS ET STATISTIQUES");
        System.out.println("-".repeat(50));

        try {
            var allAgents = agentService.getAllAgents();
            var allDepartments = departementService.getAllDepartements();

            System.out.println("STATISTIQUES GÉNÉRALES:");
            System.out.println("   • Total agents: " + allAgents.size());
            System.out.println("   • Total départements: " + allDepartments.size());

            System.out.println("\nRÉPARTITION PAR TYPE:");
            System.out.println("   • Ouvriers: " + agentService.getAgentsByType(TypeAgent.OUVRIER).size());
            System.out.println("   • Responsables: " + agentService.getAgentsByType(TypeAgent.RESPONSABLE_DEPARTEMENT).size());
            System.out.println("   • Directeurs: " + agentService.getAgentsByType(TypeAgent.DIRECTEUR).size());
            System.out.println("   • Stagiaires: " + agentService.getAgentsByType(TypeAgent.STAGIAIRE).size());

            System.out.println("\nDÉPARTEMENTS:");
            for (Departement dept : allDepartments) {
                int count = departementService.getAgentCount(dept.getIdDepartement());
                System.out.println("   • " + dept.getNom() + ": " + count + " agents");
            }

            System.out.println("\nÉLIGIBILITÉ:");
            System.out.println("   • Agents éligibles aux bonus: " + agentService.getBonusEligibleAgents().size());
            System.out.println("   • Agents éligibles aux indemnités: " + agentService.getIndemnityEligibleAgents().size());

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
    }

    private void viewMyDepartmentTeam() {
        System.out.println("\nMON ÉQUIPE");

        if (loggedInManager.getIdDepartement() == null) {
            System.out.println("Vous n'êtes pas assigné à un département.");
            inputHandler.waitForEnter("\nAppuyez sur Entrée pour continuer...");
            return;
        }

        try {
            Departement myDept = departementService.findDepartementById(loggedInManager.getIdDepartement());
            var teamMembers = agentService.getAgentsByDepartment(loggedInManager.getIdDepartement());

            System.out.println("Département: " + (myDept != null ? myDept.getNom() : "Inconnu"));
            System.out.println("Membres de l'équipe: " + teamMembers.size() + "\n");

            for (Agent agent : teamMembers) {
                String isMe = agent.getIdAgent().equals(loggedInManager.getIdAgent()) ? " (Vous)" : "";
                System.out.println(agent.getNomComplet() + isMe);
                System.out.println("   Type: " + agent.getTypeAgent());
                System.out.println("   Email: " + agent.getEmail());
                System.out.println();
            }

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        inputHandler.waitForEnter("Appuyez sur Entrée pour continuer...");
    }

    private void waitForEnter() {
        inputHandler.waitForEnter("Appuyez sur Entrée pour continuer...");
    }
}