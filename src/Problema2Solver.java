import java.util.*;
import java.util.concurrent.*;

public class Problema2Solver {

    private static final int MAXIMO_MISIONES_PARA_BACKTRACKING = 30;
    private static final int MAXIMO_NODOS_PARA_BRANCH_AND_BOUND = 2000;
    private static final long TIEMPO_MAXIMO_EN_SEGUNDOS = 10;

    public static class Result {
        List<List<Quest>> listaDeSemanas;
        int numeroDeSemanas;
        int tiempoTotalMinutos;

        public Result(List<List<Quest>> listaDeSemanas) {
            this.listaDeSemanas = listaDeSemanas;
            this.numeroDeSemanas = listaDeSemanas.size();
            this.tiempoTotalMinutos = listaDeSemanas.stream()
                    .mapToInt(s -> s.stream().mapToInt(Quest::getTiempoEstimadoEnMinutos).sum())
                    .sum();
        }
    }


    public static Result greedy(List<Quest> listaDeMisionesDeEntrada) {

        List<Quest> listaDeMisiones = new ArrayList<>(listaDeMisionesDeEntrada);
        Ordenador.ordenarPorRarezaYTiempoDescendente(listaDeMisiones);

        List<List<Quest>> listaDeSemanas = new ArrayList<>();
        for (Quest misionActual : listaDeMisiones) {
            boolean misionAsignada = false;
            for (List<Quest> setmana : listaDeSemanas) {
                if (Utils.sePuedeAgregarMisionALaSemana(misionActual, setmana)) {
                    setmana.add(misionActual);
                    misionAsignada = true;
                    break;
                }
            }
            if (!misionAsignada) {
                List<Quest> nova = new ArrayList<>();
                nova.add(misionActual);
                listaDeSemanas.add(nova);
            }
        }
        return new Result(listaDeSemanas);
    }


    private static volatile List<List<Quest>> millorSolucioBT;
    private static volatile int millorCostBT;
    private static volatile boolean timeoutReached = false;

    public static Result backtracking(List<Quest> listaDeMisionesDeEntrada) {
        if (listaDeMisionesDeEntrada.size() > MAXIMO_MISIONES_PARA_BACKTRACKING) {
            System.out.println("[Backtracking P2] Dataset massa gran (n=" + listaDeMisionesDeEntrada.size() + "). S'omet.");
            return greedy(listaDeMisionesDeEntrada);
        }


        Result greedyResult = greedy(listaDeMisionesDeEntrada);
        millorCostBT = greedyResult.numeroDeSemanas;
        millorSolucioBT = copiaSetmanes(greedyResult.listaDeSemanas);
        System.out.println("[Backtracking P2] Poda inicial amb Greedy: " + millorCostBT + " listaDeSemanas");


        List<Quest> listaDeMisiones = new ArrayList<>(listaDeMisionesDeEntrada);
        Ordenador.ordenarPorRarezaYTiempoDescendente(listaDeMisiones);
        timeoutReached = false;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            LinkedList<Quest> restants = new LinkedList<>(listaDeMisiones);
            backtrackingRecP2(new ArrayList<>(), restants);
        });

        try {
            future.get(TIEMPO_MAXIMO_EN_SEGUNDOS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            timeoutReached = true;
            System.out.println("[Backtracking P2] Timeout de " + TIEMPO_MAXIMO_EN_SEGUNDOS + "s. S'usa la millor solució trobada.");
        } catch (Exception e) {
            System.out.println("[Backtracking P2] Error: " + e.getMessage());
        } finally {
            executor.shutdownNow();
        }

        return new Result(millorSolucioBT != null ? millorSolucioBT : greedyResult.listaDeSemanas);
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

        if (setmanesActuals.size() + Utils.calcularCotaInferiorDeSemanas(restants) >= millorCostBT) return;

        Quest misionActual = restants.pollFirst();


        for (int i = 0; i < setmanesActuals.size(); i++) {
            List<Quest> setmana = setmanesActuals.get(i);
            if (Utils.sePuedeAgregarMisionALaSemana(misionActual, setmana)) {
                setmana.add(misionActual);
                backtrackingRecP2(setmanesActuals, restants);
                setmana.remove(setmana.size() - 1);
                if (timeoutReached) {
                    restants.addFirst(misionActual);
                    return;
                }
            }
        }


        List<Quest> novaSetmana = new ArrayList<>();
        novaSetmana.add(misionActual);
        setmanesActuals.add(novaSetmana);
        backtrackingRecP2(setmanesActuals, restants);
        setmanesActuals.remove(setmanesActuals.size() - 1);

        restants.addFirst(misionActual);
    }


    private static class State implements Comparable<State> {
        List<List<Quest>> listaDeSemanas;
        int index;
        int costEstimat;

        State(List<List<Quest>> listaDeSemanas, int index, int estimat) {
            this.listaDeSemanas = listaDeSemanas;
            this.index = index;
            this.costEstimat = estimat;
        }

        @Override
        public int compareTo(State o) {
            int cmp = Integer.compare(this.costEstimat, o.costEstimat);
            if (cmp != 0) return cmp;
            return Integer.compare(o.index, this.index);
        }
    }

    public static Result branchAndBound(List<Quest> listaDeMisionesDeEntrada) {
        if (listaDeMisionesDeEntrada.size() > MAXIMO_NODOS_PARA_BRANCH_AND_BOUND) {
            System.out.println("[Branch & Bound P2] Dataset massa gran (n=" + listaDeMisionesDeEntrada.size() + "). S'usa Greedy.");
            return greedy(listaDeMisionesDeEntrada);
        }


        List<Quest> listaDeMisiones = new ArrayList<>(listaDeMisionesDeEntrada);
        Ordenador.ordenarPorRarezaYTiempoDescendente(listaDeMisiones);
        int n = listaDeMisiones.size();


        int[] cumTemps = new int[n + 1];
        int[] cumComunes = new int[n + 1];
        for (int i = n - 1; i >= 0; i--) {
            cumTemps[i] = cumTemps[i + 1] + listaDeMisiones.get(i).getTiempoEstimadoEnMinutos();
            cumComunes[i] = cumComunes[i + 1] + (listaDeMisiones.get(i).getCodigoHexDeRareza().equalsIgnoreCase("#4fd945") ? 1 : 0);
        }


        Result greedyResult = greedy(listaDeMisionesDeEntrada);
        int millorCost = greedyResult.numeroDeSemanas;
        List<List<Quest>> millorSolucio = copiaSetmanes(greedyResult.listaDeSemanas);
        System.out.println("[B&B P2] Poda inicial amb Greedy: " + millorCost + " listaDeSemanas");

        PriorityQueue<State> queue = new PriorityQueue<>();
        int lbInicial = lowerBoundPrecomp(cumTemps, cumComunes, 0);
        queue.add(new State(new ArrayList<>(), 0, lbInicial));

        long iteracions = 0;
        final long MAX_ITER = 10_000_000;

        while (!queue.isEmpty() && iteracions < MAX_ITER) {
            iteracions++;
            State estat = queue.poll();

            if (estat.index >= n) {
                int cost = estat.listaDeSemanas.size();
                if (cost < millorCost) {
                    millorCost = cost;
                    millorSolucio = copiaSetmanes(estat.listaDeSemanas);
                    System.out.println("[B&B P2] Nova millor solució: " + millorCost + " listaDeSemanas (iteració " + iteracions + ")");
                }
                continue;
            }

            if (estat.costEstimat >= millorCost) continue;

            Quest misionActual = listaDeMisiones.get(estat.index);
            int lbRemaining = lowerBoundPrecomp(cumTemps, cumComunes, estat.index + 1);


            List<Quest> millorSetmana = null;
            int millorEspaiLliure = Integer.MAX_VALUE;

            for (List<Quest> setmana : estat.listaDeSemanas) {
                if (Utils.sePuedeAgregarMisionALaSemana(misionActual, setmana)) {
                    int tempsActual = setmana.stream().mapToInt(Quest::getTiempoEstimadoEnMinutos).sum();
                    int espaiLliure = 1200 - (tempsActual + misionActual.getTiempoEstimadoEnMinutos());
                    if (espaiLliure < millorEspaiLliure) {
                        millorEspaiLliure = espaiLliure;
                        millorSetmana = setmana;
                    }
                }
            }

            if (millorSetmana != null) {
                List<List<Quest>> noves = copiaSetmanes(estat.listaDeSemanas);

                int idx = estat.listaDeSemanas.indexOf(millorSetmana);
                noves.get(idx).add(misionActual);

                int nouEstimat = noves.size() + lbRemaining;
                if (nouEstimat < millorCost) {
                    queue.add(new State(noves, estat.index + 1, nouEstimat));
                }
            }


            List<List<Quest>> novaSetmana = copiaSetmanes(estat.listaDeSemanas);
            List<Quest> nova = new ArrayList<>();
            nova.add(misionActual);
            novaSetmana.add(nova);

            int nouEstimat = novaSetmana.size() + lbRemaining;
            if (nouEstimat < millorCost) {
                queue.add(new State(novaSetmana, estat.index + 1, nouEstimat));
            }
        }

        if (iteracions >= MAX_ITER) {
            System.out.println("[BB P2] Límit d'iteracions assolit (" + MAX_ITER + "). Retornant millor solució trobada.");
        }

        System.out.println("[BB P2] Iteracions: " + iteracions + " | Millor: " + millorCost + " listaDeSemanas");
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
