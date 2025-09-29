package com.paymentManagement.view.menu;

import com.paymentManagement.model.entity.Agent;
import com.paymentManagement.model.entity.Departement;
import com.paymentManagement.model.enums.TypeAgent;
import com.paymentManagement.repository.interfaces.DepartementRepository;
import com.paymentManagement.service.impl.AgentServiceImpl;
import com.paymentManagement.service.impl.DepartementServiceImpl;
import com.paymentManagement.service.interfaces.DepartementService;
import com.paymentManagement.view.InputHandler;

import java.util.Scanner;

public class AgentMenu {

    private final Agent loggedInAgent;
    private final AgentServiceImpl agentService;
    private final DepartementServiceImpl departementService;
    private final InputHandler inputHandler;


    public AgentMenu(Agent loggedInAgent) {
        this.loggedInAgent = loggedInAgent;
        this.agentService = new AgentServiceImpl();
        this.departementService = new DepartementServiceImpl();
        this.inputHandler = new InputHandler();
    }


    public void show()
    {
        boolean running = true;
        while(running)
        {
            displayAgentMenu();
            int choice = inputHandler.getIntInput("votre choix: ");
            switch(choice)
            {
                case 1:
                    showMyProfile();
                    break;

                case 2:
                    showMyDepartement();
                    break;

                case 3:
                    if (loggedInAgent.getTypeAgent().name().contains("RESPONSABLE") ||
                            loggedInAgent.getTypeAgent().name().contains("DIRECTEUR")) {
                        showAllAgents();
                    } else {
                        System.out.println("Accès refusé! Vous n'avez pas les droits de d'afficher les Agents" +
                                ".");
                    }
                    break;
                case 4:
                    if (loggedInAgent.getTypeAgent().name().contains("RESPONSABLE") ||
                            loggedInAgent.getTypeAgent().name().contains("DIRECTEUR")) {
                        showManagerOptions();
                    } else {
                        System.out.println("Accès refusé! Vous n'avez pas les droits de gestion.");
                    }
                    break;
                case 0:
                     running = false;
                    System.out.println("deconnection réussi!");
                    break;
                default:
                    System.out.println("choix invalide");




            }



        }
    }




    private void displayAgentMenu()
    {
        System.out.println("\n" + "=".repeat(50) );
        System.out.println("Menu Agent - " + loggedInAgent.getNom());
        System.out.println("fonction:" +loggedInAgent.getTypeAgent());
        System.out.println("\n" + "=".repeat(50) );
        System.out.println("1. Mon profil");
        System.out.println("2 mon departement");

        if(loggedInAgent.getTypeAgent().name().contains("RESPONSABLE") || loggedInAgent.getTypeAgent().name().contains("DIRECTEUR"))
        {
            System.out.println("3. voir tous les agents");
            System.out.println("4. Options de Gestion");
        }

    }


    private void showMyProfile()
    {
        System.out.println("Informations personnelles");
        System.out.println("nom: " + loggedInAgent.getNom());
        System.out.println("function : " + loggedInAgent.getTypeAgent());
        System.out.println("email : " + loggedInAgent.getEmail());
        System.out.println("\n🎯 Droits et privilèges:");
        System.out.println("   • Éligible aux bonus: " + (loggedInAgent.isEligibleForBonus() ? " Oui" : " Non"));
        System.out.println("   • Éligible aux indemnités: " + (loggedInAgent.isEligibleForIndemnity() ? " Oui" : " Non"));
        inputHandler.waitForEnter("Appuyez sur Entrée pour continuer.");

    }




    private void getAgentDep(Agent loggedInAgent)
    {
        try{
             Departement departement = departementService.findDepartementById(loggedInAgent.getIdAgent());
             if(departement == null)
             {
                 System.out.println("department not found");
                 return
             }
            System.out.println("departement :" departement.getNom());

             //show Manager
            Agent responsable = agentService.findAgentById(departement.getResponsableId());
            System.out.println("Responsable departement : " + responsable.getNom());

        }
    }


}
