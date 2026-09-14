package de.abiturplanung.importer;

public class FachDatensatz {
    private final String kuerzel;
    private final String bezeichnung;
    private final String stammfachKuerzel;
    private final String faechergruppe;

    public FachDatensatz(String kuerzel, String bezeichnung, String stammfachKuerzel, String faechergruppe) {
        this.kuerzel = kuerzel;
        this.bezeichnung = bezeichnung;
        this.stammfachKuerzel = stammfachKuerzel;
        this.faechergruppe = faechergruppe;
    }

    public String getKuerzel() {
        return kuerzel;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public String getStammfachKuerzel() {
        return stammfachKuerzel;
    }

    public String getFaechergruppe() {
        return faechergruppe;
    }
}
