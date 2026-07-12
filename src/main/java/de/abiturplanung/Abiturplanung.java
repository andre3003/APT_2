package de.abiturplanung;

import de.abiturplanung.importer.SchuelerleistungsdatenImporter;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Pruefung;

import java.io.IOException;
import java.nio.file.Path;

public class Abiturplanung {

    public static void main(String[] args) {
        Path datei = Path.of("Schuelerleistungsdaten.dat");

        SchuelerleistungsdatenImporter importer =
                new SchuelerleistungsdatenImporter();

        try {
            Abitur abitur = importer.importiere(datei);

            System.out.println("Schüler: " + abitur.getSchueler().size());
            System.out.println("Lehrer: " + abitur.getLehrer().size());
            System.out.println("Fächer: " + abitur.getFaecher().size());
            System.out.println("Kurse: " + abitur.getKurse().size());
            System.out.println(
                    "Mündliche Prüfungen: "
                            + abitur.getPruefungen().size()
            );

            for (Pruefung pruefung : abitur.getPruefungen()) {
                System.out.printf(
                        "%s, %s – %s – %s – Prüfer: %s%n",
                        pruefung.getPruefling().getNachname(),
                        pruefung.getPruefling().getVorname(),
                        pruefung.getKurs().getFach().getKuerzel(),
                        pruefung.getKurs().getBezeichnung(),
                        pruefung.getPruefer().getKuerzel()
                );
            }

        } catch (IOException | IllegalArgumentException exception) {
            System.err.println(
                    "Import fehlgeschlagen: " + exception.getMessage()
            );
        }
    }
}