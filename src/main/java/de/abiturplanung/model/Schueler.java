package de.abiturplanung.model;

import de.abiturplanung.importer.SchuelerDatensatz;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Schueler {
    private String schildId;
    private String nachname;
    private String vorname;
    private LocalDate geburtsdatum;
    private Geschlecht geschlecht;
    private ArrayList<Pruefung> pruefungen = new ArrayList<>();

    public Schueler(String schildID) {
        this.schildId = schildID;
    }

    public String getSchildId() {
        return schildId;
    }

    public String getNachname() {
        return nachname;
    }

    public String getVorname() {
        return vorname;
    }

    public Geschlecht getGeschlecht() {
        return geschlecht;
    }

    public void addPruefung(Pruefung pruefung) {
        pruefungen.add(pruefung);
    }

    public List<Pruefung> getPruefungen() {
        return Collections.unmodifiableList(pruefungen);
    }

    public Pruefung getPruefung(Abiturfach abiturfach) {
        for (Pruefung p : pruefungen) {
            if (p.getAbiturfach() == abiturfach)
                return p;
        }
        return null;
    }

    public LocalDate getGeburtsdatum() {
        return geburtsdatum;
    }
    public void aktualisiereStammdaten(SchuelerDatensatz datensatz) { //Nur für den Initialimport relevant
        aktualisiereStammdaten(datensatz.getNachname(), datensatz.getVorname(), datensatz.getGeburtsdatum(), datensatz.getGeschlecht());
    }

    public void aktualisiereStammdaten(String nachname, String vorname, LocalDate geburtsdatum, Geschlecht geschlecht) {
        this.nachname = nachname;
        this.vorname = vorname;
        this.geburtsdatum = geburtsdatum;
        this.geschlecht = geschlecht;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Schueler schueler)) return false;
        return Objects.equals(schildId, schueler.schildId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schildId);
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public void setGeburtsdatum(LocalDate geburtsdatum) {
        this.geburtsdatum = geburtsdatum;
    }

    public void setGeschlecht(Geschlecht geschlecht) {
        this.geschlecht = geschlecht;
    }
}