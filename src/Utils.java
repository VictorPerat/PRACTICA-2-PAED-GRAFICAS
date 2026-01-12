import java.time.LocalDate;
import java.util.*;

public class Utils {


    public static Map<String, Integer> getTempsPerAsig(List<Quest> seleccionades) {
        Map<String, Integer> map = new HashMap<>();
        for (Quest q : seleccionades) {
            String asig = q.getAsignatura();
            map.put(asig, map.getOrDefault(asig, 0) + q.getTempsEstim());
        }
        return map;
    }

    public static int lowerBoundSetmanes(List<Quest> quests) {
        if (quests == null || quests.isEmpty()) return 0;

        int totalTemps = quests.stream().mapToInt(Quest::getTempsEstim).sum();
        int lbTemps = (int) Math.ceil(totalTemps / 1200.0);

        long numComunes = quests.stream()
                .filter(q -> q.getPes().equalsIgnoreCase("#4fd945"))
                .count();
        int lbComunes = (int) Math.ceil(numComunes / 6.0);

        return Math.max(lbTemps, lbComunes);
    }

    public static boolean setmanaOK(Quest q, List<Quest> semana) {
        if (q == null || semana == null) return false;

        int tempsActual = semana.stream().mapToInt(Quest::getTempsEstim).sum();
        if (tempsActual + q.getTempsEstim() > 1200) {
            return false;
        }

        int comunesActuals = (int) semana.stream()
                .filter(w -> w.getPes().equalsIgnoreCase("#4fd945"))
                .count();

        boolean esComu = q.getPes().equalsIgnoreCase("#4fd945");
        if (esComu && comunesActuals >= 6) {
            return false;
        }

        return true;
    }


}