import java.time.LocalDate;
import java.util.*;

public class Utils {


    public static Map<String, Integer> obtenerMapaTiempoAcumuladoPorAsignatura(List<Quest> listaDeMisionesSeleccionadas) {
        Map<String, Integer> mapaTiempoAcumuladoPorAsignatura = new HashMap<>();
        for (Quest misionActual : listaDeMisionesSeleccionadas) {
            String asignaturaActual = misionActual.getNombreDeLaAsignatura();
            mapaTiempoAcumuladoPorAsignatura.put(asignaturaActual, mapaTiempoAcumuladoPorAsignatura.getOrDefault(asignaturaActual, 0) + misionActual.getTiempoEstimadoEnMinutos());
        }
        return mapaTiempoAcumuladoPorAsignatura;
    }


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


    public static boolean sePuedeAgregarMisionALaSemana(Quest misionActual, List<Quest> semana) {
        if (misionActual == null || semana == null) return false;


        int tiempoAcumuladoActualEnLaSemanaMinutos = semana.stream().mapToInt(Quest::getTiempoEstimadoEnMinutos).sum();
        if (tiempoAcumuladoActualEnLaSemanaMinutos + misionActual.getTiempoEstimadoEnMinutos() > 1200) {
            return false;
        }


        int numeroDeMisionesComunesEnLaSemana = (int) semana.stream()
                .filter(w -> w.getCodigoHexDeRareza().equalsIgnoreCase("#4fd945"))
                .count();

        boolean laMisionActualEsComun = misionActual.getCodigoHexDeRareza().equalsIgnoreCase("#4fd945");
        if (laMisionActualEsComun && numeroDeMisionesComunesEnLaSemana >= 6) {
            return false;
        }

        return true;
    }


}
