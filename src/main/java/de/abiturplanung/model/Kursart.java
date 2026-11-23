package de.abiturplanung.model;

public enum Kursart {
    LEISTUNGSKURS("LK"),
    GRUNDKURS("GK"),
    NICHT_DEFINIERT("NN");


    private final String kuerzel;

    Kursart(String kuerzel) {
        this.kuerzel = kuerzel;
    }

    public String getKuerzel() {
        return kuerzel;
    }
}
