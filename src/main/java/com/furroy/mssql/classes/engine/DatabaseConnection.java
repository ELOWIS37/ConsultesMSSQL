package com.furroy.mssql.classes.engine;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    public static String rutaProjecte = System.getProperty("user.dir");
    public static String sep = File.separator;
    public static String rutaConf = new File(rutaProjecte) + sep + "config" + sep;

    public static String ip;
    public static String user;
    public static String password;
    public static String databaseName;

    public static Connection conn = null;

    public static Connection getConnection() {

        FileInputStream fis = null;

        try {
            Properties properties = new Properties();

            fis = new FileInputStream(rutaConf + sep + "credencials" + sep + "connection.properties");
            properties.load(fis);

            ip = properties.getProperty("MSSqlIp");
            user = properties.getProperty("MSSqlUser");
            password = properties.getProperty("MSSqlPass");
            databaseName = properties.getProperty("MSSqlDataBase");

            // Construir la URL de conexión JDBC
            String jdbcUrl = "jdbc:sqlserver:" + ip + ";databaseName=" + databaseName + ";user=" + user + ";password=" + password;

            // Establecer la conexión
            // Sensa validació de certificat
            Properties props = new Properties();
            props.setProperty("encrypt", "true");
            props.setProperty("trustServerCertificate", "true");
            conn = DriverManager.getConnection(jdbcUrl, props);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Connexió incorrecte a " + ip + "\nError inesperat en la connexió.\nRevisa que les dades siguin correctes.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return conn;
    }
}