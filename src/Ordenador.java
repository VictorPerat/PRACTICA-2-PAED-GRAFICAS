import java.util.*; // Utilidades generales: List, ArrayList, Collections, etc.

public class Ordenador {

    // NOUS: Ordenació per ratio valor DESC (per Greedy Prob1)
    public static void sortPerRatioValor(List<Quest> lista) {
        lista.sort((a, b) -> Double.compare(b.getRatio(), a.getRatio()));
    }

    // NOUS: Ordenació per rareses + temps DESC (per Greedy Prob2, però útil per Prob1 si cal)
    public static void sortPerRaresaTemps(List<Quest> lista) {
        lista.sort((a, b) -> {
            double ra = QuestValueCalculator.getMultiplicadorRaresa(a.getPes());
            double rb = QuestValueCalculator.getMultiplicadorRaresa(b.getPes());
            if (ra != rb) return Double.compare(rb, ra);  // Llegendari > Rar > Comú
            return Integer.compare(b.getTempsEstim(), a.getTempsEstim());  // Temps DESC
        });
    }
}