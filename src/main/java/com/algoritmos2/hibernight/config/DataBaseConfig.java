package com.algoritmos2.hibernight.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConfig {

    Connection connection;

    protected Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/hibernight";
        String driver = "com.mysql.jdbc.Driver";
        String user = "root";
        String pass = "";

        try {
            Class.forName(driver).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Connected to the database");
        connection = DriverManager.getConnection(url, user, pass);
        return connection;
    }

    protected void closeConnection() throws SQLException {
        if (null != connection) {
            connection.close();
        }
    }
}
