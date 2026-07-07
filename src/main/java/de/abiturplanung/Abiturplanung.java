package de.abiturplanung;

import de.abiturplanung.importer.SchuelerleistungsdatenImporter;
import de.abiturplanung.model.*;
import java.io.IOException;
import java.nio.file.Path;

public class Abiturplanung {


    public static void main(String[] args) throws IOException {

        Path datei = Path.of("Schuelerleistungsdaten.dat");

        SchuelerleistungsdatenImporter importer =
                new SchuelerleistungsdatenImporter();

        Abitur abitur = importer.importiere(datei);

        System.out.println(abitur);

    }
}
