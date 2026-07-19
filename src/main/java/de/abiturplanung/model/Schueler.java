package de.abiturplanung.model;

import de.abiturplanung.importer.SchuelerDatensatz;

import java.time.LocalDate;
import java.util.Objects;

public class Schueler {
    private String schildId;
    private String nachname;
    private String vorname;
    private LocalDate geburtsdatum;
    private Geschlecht geschlecht;

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

    public void aktualisiereStammdaten(SchuelerDatensatz datensatz) {
        nachname = datensatz.getNachname();
        vorname = datensatz.getVorname();
        geburtsdatum = datensatz.getGeburtsdatum();
        geschlecht = datensatz.getGeschlecht();
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