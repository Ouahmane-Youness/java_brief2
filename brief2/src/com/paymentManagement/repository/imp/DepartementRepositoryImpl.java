package com.paymentManagement.repository.imp;

import com.paymentManagement.config.database.DatabaseConnection;
import com.paymentManagement.model.entity.Departement;
import com.paymentManagement.repository.interfaces.DepartementRepository;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartementRepositoryImpl implements DepartementRepository {
    @Override
    public void save(Departement departement) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String sql = "INSERT INTO departements (nom) values (?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, departement.getNom());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            System.out.println("error saving departement" + e.getMessage());
        }
    }

    @Override
    public Departement findById(Integer id) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String sql = "select * from departements where departement.id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                Departement dep = new Departement(resultSet.getInt("id_departement"), resultSet.getString("nom"), resultSet.getInt("responsable_id"));
                resultSet.close();
                stmt.close();
                return dep;
            }
            resultSet.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Error finding departement: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Departement> findAll() {
        List<Departement> departements = new ArrayList<>();
        try {

            Connection connection = DatabaseConnection.getInstance().getConnection();
            String sql = "select * from departements";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultSet = stmt.getResultSet();
            while (resultSet.next()) {
                Departement departement = new Departement(resultSet.getInt("id_departement"), resultSet.getString("nom"), resultSet.getInt("id_departement"));
                departements.add(departement);
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error getting deps" + e.getMessage());
        }
        return departements;

    }

    public Departement findByNom(String nom) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT * FROM departements WHERE nom = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, nom);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Departement departement = new Departement(resultSet.getInt("id_departement"), resultSet.getString("nom"), resultSet.getInt("responsable_id"));

                resultSet.close();
                statement.close();
                return departement;
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            System.out.println("Error finding departement by name: " + e.getMessage());
        }

        return null;
    }

    @Override
    public void update(Departement departement) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String sql = "UPDATE departements SET nom = ? WHERE id_departement = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, departement.getNom());
            statement.setInt(2, departement.getIdDepartement());
            statement.executeUpdate();

            statement.close();

        } catch (SQLException e) {
            System.out.println("Error updating departement: " + e.getMessage());
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


