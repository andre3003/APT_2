package de.abiturplanung.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class Utilities {

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private Utilities() {
        // Verhindert die Instanziierung
    }

    /**
     * Bringt Datumsangaben in das Format dd.MM.yyyy.
     * Erwartet entweder:
     *   dd.MM.yyyy
     * oder
     *   dMuuuu, ddMuuuu, dMMuuuu oder ddMMuuuu
     */
    public static String normalisiereDatum(String datum) {

        datum = datum.trim();

        if (datum.isEmpty()) {
            return datum;
        }

        // Bereits korrekt formatiert
        if (datum.contains(".")) {
            return datum;
        }

        switch (datum.length()) {

            case 7 -> {
                return "0"
                        + datum.substring(0, 1)
                        + "."
                        + datum.substring(1, 3)
                        + "."
                        + datum.substring(3);
            }

            case 8 -> {
                return datum.substring(0, 2)
                        + "."
                        + datum.substring(2, 4)
                        + "."
                        + datum.substring(4);
            }

            default ->
                    throw new IllegalArgumentException(
                            "Ungültiges Datumsformat: " + datum);
        }
    }

    public static LocalDate parseDatum(String datum) {

        return LocalDate.parse(normalisiereDatum(datum), FORMAT);

    }

}