
import com.paymentManagement.model.entity.Agent;
import com.paymentManagement.model.entity.Departement;
import com.paymentManagement.model.enums.TypeAgent;
import com.paymentManagement.repository.imp.AgentRepositoryImpl;
import com.paymentManagement.repository.imp.DepartementRepositoryImpl;
import com.sun.tools.javac.Main;

public class TestAgentRepositoryFixed {

    public static void main(String[] args) {


        System.out.println("=".repeat(50));
        System.out.println("System gestion de paiements");
        System.out.println("payment management System");
        System.out.println("=".repeat(50));
        System.out.println();

        try{
            MainMenu mainMenu = new MainMenu();
            mainMenu.start();
        }catch (Exception e)
        {
            System.out.println("error starting the App" + e.getMessage());
            e.setStackTrace();
        }
        System.out.println("\nMerci d'avoir utilisé le système de gestion des paiements!");
        System.out.println("Au revoir");


    }

}