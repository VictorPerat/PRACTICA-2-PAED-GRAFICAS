import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class QuestParser {

    public static List<Quest> leerListaDeMisionesDesdeArchivo(String nombreArchivoDataset) throws IOException {
        List<Quest> listaDeMisionesLeidas = new ArrayList<>();

        BufferedReader lectorBufferedDelArchivo = new BufferedReader(new FileReader(nombreArchivoDataset));


        String primeraLineaConNumeroDeMisiones = lectorBufferedDelArchivo.readLine();
        int numeroDeMisionesEnElArchivo = Integer.parseInt(primeraLineaConNumeroDeMisiones.trim());

        DateTimeFormatter formateadorDeFechas = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (int i = 0; i < numeroDeMisionesEnElArchivo; i++) {
            String lineaLeidaDelArchivo = lectorBufferedDelArchivo.readLine();

            if (lineaLeidaDelArchivo == null || lineaLeidaDelArchivo.trim().isEmpty()) {
                throw new IOException("Línia buida o null a la línia " + (i + 1));
            }

            String[] partesSeparadasPorPuntoYComa = lineaLeidaDelArchivo.split(";");
            if (partesSeparadasPorPuntoYComa.length != 8) {
                throw new IOException("Línia " + (i + 1) + " mal formada: esperats 8 camps, trobats " + partesSeparadasPorPuntoYComa.length);
            }

            String nombreDeLaMision = partesSeparadasPorPuntoYComa[0].trim();
            String nombreDeLaAsignatura = partesSeparadasPorPuntoYComa[1].trim();
            LocalDate fechaEntrega = LocalDate.parse(partesSeparadasPorPuntoYComa[2].trim(), formateadorDeFechas);
            int tiempoEstimadoEnMinutos = Integer.parseInt(partesSeparadasPorPuntoYComa[3].trim());
            int dificultad = Integer.parseInt(partesSeparadasPorPuntoYComa[4].trim());
            int progreso = Integer.parseInt(partesSeparadasPorPuntoYComa[5].trim());
            String codigoHexDeRareza = partesSeparadasPorPuntoYComa[6].trim();

            String[] partesDeLaUbicacionSeparadasPorComa = partesSeparadasPorPuntoYComa[7].trim().split("-");
            if (partesDeLaUbicacionSeparadasPorComa.length != 2) {
                throw new IOException("Ubicació mal formada a la línia " + (i + 1) + ": " + partesSeparadasPorPuntoYComa[7]);
            }

            int coordenadaXDeLaUbicacion = Integer.parseInt(partesDeLaUbicacionSeparadasPorComa[0].trim());
            int coordenadaYDeLaUbicacion = Integer.parseInt(partesDeLaUbicacionSeparadasPorComa[1].trim());


            listaDeMisionesLeidas.add(new Quest(nombreDeLaMision, nombreDeLaAsignatura, fechaEntrega, tiempoEstimadoEnMinutos, dificultad, progreso, codigoHexDeRareza, coordenadaXDeLaUbicacion, coordenadaYDeLaUbicacion));
        }

        lectorBufferedDelArchivo.close();


        System.out.println("Dataset carregat correctament: " + listaDeMisionesLeidas.size() + " listaDeMisiones (esperades: " + numeroDeMisionesEnElArchivo + ") des de " + nombreArchivoDataset);

        return listaDeMisionesLeidas;
    }
}
