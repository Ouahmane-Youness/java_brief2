package com.paymentManagement.config.database;

import com.paymentManagement.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;


    private DatabaseConnection() {
        initializeConnection();
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            // Check if connection exists and is still valid
            if (connection == null || connection.isClosed()) {
                System.out.println("Connection is null or closed. Reconnecting...");
                initializeConnection();
            }

            return connection;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get database connection: " + e.getMessage(), e);
        }
    }

    private void initializeConnection()
    {
        try{
            String driverClass = DatabaseConfig.getDatabaseDriver();
            Class.forName(driverClass);
            System.out.println("jdbc driver loaded" + driverClass);

            String url= DatabaseConfig.getDatabaseUrl();
            String username= DatabaseConfig.getDatabaseUsername();
            String password = DatabaseConfig.getDatabasePassword();

            connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(true);
            System.out.println("Database connection established successfully!");
            displayConnectionInfo();

        }catch(ClassNotFoundException e){
            throw new RuntimeException("jDBC Driver not found." + e.getMessage(),e);


    }
        catch (SQLException e){
            throw new RuntimeException("Failed to establish database connection."+e.getMessage(), e);

        }
    }

    private void displayConnectionInfo() {
        try {
            var metadata = connection.getMetaData();
            System.out.println("\n=== Connection Information ===");
            System.out.println("Database: " + metadata.getDatabaseProductName() +
                    " " + metadata.getDatabaseProductVersion());
            System.out.println("Driver: " + metadata.getDriverName() +
                    " " + metadata.getDriverVersion());
            System.out.println("URL: " + metadata.getURL());
            System.out.println("Username: " + metadata.getUserName());
            System.out.println("Auto-commit: " + connection.getAutoCommit());
            System.out.println("==============================\n");

        } catch (SQLException e) {
            System.out.println("Could not retrieve connection metadata: " + e.getMessage());
        }
    }

}
