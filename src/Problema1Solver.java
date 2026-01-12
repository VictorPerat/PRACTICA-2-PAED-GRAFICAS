import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

public class Problema1Solver {

    private static final int MAX_N_BACKTRACKING = 30;
    private static final long TIMEOUT_SECONDS = 10; // 10 segons màxim per backtracking

    public static class Result {
        List<Quest> seleccionades;
        double valorTotal;
        int tempsTotal;

        public Result(List<Quest> sel, double val, int temps) {
            this.seleccionades = sel;
            this.valorTotal = val;
            this.tempsTotal = temps;
        }
    }

    // GREEDY (sense canvis)
// LocalDate --> Data (dia/mes/any) sense hora
// String --> Nom de l’assignatura
// Integer --> Minuts acumulats (enter)
    public static Result greedy(List<Quest> quests, int limitTemps) {

        Ordenador.sortPerRatioValor(quests);
        List<Quest> sel = new ArrayList<>();
        int tempsAct = 0;
        Map<LocalDate, Integer> tempsDia = new HashMap<>();
        Map<String, Integer> tempsAsig = new HashMap<>();
        for (Quest q : quests) {
            double tempsEf = QuestValueCalculator.tempsEfectiu(q, tempsAsig);
            LocalDate dia = q.getDataLliurament();
            int tempsDiaNou = tempsDia.getOrDefault(dia, 0) + q.getTempsEstim();
            if (tempsAct + tempsEf <= limitTemps && tempsDiaNou <= 480) {
                sel.add(q);
                tempsAct += (int) tempsEf;
                tempsDia.put(dia, tempsDiaNou);
                tempsAsig.merge(q.getAsignatura(), q.getTempsEstim(), Integer::sum);
            }
        }

        double valor = sel.stream().mapToDouble(Quest::getValor).sum();

        return new Result(sel, valor, tempsAct);
    }


    // BACKTRACKING amb timeout
    private static volatile List<Quest> millorSelBT;
    private static volatile double millorValorBT;
    private static volatile boolean timeoutReached = false;

    public static Result backtracking(List<Quest> quests, int limitTemps) {
        if (quests.size() > MAX_N_BACKTRACKING) {
            System.out.println("[Backtracking P1] Dataset massa gran (n=" + quests.size() + "). Omes.");
            return new Result(new ArrayList<>(), 0.0, 0);
        }

        Ordenador.sortPerRatioValor(quests);
        millorSelBT = new ArrayList<>();
        millorValorBT = 0.0;
        timeoutReached = false;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            List<Quest> actual = new ArrayList<>();
            Map<LocalDate, Integer> tempsDia = new HashMap<>();
            Map<String, Integer> tempsAsig = new HashMap<>();
            backtrackingRec(quests, 0, actual, 0.0, 0, tempsDia, tempsAsig, limitTemps);
        });

        try {
            future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            timeoutReached = true;
            System.out.println("[Backtracking P1] Timeout després de " + TIMEOUT_SECONDS + " segons. Millor solució parcial utilitzada.");
        } catch (Exception e) {
            System.out.println("[Backtracking P1] Error: " + e.getMessage());
        } finally {
            executor.shutdownNow();
        }

        int tempsTotal = 0;
        if (!millorSelBT.isEmpty()) {
            Map<String, Integer> tempsAsigFinal = Utils.getTempsPerAsig(millorSelBT);
            for (Quest q : millorSelBT) {
                tempsTotal += (int) QuestValueCalculator.tempsEfectiu(q, tempsAsigFinal);
            }
        }

        return new Result(new ArrayList<>(millorSelBT), millorValorBT, tempsTotal);
    }

    private static void backtrackingRec(List<Quest> quests, int index,
                                        List<Quest> actual, double valorActual, int tempsActual,
                                        Map<LocalDate, Integer> tempsDia,
                                        Map<String, Integer> tempsAsig, int limitTemps) {
        if (timeoutReached) return;

        if (valorActual > millorValorBT) {
            millorValorBT = valorActual;
            millorSelBT = new ArrayList<>(actual);
        }

        if (index >= quests.size()) return;

        Quest q = quests.get(index);

        backtrackingRec(quests, index + 1, actual, valorActual, tempsActual, tempsDia, tempsAsig, limitTemps);

        double tempsEf = QuestValueCalculator.tempsEfectiu(q, tempsAsig);
        int tempsNou = tempsActual + (int) tempsEf;
        LocalDate dia = q.getDataLliurament();
        int tempsDiaNou = tempsDia.getOrDefault(dia, 0) + q.getTempsEstim();

        if (tempsNou <= limitTemps && tempsDiaNou <= 480) {
            actual.add(q);
            tempsDia.put(dia, tempsDiaNou);
            tempsAsig.merge(q.getAsignatura(), q.getTempsEstim(), Integer::sum);

            backtrackingRec(quests, index + 1, actual, valorActual + q.getValor(),
                    tempsNou, tempsDia, tempsAsig, limitTemps);

            actual.remove(actual.size() - 1);
            tempsDia.put(dia, tempsDia.get(dia) - q.getTempsEstim());
            if (tempsDia.get(dia) <= 0) tempsDia.remove(dia);
            tempsAsig.merge(q.getAsignatura(), -q.getTempsEstim(), Integer::sum);
            if (tempsAsig.get(q.getAsignatura()) <= 0) tempsAsig.remove(q.getAsignatura());
        }
    }

    // ==================== BRANCH & BOUND ====================
    private static class BBState implements Comparable<BBState> {
        List<Quest> seleccionades;
        double valorActual;
        int tempsActual;
        double upperBound;
        int index;
        Map<LocalDate, Integer> tempsDia;
        Map<String, Integer> tempsAsig;

        BBState(List<Quest> sel, double val, int temps, double bound, int idx,
                Map<LocalDate, Integer> dia, Map<String, Integer> asig) {
            this.seleccionades = sel;
            this.valorActual = val;
            this.tempsActual = temps;
            this.upperBound = bound;
            this.index = idx;
            this.tempsDia = dia;
            this.tempsAsig = asig;
        }

        @Override
        public int compareTo(BBState o) {
            return Double.compare(o.upperBound, this.upperBound);
        }
    }

    public static Result branchAndBound(List<Quest> quests, int limitTemps) {
        Ordenador.sortPerRatioValor(quests);

        PriorityQueue<BBState> queue = new PriorityQueue<>();
        queue.add(new BBState(new ArrayList<>(), 0.0, 0, upperBoundInicial(quests, limitTemps),
                0, new HashMap<>(), new HashMap<>()));

        List<Quest> millorSel = new ArrayList<>();
        double millorValor = 0.0;
        int millorTemps = 0;

        while (!queue.isEmpty()) {
            BBState estat = queue.poll();

            if (estat.valorActual > millorValor) {
                millorValor = estat.valorActual;
                millorSel = new ArrayList<>(estat.seleccionades);
                millorTemps = estat.tempsActual;
            }

            if (estat.index >= quests.size()) continue;

            Quest q = quests.get(estat.index);

            // NO incluir
            double boundNo = estat.valorActual + upperBoundRestantes(quests, estat.index + 1,
                    limitTemps - estat.tempsActual);
            if (boundNo > millorValor) {
                queue.add(new BBState(new ArrayList<>(estat.seleccionades), estat.valorActual,
                        estat.tempsActual, boundNo, estat.index + 1,
                        new HashMap<>(estat.tempsDia), new HashMap<>(estat.tempsAsig)));
            }

            double tempsEf = QuestValueCalculator.tempsEfectiu(q, estat.tempsAsig);
            int tempsNou = estat.tempsActual + (int) tempsEf;
            LocalDate dia = q.getDataLliurament();
            int tempsDiaNou = estat.tempsDia.getOrDefault(dia, 0) + q.getTempsEstim();

            if (tempsNou <= limitTemps && tempsDiaNou <= 480) {
                List<Quest> novaSel = new ArrayList<>(estat.seleccionades);
                novaSel.add(q);

                Map<LocalDate, Integer> novaDia = new HashMap<>(estat.tempsDia);
                novaDia.put(dia, tempsDiaNou);

                Map<String, Integer> novaAsig = new HashMap<>(estat.tempsAsig);
                novaAsig.merge(q.getAsignatura(), q.getTempsEstim(), Integer::sum);

                double boundSi = estat.valorActual + q.getValor() +
                        upperBoundRestantes(quests, estat.index + 1, limitTemps - tempsNou);

                if (boundSi > millorValor) {
                    queue.add(new BBState(novaSel, estat.valorActual + q.getValor(),
                            tempsNou, boundSi, estat.index + 1, novaDia, novaAsig));
                }
            }
        }

        return new Result(millorSel, millorValor, millorTemps);
    }

    private static double upperBoundInicial(List<Quest> quests, int limitTemps) {
        return upperBoundRestantes(quests, 0, limitTemps);
    }

    private static double upperBoundRestantes(List<Quest> quests, int start, int capacitatRestant) {
        double bound = 0.0;
        for (int i = start; i < quests.size() && capacitatRestant > 0; i++) {
            Quest q = quests.get(i);
            if (q.getTempsEstim() <= capacitatRestant) {
                bound += q.getValor();
                capacitatRestant -= q.getTempsEstim();
            } else {
                bound += (capacitatRestant / (double) q.getTempsEstim()) * q.getValor();
                break;
            }
        }
        return bound;
    }
}