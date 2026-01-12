import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

public class Problema1Solver {

    private static final int MAXIMO_MISIONES_PARA_BACKTRACKING = 30;
    private static final long TIEMPO_MAXIMO_EN_SEGUNDOS = 10;

    public static class Result {
        List<Quest> listaDeMisionesSeleccionadas;
        double valorTotalAcumulado;
        int tiempoTotalMinutos;

        public Result(List<Quest> listaDeMisionesSeleccionadas, double valorTotalAcumulado, int tiempoTotalMinutos) {
            this.listaDeMisionesSeleccionadas = listaDeMisionesSeleccionadas;
            this.valorTotalAcumulado = valorTotalAcumulado;
            this.tiempoTotalMinutos = tiempoTotalMinutos;
        }
    }


    public static Result greedy(List<Quest> listaDeMisiones, int limiteDeTiempoMaximoMinutos) {

        Ordenador.ordenarPorRatioValorSobreTiempoDescendente(listaDeMisiones);
        List<Quest> listaDeMisionesSeleccionadas = new ArrayList<>();
        int tiempoAcumuladoActualMinutos = 0;
        Map<LocalDate, Integer> tiempoAcumuladoPorDia = new HashMap<>();
        Map<String, Integer> tiempoAcumuladoPorAsignatura = new HashMap<>();

        for (Quest misionActual : listaDeMisiones) {
            double tiempoEfectivoConDescuento = QuestValueCalculator.calcularTiempoEfectivoConDescuentoPorAsignatura(misionActual, tiempoAcumuladoPorAsignatura);
            LocalDate fechaDeEntregaMision = misionActual.getFechaDeEntrega();
            int tiempoTotalDelDiaTrasAgregar = tiempoAcumuladoPorDia.getOrDefault(fechaDeEntregaMision, 0) + misionActual.getTiempoEstimadoEnMinutos();

            if (tiempoAcumuladoActualMinutos + tiempoEfectivoConDescuento <= limiteDeTiempoMaximoMinutos && tiempoTotalDelDiaTrasAgregar <= 480) {
                listaDeMisionesSeleccionadas.add(misionActual);
                tiempoAcumuladoActualMinutos += (int) tiempoEfectivoConDescuento;
                tiempoAcumuladoPorDia.put(fechaDeEntregaMision, tiempoTotalDelDiaTrasAgregar);
                tiempoAcumuladoPorAsignatura.merge(misionActual.getNombreDeLaAsignatura(), misionActual.getTiempoEstimadoEnMinutos(), Integer::sum);
            }
        }
        double valorTotalCalculadoDeLaMision = listaDeMisionesSeleccionadas.stream().mapToDouble(Quest::getValorTotalCalculadoDeLaMision).sum();
        return new Result(listaDeMisionesSeleccionadas, valorTotalCalculadoDeLaMision, tiempoAcumuladoActualMinutos);
    }


    private static volatile List<Quest> millorSelBT;
    private static volatile double millorValorBT;
    private static volatile boolean timeoutReached = false;

    public static Result backtracking(List<Quest> listaDeMisiones, int limiteDeTiempoMaximoMinutos) {
        if (listaDeMisiones.size() > MAXIMO_MISIONES_PARA_BACKTRACKING) {
            System.out.println("[Backtracking P1] Dataset massa gran (n=" + listaDeMisiones.size() + "). Omes.");
            return new Result(new ArrayList<>(), 0.0, 0);
        }

        Ordenador.ordenarPorRatioValorSobreTiempoDescendente(listaDeMisiones);
        millorSelBT = new ArrayList<>();
        millorValorBT = 0.0;
        timeoutReached = false;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            List<Quest> actual = new ArrayList<>();
            Map<LocalDate, Integer> tiempoAcumuladoPorDia = new HashMap<>();
            Map<String, Integer> tiempoAcumuladoPorAsignatura = new HashMap<>();
            backtrackingRec(listaDeMisiones, 0, actual, 0.0, 0, tiempoAcumuladoPorDia, tiempoAcumuladoPorAsignatura, limiteDeTiempoMaximoMinutos);
        });

        try {
            future.get(TIEMPO_MAXIMO_EN_SEGUNDOS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            timeoutReached = true;
            System.out.println("[Backtracking P1] Timeout després de " + TIEMPO_MAXIMO_EN_SEGUNDOS + " segons. Millor solució parcial utilitzada.");
        } catch (Exception e) {
            System.out.println("[Backtracking P1] Error: " + e.getMessage());
        } finally {
            executor.shutdownNow();
        }

        int tiempoTotalMinutos = 0;
        if (!millorSelBT.isEmpty()) {
            Map<String, Integer> tempsAsigFinal = Utils.obtenerMapaTiempoAcumuladoPorAsignatura(millorSelBT);
            for (Quest misionActual : millorSelBT) {
                tiempoTotalMinutos += (int) QuestValueCalculator.calcularTiempoEfectivoConDescuentoPorAsignatura(misionActual, tempsAsigFinal);
            }
        }

        return new Result(new ArrayList<>(millorSelBT), millorValorBT, tiempoTotalMinutos);
    }

    private static void backtrackingRec(List<Quest> listaDeMisiones, int index,
                                        List<Quest> actual, double valorActual, int tempsActual,
                                        Map<LocalDate, Integer> tiempoAcumuladoPorDia,
                                        Map<String, Integer> tiempoAcumuladoPorAsignatura, int limiteDeTiempoMaximoMinutos) {
        if (timeoutReached) return;

        if (valorActual > millorValorBT) {
            millorValorBT = valorActual;
            millorSelBT = new ArrayList<>(actual);
        }

        if (index >= listaDeMisiones.size()) return;

        Quest misionActual = listaDeMisiones.get(index);

        backtrackingRec(listaDeMisiones, index + 1, actual, valorActual, tempsActual, tiempoAcumuladoPorDia, tiempoAcumuladoPorAsignatura, limiteDeTiempoMaximoMinutos);

        double tiempoEfectivoConDescuento = QuestValueCalculator.calcularTiempoEfectivoConDescuentoPorAsignatura(misionActual, tiempoAcumuladoPorAsignatura);
        int tempsNou = tempsActual + (int) tiempoEfectivoConDescuento;
        LocalDate fechaDeEntregaMision = misionActual.getFechaDeEntrega();
        int tiempoTotalDelDiaTrasAgregar = tiempoAcumuladoPorDia.getOrDefault(fechaDeEntregaMision, 0) + misionActual.getTiempoEstimadoEnMinutos();

        if (tempsNou <= limiteDeTiempoMaximoMinutos && tiempoTotalDelDiaTrasAgregar <= 480) {
            actual.add(misionActual);
            tiempoAcumuladoPorDia.put(fechaDeEntregaMision, tiempoTotalDelDiaTrasAgregar);
            tiempoAcumuladoPorAsignatura.merge(misionActual.getNombreDeLaAsignatura(), misionActual.getTiempoEstimadoEnMinutos(), Integer::sum);

            backtrackingRec(listaDeMisiones, index + 1, actual, valorActual + misionActual.getValorTotalCalculadoDeLaMision(),
                    tempsNou, tiempoAcumuladoPorDia, tiempoAcumuladoPorAsignatura, limiteDeTiempoMaximoMinutos);

            actual.remove(actual.size() - 1);
            tiempoAcumuladoPorDia.put(fechaDeEntregaMision, tiempoAcumuladoPorDia.get(fechaDeEntregaMision) - misionActual.getTiempoEstimadoEnMinutos());
            if (tiempoAcumuladoPorDia.get(fechaDeEntregaMision) <= 0) tiempoAcumuladoPorDia.remove(fechaDeEntregaMision);
            tiempoAcumuladoPorAsignatura.merge(misionActual.getNombreDeLaAsignatura(), -misionActual.getTiempoEstimadoEnMinutos(), Integer::sum);
            if (tiempoAcumuladoPorAsignatura.get(misionActual.getNombreDeLaAsignatura()) <= 0) tiempoAcumuladoPorAsignatura.remove(misionActual.getNombreDeLaAsignatura());
        }
    }


    private static class BBState implements Comparable<BBState> {
        List<Quest> listaDeMisionesSeleccionadas;
        double valorActual;
        int tempsActual;
        double upperBound;
        int index;
        Map<LocalDate, Integer> tiempoAcumuladoPorDia;
        Map<String, Integer> tiempoAcumuladoPorAsignatura;

        BBState(List<Quest> listaDeMisionesSeleccionadas, double valorTotalAcumulado, int tiempoTotalMinutos, double bound, int idx,
                Map<LocalDate, Integer> fechaDeEntregaMision, Map<String, Integer> asignaturaActual) {
            this.listaDeMisionesSeleccionadas = listaDeMisionesSeleccionadas;
            this.valorActual = valorTotalAcumulado;
            this.tempsActual = tiempoTotalMinutos;
            this.upperBound = bound;
            this.index = idx;
            this.tiempoAcumuladoPorDia = fechaDeEntregaMision;
            this.tiempoAcumuladoPorAsignatura = asignaturaActual;
        }

        @Override
        public int compareTo(BBState o) {
            return Double.compare(o.upperBound, this.upperBound);
        }
    }

    public static Result branchAndBound(List<Quest> listaDeMisiones, int limiteDeTiempoMaximoMinutos) {
        Ordenador.ordenarPorRatioValorSobreTiempoDescendente(listaDeMisiones);

        PriorityQueue<BBState> queue = new PriorityQueue<>();
        queue.add(new BBState(new ArrayList<>(), 0.0, 0, upperBoundInicial(listaDeMisiones, limiteDeTiempoMaximoMinutos),
                0, new HashMap<>(), new HashMap<>()));

        List<Quest> millorSel = new ArrayList<>();
        double millorValor = 0.0;
        int millorTemps = 0;

        while (!queue.isEmpty()) {
            BBState estat = queue.poll();

            if (estat.valorActual > millorValor) {
                millorValor = estat.valorActual;
                millorSel = new ArrayList<>(estat.listaDeMisionesSeleccionadas);
                millorTemps = estat.tempsActual;
            }

            if (estat.index >= listaDeMisiones.size()) continue;

            Quest misionActual = listaDeMisiones.get(estat.index);


            double boundNo = estat.valorActual + upperBoundRestantes(listaDeMisiones, estat.index + 1,
                    limiteDeTiempoMaximoMinutos - estat.tempsActual);
            if (boundNo > millorValor) {
                queue.add(new BBState(new ArrayList<>(estat.listaDeMisionesSeleccionadas), estat.valorActual,
                        estat.tempsActual, boundNo, estat.index + 1,
                        new HashMap<>(estat.tiempoAcumuladoPorDia), new HashMap<>(estat.tiempoAcumuladoPorAsignatura)));
            }


            double tiempoEfectivoConDescuento = QuestValueCalculator.calcularTiempoEfectivoConDescuentoPorAsignatura(misionActual, estat.tiempoAcumuladoPorAsignatura);
            int tempsNou = estat.tempsActual + (int) tiempoEfectivoConDescuento;
            LocalDate fechaDeEntregaMision = misionActual.getFechaDeEntrega();
            int tiempoTotalDelDiaTrasAgregar = estat.tiempoAcumuladoPorDia.getOrDefault(fechaDeEntregaMision, 0) + misionActual.getTiempoEstimadoEnMinutos();

            if (tempsNou <= limiteDeTiempoMaximoMinutos && tiempoTotalDelDiaTrasAgregar <= 480) {
                List<Quest> novaSel = new ArrayList<>(estat.listaDeMisionesSeleccionadas);
                novaSel.add(misionActual);

                Map<LocalDate, Integer> novaDia = new HashMap<>(estat.tiempoAcumuladoPorDia);
                novaDia.put(fechaDeEntregaMision, tiempoTotalDelDiaTrasAgregar);

                Map<String, Integer> novaAsig = new HashMap<>(estat.tiempoAcumuladoPorAsignatura);
                novaAsig.merge(misionActual.getNombreDeLaAsignatura(), misionActual.getTiempoEstimadoEnMinutos(), Integer::sum);

                double boundSi = estat.valorActual + misionActual.getValorTotalCalculadoDeLaMision() +
                        upperBoundRestantes(listaDeMisiones, estat.index + 1, limiteDeTiempoMaximoMinutos - tempsNou);

                if (boundSi > millorValor) {
                    queue.add(new BBState(novaSel, estat.valorActual + misionActual.getValorTotalCalculadoDeLaMision(),
                            tempsNou, boundSi, estat.index + 1, novaDia, novaAsig));
                }
            }
        }

        return new Result(millorSel, millorValor, millorTemps);
    }

    private static double upperBoundInicial(List<Quest> listaDeMisiones, int limiteDeTiempoMaximoMinutos) {
        return upperBoundRestantes(listaDeMisiones, 0, limiteDeTiempoMaximoMinutos);
    }

    private static double upperBoundRestantes(List<Quest> listaDeMisiones, int start, int capacitatRestant) {
        double bound = 0.0;
        for (int i = start; i < listaDeMisiones.size() && capacitatRestant > 0; i++) {
            Quest misionActual = listaDeMisiones.get(i);
            if (misionActual.getTiempoEstimadoEnMinutos() <= capacitatRestant) {
                bound += misionActual.getValorTotalCalculadoDeLaMision();
                capacitatRestant -= misionActual.getTiempoEstimadoEnMinutos();
            } else {
                bound += (capacitatRestant / (double) misionActual.getTiempoEstimadoEnMinutos()) * misionActual.getValorTotalCalculadoDeLaMision();
                break;
            }
        }
        return bound;
    }
}
