package de.abiturplanung.service;

import de.abiturplanung.importer.SchuelerleistungsDatensatz;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Kurs;
import de.abiturplanung.model.Lehrer;
import de.abiturplanung.model.Schueler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ImportService {

    private final Abitur abitur;

    public ImportService(Abitur abitur) {
        this.abitur = abitur;
    }

    /**
     * Importiert die Stammdaten der Schüler.
     * (Noch nicht implementiert)
     */
    public void importiereSchueler(Path datei) throws IOException {

    }

    /**
     * Importiert die Stammdaten der Lehrer.
     * (Noch nicht implementiert)
     */
    public void importiereLehrer(Path datei) throws IOException {

    }

    /**
     * Importiert die Schülerleistungsdaten und erzeugt
     * Kurse sowie mündliche Prüfungen.
     */
    public void importiereLeistungsdaten(Path datei) throws IOException {

        List<String> zeilen = leseDatei(datei);

        for (int i = 1; i < zeilen.size(); i++) {

            String zeile = zeilen.get(i);

            if (zeile.isBlank()) {
                continue;
            }

            SchuelerleistungsDatensatz datensatz =
                    parseLeistungsZeile(zeile);

            // Die folgenden Schritte werden in der nächsten Ausbaustufe ergänzt.

            // Schueler schueler = findeSchueler(datensatz);

            // Lehrer lehrer = findeLehrer(datensatz.getFachlehrer());

            // Kurs kurs = findeOderErzeugeKurs(...);

            // if(datensatz.istMuendlichePruefung()) {
            //     ...
            // }

        }

    }

    /**
     * Liest eine UTF-8-Datei vollständig ein.
     */
    private List<String> leseDatei(Path datei) throws IOException {

        return Files.readAllLines(datei, StandardCharsets.UTF_8);

    }

    /**
     * Wandelt eine Zeile der Schülerleistungsdaten.dat
     * in einen Datensatz um.
     */
    private SchuelerleistungsDatensatz parseLeistungsZeile(String zeile) {

        String[] spalten = zeile.split("\\|", -1);

        if (spalten.length < 11) {
            throw new IllegalArgumentException(
                    "Ungültige Datenzeile.");
        }

        return new SchuelerleistungsDatensatz(

                spalten[0].trim(),      // Nachname
                spalten[1].trim(),      // Vorname
                spalten[5].trim(),      // Fach
                spalten[6].trim(),      // Fachlehrer
                spalten[7].trim(),      // Kursart
                spalten[8].trim(),      // Kurs
                spalten[10].trim()      // Abiturfach

        );

    }

    /**
     * Sucht einen bereits importierten Schüler.
     * Der Schüler muss bereits durch den Schülerimport
     * vorhanden sein.
     */
    private Schueler findeSchueler(
            SchuelerleistungsDatensatz datensatz) {

        return abitur.getSchueler().stream()

                .filter(s ->
                        s.getNachname().equals(datensatz.getNachname())
                                && s.getVorname().equals(datensatz.getVorname()))

                .findFirst()

                .orElseThrow(() -> new IllegalStateException(
                        "Schüler nicht gefunden: "
                                + datensatz.getNachname()
                                + ", "
                                + datensatz.getVorname()));

    }

    /**
     * Sucht einen bereits importierten Lehrer.
     */
    private Lehrer findeLehrer(String kuerzel) {

        return abitur.getLehrer().stream()

                .filter(l -> l.getKuerzel().equals(kuerzel))

                .findFirst()

                .orElseThrow(() -> new IllegalStateException(
                        "Lehrer nicht gefunden: " + kuerzel));

    }

    /**
     * Kurse entstehen erst durch den Import der
     * Leistungsdaten und werden deshalb bei Bedarf erzeugt.
     */
    private Kurs findeOderErzeugeKurs() {

        // folgt in der nächsten Ausbaustufe

        return null;

    }

}