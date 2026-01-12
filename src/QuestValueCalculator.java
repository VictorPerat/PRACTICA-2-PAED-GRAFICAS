import java.util.Map;

public class QuestValueCalculator {


    public static double obtenerMultiplicadorDeRareza(String codigoHexDeRareza) {
        if (codigoHexDeRareza == null) return 1.0;
        String codigoHexNormalizado = codigoHexDeRareza.toLowerCase().trim();
        if (codigoHexNormalizado.equals("#4fd945")) return 1.0;
        if (codigoHexNormalizado.equals("#cc00ff")) return 2.0;
        if (codigoHexNormalizado.equals("#ff8000")) return 5.0;
        return 1.0;
    }


    public static double calcularValorTotalDeLaMision(Quest misionActual) {
        double multiplicadorPorRareza = obtenerMultiplicadorDeRareza(misionActual.getCodigoHexDeRareza());
        double factorDeUrgenciaPorProgresoPendiente = (100.0 - misionActual.getPorcentajeDeProgreso()) / 100.0;
        return misionActual.getTiempoEstimadoEnMinutos() * misionActual.getNivelDeDificultad() * factorDeUrgenciaPorProgresoPendiente * multiplicadorPorRareza;
    }


    public static double calcularTiempoEfectivoConDescuentoPorAsignatura(Quest misionActual, Map<String, Integer> tempsPerAsig) {
        if (misionActual == null || tempsPerAsig == null) return misionActual != null ? misionActual.getTiempoEstimadoEnMinutos() : 0;

        String asignaturaActual = misionActual.getNombreDeLaAsignatura();
        boolean asignaturaYaRealizada = tempsPerAsig.containsKey(asignaturaActual) && tempsPerAsig.get(asignaturaActual) > 0;
        return misionActual.getTiempoEstimadoEnMinutos() * (asignaturaYaRealizada ? 0.9 : 1.0);
    }
}
