package com.paymentManagement.repository.imp;

import com.paymentManagement.config.database.DatabaseConnection;
import com.paymentManagement.model.entity.Departement;
import com.paymentManagement.repository.interfaces.DepartementRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartementRepositoryImpl implements DepartementRepository {

    @Override
    public void save(Departement departement) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String sql = "INSERT INTO departements (nom) VALUES (?)";

            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, departement.getNom());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                departement.setIdDepartement(generatedKeys.getInt(1));
                System.out.println("✅ Departement saved with ID: " + departement.getIdDepartement());
            }

            generatedKeys.close();
            statement.close();

        } catch (SQLException e) {
            System.out.println("Error saving departement: " + e.getMessage());
            throw new RuntimeException("Failed to save departement", e);
        }
    }

    @Override
    public Departement findById(Integer id) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            // FIX: Correct column name
            String sql = "SELECT * FROM departements WHERE id_departement = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                Departement dep = new Departement(
                        resultSet.getInt("id_departement"),
                        resultSet.getString("nom"),
                        resultSet.getObject("responsable_id", Integer.class) // Handle NULL
                );
                resultSet.close();
                stmt.close();
                return dep;
            }
            resultSet.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Error finding departement: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Departement> findAll() {
        List<Departement> departements = new ArrayList<>();
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT * FROM departements";
            PreparedStatement stmt = connection.prepareStatement(sql);

            // FIX: Use executeQuery() instead of getResultSet()
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                // FIX: Use responsable_id for the third parameter, not id_departement
                Departement departement = new Departement(
                        resultSet.getInt("id_departement"),
                        resultSet.getString("nom"),
                        resultSet.getObject("responsable_id", Integer.class) // Handle NULL
                );
                departements.add(departement);
            }
            resultSet.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Error getting departments: " + e.getMessage());
            e.printStackTrace();
        }
        return departements;
    }

    @Override
    public Departement findByNom(String nom) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT * FROM departements WHERE nom = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, nom);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Departement departement = new Departement(
                        resultSet.getInt("id_departement"),
                        resultSet.getString("nom"),
                        resultSet.getObject("responsable_id", Integer.class)
                );

                resultSet.close();
                statement.close();
                return departement;
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            System.out.println("Error finding departement by name: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void update(Departement departement) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            // FIX: Include responsable_id in update
            String sql = "UPDATE departements SET nom = ?, responsable_id = ? WHERE id_departement = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, departement.getNom());

            // Handle NULL responsable_id
            if (departement.getResponsableId() != null) {
                statement.setInt(2, departement.getResponsableId());
            } else {
                statement.setNull(2, Types.INTEGER);
            }

            statement.setInt(3, departement.getIdDepartement());

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("✅ Department updated successfully");
            }

            statement.close();

        } catch (SQLException e) {
            System.out.println("Error updating departement: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update departement", e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String sql = "DELETE FROM departements WHERE id_departement = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();

            statement.close();

            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting departement: " + e.getMessage());
            return false;
        }
    }

    public boolean existsById(Integer id) {
        Departement departement = findById(id);
        return departement != null;
    }
}