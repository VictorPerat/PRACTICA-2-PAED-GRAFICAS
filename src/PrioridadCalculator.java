public class PrioridadCalculator {

    public static double calcular(Quest quest) {

        final int MAX_PROGRES = 100;
        final int MAX_DIFICULTAT = 10;
        final int MAX_TEMPS_ESTIMAT = 100;

        final double PESO_PROGRES = 0.4;
        final double PESO_DIFICULTAT = 0.3;
        final double PESO_TEMPS = 0.3;

        int progres = quest.getProgres();
        int dificultat = quest.getDificultat();
        int temps = quest.getTempsEstim();

        double prioridad = (progres * PESO_PROGRES) + ((dificultat / (double) MAX_DIFICULTAT) * MAX_PROGRES * PESO_DIFICULTAT) + ((MAX_TEMPS_ESTIMAT - Math.min(temps, MAX_TEMPS_ESTIMAT)) * PESO_TEMPS);

        return prioridad;
    }

}
