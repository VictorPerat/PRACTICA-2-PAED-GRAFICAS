import java.util.Map;

public class QuestValueCalculator {

    public static double getMultiplicadorRaresa(String pes) {
        if (pes == null) return 1.0;
        // Multiplicador per raresa
        String hex = pes.toLowerCase().trim();
        if (hex.equals("#4fd945")) return 1.0;   // Comú
        if (hex.equals("#cc00ff")) return 2.0;   // Rar
        if (hex.equals("#ff8000")) return 5.0;   // Llegendari
        return 1.0;
    }

    // Valor de la missió
    public static double calcularValor(Quest q) {
        double mult = getMultiplicadorRaresa(q.getPes()); // Cogemos el multiplicador
        double urgent = (100.0 - q.getProgres()) / 100.0;  // Menys progrés → més valor
        return q.getTempsEstim() * q.getDificultat() * urgent * mult;
    }

    public static double tempsEfectiu(Quest q, Map<String, Integer> tempsPerAsig) {
        // Si q == Null o el tempsPerAsig == null se ejecuta el condicional
        // Si q es diferent de null retorna q.getTempsEstim()
        // Si q es null retorna 0
        if (q == null || tempsPerAsig == null) return q != null ? q.getTempsEstim() : 0;

        String asig = q.getAsignatura();
        // Si el mapa conte ja una asignatura
        boolean jaFeta = tempsPerAsig.containsKey(asig) && tempsPerAsig.get(asig) > 0;
        return q.getTempsEstim() * (jaFeta ? 0.9 : 1.0); //Apliquem el descompte per asignatura
    }
}