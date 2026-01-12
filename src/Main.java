import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Benchmark evaluadorDeRendimiento = new Benchmark();

        File carpetaDeDatasets = new File("dataset_P2");
        int limiteDeTiempoMaximoMinutos = 5000;

        System.out.println("=====================================");
        System.out.println("   EXECUTANT PRÀCTICA 2 - AMB ELS DOS PROBLEMES   ");
        System.out.println("=====================================");
        System.out.println("Carpeta: " + carpetaDeDatasets.getPath());
        System.out.println("Limit temps Problema 1: " + limiteDeTiempoMaximoMinutos + " min");
        System.out.println();


        System.out.println(">>> PROBLEMA 1 (Max valorTotalCalculadoDeLaMision) <<<");
        evaluadorDeRendimiento.ejecutarBenchmarkDelProblema1(carpetaDeDatasets, limiteDeTiempoMaximoMinutos);

        System.out.println("\n\n>>> PROBLEMA 2 (Min listaDeSemanas) <<<");
        evaluadorDeRendimiento.ejecutarBenchmarkDelProblema2(carpetaDeDatasets);

        System.out.println("\nFinalitzat! Tots els problemes executats.");
    }
}
