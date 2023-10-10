package com.furroy.mssql.classes.operations;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Incidencies {

    public static void escriureIncidencia(String rutaArxiu, String missatgeError) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArxiu));
            writer.write(missatgeError);
            writer.close();
        } catch (IOException e) {
            System.out.println("Error al escriure l'arxiu d'incidències: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error al escriure l'arxiu d'incidències", "Error", JOptionPane.ERROR_MESSAGE);

        }
    }

    public static void escriureMultiIncidencia(String rutaArxiu, List<String> incidencias) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArxiu));
            for (int i = 0; i < incidencias.size(); i++) {
                writer.write("Consulta " + (i + 1) + ": " + incidencias.get(i));
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error al escriure l'arxiu d'incidències: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error al escriure l'arxiu d'incidències", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
