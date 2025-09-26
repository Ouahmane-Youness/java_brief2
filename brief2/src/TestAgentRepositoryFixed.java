
import com.paymentManagement.model.entity.Agent;
import com.paymentManagement.model.entity.Departement;
import com.paymentManagement.model.enums.TypeAgent;
import com.paymentManagement.repository.imp.AgentRepositoryImpl;
import com.paymentManagement.repository.imp.DepartementRepositoryImpl;

public class TestAgentRepositoryFixed {

    public static void main(String[] args) {
        System.out.println("=== Testing AgentRepository (Your Version) ===\n");

        AgentRepositoryImpl agentRepo = new AgentRepositoryImpl();
        DepartementRepositoryImpl deptRepo = new DepartementRepositoryImpl();

        try {
            // First, create a department for our agents
            System.out.println("0. Creating test department...");
            Departement dept = new Departement(5, "IT Test Department", 3);
            deptRepo.save(dept);

            // Find the department to get its ID
            Departement savedDept = deptRepo.findByNom("IT Test Department");
            if (savedDept == null) {
                System.out.println("   ✗ Could not create or find department");
                return;
            }
            System.out.println("   ✓ Department created with ID: " + savedDept.getIdDepartement());

            // Test 1: Save an agent
            System.out.println("\n1. Testing save agent...");
            Agent agent = new Agent();
            agent.setNom("Dupont");
            agent.setPrenom("Jean");
            agent.setEmail("jean.dupont@test.com");
            agent.setMotDePasse("password123");
            agent.setTypeAgent(TypeAgent.OUVRIER);
            agent.setIdDepartement(savedDept.getIdDepartement());

            agentRepo.save(agent);
            System.out.println("   ✓ Agent saved: " + agent.getNomComplet());

            // Test 2: Find by email
            System.out.println("\n2. Testing findByEmail...");
            Agent foundAgent = agentRepo.findByEmail("jean.dupont@test.com");
            if (foundAgent != null) {
                System.out.println("   ✓ Found agent: " + foundAgent.getNomComplet());
                System.out.println("      ID: " + foundAgent.getIdAgent());
                System.out.println("      Type: " + foundAgent.getTypeAgent());
                System.out.println("      Department ID: " + foundAgent.getIdDepartement());
            } else {
                System.out.println("   ✗ Agent not found by email");
                return;
            }

            // Test 3: Find by ID
            System.out.println("\n3. Testing findById...");
            Agent foundById = agentRepo.findById(foundAgent.getIdAgent());
            if (foundById != null) {
                System.out.println("   ✓ Found by ID: " + foundById.getNomComplet());
            } else {
                System.out.println("   ✗ Agent not found by ID");
            }

            // Test 4: Create another agent with different type
            System.out.println("\n4. Testing save manager agent...");
            Agent manager = new Agent();
            manager.setNom("Martin");
            manager.setPrenom("Marie");
            manager.setEmail("marie.martin@test.com");
            manager.setMotDePasse("password456");
            manager.setTypeAgent(TypeAgent.RESPONSABLE_DEPARTEMENT);
            manager.setIdDepartement(savedDept.getIdDepartement());

            agentRepo.save(manager);
            System.out.println("   ✓ Manager saved: " + manager.getNomComplet());

            // Test 5: Find by department
            System.out.println("\n5. Testing findByDepartementId...");
            var agentsInDept = agentRepo.findByDepartementId(savedDept.getIdDepartement());
            System.out.println("   ✓ Found " + agentsInDept.size() + " agents in department:");
            for (Agent a : agentsInDept) {
                System.out.println("      - " + a.getNomComplet() + " (" + a.getTypeAgent() + ")");
            }

            // Test 6: Find by type
            System.out.println("\n6. Testing findByTypeAgent...");
            var managers = agentRepo.findByTypeAgent(TypeAgent.RESPONSABLE_DEPARTEMENT);
            System.out.println("   ✓ Found " + managers.size() + " managers:");
            for (Agent a : managers) {
                System.out.println("      - " + a.getNomComplet());
            }

            // Test 7: Find all agents
            System.out.println("\n7. Testing findAll...");
            var allAgents = agentRepo.findAll();
            System.out.println("   ✓ Found " + allAgents.size() + " total agents:");
            for (Agent a : allAgents) {
                System.out.println("      - " + a.getNomComplet() + " (" + a.getTypeAgent() + ")");
            }

            // Test 8: Update agent
            System.out.println("\n8. Testing update agent...");
            foundAgent.setTypeAgent(TypeAgent.RESPONSABLE_DEPARTEMENT);
            agentRepo.update(foundAgent);

            Agent updatedAgent = agentRepo.findById(foundAgent.getIdAgent());
            if (updatedAgent != null) {
                System.out.println("   ✓ Updated agent type: " + updatedAgent.getTypeAgent());
            } else {
                System.out.println("   ✗ Could not find updated agent");
            }

            System.out.println("\n=== All agent tests completed successfully! ===");

            // Note: We're not deleting test data to keep it simple
            // You can delete manually from database if needed

        } catch (Exception e) {
            System.out.println("✗ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}