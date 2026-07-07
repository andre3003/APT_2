package de.abiturplanung.model;

import java.util.Objects;

public class Schueler {

    private final String nachname;
    private final String vorname;

    public Schueler(String nachname, String vorname) {
        this.nachname = nachname;
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public String getVorname() {
        return vorname;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Schueler schueler)) return false;
        return Objects.equals(nachname, schueler.nachname)
                && Objects.equals(vorname, schueler.vorname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nachname, vorname);
    }
}