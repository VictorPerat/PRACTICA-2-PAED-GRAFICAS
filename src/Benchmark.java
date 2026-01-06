import java.io.*;
import java.util.*;

public class Benchmark {

    private static final String DIRECTORIO_DE_RESULTADOS = "results/";
    private static final int MAXIMO_MISIONES_PARA_BACKTRACKING = 30;

    static {
        new File(DIRECTORIO_DE_RESULTADOS).mkdirs();
        new File(DIRECTORIO_DE_RESULTADOS + "problema1/").mkdirs();
        new File(DIRECTORIO_DE_RESULTADOS + "problema2/").mkdirs();
    }

    public void ejecutarBenchmarkDelProblema1(File carpetaDeDatasets, int limiteDeTiempoPorDefectoMinutos) throws IOException {
        File[] archivosDataset = carpetaDeDatasets.listFiles((dir, name) -> name.endsWith(".paed"));
        if (archivosDataset == null || archivosDataset.length == 0) {
            System.out.println("No s'han trobat fitxers .paed");
            return;
        }

        // Mapa para agrupar datos por nombreDelAlgoritmo
        Map<String, List<GraficadorResultats.DatosDelDatasetParaGraficas>> datosParaGraficasProblema1 = new HashMap<>();
        datosParaGraficasProblema1.put("Greedy", new ArrayList<>());
        datosParaGraficasProblema1.put("Backtracking", new ArrayList<>());
        datosParaGraficasProblema1.put("Branch&Bound", new ArrayList<>());

        for (File archivoDataset : archivosDataset) {
            System.out.println("\n══════════════════════════════════════════════════════════════");
            System.out.println("📂 Dataset: " + archivoDataset.getName());
            System.out.println("══════════════════════════════════════════════════════════════");

            List<Quest> listaDeMisiones = QuestParser.leerListaDeMisionesDesdeArchivo(archivoDataset.getPath());
            int n = listaDeMisiones.size();

            // Greedy
            System.out.print("Greedy... ");
            long start = System.nanoTime();
            long memIni = getMemoriaUsada();
            Problema1Solver.Result rg = Problema1Solver.greedy(copiarLista(listaDeMisiones), limiteDeTiempoPorDefectoMinutos);
            double tempsG = (System.nanoTime() - start) / 1e6;
            long memG = getMemoriaUsada() - memIni;
            mostrarResultatP1(rg, tempsG, memG, "Greedy");
            guardarDetalleProblema1(archivoDataset.getName(), rg, "Greedy", false);
            datosParaGraficasProblema1.get("Greedy").add(new GraficadorResultats.DatosDelDatasetParaGraficas("Greedy", archivoDataset.getName(), n,
                    (long) tempsG, memG / 1024, rg.valorTotalAcumulado, false));

            // Backtracking
            System.out.print("Backtracking... ");
            start = System.nanoTime();
            memIni = getMemoriaUsada();
            Problema1Solver.Result rbt = Problema1Solver.backtracking(copiarLista(listaDeMisiones), limiteDeTiempoPorDefectoMinutos);
            double tempsBT = (System.nanoTime() - start) / 1e6;
            long memBT = getMemoriaUsada() - memIni;

            boolean btOmitido = n > MAXIMO_MISIONES_PARA_BACKTRACKING;
            if (btOmitido) {
                System.out.println("Omes (n=" + n + " > " + MAXIMO_MISIONES_PARA_BACKTRACKING + ")");
            } else {
                mostrarResultatP1(rbt, tempsBT, memBT, "Backtracking");
            }
            // Guardamos igualmente un .txt. Si se ha omitido, el fichero dejará constancia del motivo.
            guardarDetalleProblema1(archivoDataset.getName(), rbt, "Backtracking", btOmitido);
            datosParaGraficasProblema1.get("Backtracking").add(new GraficadorResultats.DatosDelDatasetParaGraficas("Backtracking", archivoDataset.getName(), n,
                    (long) tempsBT, memBT / 1024, rbt.valorTotalAcumulado, btOmitido));

            // Branch & Bound
            System.out.print("Branch & Bound... ");
            start = System.nanoTime();
            memIni = getMemoriaUsada();
            Problema1Solver.Result rbb = Problema1Solver.branchAndBound(copiarLista(listaDeMisiones), limiteDeTiempoPorDefectoMinutos);
            double tempsBB = (System.nanoTime() - start) / 1e6;
            long memBB = getMemoriaUsada() - memIni;
            mostrarResultatP1(rbb, tempsBB, memBB, "Branch & Bound");
            guardarDetalleProblema1(archivoDataset.getName(), rbb, "Branch&Bound", false);
            datosParaGraficasProblema1.get("Branch&Bound").add(new GraficadorResultats.DatosDelDatasetParaGraficas("Branch&Bound", archivoDataset.getName(), n,
                    (long) tempsBB, memBB / 1024, rbb.valorTotalAcumulado, false));
        }

        // Generar gráficos para Problema 1
        try {
            GraficadorResultats.generarAnalisisCompletoDeResultados(datosParaGraficasProblema1, "Problema1", DIRECTORIO_DE_RESULTADOS + "problema1");
        } catch (Exception e) {
            System.out.println("Error generant gràfiques P1: " + e.getMessage());
        }
    }

    public void ejecutarBenchmarkDelProblema2(File carpetaDeDatasets) throws IOException {
        File[] archivosDataset = carpetaDeDatasets.listFiles((dir, name) -> name.endsWith(".paed"));
        if (archivosDataset == null || archivosDataset.length == 0) return;

        Map<String, List<GraficadorResultats.DatosDelDatasetParaGraficas>> datosParaGraficasProblema2 = new HashMap<>();
        datosParaGraficasProblema2.put("Greedy", new ArrayList<>());
        datosParaGraficasProblema2.put("Backtracking", new ArrayList<>());
        datosParaGraficasProblema2.put("Branch&Bound", new ArrayList<>());

        for (File archivoDataset : archivosDataset) {
            System.out.println("\n══════════════════════════════════════════════════════════════");
            System.out.println("📂 Dataset: " + archivoDataset.getName());
            System.out.println("══════════════════════════════════════════════════════════════");

            List<Quest> listaDeMisiones = QuestParser.leerListaDeMisionesDesdeArchivo(archivoDataset.getPath());
            int n = listaDeMisiones.size();

            // Greedy
            System.out.print("Greedy... ");
            long start = System.nanoTime();
            long memIni = getMemoriaUsada();
            Problema2Solver.Result rg = Problema2Solver.greedy(copiarLista(listaDeMisiones));
            double tempsG = (System.nanoTime() - start) / 1e6;
            long memG = getMemoriaUsada() - memIni;
            mostrarResultatP2(rg, tempsG, memG, "Greedy");
            guardarDetalleProblema2(archivoDataset.getName(), rg, "Greedy", false);
            datosParaGraficasProblema2.get("Greedy").add(new GraficadorResultats.DatosDelDatasetParaGraficas("Greedy", archivoDataset.getName(), n,
                    (long) tempsG, memG / 1024, -rg.numeroDeSemanas, false)); // -numeroDeSemanas para que menor sea mejor

            // Backtracking
            System.out.print("Backtracking... ");
            start = System.nanoTime();
            memIni = getMemoriaUsada();
            Problema2Solver.Result rbt = Problema2Solver.backtracking(copiarLista(listaDeMisiones));
            double tempsBT = (System.nanoTime() - start) / 1e6;
            long memBT = getMemoriaUsada() - memIni;

            boolean btOmitido = n > MAXIMO_MISIONES_PARA_BACKTRACKING;
            if (btOmitido) {
                System.out.println("Omes (n=" + n + " > " + MAXIMO_MISIONES_PARA_BACKTRACKING + ")");
            } else {
                mostrarResultatP2(rbt, tempsBT, memBT, "Backtracking");
            }
            // Guardamos igualmente un .txt. Si se ha omitido, el fichero dejará constancia del motivo.
            guardarDetalleProblema2(archivoDataset.getName(), rbt, "Backtracking", btOmitido);
            datosParaGraficasProblema2.get("Backtracking").add(new GraficadorResultats.DatosDelDatasetParaGraficas("Backtracking", archivoDataset.getName(), n,
                    (long) tempsBT, memBT / 1024, -rbt.numeroDeSemanas, btOmitido));

            // Branch & Bound
            System.out.print("Branch & Bound... ");
            start = System.nanoTime();
            memIni = getMemoriaUsada();
            Problema2Solver.Result rbb = Problema2Solver.branchAndBound(copiarLista(listaDeMisiones));
            double tempsBB = (System.nanoTime() - start) / 1e6;
            long memBB = getMemoriaUsada() - memIni;
            mostrarResultatP2(rbb, tempsBB, memBB, "Branch & Bound");
            guardarDetalleProblema2(archivoDataset.getName(), rbb, "Branch&Bound", false);
            datosParaGraficasProblema2.get("Branch&Bound").add(new GraficadorResultats.DatosDelDatasetParaGraficas("Branch&Bound", archivoDataset.getName(), n,
                    (long) tempsBB, memBB / 1024, -rbb.numeroDeSemanas, false));
        }

        // Generar gráficos para Problema 2
        try {
            GraficadorResultats.generarAnalisisCompletoDeResultados(datosParaGraficasProblema2, "Problema2", DIRECTORIO_DE_RESULTADOS + "problema2");
        } catch (Exception e) {
            System.out.println("Error generant gràfiques P2: " + e.getMessage());
        }
    }

    private void mostrarResultatP1(Problema1Solver.Result r, double tiempoEjecucionEnMilisegundos, long memBytes, String algo) {
        System.out.printf("%s → Temps: %.3f ms | Mem: %d KB | Valor: %.2f | Missions: %d\n",
                algo, tiempoEjecucionEnMilisegundos, memBytes / 1024, r.valorTotalAcumulado, r.listaDeMisionesSeleccionadas.size());
    }

    private void mostrarResultatP2(Problema2Solver.Result r, double tiempoEjecucionEnMilisegundos, long memBytes, String algo) {
        System.out.printf("%s → Temps: %.3f ms | Mem: %d KB | Setmanes: %d\n",
                algo, tiempoEjecucionEnMilisegundos, memBytes / 1024, r.numeroDeSemanas);
    }

    
    // ──────────────────────────────────────────────────────────────
    // SALIDAS DETALLADAS A FICHEROS .txt (para no saturar la consola)
    // ──────────────────────────────────────────────────────────────

    private static final int MAXIMO_MISIONES_EN_SALIDA_DETALLADA = 5000;
    private static final int MAXIMO_SEMANAS_EN_SALIDA_DETALLADA = 200;

    private void guardarDetalleProblema1(String nombreDataset, Problema1Solver.Result resultado, String nombreAlgoritmo, boolean omitido) throws IOException {
        String algoritmoSeguro = sanitizarParaNombreDeArchivo(nombreAlgoritmo);
        String ruta = DIRECTORIO_DE_RESULTADOS + "problema1/" + nombreDataset + "_" + algoritmoSeguro + ".txt";

        try (PrintWriter escritor = new PrintWriter(new BufferedWriter(new FileWriter(ruta)))) {
            escritor.println("=== Resultado " + nombreAlgoritmo + " | " + nombreDataset + " | Problema 1 (Maximizar valor) ===");

            if (omitido) {
                escritor.println("NOTA: Backtracking omitido por tamaño del dataset (n > " + MAXIMO_MISIONES_PARA_BACKTRACKING + ").");
                escritor.println("Se genera este fichero para dejar constancia del motivo.");
                return;
            }

            escritor.printf(Locale.US, "Valor total: %.2f%n", resultado.valorTotalAcumulado);
            escritor.println("Tiempo total (min): " + resultado.tiempoTotalMinutos);
            escritor.println("Número de misiones seleccionadas: " + resultado.listaDeMisionesSeleccionadas.size());
            escritor.println();
            escritor.println("Misiones seleccionadas (orden de la solución):");

            int limite = Math.min(resultado.listaDeMisionesSeleccionadas.size(), MAXIMO_MISIONES_EN_SALIDA_DETALLADA);
            for (int i = 0; i < limite; i++) {
                escritor.println(resultado.listaDeMisionesSeleccionadas.get(i));
            }
            if (resultado.listaDeMisionesSeleccionadas.size() > limite) {
                escritor.println();
                escritor.println("... (SALIDA TRUNCADA) Total misiones: " + resultado.listaDeMisionesSeleccionadas.size()
                        + " | Mostradas: " + limite);
            }
        }
    }

    private void guardarDetalleProblema2(String nombreDataset, Problema2Solver.Result resultado, String nombreAlgoritmo, boolean omitido) throws IOException {
        String algoritmoSeguro = sanitizarParaNombreDeArchivo(nombreAlgoritmo);
        String ruta = DIRECTORIO_DE_RESULTADOS + "problema2/" + nombreDataset + "_" + algoritmoSeguro + ".txt";

        try (PrintWriter escritor = new PrintWriter(new BufferedWriter(new FileWriter(ruta)))) {
            escritor.println("=== Resultado " + nombreAlgoritmo + " | " + nombreDataset + " | Problema 2 (Minimizar semanas) ===");

            if (omitido) {
                escritor.println("NOTA: Backtracking omitido por tamaño del dataset (n > " + MAXIMO_MISIONES_PARA_BACKTRACKING + ").");
                escritor.println("En su lugar, el solver devuelve una solución heurística (Greedy) para mantener viabilidad.");
                escritor.println();
            }

            escritor.println("Número de semanas: " + resultado.numeroDeSemanas);
            escritor.println("Tiempo total (min): " + resultado.tiempoTotalMinutos);

            int totalMisionesPlanificadas = resultado.listaDeSemanas.stream().mapToInt(List::size).sum();
            escritor.println("Número total de misiones planificadas: " + totalMisionesPlanificadas);
            escritor.println();
            escritor.println("Detalle por semanas:");

            int semanasAMostrar = Math.min(resultado.listaDeSemanas.size(), MAXIMO_SEMANAS_EN_SALIDA_DETALLADA);
            int misionesEscritas = 0;

            for (int indiceSemana = 0; indiceSemana < semanasAMostrar; indiceSemana++) {
                List<Quest> semana = resultado.listaDeSemanas.get(indiceSemana);

                int tiempoSemana = semana.stream().mapToInt(Quest::getTiempoEstimadoEnMinutos).sum();
                long comunes = semana.stream()
                        .filter(m -> m.getCodigoHexDeRareza().equalsIgnoreCase("#4fd945"))
                        .count();

                escritor.printf(Locale.US, "%nSemana %d (Tiempo: %d min | Comunes: %d | Misiones: %d)%n",
                        indiceSemana + 1, tiempoSemana, comunes, semana.size());

                for (Quest mision : semana) {
                    escritor.println(" - " + mision);
                    misionesEscritas++;
                    if (misionesEscritas >= MAXIMO_MISIONES_EN_SALIDA_DETALLADA) break;
                }
                if (misionesEscritas >= MAXIMO_MISIONES_EN_SALIDA_DETALLADA) break;
            }

            if (resultado.listaDeSemanas.size() > semanasAMostrar || totalMisionesPlanificadas > MAXIMO_MISIONES_EN_SALIDA_DETALLADA) {
                escritor.println();
                escritor.println("... (SALIDA TRUNCADA) Total semanas: " + resultado.listaDeSemanas.size()
                        + " | Mostradas: " + semanasAMostrar
                        + " | Total misiones: " + totalMisionesPlanificadas
                        + " | Misiones listadas: " + Math.min(misionesEscritas, MAXIMO_MISIONES_EN_SALIDA_DETALLADA));
            }
        }
    }

    private String sanitizarParaNombreDeArchivo(String texto) {
        if (texto == null || texto.isEmpty()) return "Algoritmo";
        // Elimina espacios y caracteres raros (como &), dejando solo letras, números, guion y guion bajo
        return texto.replaceAll("[^a-zA-Z0-9_-]", "");
    }

private long getMemoriaUsada() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    private List<Quest> copiarLista(List<Quest> original) {
        return new ArrayList<>(original);
    }
}