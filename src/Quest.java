import java.time.LocalDate; // Trabajar con fechas

// Función principal
public class Quest {
    private String nombre; // Nombre quest
    private String asignatura; // Nombre materia
    private LocalDate dataLliurament; // Fecha entrega
    private int tempsEstim; //Tiempo estimado
    private int dificultat; // Nivel de dificultad
    private int progres; // Porcentaje de progreso
    private String pes; // Raresa (codi hex, ex: #4fd945)
    private int x; // Coordenada x de la ubicació
    private int y; // Coordenada y de la ubicació
    private double prioritat; // Para la recursividad

    // NOU: Camps calculats (per Problema1/2)
    private double valor;  // Calculat: temps * dif * urgent * rares
    private double ratio;  // valor / tempsEstim (per greedy)

    // Constructor actualitzat
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
        this.prioritat = 0.0; // Inicializamos con 0

        // NOU: Calcula immediat (usa QuestValueCalculator)
        this.valor = QuestValueCalculator.calcularValor(this);
        this.ratio = this.valor / (double) this.tempsEstim;
    }

    // Getter necesario para los algoritmos iterativos
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

    // Nous getters per als camps afegits
    public String getPes() {
        return pes;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // NOUS getters per als camps calculats i assignatura
    public double getValor() {
        return valor;
    }

    public double getRatio() {
        return ratio;
    }

    public String getAsignatura() {
        return asignatura;
    }

    // NOU: toString per debug i resultats
    @Override
    public String toString() {
        return String.format("%s | Asig: %s | Data: %s | Temps: %d | Dif: %d | Prog: %d | Pes: %s | Val: %.2f | Ratio: %.2f | XY: (%d,%d)",
                nombre, asignatura, dataLliurament, tempsEstim, dificultat, progres, pes, valor, ratio, x, y);
    }
}