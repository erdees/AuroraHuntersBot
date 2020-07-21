package ru.aurorahunters.bot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
    //  Database credentials
    private static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/vtorov";
    private static final String USER = "vtorov";
    private static final String PASS = "";
    private static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}
