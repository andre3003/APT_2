package de.abiturplanung.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lehrer {
    private final String kuerzel;
    private String anrede;
    private String nachname;
    private String vorname;
    private String amtsbez;
    private List<Fach> fakultas = new ArrayList<>();

    public Lehrer(String kuerzel) {
        this.kuerzel = kuerzel;
    }

    public void aktualisiereFakultas(List<Fach> fakultas) {
        this.fakultas.clear();
        this.fakultas.addAll(fakultas);
    }

    public void aktualisiereStammdaten(String anrede, String nachname, String vorname, String amtsbez) {
        this.anrede = anrede;
        this.nachname = nachname;
        this.vorname = vorname;
        this.amtsbez = amtsbez;
    }

    public String getKuerzel() {
        return kuerzel;
    }

    public String getAnrede() {
        return anrede;
    }

    public String getNachname() {
        return nachname;
    }

    public String getVorname() {
        return vorname;
    }

    public String getAmtsbez() {
        return amtsbez;
    }

    public List<Fach> getFakultas() {
        return fakultas;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Lehrer lehrer)) return false;
        return Objects.equals(kuerzel, lehrer.kuerzel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kuerzel);
    }
}