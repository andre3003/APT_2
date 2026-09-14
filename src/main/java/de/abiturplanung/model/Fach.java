package de.abiturplanung.model;

import java.util.Objects;

public class Fach implements Comparable<Fach> {

    private final String kuerzel;
    private String bezeichnung;
    private Fach stammfach;
    private String faechergruppe;

    public Fach(String kuerzel) {
        this.kuerzel = kuerzel;
    }

    public String getKuerzel() {
        return kuerzel;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public Fach getStammfach() {
        return stammfach;
    }

    public String getFaechergruppe() {
        return faechergruppe;
    }

    public void aktualisiereStammdaten(String bezeichnung, String faechergruppe) {
        this.bezeichnung = bezeichnung;
        this.faechergruppe = faechergruppe;
    }

    public void setStammfach(Fach stammfach) {
        this.stammfach = stammfach;
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
        if (this == o) return true;
        if (!(o instanceof Fach fach)) return false;
        return Objects.equals(kuerzel, fach.kuerzel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kuerzel);
    }
}