package de.abiturplanung.importer;

import de.abiturplanung.model.Abiturfach;

import java.time.LocalDate;

public class SchuelerleistungsDatensatz {

    private final String nachname;
    private final String vorname;
    private final LocalDate geburtsdatum;

    private final String fach;
    private final String kursbezeichnung;

    private final String lehrerkuerzel;

    private final Abiturfach abiturfach;

    public SchuelerleistungsDatensatz(
            String nachname,
            String vorname,
            LocalDate geburtsdatum,
            String fach,
            String kursbezeichnung,
            String lehrerkuerzel,
            Abiturfach abiturfach) {

        this.nachname = nachname;
        this.vorname = vorname;
        this.geburtsdatum = geburtsdatum;

        this.fach = fach;
        this.kursbezeichnung = kursbezeichnung;

        this.lehrerkuerzel = lehrerkuerzel;

        this.abiturfach = abiturfach;
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

    public String getFach() {
        return fach;
    }

    public String getKursbezeichnung() {
        return kursbezeichnung;
    }

    public String getLehrerkuerzel() {
        return lehrerkuerzel;
    }

    public Abiturfach getAbiturfach() {
        return abiturfach;
    }
}