package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages database connections for the Quiz Application using MySQL.
 * Implements singleton pattern to ensure single connection instance.
 *
 * @author Tenzing Kunsang Sherpa
 * @version 2.0
 * @since 2024
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/quiz_app_db";
    private static final String USER = "quiz_user";
    private static final String PASSWORD = "secure_quiz_pw";
    private static Connection connection;

    private DatabaseConnection() {} // Private constructor for singleton

    /**
     * Gets the database connection instance.
     * Creates new connection if none exists or existing is closed.
     *
     * @return Established database Connection
     * @throws SQLException if database access error occurs
     * @throws ClassNotFoundException if JDBC driver not found
     */
    public static synchronized Connection getConnection()
            throws SQLException, ClassNotFoundException {
        if (connection == null || connection.isClosed()) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            connection.setAutoCommit(true);
        }
        return connection;
    }

    /**
     * Closes the database connection if open.
     *
     * @throws SQLException if closing connection fails
     */
    public static synchronized void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Tests the database connection.
     *
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn.isValid(2); // 2 second timeout
        } catch (Exception e) {
            return false;
        }
    }
}