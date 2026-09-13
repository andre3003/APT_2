package de.abiturplanung.model;

import java.util.Locale;
import java.util.Objects;

public class Fach implements Comparable<Fach> {

    private final String kuerzel;

    public Fach(String kuerzel) {
        this.kuerzel = normalisiereKuerzel(kuerzel);
    }

    private static String normalisiereKuerzel(String kuerzel) {
        String normalisiert = kuerzel == null ? "" : kuerzel.trim().toUpperCase(Locale.ROOT);

        return switch (normalisiert) {
            case "E5" -> "E";
            case "S7", "S9", "S0" -> "S";
            case "L7", "L0" -> "L";
            case "F7", "F9", "F0" -> "F";
            default -> normalisiert;
        };
    }

    public String getKuerzel() {
        return kuerzel;
    }

    public static Fach ausFachbezeichnung(String fachbezeichnung) {
        String kuerzel = switch (fachbezeichnung) {
            case "E5" -> "E";
            case "S7", "S9", "S0" -> "S";
            case "L7", "L0" -> "L";
            case "F7", "F9", "F0" -> "F";
            default -> fachbezeichnung;
        };

        return new Fach(kuerzel);
    }

    @Override
    public int compareTo(Fach fach) {
        return kuerzel.compareToIgnoreCase(fach.kuerzel);
    }

    @Override
    public String toString() {
        return kuerzel;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Fach fach)) return false;
        return Objects.equals(kuerzel, fach.kuerzel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kuerzel);
    }
}
