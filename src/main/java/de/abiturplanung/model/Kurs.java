package de.abiturplanung.model;

import de.abiturplanung.model.Lehrer;

public class Kurs {

    private final String bezeichnung;
    private final String fach;
    private Lehrer fachlehrer;

    public Kurs(String bezeichnung,
                String fach,
                Lehrer fachlehrer) {

        this.bezeichnung = bezeichnung;
        this.fach = fach;
        this.fachlehrer = fachlehrer;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public String getFach() {
        return fach;
    }

    public Lehrer getFachlehrer() {
        return fachlehrer;
    }

}