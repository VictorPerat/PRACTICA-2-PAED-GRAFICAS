import java.util.*;

public class Ordenador {


    public static void ordenarPorRatioValorSobreTiempoDescendente(List<Quest> listaDeMisionesAOrdenar) {
        listaDeMisionesAOrdenar.sort((misionAComparar, misionBComparar) -> Double.compare(misionBComparar.getRatioValorSobreTiempo(), misionAComparar.getRatioValorSobreTiempo()));
    }


    public static void ordenarPorRarezaYTiempoDescendente(List<Quest> listaDeMisionesAOrdenar) {
        listaDeMisionesAOrdenar.sort((misionAComparar, misionBComparar) -> {
            double ra = QuestValueCalculator.obtenerMultiplicadorDeRareza(misionAComparar.getCodigoHexDeRareza());
            double rb = QuestValueCalculator.obtenerMultiplicadorDeRareza(misionBComparar.getCodigoHexDeRareza());
            if (ra != rb) return Double.compare(rb, ra);
            return Integer.compare(misionBComparar.getTiempoEstimadoEnMinutos(), misionAComparar.getTiempoEstimadoEnMinutos());
        });
    }
}
