package com.paymentManagement.repository.imp;

import com.paymentManagement.config.database.DatabaseConnection;
import com.paymentManagement.model.entity.Agent;
import com.paymentManagement.model.enums.TypeAgent;
import com.paymentManagement.repository.interfaces.AgentRepository;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgentRepositoryImpl implements AgentRepository {

    @Override
    public void save(Agent agent) {
        try{

        Connection connection = DatabaseConnection.getInstance().getConnection();
        String sql = "INSERT INTO AGENTS (nom, prenom, email, mot_de_passe, type_agent, id_departement) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, agent.getNom());
        stmt.setString(2, agent.getPrenom());
        stmt.setString(3, agent.getEmail());
        stmt.setString(4, agent.getMotDePasse());
        stmt.setObject(5, agent.getTypeAgent().name(), Types.OTHER);

        if(agent.getDepartementId() != null)
        {
            stmt.setInt(6, agent.getDepartementId());
        }else{
            stmt.setNull(6, Types.INTEGER);
        }
        stmt.executeUpdate();
        stmt.close();
        }catch (SQLException e)
        {
            System.out.println("error inserting agent" + e.getMessage());
        }


    }

    @Override
    public Agent findById(Integer id) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT * FROM agents WHERE id_agent = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);

            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                Agent agent = createAgentFromResultSet(resultSet);
                resultSet.close();
                stmt.close();
                return agent;

            }
            resultSet.close();
            stmt.close();
        }catch(SQLException e)
            {
                System.out.println("error finding agent " + e.getMessage());
            }
                return null;

        }
        @Override
        public List<Agent> findAll()
        {
            List<Agent> agents = new ArrayList<>();
            try {
                Connection connection = DatabaseConnection.getInstance().getConnection();
                String sql = "SELECT * FROM agents";
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet resultSet = stmt.executeQuery();
                while(resultSet.next())
                {
                    Agent agent = createAgentFromResultSet(resultSet);
                    agents.add(agent);
                }
                    resultSet.close();
                    stmt.close();
            }catch (SQLException e)
            {
                System.out.println("error fetching agents" + e.getMessage());
            }
            return agents;
        }

    @Override
    public Agent findByEmail(String email) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT * FROM agents WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Agent agent = createAgentFromResultSet(resultSet);

                resultSet.close();
                statement.close();
                return agent;
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            System.out.println("Error finding agent by email: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Agent> findByDepartementId(Integer departmentId) {
        List<Agent> agents = new ArrayList<>();

        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT * FROM agents WHERE id_departement = ? ORDER BY nom, prenom";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, departmentId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Agent agent = createAgentFromResultSet(resultSet);
                agents.add(agent);
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            System.out.println("Error finding agents by department: " + e.getMessage());
        }

        return agents;
    }

    @Override
    public List<Agent> findByTypeAgent(TypeAgent typeAgent) {
        List<Agent> agents = new ArrayList<>();

        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT * FROM agents WHERE type_agent = ? ORDER BY nom, prenom";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setObject(1, typeAgent.name(), Types.OTHER); // Convert enum to string
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Agent agent = createAgentFromResultSet(resultSet);
                agents.add(agent);
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            System.out.println("Error finding agents by type: " + e.getMessage());
        }

        return agents;
    }

    public void update(Agent agent) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String sql = "UPDATE agents SET nom = ?, prenom = ?, email = ?, mot_de_passe = ?, type_agent = ?, id_departement = ? WHERE id_agent = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, agent.getNom());
            statement.setString(2, agent.getPrenom());
            statement.setString(3, agent.getEmail());
            statement.setString(4, agent.getMotDePasse());
            statement.setObject(5, agent.getTypeAgent().name(),  Types.OTHER);

            if (agent.getIdDepartement() != null) {
                statement.setInt(6, agent.getIdDepartement());
            } else {
                statement.setNull(6, Types.INTEGER);
            }

            statement.setInt(7, agent.getIdAgent());

            statement.executeUpdate();
            statement.close();

        } catch (SQLException e) {
            System.out.println("Error updating agent: " + e.getMessage());
        }
    }

    private Agent createAgentFromResultSet(ResultSet resultSet) throws SQLException {
        String typeAgent = resultSet.getString("type_agent");

        Agent agent = new Agent();
        agent.setIdAgent(resultSet.getInt("id_agent"));
        agent.setNom(resultSet.getString("nom"));
        agent.setPrenom(resultSet.getString("prenom"));
        agent.setEmail(resultSet.getString("email"));
        agent.setMotDePasse(resultSet.getString("mot_de_passe"));
        agent.setTypeAgent(TypeAgent.valueOf(typeAgent));
        agent.setIdDepartement(resultSet.getInt("id_departement"));

        return agent;
    }



    }

