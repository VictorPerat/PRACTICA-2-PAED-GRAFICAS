import java.time.LocalDate; // Trabajar con fechas

public class Quest {
    private String nombre;
    private String asignatura;
    private LocalDate dataLliurament;
    private int tempsEstim;
    private int dificultat;
    private int progres;
    private String pes;
    private int x;
    private int y;
    private double prioritat;

    private double valor;
    private double ratio;

    public Quest(String nombre, String asignatura, LocalDate dataLliurament, int tempsEstim, int dificultat, int progres, String pes, int x, int y) {
        this.nombre = nombre;
        this.asignatura = asignatura;
        this.dataLliurament = dataLliurament;
        this.tempsEstim = tempsEstim;
        this.dificultat = dificultat;
        this.progres = progres;
        this.pes = pes;
        this.x = x;
        this.y = y;
        this.prioritat = 0.0;

        this.valor = QuestValueCalculator.calcularValor(this);
        this.ratio = this.valor / (double) this.tempsEstim;
    }

    public LocalDate getDataLliurament() {
        return this.dataLliurament;
    }

    public int getTempsEstim() {
        return tempsEstim;
    }

    public int getDificultat() {
        return dificultat;
    }

    public int getProgres() {
        return progres;
    }

    public String getPes() {
        return pes;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getValor() {
        return valor;
    }

    // Valor de la mission partido por el tiempo
    public double getRatio() {
        return ratio;
    }

    public String getAsignatura() {
        return asignatura;
    }

    @Override
    public String toString() {
        return String.format("%s | Asig: %s | Data: %s | Temps: %d | Dif: %d | Prog: %d | Pes: %s | Val: %.2f | Ratio: %.2f | XY: (%d,%d)",
                nombre, asignatura, dataLliurament, tempsEstim, dificultat, progres, pes, valor, ratio, x, y);
    }
}