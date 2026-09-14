package de.abiturplanung.model;

import java.util.Objects;

public class Kurs {

    private final String bezeichnung;
    private Fach fach;
    private Lehrer fachlehrer;

    public Kurs(String bezeichnung, Fach fach, Lehrer fachlehrer) {
        this.bezeichnung = bezeichnung;
        this.fach = fach;
        this.fachlehrer = fachlehrer;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public Fach getFach() {
        return fach;
    }

    public Lehrer getFachlehrer() {
        return fachlehrer;
    }

    public void setFach(Fach fach) {
        this.fach = fach;
    }

    public void setFachlehrer(Lehrer fachlehrer) {
        this.fachlehrer = fachlehrer;
    }

    public Kursart getKursart() {
        String[] teile = bezeichnung.trim().split("\\s+");

        if (teile.length < 2) {
            throw new IllegalStateException("Ungültige Kursbezeichnung: " + bezeichnung);
        }

        return switch (teile[1].charAt(0)) {
            case 'L' -> Kursart.LEISTUNGSKURS;
            case 'G' -> Kursart.GRUNDKURS;
            default -> throw new IllegalStateException("Unbekannte Kursart in Kursbezeichnung: " + bezeichnung);
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Kurs kurs)) return false;
        return Objects.equals(bezeichnung, kurs.bezeichnung);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bezeichnung);
    }
}