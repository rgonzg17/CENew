package cne;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Ventana extends JFrame {

    /**
     * Constructor de Ventana
     * Si tipoGrafica es 0, se creará la ventana de las aptitudes de la población
     * Si tipoGrafica es 1, se creará la ventana de las aptitudes del mejor cromosoma
     * @param aptitudes
     * @param tipoGrafica
     */
    public Ventana(ArrayList aptitudes, int tipoGrafica) {
        if (tipoGrafica == 0)
            initAptitudesPoblacion(aptitudes);
        else if(tipoGrafica == 1)
            initAptitudesMejorCromosoma(aptitudes);
    }


    /**
     * Método para iniciar la gráfica de las aptitudes totales de cada generación del algoritmo
     * @param aptitudesPoblacion
     */
    private void initAptitudesPoblacion(ArrayList aptitudesPoblacion) {

        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
        for (int i = 0; i < aptitudesPoblacion.size(); i++) {
            line_chart_dataset.addValue((int) aptitudesPoblacion.get(i), "aptitud", "" + i);
        }
        JFreeChart chart = ChartFactory.createLineChart("Evolucion aptitudes",
                "Generacion", "Aptitud", line_chart_dataset, PlotOrientation.VERTICAL,
                true, true, false);


        ChartPanel panel = new ChartPanel(chart);
        JFrame ventana = new JFrame("Aptitudes de la Población");
        ventana.getContentPane().add(panel);
        ventana.pack();
        ventana.setVisible(true);
        ventana.setSize(800, 600);
        ventana.setLocationRelativeTo(null);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    /**
     * Método para iniciar la gráfica de las aptitudes del mejor cromosoma en cada generación
     * @param aptitudesCromosoma
     */
    private void initAptitudesMejorCromosoma(ArrayList aptitudesCromosoma){

        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
        for (int i = 0; i < aptitudesCromosoma.size(); i++) {
            line_chart_dataset.addValue((int) aptitudesCromosoma.get(i), "aptitud", "" + i);
        }
        JFreeChart chart = ChartFactory.createLineChart("Evolucion aptitudes",
                "Generacion", "Aptitud", line_chart_dataset, PlotOrientation.VERTICAL,
                true, true, false);


        ChartPanel panel = new ChartPanel(chart);
        JFrame ventana = new JFrame("Mejor Cromosoma");
        ventana.getContentPane().add(panel);
        ventana.pack();
        ventana.setVisible(true);
        ventana.setSize(400, 300);
        ventana.setLocationRelativeTo(getRootPane());
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
