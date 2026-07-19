package de.abiturplanung.importer;

public class LehrerDatensatz {
    private final String kuerzel;
    private final String anrede;
    private final String nachname;
    private final String vorname;
    private final String fak1;
    private final String fak2;
    private final String fak3;
    private final String fak4;
    private final String amtsbez;

    public LehrerDatensatz(String kuerzel, String anrede, String nachname, String vorname, String fak1, String fak2, String fak3, String fak4, String amtsbez) {
        this.kuerzel = kuerzel;
        this.anrede = anrede;
        this.nachname = nachname;
        this.vorname = vorname;
        this.fak1 = fak1;
        this.fak2 = fak2;
        this.fak3 = fak3;
        this.fak4 = fak4;
        this.amtsbez  = amtsbez;
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

    public String getFak1() {
        return fak1;
    }

    public String getFak2() {
        return fak2;
    }

    public String getFak3() {
        return fak3;
    }

    public String getFak4() {
        return fak4;
    }

    public String getAmtsbez() {
        return amtsbez;
    }
}
