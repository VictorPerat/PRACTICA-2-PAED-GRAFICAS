import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class QuestParser {

    public static List<Quest> parseFile(String filename) throws IOException {
        List<Quest> lista = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(filename));

        // Primera línia: nombre de quests
        String primeraLinia = br.readLine();
        int numQuests = Integer.parseInt(primeraLinia.trim());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (int i = 0; i < numQuests; i++) {
            String linia = br.readLine();

            if (linia == null || linia.trim().isEmpty()) {
                throw new IOException("Línia buida o null a la línia " + (i + 1));
            }

            String[] parts = linia.split(";");
            if (parts.length != 8) {
                throw new IOException("Línia " + (i + 1) + " mal formada: esperats 8 camps, trobats " + parts.length);
            }

            String nombre = parts[0].trim();
            String asignatura = parts[1].trim();
            LocalDate fechaEntrega = LocalDate.parse(parts[2].trim(), formatter);
            int tiempoEstimado = Integer.parseInt(parts[3].trim());
            int dificultad = Integer.parseInt(parts[4].trim());
            int progreso = Integer.parseInt(parts[5].trim());
            String pes = parts[6].trim();

            String[] ubicacioParts = parts[7].trim().split("-");
            if (ubicacioParts.length != 2) {
                throw new IOException("Ubicació mal formada a la línia " + (i + 1) + ": " + parts[7]);
            }

            int x = Integer.parseInt(ubicacioParts[0].trim());
            int y = Integer.parseInt(ubicacioParts[1].trim());

            // Creació de l'objecte
            lista.add(new Quest(nombre, asignatura, fechaEntrega, tiempoEstimado, dificultad, progreso, pes, x, y));
        }

        br.close();

        // Resum final (pots comentar aquesta línia si vols silenciar completament)
        System.out.println("Dataset carregat correctament: " + lista.size() + " quests (esperades: " + numQuests + ") des de " + filename);

        return lista;
    }
}