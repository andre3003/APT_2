package de.abiturplanung.importer;

public class SchuelerleistungsDatensatz {

    private final String nachname;
    private final String vorname;
    private final String fach;
    private final String fachlehrer;
    private final String kursart;
    private final String kurs;
    private final String abiturfach;

    public SchuelerleistungsDatensatz(
            String nachname,
            String vorname,
            String fach,
            String fachlehrer,
            String kursart,
            String kurs,
            String abiturfach) {

        this.nachname = nachname;
        this.vorname = vorname;
        this.fach = fach;
        this.fachlehrer = fachlehrer;
        this.kursart = kursart;
        this.kurs = kurs;
        this.abiturfach = abiturfach;
    }

    public String getNachname() {
        return nachname;
    }

    public String getVorname() {
        return vorname;
    }

    public String getFach() {
        return fach;
    }

    public String getFachlehrer() {
        return fachlehrer;
    }

    public String getKursart() {
        return kursart;
    }

    public String getKurs() {
        return kurs;
    }

    public String getAbiturfach() {
        return abiturfach;
    }

    public boolean istMuendlichePruefung() {
        return "4".equals(abiturfach);
    }
}