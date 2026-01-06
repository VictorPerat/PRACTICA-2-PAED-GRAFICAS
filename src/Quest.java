import java.time.LocalDate; // Trabajar con fechas

// Función principal
public class Quest {
    private String nombreDeLaMision; // Nombre quest
    private String nombreDeLaAsignatura; // Nombre materia
    private LocalDate fechaDeEntrega; // Fecha entrega
    private int tiempoEstimadoEnMinutos; //Tiempo estimado
    private int nivelDeDificultad; // Nivel de dificultad
    private int porcentajeDeProgreso; // Porcentaje de progreso
    private String codigoHexDeRareza; // Raresa (codi hex, ex: #4fd945)
    private int coordenadaXDeLaUbicacion; // Coordenada coordenadaXDeLaUbicacion de la ubicació
    private int coordenadaYDeLaUbicacion; // Coordenada coordenadaYDeLaUbicacion de la ubicació
    private double prioridadParaRecursividad; // Para la recursividad

    // NOU: Camps calculats (per Problema1/2)
    private double valorTotalCalculadoDeLaMision;  // Calculat: temps * dif * urgent * rares
    private double ratioValorSobreTiempo;  // valorTotalCalculadoDeLaMision / tiempoEstimadoEnMinutos (per greedy)

    // Constructor actualitzat
    public Quest(String nombreDeLaMision, String nombreDeLaAsignatura, LocalDate fechaDeEntrega, int tiempoEstimadoEnMinutos, int nivelDeDificultad, int porcentajeDeProgreso, String codigoHexDeRareza, int coordenadaXDeLaUbicacion, int coordenadaYDeLaUbicacion) {
        this.nombreDeLaMision = nombreDeLaMision;
        this.nombreDeLaAsignatura = nombreDeLaAsignatura;
        this.fechaDeEntrega = fechaDeEntrega;
        this.tiempoEstimadoEnMinutos = tiempoEstimadoEnMinutos;
        this.nivelDeDificultad = nivelDeDificultad;
        this.porcentajeDeProgreso = porcentajeDeProgreso;
        this.codigoHexDeRareza = codigoHexDeRareza;
        this.coordenadaXDeLaUbicacion = coordenadaXDeLaUbicacion;
        this.coordenadaYDeLaUbicacion = coordenadaYDeLaUbicacion;
        this.prioridadParaRecursividad = 0.0; // Inicializamos con 0

        // NOU: Calcula immediat (usa QuestValueCalculator)
        this.valorTotalCalculadoDeLaMision = QuestValueCalculator.calcularValorTotalDeLaMision(this);
        this.ratioValorSobreTiempo = this.valorTotalCalculadoDeLaMision / (double) this.tiempoEstimadoEnMinutos;
    }

    // Getter necesario para los algoritmos iterativos
    public LocalDate getFechaDeEntrega() {
        return this.fechaDeEntrega;
    }

    public int getTiempoEstimadoEnMinutos() {
        return tiempoEstimadoEnMinutos;
    }

    public int getNivelDeDificultad() {
        return nivelDeDificultad;
    }

    public int getPorcentajeDeProgreso() {
        return porcentajeDeProgreso;
    }

    // Nous getters per als camps afegits
    public String getCodigoHexDeRareza() {
        return codigoHexDeRareza;
    }

    public int getCoordenadaXDeLaUbicacion() {
        return coordenadaXDeLaUbicacion;
    }

    public int getCoordenadaYDeLaUbicacion() {
        return coordenadaYDeLaUbicacion;
    }

    // NOUS getters per als camps calculats i assignatura
    public double getValorTotalCalculadoDeLaMision() {
        return valorTotalCalculadoDeLaMision;
    }

    public double getRatioValorSobreTiempo() {
        return ratioValorSobreTiempo;
    }

    public String getNombreDeLaAsignatura() {
        return nombreDeLaAsignatura;
    }

    // NOU: toString per debug i resultats
    @Override
    public String toString() {
        return String.format("%s | Asig: %s | Data: %s | Temps: %d | Dif: %d | Prog: %d | Pes: %s | Val: %.2f | Ratio: %.2f | XY: (%d,%d)",
                nombreDeLaMision, nombreDeLaAsignatura, fechaDeEntrega, tiempoEstimadoEnMinutos, nivelDeDificultad, porcentajeDeProgreso, codigoHexDeRareza, valorTotalCalculadoDeLaMision, ratioValorSobreTiempo, coordenadaXDeLaUbicacion, coordenadaYDeLaUbicacion);
    }
}