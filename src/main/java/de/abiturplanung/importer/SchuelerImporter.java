package de.abiturplanung.importer;
import de.abiturplanung.model.Geschlecht;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SchuelerImporter {

    private static final DateTimeFormatter DATUM_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public List<SchuelerDatensatz> lese(Path datei) throws IOException {

        List<String> zeilen = Files.readAllLines(datei, StandardCharsets.UTF_8);

        List<SchuelerDatensatz> datensaetze = new ArrayList<>();

        for (int i = 1; i < zeilen.size(); i++) {
            String zeile = zeilen.get(i);
            if (zeile.isBlank()) {
                continue;
            }
            datensaetze.add(parseZeile(zeile));
        }
        return datensaetze;
    }

    private SchuelerDatensatz parseZeile(String zeile) {
        String[] spalten = zeile.split(";", -1);
        if (spalten.length < 5) {
            throw new IllegalArgumentException(
                    "Der Schülerexport hat nicht das erwartete Format.");
        }

        String schildId = spalten[0].trim();
        if (schildId.isEmpty()) {
            throw new IllegalArgumentException(
                    "Schülerdatensatz ohne Schild-ID: " + zeile);
        }
        String nachname = spalten[1].trim();
        String vorname  = spalten[2].trim();
        return new SchuelerDatensatz(schildId, nachname, vorname, de.abiturplanung.util.Utilities.parseDatum(spalten[3]), parseGeschlecht(spalten[4]));
    }

    private Geschlecht parseGeschlecht(String wert) {
        return switch (wert.trim().toLowerCase()) {
            case "m" -> Geschlecht.M;
            case "w" -> Geschlecht.W;
            case "d" -> Geschlecht.D;
            case"" -> Geschlecht.KA;
            default -> throw new IllegalArgumentException(
                    "Ungültiges Geschlecht: " + wert);
        };
    }
}