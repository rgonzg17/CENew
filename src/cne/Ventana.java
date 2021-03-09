package cne;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;

public class Ventana extends JFrame {
    JPanel panel;

    public Ventana(Poblacion poblacion) {
        init(poblacion);
    }

    private void init(Poblacion poblacion){
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
        for (int i = 0; i < poblacion.getAptitudesGeneracion().size(); i++) {
            line_chart_dataset.addValue((int) poblacion.getAptitudesGeneracion().get(i), "aptitud", ""+i);
        }
        JFreeChart chart = ChartFactory.createLineChart("Evolucion aptitudes",
                "Generacion", "Aptitudes", line_chart_dataset, PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel panel = new ChartPanel(chart);
        JFrame ventana = new JFrame("Algoritmo Genético");
        ventana.getContentPane().add(panel);
        ventana.pack();
        ventana.setVisible(true);
        ventana.setSize(800, 600);
        ventana.setLocationRelativeTo(null);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public DefaultCategoryDataset valoresGrafica(DefaultCategoryDataset valores) {
        return valores;
    }
}
