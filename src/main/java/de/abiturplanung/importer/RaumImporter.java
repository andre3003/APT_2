package de.abiturplanung.importer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RaumImporter {

    public List<RaumDatensatz> lese(Path datei) throws IOException {
        List<String> zeilen = Files.readAllLines(datei, StandardCharsets.UTF_8);
        List<RaumDatensatz> datensaetze = new ArrayList<>();
        for (int i = 1; i < zeilen.size(); i++) {
            String zeile = zeilen.get(i);
            if (zeile.isBlank()) {
                continue;
            }
            datensaetze.add(parseZeile(zeile));
        }
        return datensaetze;
    }

    private RaumDatensatz parseZeile(String zeile) {
        String[] spalten = zeile.split(";", -1);
        if (spalten.length < 2) {
            throw new IllegalArgumentException(
                    "Der Raumimport hat nicht das erwartete Format.");
        }
        String bezeichnung = spalten[0].trim();
        if (bezeichnung.isEmpty()) {
            throw new IllegalArgumentException(
                    "Raum ohne Bezeichnung: " + zeile);
        }
        return new RaumDatensatz(bezeichnung, Integer.parseInt(spalten[1].trim()));
    }
}