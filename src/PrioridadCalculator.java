public class PrioridadCalculator {

    public static double calcular(Quest quest) {
        // Parámetros ajustables
        final int MAX_PROGRES = 100;
        final int MAX_DIFICULTAT = 10;
        final int MAX_TEMPS_ESTIMAT = 100; // Asumimos que más de 100 minutos ya no es prioritario

        final double PESO_PROGRES = 0.4;
        final double PESO_DIFICULTAT = 0.3;
        final double PESO_TEMPS = 0.3;

        // Normalizamos: progreso (0-100), dificultad (1-10), temps (lo convertimos a "menos es mejor")
        int progres = quest.getProgres();
        int dificultat = quest.getDificultat();
        int temps = quest.getTempsEstim();

        // Cálculo de prioridad (valores más altos = más urgentes/importantes)
        double prioridad = (progres * PESO_PROGRES) + ((dificultat / (double) MAX_DIFICULTAT) * MAX_PROGRES * PESO_DIFICULTAT) + ((MAX_TEMPS_ESTIMAT - Math.min(temps, MAX_TEMPS_ESTIMAT)) * PESO_TEMPS);

        return prioridad;
    }

    // NOU: Prioritat amb rareses (per Prob2 o expansió Prob1)
    public static double calcularAmbRaresa(Quest quest) {
        double base = calcular(quest);
        double mult = QuestValueCalculator.getMultiplicadorRaresa(quest.getPes());
        return base * mult;
    }
}