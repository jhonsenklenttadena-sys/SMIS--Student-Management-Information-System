package com.smis.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

/**
 * SQLite connection — no installation needed.
 * Database file: smis.db (created automatically next to the JAR)
 */
public class DatabaseConnection {

    private static Connection connection = null;

    // DB file sits next to wherever the app is run from
    private static final String DB_URL = "jdbc:sqlite:smis.db";

    private DatabaseConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                // Enable foreign keys for SQLite
                connection.createStatement().execute("PRAGMA foreign_keys = ON");
                System.out.println("[DB] Connected to SQLite: smis.db");
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] " + e.getMessage());
        }
    }

    public static boolean testConnection() {
        return getConnection() != null;
    }
}
