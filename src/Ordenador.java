import java.util.*;

public class Ordenador {

    public static void sortPerRatioValor(List<Quest> lista) {
        lista.sort((a, b) -> Double.compare(b.getRatio(), a.getRatio()));
    }

    public static void sortPerRaresaTemps(List<Quest> lista) {
        lista.sort((a, b) -> {
            double ra = QuestValueCalculator.getMultiplicadorRaresa(a.getPes());
            double rb = QuestValueCalculator.getMultiplicadorRaresa(b.getPes());
            if (ra != rb) return Double.compare(rb, ra);
            return Integer.compare(b.getTempsEstim(), a.getTempsEstim());
        });
    }
}