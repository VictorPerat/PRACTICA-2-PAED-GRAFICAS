import java.util.*;
import java.util.concurrent.*;

public class Problema2Solver {

    private static final int MAX_N_BACKTRACKING = 30;
    private static final int MAX_N_BB = 2000;
    private static final long TIMEOUT_SECONDS = 10;

    public static class Result {
        List<List<Quest>> setmanes;
        int numSetmanes;
        int tempsTotal;

        public Result(List<List<Quest>> setmanes) {
            this.setmanes = setmanes;
            this.numSetmanes = setmanes.size();
            this.tempsTotal = setmanes.stream()
                    .mapToInt(s -> s.stream().mapToInt(Quest::getTempsEstim).sum())
                    .sum();
        }
    }

    // ==================== GREEDY ====================
    public static Result greedy(List<Quest> inputQuests) {
        // Copia defensiva para no mutar la entrada
        List<Quest> quests = new ArrayList<>(inputQuests);
        Ordenador.sortPerRaresaTemps(quests);

        List<List<Quest>> setmanes = new ArrayList<>();
        for (Quest q : quests) {
            boolean assignada = false;
            for (List<Quest> setmana : setmanes) {
                if (Utils.setmanaOK(q, setmana)) {
                    setmana.add(q);
                    assignada = true;
                    break;
                }
            }
            if (!assignada) {
                List<Quest> nova = new ArrayList<>();
                nova.add(q);
                setmanes.add(nova);
            }
        }
        return new Result(setmanes);
    }

    // ==================== BACKTRACKING ====================
    private static volatile List<List<Quest>> millorSolucioBT;
    private static volatile int millorCostBT;
    private static volatile boolean timeoutReached = false;

    public static Result backtracking(List<Quest> inputQuests) {
        if (inputQuests.size() > MAX_N_BACKTRACKING) {
            System.out.println("[Backtracking P2] Dataset massa gran (n=" + inputQuests.size() + "). S'omet.");
            return greedy(inputQuests);
        }

        // Poda inicial con Greedy
        Result greedyResult = greedy(inputQuests);
        millorCostBT = greedyResult.numSetmanes;
        millorSolucioBT = copiaSetmanes(greedyResult.setmanes);
        System.out.println("[Backtracking P2] Poda inicial amb Greedy: " + millorCostBT + " setmanes");

        // Copia y ordenación
        List<Quest> quests = new ArrayList<>(inputQuests);
        Ordenador.sortPerRaresaTemps(quests);
        timeoutReached = false;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            LinkedList<Quest> restants = new LinkedList<>(quests);
            backtrackingRecP2(new ArrayList<>(), restants);
        });

        try {
            future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            timeoutReached = true;
            System.out.println("[Backtracking P2] Timeout de " + TIMEOUT_SECONDS + "s. S'usa la millor solució trobada.");
        } catch (Exception e) {
            System.out.println("[Backtracking P2] Error: " + e.getMessage());
        } finally {
            executor.shutdownNow();
        }

        return new Result(millorSolucioBT != null ? millorSolucioBT : greedyResult.setmanes);
    }

    private static void backtrackingRecP2(List<List<Quest>> setmanesActuals, LinkedList<Quest> restants) {
        if (timeoutReached) return;

        if (restants.isEmpty()) {
            int cost = setmanesActuals.size();
            if (cost < millorCostBT) {
                synchronized (Problema2Solver.class) {
                    if (cost < millorCostBT) {
                        millorCostBT = cost;
                        millorSolucioBT = copiaSetmanes(setmanesActuals);
                    }
                }
            }
            return;
        }

        if (setmanesActuals.size() + Utils.lowerBoundSetmanes(restants) >= millorCostBT) return;

        Quest q = restants.pollFirst();

        // Afegir a setmana existent
        for (int i = 0; i < setmanesActuals.size(); i++) {
            List<Quest> setmana = setmanesActuals.get(i);
            if (Utils.setmanaOK(q, setmana)) {
                setmana.add(q);
                backtrackingRecP2(setmanesActuals, restants);
                setmana.remove(setmana.size() - 1);
                if (timeoutReached) {
                    restants.addFirst(q);
                    return;
                }
            }
        }

        // Crear nova setmana
        List<Quest> novaSetmana = new ArrayList<>();
        novaSetmana.add(q);
        setmanesActuals.add(novaSetmana);
        backtrackingRecP2(setmanesActuals, restants);
        setmanesActuals.remove(setmanesActuals.size() - 1);

        restants.addFirst(q);
    }

    // ==================== BRANCH & BOUND ====================
    private static class State implements Comparable<State> {
        List<List<Quest>> setmanes;
        int index;
        int costEstimat;

        State(List<List<Quest>> setmanes, int index, int estimat) {
            this.setmanes = setmanes;
            this.index = index;
            this.costEstimat = estimat;
        }

        @Override
        public int compareTo(State o) {
            int cmp = Integer.compare(this.costEstimat, o.costEstimat);
            if (cmp != 0) return cmp;
            return Integer.compare(o.index, this.index); // Tiebreaker: más progreso primero
        }
    }

    public static Result branchAndBound(List<Quest> inputQuests) {
        if (inputQuests.size() > MAX_N_BB) {
            System.out.println("[Branch & Bound P2] Dataset massa gran (n=" + inputQuests.size() + "). S'usa Greedy.");
            return greedy(inputQuests);
        }

        // Copia defensiva y ordenación
        List<Quest> quests = new ArrayList<>(inputQuests);
        Ordenador.sortPerRaresaTemps(quests);
        int n = quests.size();

        // Precomputación lower bound
        int[] cumTemps = new int[n + 1];
        int[] cumComunes = new int[n + 1];
        for (int i = n - 1; i >= 0; i--) {
            cumTemps[i] = cumTemps[i + 1] + quests.get(i).getTempsEstim();
            cumComunes[i] = cumComunes[i + 1] + (quests.get(i).getPes().equalsIgnoreCase("#4fd945") ? 1 : 0);
        }

        // Solución inicial con Greedy
        Result greedyResult = greedy(inputQuests);
        int millorCost = greedyResult.numSetmanes;
        List<List<Quest>> millorSolucio = copiaSetmanes(greedyResult.setmanes);
        System.out.println("[B&B P2] Poda inicial amb Greedy: " + millorCost + " setmanes");

        PriorityQueue<State> queue = new PriorityQueue<>();
        int lbInicial = lowerBoundPrecomp(cumTemps, cumComunes, 0);
        queue.add(new State(new ArrayList<>(), 0, lbInicial));

        long iteracions = 0;
        final long MAX_ITER = 10_000_000;

        while (!queue.isEmpty() && iteracions < MAX_ITER) {
            iteracions++;
            State estat = queue.poll();

            if (estat.index >= n) {
                int cost = estat.setmanes.size();
                if (cost < millorCost) {
                    millorCost = cost;
                    millorSolucio = copiaSetmanes(estat.setmanes);
                    System.out.println("[B&B P2] Nova millor solució: " + millorCost + " setmanes (iteració " + iteracions + ")");
                }
                continue;
            }

            if (estat.costEstimat >= millorCost) continue;

            Quest q = quests.get(estat.index);
            int lbRemaining = lowerBoundPrecomp(cumTemps, cumComunes, estat.index + 1);

            // Rama 1: Añadir a la mejor semana existente (best-fit)
            List<Quest> millorSetmana = null;
            int millorEspaiLliure = Integer.MAX_VALUE;

            for (List<Quest> setmana : estat.setmanes) {
                if (Utils.setmanaOK(q, setmana)) {
                    int tempsActual = setmana.stream().mapToInt(Quest::getTempsEstim).sum();
                    int espaiLliure = 1200 - (tempsActual + q.getTempsEstim());
                    if (espaiLliure < millorEspaiLliure) {
                        millorEspaiLliure = espaiLliure;
                        millorSetmana = setmana;
                    }
                }
            }

            if (millorSetmana != null) {
                List<List<Quest>> noves = copiaSetmanes(estat.setmanes);
                // Encontrar la copia correspondiente
                int idx = estat.setmanes.indexOf(millorSetmana);
                noves.get(idx).add(q);

                int nouEstimat = noves.size() + lbRemaining;
                if (nouEstimat < millorCost) {
                    queue.add(new State(noves, estat.index + 1, nouEstimat));
                }
            }

            // Rama 2: Crear nueva semana
            List<List<Quest>> novaSetmana = copiaSetmanes(estat.setmanes);
            List<Quest> nova = new ArrayList<>();
            nova.add(q);
            novaSetmana.add(nova);

            int nouEstimat = novaSetmana.size() + lbRemaining;
            if (nouEstimat < millorCost) {
                queue.add(new State(novaSetmana, estat.index + 1, nouEstimat));
            }
        }

        if (iteracions >= MAX_ITER) {
            System.out.println("[BB P2] Límit d'iteracions assolit (" + MAX_ITER + "). Retornant millor solució trobada.");
        }

        System.out.println("[BB P2] Iteracions: " + iteracions + " | Millor: " + millorCost + " setmanes");
        return new Result(millorSolucio);
    }

    private static int lowerBoundPrecomp(int[] cumTemps, int[] cumComunes, int index) {
        if (index >= cumTemps.length - 1) return 0;
        int tempsPend = cumTemps[index];
        int comunesPend = cumComunes[index];
        int lbTemps = (int) Math.ceil(tempsPend / 1200.0);
        int lbComunes = (int) Math.ceil(comunesPend / 6.0);
        return Math.max(lbTemps, lbComunes);
    }

    private static List<List<Quest>> copiaSetmanes(List<List<Quest>> original) {
        List<List<Quest>> copia = new ArrayList<>();
        for (List<Quest> s : original) {
            copia.add(new ArrayList<>(s));
        }
        return copia;
    }
}