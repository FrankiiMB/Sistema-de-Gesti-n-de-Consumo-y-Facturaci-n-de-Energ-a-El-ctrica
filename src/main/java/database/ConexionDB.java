package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static ConexionDB instancia;
    private Connection conexion;
    private final String url =
        "jdbc:sqlserver://localhost:1433;databaseName=EnergiaElectricaDB;encrypt=true;trustServerCertificate=true;"
        + "user=sa; password=404110602;";

    private ConexionDB() throws SQLException {
        try {
            conexion = DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw e;
        }
    }

    public static ConexionDB getInstancia() throws SQLException {
        if (instancia == null || instancia.getConexion() == null || instancia.getConexion().isClosed()) {
            instancia = new ConexionDB();
        }
        return instancia;
    }

    public Connection getConexion() {
        return conexion;
    }
}