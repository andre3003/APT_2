package de.abiturplanung;
import de.abiturplanung.model.Abitur;

import java.io.IOException;
import java.nio.file.Path;
import de.abiturplanung.service.*;

public class Abiturplanung {

    public static void main(String[] args) throws IOException {
        Abitur abitur = new Abitur();
        ImportService service = new ImportService(abitur);
        Path.of("Schueler.csv");
        service.importiereSchueler(Path.of("Schueler.csv"));
        System.out.println("Schüler: " + abitur.getSchueler().size());
        System.out.println(abitur.getSchueler().get(0).getNachname());

        Path.of("Lehrer.csv");
        service.importiereLehrer(Path.of("Lehrer.csv"));
        System.out.println("Lehrer: " + abitur.getLehrer().size());
        System.out.println(abitur.getLehrer().get(0).getNachname());
    }
}