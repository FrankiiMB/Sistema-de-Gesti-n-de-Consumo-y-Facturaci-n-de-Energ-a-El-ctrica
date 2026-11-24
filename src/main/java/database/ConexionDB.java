package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String HOST = "localhost"; // En 3 PC: poner IP de la PC servidor (ej. 100.85.12.10)
    private static final String PUERTO = "1433";
    private static final String BASE_DATOS = "EnergiaElectricaDB";
    private static final String USER = "sa";
    private static final String PASS = "404110602"; // <-- Cambia por la contraseña de tu SQL Server

    private static final String URL = "jdbc:sqlserver://" + HOST + ":" + PUERTO
            + ";databaseName=" + BASE_DATOS + ";encrypt=false;trustServerCertificate=true;";

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}