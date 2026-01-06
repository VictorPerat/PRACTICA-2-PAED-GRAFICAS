import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.category.*;
import org.jfree.data.xy.*;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class GraficadorResultats {

    public static class DatosDelDatasetParaGraficas {
        public String nombreDelAlgoritmo;      // "Greedy", "Backtracking", "Branch&Bound"
        public String nombreDelDataset;
        public int numeroDeMisiones;
        public long tiempoEjecucionEnMilisegundos;
        public long memoriaConsumidaEnKilobytes;        // Se mantiene por si lo necesitas en futuro, pero no se usa en gráficos
        public double calidadDeLaSolucion;       // Valor (P1) o -numeroDeSemanas (P2)
        public boolean seAgotoElTiempoDeEjecucion = false;

        public DatosDelDatasetParaGraficas(String nombreDelAlgoritmo, String nombreDelDataset, int numeroDeMisiones,
                            long tiempoEjecucionEnMilisegundos, long memoriaConsumidaEnKilobytes, double calidadDeLaSolucion, boolean seAgotoElTiempoDeEjecucion) {
            this.nombreDelAlgoritmo = nombreDelAlgoritmo;
            this.nombreDelDataset = nombreDelDataset;
            this.numeroDeMisiones = numeroDeMisiones;
            this.tiempoEjecucionEnMilisegundos = tiempoEjecucionEnMilisegundos;
            this.memoriaConsumidaEnKilobytes = memoriaConsumidaEnKilobytes;
            this.calidadDeLaSolucion = calidadDeLaSolucion;
            this.seAgotoElTiempoDeEjecucion = seAgotoElTiempoDeEjecucion;
        }
    }

    private static final Color COLOR_GREEDY = new Color(46, 204, 113);   // Verde
    private static final Color COLOR_BACKTRACK = new Color(231, 76, 60); // Rojo
    private static final Color COLOR_BB = new Color(52, 152, 219);       // Azul

    public static void generarAnalisisCompletoDeResultados(Map<String, List<DatosDelDatasetParaGraficas>> dadesPerAlgoritme,
                                               String problema, String outputDir) throws IOException {
        File dir = new File(outputDir);
        if (!dir.exists()) dir.mkdirs();

        generarGraficaEscalabilidadTemporal(dadesPerAlgoritme, outputDir + "/01_temps_vs_n.png");
        generarGraficaCalidad(dadesPerAlgoritme, problema, outputDir + "/02_calidad_vs_n.png");
        generarGraficaTradeOff(dadesPerAlgoritme, problema, outputDir + "/03_tradeoff.png");
        generarGraficaBarras(dadesPerAlgoritme, problema, outputDir + "/04_comparacion_barras.png");

        System.out.println("✅ Gráficos generados en: " + outputDir);
    }

    private static void generarGraficaEscalabilidadTemporal(Map<String, List<DatosDelDatasetParaGraficas>> dades, String file) throws IOException {
        XYSeriesCollection dataset = new XYSeriesCollection();

        addSeries(dataset, dades.get("Greedy"), "Greedy");
        addSeries(dataset, dades.get("Backtracking"), "Backtracking");
        addSeries(dataset, dades.get("Branch&Bound"), "Branch & Bound");

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Escalabilidad Temporal", "Número de missions (n)", "Temps (ms)", dataset,
                PlotOrientation.VERTICAL, true, true, false);

        XYPlot plot = chart.getXYPlot();
        LogAxis yAxis = new LogAxis("Temps (ms)");
        yAxis.setBase(10);
        plot.setRangeAxis(yAxis);

        setColors(plot);

        ChartUtilities.saveChartAsPNG(new File(file), chart, 1000, 600);
    }

    private static void generarGraficaCalidad(Map<String, List<DatosDelDatasetParaGraficas>> dades, String problema, String file) throws IOException {
        XYSeriesCollection dataset = new XYSeriesCollection();

        addSeriesQualitat(dataset, dades.get("Greedy"), "Greedy", problema);
        addSeriesQualitat(dataset, dades.get("Backtracking"), "Backtracking", problema);
        addSeriesQualitat(dataset, dades.get("Branch&Bound"), "Branch & Bound", problema);

        String yLabel = problema.equals("Problema1") ? "Valor total" : "Setmanes (menys és millor)";
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Qualitat de Solució", "Número de missions (n)", yLabel, dataset,
                PlotOrientation.VERTICAL, true, true, false);

        setColors(chart.getXYPlot());
        ChartUtilities.saveChartAsPNG(new File(file), chart, 1000, 600);
    }

    private static void generarGraficaTradeOff(Map<String, List<DatosDelDatasetParaGraficas>> dades, String problema, String file) throws IOException {
        XYSeriesCollection dataset = new XYSeriesCollection();

        addScatter(dataset, dades.get("Greedy"), "Greedy");
        addScatter(dataset, dades.get("Backtracking"), "Backtracking");
        addScatter(dataset, dades.get("Branch&Bound"), "Branch & Bound");

        JFreeChart chart = ChartFactory.createScatterPlot(
                "Trade-off Qualitat vs Temps", "Temps (ms)", "Qualitat", dataset);

        XYPlot plot = chart.getXYPlot();
        LogAxis xAxis = new LogAxis("Temps (ms)");
        plot.setDomainAxis(xAxis);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
        renderer.setSeriesPaint(0, COLOR_GREEDY);
        renderer.setSeriesPaint(1, COLOR_BACKTRACK);
        renderer.setSeriesPaint(2, COLOR_BB);
        renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-5, -5, 10, 10));
        renderer.setSeriesShape(1, new java.awt.geom.Ellipse2D.Double(-5, -5, 10, 10));
        renderer.setSeriesShape(2, new java.awt.geom.Ellipse2D.Double(-5, -5, 10, 10));
        plot.setRenderer(renderer);

        ChartUtilities.saveChartAsPNG(new File(file), chart, 1000, 600);
    }

    private static void generarGraficaBarras(Map<String, List<DatosDelDatasetParaGraficas>> dades, String problema, String file) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Set<String> datasets = new TreeSet<>();
        dades.values().forEach(list -> list.forEach(d -> datasets.add(d.nombreDelDataset)));

        for (String ds : datasets) {
            for (String alg : dades.keySet()) {
                dades.get(alg).stream()
                        .filter(d -> d.nombreDelDataset.equals(ds))
                        .findFirst()
                        .ifPresent(d -> dataset.addValue(d.tiempoEjecucionEnMilisegundos, alg, ds));
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Comparació per Dataset (Temps)", "Dataset", "Temps (ms)", dataset,
                PlotOrientation.VERTICAL, true, true, false);

        setColors(chart.getCategoryPlot());
        ChartUtilities.saveChartAsPNG(new File(file), chart, 1200, 600);
    }

    // Helpers
    private static void addSeries(XYSeriesCollection coll, List<DatosDelDatasetParaGraficas> data, String name) {
        if (data == null) return;
        XYSeries series = new XYSeries(name);
        data.forEach(d -> series.add(d.numeroDeMisiones, Math.max(0.1, d.tiempoEjecucionEnMilisegundos))); // Evitar log(0)
        coll.addSeries(series);
    }

    private static void addSeriesQualitat(XYSeriesCollection coll, List<DatosDelDatasetParaGraficas> data, String name, String problema) {
        if (data == null) return;
        XYSeries series = new XYSeries(name);
        data.forEach(d -> {
            double misionActual = problema.equals("Problema1") ? d.calidadDeLaSolucion : -d.calidadDeLaSolucion; // Invertir semanas
            series.add(d.numeroDeMisiones, misionActual);
        });
        coll.addSeries(series);
    }

    private static void addScatter(XYSeriesCollection coll, List<DatosDelDatasetParaGraficas> data, String name) {
        if (data == null) return;
        XYSeries series = new XYSeries(name);
        data.forEach(d -> series.add(Math.max(0.1, d.tiempoEjecucionEnMilisegundos), d.calidadDeLaSolucion));
        coll.addSeries(series);
    }

    private static void setColors(XYPlot plot) {
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, COLOR_GREEDY);
        renderer.setSeriesPaint(1, COLOR_BACKTRACK);
        renderer.setSeriesPaint(2, COLOR_BB);
    }

    private static void setColors(CategoryPlot plot) {
        CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, COLOR_GREEDY);
        renderer.setSeriesPaint(1, COLOR_BACKTRACK);
        renderer.setSeriesPaint(2, COLOR_BB);
    }
}