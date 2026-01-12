import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Benchmark benchmark = new Benchmark();

        File carpeta = new File("dataset_P2");
        int limitTemps = 5000;

        System.out.println("=====================================");
        System.out.println("   EXECUTANT PRÀCTICA 2 - AMB ELS DOS PROBLEMES   ");
        System.out.println("=====================================");
        System.out.println("Carpeta: " + carpeta.getPath());
        System.out.println("Limit temps Problema 1: " + limitTemps + " min");
        System.out.println();

        System.out.println(">>> PROBLEMA 1 (Max valor) <<<");
        benchmark.evaluarProblema1(carpeta, limitTemps);

        System.out.println("\n\n>>> PROBLEMA 2 (Min setmanes) <<<");
        benchmark.evaluarProblema2(carpeta);

        System.out.println("\nFinalitzat! Tots els problemes executats.");
    }
}