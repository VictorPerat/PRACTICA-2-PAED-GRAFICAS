import java.time.LocalDate;


public class Quest {
    private String nombreDeLaMision;
    private String nombreDeLaAsignatura;
    private LocalDate fechaDeEntrega;
    private int tiempoEstimadoEnMinutos;
    private int nivelDeDificultad;
    private int porcentajeDeProgreso;
    private String codigoHexDeRareza;
    private int coordenadaXDeLaUbicacion;
    private int coordenadaYDeLaUbicacion;
    private double prioridadParaRecursividad;


    private double valorTotalCalculadoDeLaMision;
    private double ratioValorSobreTiempo;


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
        this.prioridadParaRecursividad = 0.0;


        this.valorTotalCalculadoDeLaMision = QuestValueCalculator.calcularValorTotalDeLaMision(this);
        this.ratioValorSobreTiempo = this.valorTotalCalculadoDeLaMision / (double) this.tiempoEstimadoEnMinutos;
    }


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


    public String getCodigoHexDeRareza() {
        return codigoHexDeRareza;
    }

    public int getCoordenadaXDeLaUbicacion() {
        return coordenadaXDeLaUbicacion;
    }

    public int getCoordenadaYDeLaUbicacion() {
        return coordenadaYDeLaUbicacion;
    }


    public double getValorTotalCalculadoDeLaMision() {
        return valorTotalCalculadoDeLaMision;
    }

    public double getRatioValorSobreTiempo() {
        return ratioValorSobreTiempo;
    }

    public String getNombreDeLaAsignatura() {
        return nombreDeLaAsignatura;
    }


    @Override
    public String toString() {
        return String.format("%s | Asig: %s | Data: %s | Temps: %d | Dif: %d | Prog: %d | Pes: %s | Val: %.2f | Ratio: %.2f | XY: (%d,%d)",
                nombreDeLaMision, nombreDeLaAsignatura, fechaDeEntrega, tiempoEstimadoEnMinutos, nivelDeDificultad, porcentajeDeProgreso, codigoHexDeRareza, valorTotalCalculadoDeLaMision, ratioValorSobreTiempo, coordenadaXDeLaUbicacion, coordenadaYDeLaUbicacion);
    }
}
