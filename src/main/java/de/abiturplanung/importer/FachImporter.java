package de.abiturplanung.importer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FachImporter {


    public List<FachDatensatz> lese(Path datei) throws IOException {
        List<String> zeilen = Files.readAllLines(datei, StandardCharsets.UTF_8);
        List<FachDatensatz> datensaetze = new ArrayList<>();
        for (int i = 1; i < zeilen.size(); i++) {
            String zeile = zeilen.get(i);
            if (zeile.isBlank()) {
                continue;
            }
            datensaetze.add(parseZeile(zeile));
        }
        return datensaetze;
    }


    private FachDatensatz parseZeile(String zeile) {
        String[] spalten = zeile.split(";", -1);
        if (spalten.length < 4) {
            throw new IllegalArgumentException("Der Fächerexport hat nicht das erwartete Format.");
        }
        String kuerzel = spalten[0].trim();
        if (kuerzel.isEmpty()) {
            throw new IllegalArgumentException("Fachdatensatz ohne Kürzel: " + zeile);
        }
        String bezeichnung = leerZuNull(spalten[1]);
        String stammfachKuerzel = leerZuNull(spalten[2]);
        String faechergruppe = leerZuNull(spalten[3]);
        return new FachDatensatz(kuerzel, bezeichnung, stammfachKuerzel, faechergruppe);
    }

    private String leerZuNull(String wert) {
        String text = wert.trim();
        return text.isEmpty() ? null : text;
    }
}
