package de.abiturplanung.importer;
import de.abiturplanung.model.Abiturfach;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LeistungsdatenImporter {

    public List<SchuelerleistungsDatensatz> lese(Path datei) throws IOException {

        List<SchuelerleistungsDatensatz> datensaetze = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(datei)) {
            br.readLine();     // Kopfzeile
            String zeile;
            while ((zeile = br.readLine()) != null) {
                SchuelerleistungsDatensatz ds = parseZeile(zeile);
                if (ds != null)
                    datensaetze.add(ds);
            }
        }

        return datensaetze;
    }

    private SchuelerleistungsDatensatz parseZeile(String zeile) {

        String[] spalten = zeile.split("\\|", -1);

        if (spalten.length < 22) {
            throw new IllegalArgumentException(
                    "Der Export der Schülerleistungsdaten hat nicht das erwartete Format.");
        }

        String kursart = spalten[7].trim();

        if (kursart.equals("GKM") || kursart.equals("GKS"))
            return null;

        Abiturfach abiturfach = switch (kursart) {

            case "LK1" -> Abiturfach.AB1;

            case "LK2" -> Abiturfach.AB2;

            case "AB3" -> Abiturfach.AB3;

            case "AB4" -> Abiturfach.AB4;

            default ->
                    throw new IllegalArgumentException(
                            "Unbekannte Kursart: " + kursart);

        };

        return new SchuelerleistungsDatensatz(
                spalten[0].trim(),   // Nachname
                spalten[1].trim(), // Vorname
                de.abiturplanung.Utilities.parseDatum(spalten[2].trim()), //Geburtsdatum
                spalten[5].trim(), // Fach
                spalten[8].trim(),   // Kurs
                spalten[6].trim(),   // Fachlehrer
                abiturfach);   // Abiturfach
    }
}