package de.abiturplanung.importer;
import de.abiturplanung.model.Abiturfach;
import de.abiturplanung.model.Kursart;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LeistungsdatenImporter {

    public List<SchuelerleistungsDatensatz> lese(Path datei, int abiturjahrgang) throws IOException {
        int passendeDatensaetze = 0;
        int abweichendeDatensaetze = 0;

        List<SchuelerleistungsDatensatz> datensaetze = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(datei)) {
            br.readLine();     // Kopfzeile
            String zeile;
            while ((zeile = br.readLine()) != null) {
                String[] spalten = zeile.split("\\|", -1);
                String kursart = spalten[7].trim();

                if (!kursart.equals("LK1") && !kursart.equals("LK2") && !kursart.equals("AB3") && !kursart.equals("AB4"))
                    continue;

                int jahr = Integer.parseInt(spalten[3].trim());
                if (jahr != abiturjahrgang - 1) {
                    abweichendeDatensaetze++;
                    continue;
                }
                passendeDatensaetze++;

                SchuelerleistungsDatensatz ds = parseZeile(zeile);
                if (ds != null)
                    datensaetze.add(ds);
            }
        }
        if (passendeDatensaetze == 0) {
            if (abweichendeDatensaetze > 0) {
                throw new IOException(
                        "Die Jahrgangsangabe in den Importdaten passt nicht zum gewählten Abiturjahrgang der Planung.\n\n"
                                + "Gewählter Abiturjahrgang: " + abiturjahrgang + "\n"
                                + "Erwartete Angabe in Schild: " + (abiturjahrgang - 1) + "\n\n"
                                + "Der Abiturjahrgang gibt das Kalenderjahr an, in dem die Abiturprüfung stattfindet.\n"
                                + "Die Spalte \"Jahr\" in der \"schuelerleistungsdaten.dat\" aus Schild bezeichnet das Jahr,\n"
                                + "in dem die Schüler in die Q2 eintreten."
                );
            }
            throw new IOException("Die Importdatei enthält keine verwertbaren Abiturprüfungsdaten.");
        }
        return datensaetze;
    }

    private SchuelerleistungsDatensatz parseZeile(String zeile) {
        String[] spalten = zeile.split("\\|", -1);
        String kursart = spalten[7].trim();

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