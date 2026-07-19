package de.abiturplanung.importer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LeistungsdatenImporter {

    public List<SchuelerleistungsDatensatz> lese(Path datei) throws IOException {

        List<String> zeilen = Files.readAllLines(datei, StandardCharsets.UTF_8);
        List<SchuelerleistungsDatensatz> datensaetze = new ArrayList<>();

        for (int i = 1; i < zeilen.size(); i++) {

            String zeile = zeilen.get(i);

            if (zeile.isBlank()) {
                continue;
            }

            datensaetze.add(parseZeile(zeile));

        }

        return datensaetze;

    }

    private SchuelerleistungsDatensatz parseZeile(String zeile) {

        String[] spalten = zeile.split(";", -1);

        if (spalten.length < 11) {
            throw new IllegalArgumentException(
                    "Der Export der Schülerleistungsdaten hat nicht das erwartete Format.");
        }

        return new SchuelerleistungsDatensatz(

                spalten[0].trim(),   // Nachname
                spalten[1].trim(),   // Vorname
                spalten[5].trim(),   // Fach
                spalten[6].trim(),   // Fachlehrer
                spalten[7].trim(),   // Kursart
                spalten[8].trim(),   // Kurs
                spalten[10].trim()   // Abiturfach

        );

    }

}