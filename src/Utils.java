import java.time.LocalDate;
import java.util.*;

public class Utils {


    /** Crea un mapa amb el temps acumulat per assignatura (per descompte 10%) */
    public static Map<String, Integer> obtenerMapaTiempoAcumuladoPorAsignatura(List<Quest> listaDeMisionesSeleccionadas) {
        Map<String, Integer> mapaTiempoAcumuladoPorAsignatura = new HashMap<>();
        for (Quest misionActual : listaDeMisionesSeleccionadas) {
            String asignaturaActual = misionActual.getNombreDeLaAsignatura();
            mapaTiempoAcumuladoPorAsignatura.put(asignaturaActual, mapaTiempoAcumuladoPorAsignatura.getOrDefault(asignaturaActual, 0) + misionActual.getTiempoEstimadoEnMinutos());
        }
        return mapaTiempoAcumuladoPorAsignatura;
    }

    // ──────────────────────────────────────────────────────────────
    // FUNCIONS PER AL PROBLEMA 2 (Minimitzar listaDeSemanas)
    // ──────────────────────────────────────────────────────────────

    /**
     * Lower bound millorat: considera tant el temps com el límit de 6 comunes per setmana
     */
    public static int calcularCotaInferiorDeSemanas(List<Quest> listaDeMisiones) {
        if (listaDeMisiones == null || listaDeMisiones.isEmpty()) return 0;

        int tiempoTotalDeLaSemanaMinutos = listaDeMisiones.stream().mapToInt(Quest::getTiempoEstimadoEnMinutos).sum();
        int cotaInferiorPorTiempo = (int) Math.ceil(tiempoTotalDeLaSemanaMinutos / 1200.0);

        long numeroDeMisionesComunesTotal = listaDeMisiones.stream()
                .filter(misionActual -> misionActual.getCodigoHexDeRareza().equalsIgnoreCase("#4fd945"))
                .count();
        int cotaInferiorPorComunes = (int) Math.ceil(numeroDeMisionesComunesTotal / 6.0);

        return Math.max(cotaInferiorPorTiempo, cotaInferiorPorComunes);
    }

    /**
     * Comprova si es pot afegir una missió a una setmana
     * Restriccions: ≤1200 min i ≤6 missions comunes (#4fd945)
     */
    public static boolean sePuedeAgregarMisionALaSemana(Quest misionActual, List<Quest> semana) {
        if (misionActual == null || semana == null) return false;

        // Temps
        int tiempoAcumuladoActualEnLaSemanaMinutos = semana.stream().mapToInt(Quest::getTiempoEstimadoEnMinutos).sum();
        if (tiempoAcumuladoActualEnLaSemanaMinutos + misionActual.getTiempoEstimadoEnMinutos() > 1200) {
            return false;
        }

        // Comunes: màxim 6 (per tant, si n'hi ha 6 ja, no es pot afegir una altra comuna)
        int numeroDeMisionesComunesEnLaSemana = (int) semana.stream()
                .filter(w -> w.getCodigoHexDeRareza().equalsIgnoreCase("#4fd945"))
                .count();

        boolean laMisionActualEsComun = misionActual.getCodigoHexDeRareza().equalsIgnoreCase("#4fd945");
        if (laMisionActualEsComun && numeroDeMisionesComunesEnLaSemana >= 6) {  // >= 6 → ja està ple, no es pot afegir
            return false;
        }

        return true;
    }


}