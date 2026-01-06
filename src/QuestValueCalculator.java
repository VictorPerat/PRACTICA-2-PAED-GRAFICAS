import java.util.Map;

public class QuestValueCalculator {

    /**
     * Retorna el multiplicador segons la raresa (codigoHexDeRareza/codigoHexNormalizado)
     */
    public static double obtenerMultiplicadorDeRareza(String codigoHexDeRareza) {
        if (codigoHexDeRareza == null) return 1.0;
        String codigoHexNormalizado = codigoHexDeRareza.toLowerCase().trim();
        if (codigoHexNormalizado.equals("#4fd945")) return 1.0;   // Comú
        if (codigoHexNormalizado.equals("#cc00ff")) return 2.0;   // Rar
        if (codigoHexNormalizado.equals("#ff8000")) return 5.0;   // Llegendari
        return 1.0; // Default per seguretat
    }

    /**
     * Calcula el valorTotalCalculadoDeLaMision de la missió segons la fórmula:
     * valorTotalCalculadoDeLaMision = tiempoEstimadoEnMinutos * nivelDeDificultad * (1 - porcentajeDeProgreso/100) * multiplicador_raresa
     */
    public static double calcularValorTotalDeLaMision(Quest misionActual) {
        double multiplicadorPorRareza = obtenerMultiplicadorDeRareza(misionActual.getCodigoHexDeRareza());
        double factorDeUrgenciaPorProgresoPendiente = (100.0 - misionActual.getPorcentajeDeProgreso()) / 100.0;  // Menys progrés → més valorTotalCalculadoDeLaMision
        return misionActual.getTiempoEstimadoEnMinutos() * misionActual.getNivelDeDificultad() * factorDeUrgenciaPorProgresoPendiente * multiplicadorPorRareza;
    }

    /**
     * Calcula el temps efectiu tenint en compte el descompte del 10% si l'assignatura ja s'ha fet abans
     * @param misionActual La quest a avaluar
     * @param tempsPerAsig Mapa amb el temps acumulat per assignatura (fins ara)
     * @return tiempoEstimadoEnMinutos * 0.9 si l'assignatura ja existeix al mapa, sinó tiempoEstimadoEnMinutos
     */
    public static double calcularTiempoEfectivoConDescuentoPorAsignatura(Quest misionActual, Map<String, Integer> tempsPerAsig) {
        if (misionActual == null || tempsPerAsig == null) return misionActual != null ? misionActual.getTiempoEstimadoEnMinutos() : 0;

        String asignaturaActual = misionActual.getNombreDeLaAsignatura();
        boolean asignaturaYaRealizada = tempsPerAsig.containsKey(asignaturaActual) && tempsPerAsig.get(asignaturaActual) > 0;
        return misionActual.getTiempoEstimadoEnMinutos() * (asignaturaYaRealizada ? 0.9 : 1.0);
    }
}