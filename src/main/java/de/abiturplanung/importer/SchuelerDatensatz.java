package de.abiturplanung.importer;
import de.abiturplanung.model.Geschlecht;
import java.time.LocalDate;

public class SchuelerDatensatz {

    private final String schildId;
    private final String nachname;
    private final String vorname;
    private final LocalDate geburtsdatum;
    private final Geschlecht geschlecht;

    public SchuelerDatensatz(String schildId,
                             String nachname,
                             String vorname,
                             LocalDate geburtsdatum,
                             Geschlecht geschlecht) {

        this.schildId = schildId;
        this.nachname = nachname;
        this.vorname = vorname;
        this.geburtsdatum = geburtsdatum;
        this.geschlecht = geschlecht;
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

    public LocalDate getGeburtsdatum() {
        return geburtsdatum;
    }

    public Geschlecht getGeschlecht() {
        return geschlecht;
    }

}