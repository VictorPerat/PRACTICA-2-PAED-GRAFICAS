import java.io.*;
import java.util.*;

/**
 * Benchmark sense gràfiques (sense dependències externes).
 * - Llegeix datasets .paed de la carpeta indicada
 * - Executa Greedy, Backtracking (si n <= llindar) i Branch & Bound
 * - Mostra un resum per consola
 * - Desa el detall en fitxers .txt a results/problema1 i results/problema2
 */
public class Benchmark {

    private static final String DIRECTORI_DE_RESULTATS = "results/";
    private static final String DIRECTORI_RESULTATS_P1 = DIRECTORI_DE_RESULTATS + "problema1/";
    private static final String DIRECTORI_RESULTATS_P2 = DIRECTORI_DE_RESULTATS + "problema2/";
    private static final int MAXIM_MISSIONS_PER_BACKTRACKING = 30;

    static {
        new File(DIRECTORI_DE_RESULTATS).mkdirs();
        new File(DIRECTORI_RESULTATS_P1).mkdirs();
        new File(DIRECTORI_RESULTATS_P2).mkdirs();
    }

    public void evaluarProblema1(File carpetaDatasets, int limitTempsDefaultMinuts) throws IOException {
        File[] arxius = carpetaDatasets.listFiles((dir, name) -> name.endsWith(".paed"));
        if (arxius == null || arxius.length == 0) {
            System.out.println("No s'han trobat fitxers .paed");
            return;
        }

        for (File arxiu : arxius) {
            System.out.println("\n══════════════════════════════════════════════════════════════");
            System.out.println("📂 Dataset: " + arxiu.getName());
            System.out.println("══════════════════════════════════════════════════════════════");

            List<Quest> missions = QuestParser.parseFile(arxiu.getPath());
            int n = missions.size();

            // Greedy
            System.out.print("Greedy... ");
            long start = System.nanoTime();
            long memIni = memoriaUsada();
            Problema1Solver.Result resultatGreedy = Problema1Solver.greedy(copiar(missions), limitTempsDefaultMinuts);
            double tempsG = (System.nanoTime() - start) / 1e6;
            long memG = memoriaUsada() - memIni;
            mostrarP1(resultatGreedy, tempsG, memG, "Greedy");
            guardarDetallP1(arxiu.getName(), "Greedy", resultatGreedy, false, n);

            // Backtracking (només si és viable)
            boolean btOmes = n > MAXIM_MISSIONS_PER_BACKTRACKING;
            if (btOmes) {
                System.out.println("Backtracking... [Backtracking P1] Dataset massa gran (n=" + n + "). Omes.");
                guardarDetallP1(arxiu.getName(), "Backtracking", null, true, n);
            } else {
                System.out.print("Backtracking... ");
                start = System.nanoTime();
                memIni = memoriaUsada();
                Problema1Solver.Result resultatBT = Problema1Solver.backtracking(copiar(missions), limitTempsDefaultMinuts);
                double tempsBT = (System.nanoTime() - start) / 1e6;
                long memBT = memoriaUsada() - memIni;
                mostrarP1(resultatBT, tempsBT, memBT, "Backtracking");
                guardarDetallP1(arxiu.getName(), "Backtracking", resultatBT, false, n);
            }

            // Branch & Bound
            System.out.print("Branch & Bound... ");
            start = System.nanoTime();
            memIni = memoriaUsada();
            Problema1Solver.Result resultatBB = Problema1Solver.branchAndBound(copiar(missions), limitTempsDefaultMinuts);
            double tempsBB = (System.nanoTime() - start) / 1e6;
            long memBB = memoriaUsada() - memIni;
            mostrarP1(resultatBB, tempsBB, memBB, "Branch & Bound");
            guardarDetallP1(arxiu.getName(), "BranchBound", resultatBB, false, n);
        }
    }

    public void evaluarProblema2(File carpetaDatasets) throws IOException {
        File[] arxius = carpetaDatasets.listFiles((dir, name) -> name.endsWith(".paed"));
        if (arxius == null || arxius.length == 0) {
            System.out.println("No s'han trobat fitxers .paed");
            return;
        }

        for (File arxiu : arxius) {
            System.out.println("\n══════════════════════════════════════════════════════════════");
            System.out.println("📂 Dataset: " + arxiu.getName());
            System.out.println("══════════════════════════════════════════════════════════════");

            List<Quest> missions = QuestParser.parseFile(arxiu.getPath());
            int n = missions.size();

            // Greedy
            System.out.print("Greedy... ");
            long start = System.nanoTime();
            long memIni = memoriaUsada();
            Problema2Solver.Result resultatGreedy = Problema2Solver.greedy(copiar(missions));
            double tempsG = (System.nanoTime() - start) / 1e6;
            long memG = memoriaUsada() - memIni;
            mostrarP2(resultatGreedy, tempsG, memG, "Greedy");
            guardarDetallP2(arxiu.getName(), "Greedy", resultatGreedy, false, n);

            // Backtracking (només si és viable)
            boolean btOmes = n > MAXIM_MISSIONS_PER_BACKTRACKING;
            if (btOmes) {
                System.out.println("Backtracking... [Backtracking P2] Dataset massa gran (n=" + n + "). Omes.");
                guardarDetallP2(arxiu.getName(), "Backtracking", null, true, n);
            } else {
                System.out.print("Backtracking... ");
                start = System.nanoTime();
                memIni = memoriaUsada();
                Problema2Solver.Result resultatBT = Problema2Solver.backtracking(copiar(missions));
                double tempsBT = (System.nanoTime() - start) / 1e6;
                long memBT = memoriaUsada() - memIni;
                mostrarP2(resultatBT, tempsBT, memBT, "Backtracking");
                guardarDetallP2(arxiu.getName(), "Backtracking", resultatBT, false, n);
            }

            // Branch & Bound
            System.out.print("Branch & Bound... ");
            start = System.nanoTime();
            memIni = memoriaUsada();
            Problema2Solver.Result resultatBB = Problema2Solver.branchAndBound(copiar(missions));
            double tempsBB = (System.nanoTime() - start) / 1e6;
            long memBB = memoriaUsada() - memIni;
            mostrarP2(resultatBB, tempsBB, memBB, "Branch & Bound");
            guardarDetallP2(arxiu.getName(), "BranchBound", resultatBB, false, n);
        }
    }

    private void mostrarP1(Problema1Solver.Result r, double tempsMs, long memBytes, String algorisme) {
        System.out.printf("%s → Temps: %.3f ms | Mem: %d KB | Valor: %.2f | Missions: %d%n",
                algorisme, tempsMs, memBytes / 1024, r.valorTotal, r.seleccionades.size());
    }

    private void mostrarP2(Problema2Solver.Result r, double tempsMs, long memBytes, String algorisme) {
        System.out.printf("%s → Temps: %.3f ms | Mem: %d KB | Setmanes: %d%n",
                algorisme, tempsMs, memBytes / 1024, r.numSetmanes);
    }

    private long memoriaUsada() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    private List<Quest> copiar(List<Quest> original) {
        return new ArrayList<>(original);
    }

    // ===========================
    // FITXERS DE SORTIDA (.txt)
    // ===========================

    private void guardarDetallP1(String nomDataset, String nomAlgorisme, Problema1Solver.Result resultat,
                                 boolean omes, int n) throws IOException {

        String fitxer = DIRECTORI_RESULTATS_P1 + nomDataset + "_" + nomAlgorisme + ".txt";
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fitxer), "UTF-8"))) {

            pw.printf("=== %s | %s | Problema 1 ===%n", nomDataset, nomAlgorisme);
            pw.printf("Missions al dataset (n): %d%n", n);

            if (omes || resultat == null) {
                pw.println();
                pw.printf("NOTA: Backtracking omès (n > %d) per evitar temps d'execució inassumibles.%n",
                        MAXIM_MISSIONS_PER_BACKTRACKING);
                return;
            }

            pw.printf("Valor total: %.2f%n", resultat.valorTotal);
            pw.printf("Temps total (min): %d%n", resultat.tempsTotal);
            pw.printf("Missions seleccionades: %d%n", resultat.seleccionades.size());
            pw.println();
            pw.println("Llista de missions seleccionades:");
            for (Quest q : resultat.seleccionades) {
                pw.println(q.toString());
            }
        }
    }

    private void guardarDetallP2(String nomDataset, String nomAlgorisme, Problema2Solver.Result resultat,
                                 boolean omes, int n) throws IOException {

        String fitxer = DIRECTORI_RESULTATS_P2 + nomDataset + "_" + nomAlgorisme + ".txt";
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fitxer), "UTF-8"))) {

            pw.printf("=== %s | %s | Problema 2 ===%n", nomDataset, nomAlgorisme);
            pw.printf("Missions al dataset (n): %d%n", n);

            if (omes || resultat == null) {
                pw.println();
                pw.printf("NOTA: Backtracking omès (n > %d) per evitar temps d'execució inassumibles.%n",
                        MAXIM_MISSIONS_PER_BACKTRACKING);
                return;
            }

            pw.printf("Nombre de setmanes: %d%n", resultat.numSetmanes);
            pw.printf("Temps total (min): %d%n", resultat.tempsTotal);
            pw.println();

            int setmanaNum = 1;
            for (List<Quest> setmana : resultat.setmanes) {
                int tempsSetmana = setmana.stream().mapToInt(Quest::getTempsEstim).sum();
                int comunes = contarComunes(setmana);

                pw.printf("Setmana %d (Temps: %d min | Comunes: %d)%n", setmanaNum++, tempsSetmana, comunes);
                for (Quest q : setmana) {
                    pw.println(" - " + q.toString());
                }
                pw.println();
            }
        }
    }

    private int contarComunes(List<Quest> setmana) {
        int comunes = 0;
        for (Quest q : setmana) {
            if (q.getPes().equalsIgnoreCase("#4fd945")) {
                comunes++;
            }
        }
        return comunes;
    }
}
