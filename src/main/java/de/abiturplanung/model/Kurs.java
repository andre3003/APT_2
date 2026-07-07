package de.abiturplanung.model;

import java.util.Objects;

public class Kurs {

    private final String bezeichnung;
    private final String kursart;
    private final Fach fach;
    private final Lehrer kurslehrer;

    public Kurs(String bezeichnung, String kursart, Fach fach, Lehrer kurslehrer) {
        this.bezeichnung = bezeichnung;
        this.kursart = kursart;
        this.fach = fach;
        this.kurslehrer = kurslehrer;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public String getKursart() {
        return kursart;
    }

    public Fach getFach() {
        return fach;
    }

    public Lehrer getKurslehrer() {
        return kurslehrer;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Kurs kurs)) return false;
        return Objects.equals(bezeichnung, kurs.bezeichnung);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bezeichnung);
    }
}