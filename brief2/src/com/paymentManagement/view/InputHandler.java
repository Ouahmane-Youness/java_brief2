package com.paymentManagement.view;

import java.util.Scanner;

public class InputHandler {
    private final Scanner scanner;

    public InputHandler() {
        this.scanner = new Scanner(System.in);
    }

    public String getStringInput(String prompt)
    {
        System.out.println(prompt);
        return scanner.nextLine().trim();
    }

    public int getIntInput(String prompt){
        while(true)
        {
            try{
                System.out.println(prompt);
                String input = scanner.nextLine().trim(); // Changed from scanner.next()
                return Integer.parseInt(input);
            }catch (NumberFormatException e)
            {
                System.out.println(" Veuillez entrer un nombre valide!");
            }
        }
    }

    public boolean getBooleanInput(String prompt) {
        while (true) {
            String input = getStringInput(prompt + " (oui/non): ").toLowerCase();
            if (input.equals("oui") || input.equals("yes")) {
                return true;
            } else if (input.equals("non") || input.equals("n") || input.equals("no")) {
                return false;
            } else {
                System.out.println("Veuillez répondre par 'oui' ou 'non'!");
            }
        }

    }

    public void waitForEnter(String message) {
        System.out.print(message);
        scanner.nextLine();
    }

    public void showError(String message) {
        System.out.println("❌ Erreur: " + message);
    }

    public void showSuccess(String message) {
        System.out.println("✅ " + message);
    }

    public void showInfo(String message) {
        System.out.println("ℹ️  " + message);
    }






}

