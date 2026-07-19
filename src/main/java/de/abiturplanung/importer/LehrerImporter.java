package de.abiturplanung.importer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LehrerImporter {

    public List<LehrerDatensatz> lese(Path datei) throws IOException {
        List<String> zeilen = Files.readAllLines(datei, StandardCharsets.UTF_8);
        List<LehrerDatensatz> datensaetze = new ArrayList<>();
        for (int i = 1; i < zeilen.size(); i++) {
            String zeile = zeilen.get(i);
            if (zeile.isBlank()) {
                continue;
            }
            datensaetze.add(parseZeile(zeile));
        }
        return datensaetze;
    }

    private LehrerDatensatz parseZeile(String zeile) {
        String[] spalten = zeile.split(";", -1);
        if (spalten.length < 9) {
            throw new IllegalArgumentException(
                    "Der Lehrerexport hat nicht das erwartete Format.");
        }
        String kuerzel = spalten[0].trim();
        if (kuerzel.isEmpty()) {
            throw new IllegalArgumentException(
                    "Lehrerdatensatz ohne Kürzel " + zeile);
        }
       return new LehrerDatensatz(kuerzel, spalten[1].trim(), spalten[2].trim(), spalten[3].trim(), spalten[4].trim(), spalten[5].trim(), spalten[6].trim(), spalten[7].trim(), spalten[8].trim());
    }
}
