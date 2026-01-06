import java.util.*; // Utilidades generales: List, ArrayList, Collections, etc.

public class Ordenador {

    // NOUS: Ordenació per ratioValorSobreTiempo valorTotalCalculadoDeLaMision DESC (per Greedy Prob1)
    public static void ordenarPorRatioValorSobreTiempoDescendente(List<Quest> listaDeMisionesAOrdenar) {
        listaDeMisionesAOrdenar.sort((misionAComparar, misionBComparar) -> Double.compare(misionBComparar.getRatioValorSobreTiempo(), misionAComparar.getRatioValorSobreTiempo()));
    }

    // NOUS: Ordenació per rareses + temps DESC (per Greedy Prob2, però útil per Prob1 si cal)
    public static void ordenarPorRarezaYTiempoDescendente(List<Quest> listaDeMisionesAOrdenar) {
        listaDeMisionesAOrdenar.sort((misionAComparar, misionBComparar) -> {
            double ra = QuestValueCalculator.obtenerMultiplicadorDeRareza(misionAComparar.getCodigoHexDeRareza());
            double rb = QuestValueCalculator.obtenerMultiplicadorDeRareza(misionBComparar.getCodigoHexDeRareza());
            if (ra != rb) return Double.compare(rb, ra);  // Llegendari > Rar > Comú
            return Integer.compare(misionBComparar.getTiempoEstimadoEnMinutos(), misionAComparar.getTiempoEstimadoEnMinutos());  // Temps DESC
        });
    }
}