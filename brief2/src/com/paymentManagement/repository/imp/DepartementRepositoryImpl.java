package com.paymentManagement.repository.imp;

import com.paymentManagement.config.database.DatabaseConnection;
import com.paymentManagement.model.entity.Departement;
import com.paymentManagement.repository.interfaces.DepartementRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DepartementRepositoryImpl implements DepartementRepository {
    @Override
    public void save(Departement departement) {
        try{
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String sql = "INSERT INTO departement (nom) values (?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, departement.getNom());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
