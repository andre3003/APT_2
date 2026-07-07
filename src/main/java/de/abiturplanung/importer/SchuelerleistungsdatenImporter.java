package de.abiturplanung.importer;
import de.abiturplanung.model.Abitur;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SchuelerleistungsdatenImporter {

    public Abitur importiere(Path datei) throws IOException {

        List<String> zeilen = Files.readAllLines(datei);

        Abitur abitur = new Abitur();

        for (int i = 1; i < zeilen.size(); i++) {

            String zeile = zeilen.get(i);

            SchuelerleistungsDatensatz datensatz =
                    parse(zeile);

            // TODO
            // Schüler suchen oder anlegen
            // Lehrer suchen oder anlegen
            // Kurs suchen oder anlegen
            // Prüfung anlegen

        }

        return abitur;

    }

    private SchuelerleistungsDatensatz parse(String zeile) {

        String[] spalten = zeile.split("\\|", -1);

        return new SchuelerleistungsDatensatz(
                spalten[0],   // Nachname
                spalten[1],   // Vorname
                spalten[5],   // Fach
                spalten[6],   // Fachlehrer
                spalten[7],   // Kursart
                spalten[8],   // Kurs
                spalten[10]   // Abiturfach
        );
    }
}





