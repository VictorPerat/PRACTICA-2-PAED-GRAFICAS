import java.util.Map;

public class QuestValueCalculator {

    /**
     * Retorna el multiplicador segons la raresa (pes/hex)
     */
    public static double getMultiplicadorRaresa(String pes) {
        if (pes == null) return 1.0;
        String hex = pes.toLowerCase().trim();
        if (hex.equals("#4fd945")) return 1.0;   // Comú
        if (hex.equals("#cc00ff")) return 2.0;   // Rar
        if (hex.equals("#ff8000")) return 5.0;   // Llegendari
        return 1.0; // Default per seguretat
    }

    /**
     * Calcula el valor de la missió segons la fórmula:
     * valor = tempsEstim * dificultat * (1 - progres/100) * multiplicador_raresa
     */
    public static double calcularValor(Quest q) {
        double mult = getMultiplicadorRaresa(q.getPes());
        double urgent = (100.0 - q.getProgres()) / 100.0;  // Menys progrés → més valor
        return q.getTempsEstim() * q.getDificultat() * urgent * mult;
    }

    /**
     * Calcula el temps efectiu tenint en compte el descompte del 10% si l'assignatura ja s'ha fet abans
     * @param q La quest a avaluar
     * @param tempsPerAsig Mapa amb el temps acumulat per assignatura (fins ara)
     * @return tempsEstim * 0.9 si l'assignatura ja existeix al mapa, sinó tempsEstim
     */
    public static double tempsEfectiu(Quest q, Map<String, Integer> tempsPerAsig) {
        if (q == null || tempsPerAsig == null) return q != null ? q.getTempsEstim() : 0;

        String asig = q.getAsignatura();
        boolean jaFeta = tempsPerAsig.containsKey(asig) && tempsPerAsig.get(asig) > 0;
        return q.getTempsEstim() * (jaFeta ? 0.9 : 1.0);
    }
}