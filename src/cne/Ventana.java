package cne;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;

public class Ventana extends JFrame {
        JPanel panel;
        public Ventana(Poblacion poblacion){
            setTitle("Gráfica sobre evolución de algoritmo genético");
            setSize(800,600);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setVisible(true);
            init(poblacion);
        }

        private void init(Poblacion poblacion) {
            panel = new JPanel();
            getContentPane().add(panel);

            // Fuente de Datos
            DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
            for (int i = 0; i < poblacion.getMaximoGeneraciones()-1; i++){
                line_chart_dataset.addValue((int)poblacion.getAptitudesGeneracion().get(i), "aptitud", "generacion: "+i);
            }


            // Creando el Grafico
            JFreeChart chart= ChartFactory.createLineChart("Evolucion aptitudes",
                    "Generacion","Aptitudes",line_chart_dataset, PlotOrientation.VERTICAL,
                    true,true,false);

            // Mostrar Grafico
            ChartPanel chartPanel = new ChartPanel(chart);
            panel.add(chartPanel);
    }

    public DefaultCategoryDataset valoresGrafica(DefaultCategoryDataset valores){
            return valores;
    }
}
