package de.abiturplanung.model;

public class Raum {
    String bezeichnung;
    int kapazitaet;


    public Raum(String bezeichnung, int kapazitaet) {
        this.bezeichnung = bezeichnung;
        this.kapazitaet = kapazitaet;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public int getKapazitaet() {
        return kapazitaet;
    }

    @Override
    public String toString() {
        return bezeichnung;
    }

    public void aktualisiereStammdaten(int kapazitaet) {
        this.kapazitaet = kapazitaet;
    }
}
