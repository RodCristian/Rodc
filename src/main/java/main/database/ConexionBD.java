package main.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    public static Connection conectar() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:asistencia.db");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
