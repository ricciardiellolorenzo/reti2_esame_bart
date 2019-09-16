package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    static private final String dbLoc = "jdbc:sqlite:src/main/resources/reservations.db";
    static private DBConnect instance = null;

    private DBConnect() {
        instance = this;
    }

    public static DBConnect getInstance() {
        if (instance == null)
            return new DBConnect();
        else {
            return instance;
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(dbLoc);
        } catch (SQLException e) {
            throw new SQLException("Cannot get connection to " + dbLoc, e);
        }
    }
}
