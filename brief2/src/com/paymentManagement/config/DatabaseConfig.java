package com.paymentManagement.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static Properties properties;
    static{
        loadProperties();
    }

    private  static void loadProperties()
    {
        properties = new Properties();
        String propertiesFile = "/com/paymentManagement/config/database.properties";

        //every class has a Class object that describes it at runtime.
        try{
            InputStream inputStream = DatabaseConfig.class.getResourceAsStream(propertiesFile);
            if(inputStream == null)
            {
                throw new RuntimeException("configuration not found");
            };

            properties.load(inputStream);
            System.out.println("Database configuration loaded successfully");
        }
        catch(IOException e){
            throw new RuntimeException(
                    "failed to load database configuration" + e.getMessage(), e
            );

    }


    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getDatabaseUrl() {
        return getProperty("db.url");
    }

    public static String getDatabaseUsername() {
        return getProperty("db.username");
    }

    public static String getDatabasePassword() {
        return getProperty("db.password");
    }
    public static String getDatabaseDriver() {
        return getProperty("db.driver");
    }

    public static void displayConfiguration() {
        System.out.println("\n=== Database Configuration ===");
        System.out.println("URL: " + getDatabaseUrl());
        System.out.println("Username: " + getDatabaseUsername());
        System.out.println("Password: [HIDDEN]");
        System.out.println("Driver: " + getDatabaseDriver());
        System.out.println("===============================\n");
    }
}
