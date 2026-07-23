package de.abiturplanung.importer;

public class RaumDatensatz {

    String bezeichnung;
    int kapazitaet;

    public RaumDatensatz(String bezeichnung, int kapazitaet) {
        this.bezeichnung = bezeichnung;
        this.kapazitaet = kapazitaet;

    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public int getKapazitaet() {
        return kapazitaet;
    }
}
