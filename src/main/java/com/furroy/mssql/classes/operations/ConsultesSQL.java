package com.furroy.mssql.classes.operations;

import com.furroy.mssql.classes.IntegralConnection;

import javax.swing.*;
import java.io.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.furroy.mssql.classes.IntegralConnection.generarResultSet;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ELOWIS
 */
public class ConsultesSQL {

    static String rutaProjecte = System.getProperty("user.dir");
    static String sep = File.separator;
    static String rutaConf = new File(rutaProjecte) + sep + "config" + sep;
    static ResultSet rs;

    public static void realizarConsultaSimple() throws SQLException {
        String consulta = obtenerConsultaDesdeArchivo(rutaConf + "consulta.txt");
        rs = generarResultSet(consulta);

        int rsRows = 0;

        try (FileWriter writer = new FileWriter(rutaConf + "resultatConsultaSimple.csv")) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            // Escribir los nombres de las columnas en el archivo CSV
            List<String> columnNames = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                columnNames.add(columnName);
            }
            escribirLineaCSV(writer, columnNames);

            // Escribir los valores de cada fila en el archivo CSV
            while (rs.next()) {
                List<String> columnValues = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    columnValues.add(value);
                }
                escribirLineaCSV(writer, columnValues);
                rsRows++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\nArxiu CSV generat correctament.");
        System.out.println("Consulta = " + consulta + "\n");
        int rsColumns = rs.getMetaData().getColumnCount();
        System.out.println("\nColumnes: " + rsColumns);
        System.out.println("Files: " + rsRows);
        JOptionPane.showMessageDialog(null, "Arxiu CSV generat correctament.\nColumnes: " + rsColumns + "\nFiles: " + rsRows, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void escribirLineaCSV(FileWriter writer, List<String> values) throws IOException {
        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            if (value != null) {
                writer.write(value);
            }
            if (i < values.size() - 1) {
                writer.write(",");
            }
        }
        writer.write("\n");
    }
    public static String obtenerConsultaDesdeArchivo(String nombreArchivo) {
        StringBuilder consulta = new StringBuilder();
        try {
            File archivo = new File(nombreArchivo);
            BufferedReader br = new BufferedReader(new FileReader(archivo));
            String linea;
            while ((linea = br.readLine()) != null) {
                consulta.append(linea);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return consulta.toString();
    }

    public static void realizarMulticonsulta() throws SQLException {
        int numConsultas = 0;
        String rutaConsultas = rutaConf + sep + "consulta.txt";
        String consultas = obtenerConsultaDesdeArchivo(rutaConsultas);
        String[] consultasArray = consultas.split(";");

        List<List<String>> resultados = new ArrayList<>();

        if (!IntegralConnection.checkBoxArxiusSeparats.isSelected()) {
            for (String consulta : consultasArray) {
                consulta = consulta.trim();
                if (!consulta.isEmpty()) {
                    rs = generarResultSet(consulta);
                    List<String> resultadoConsulta = generarListaDesdeResultSet(rs);
                    resultados.add(resultadoConsulta);
                    rs.close();
                }
            }
            JOptionPane.showMessageDialog(null, "Arxiu CSV generat correctament.", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (String consulta : consultasArray) {
                consulta = consulta.trim();
                if (!consulta.isEmpty()) {
                    ResultSet rs = generarResultSet(consulta);
                    String nombreArchivo = "resultatConsulta" + (++numConsultas) + ".csv";
                    generarArchivoCSVSeparate(rs, nombreArchivo);
                    rs.close();
                }
            }
            JOptionPane.showMessageDialog(null, "Arxiu CSV generat correctament.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }

        // Combina todos los resultados en un solo archivo CSV
        String nombreArchivo = "resultatMulticonsulta.csv";
        generarArchivoCSVMulticonsulta(resultados, nombreArchivo);
    }

    private static List<String> generarListaDesdeResultSet(ResultSet rs) throws SQLException {
        List<String> resultado = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Agregar nombres de columnas como la primera fila en la lista
        for (int i = 1; i <= columnCount; i++) {
            resultado.add(metaData.getColumnName(i));
        }

        // Agregar datos de filas
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                String value = rs.getString(i);
                if (value != null) {
                    resultado.add(value);
                } else {
                    // Si el valor es nulo, agregar una cadena vacía
                    resultado.add("");
                }
            }
        }

        return resultado;
    }



    private static void generarArchivoCSVMulticonsulta(List<List<String>> resultados, String nombreArchivo) {
        try {
            String rutaArchivo = rutaConf + nombreArchivo;
            FileWriter writer = new FileWriter(rutaArchivo);

            for (List<String> consultaResultados : resultados) {
                // Escribir los datos de las filas en el archivo CSV para una consulta
                for (int i = 0; i < consultaResultados.size(); i++) {
                    writer.write(consultaResultados.get(i));
                    if (i < consultaResultados.size() - 1) {
                        writer.write(",");
                    }
                }
                writer.write("\n\n"); // Agregar una línea en blanco entre consultas
                writer.write("--");
                writer.write("\n\n");
            }

            writer.close();
            System.out.println("Archivo " + nombreArchivo + " guardado exitosamente.");
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo " + nombreArchivo);
        }
    }


    private static void generarArchivoCSVSeparate(ResultSet rs, String nombreArchivo) throws SQLException {
        try {
            String rutaArchivo = rutaConf + nombreArchivo;
            FileWriter writer = new FileWriter(rutaArchivo);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Escribir los nombres de las columnas en la primera fila del archivo CSV
            for (int i = 1; i <= columnCount; i++) {
                writer.write(metaData.getColumnName(i));
                if (i < columnCount) {
                    writer.write(",");
                }
            }
            writer.write("\n");

            // Escribir los datos de las filas en el archivo CSV
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    if (value != null) {
                        writer.write(value);
                    }
                    if (i < columnCount) {
                        writer.write(",");
                    }
                }
                writer.write("\n");
            }

            writer.close();
            System.out.println("Archivo " + nombreArchivo + " guardado exitosamente.");
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo " + nombreArchivo);
        }
    }


}
