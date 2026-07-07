package de.abiturplanung.model;

import java.util.ArrayList;
import java.util.List;

public class Abitur {

    private final List<Schueler> schueler = new ArrayList<>();
    private final List<Lehrer> lehrer = new ArrayList<>();
    private final List<Fach> faecher = new ArrayList<>();
    private final List<Kurs> kurse = new ArrayList<>();
    private final List<Pruefung> pruefungen = new ArrayList<>();

    public List<Schueler> getSchueler() {
        return schueler;
    }

    public List<Lehrer> getLehrer() {
        return lehrer;
    }

    public List<Fach> getFaecher() {
        return faecher;
    }

    public List<Kurs> getKurse() {
        return kurse;
    }

    public List<Pruefung> getPruefungen() {
        return pruefungen;
    }
}