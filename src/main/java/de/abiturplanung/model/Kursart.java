package de.abiturplanung.model;

public enum Kursart {
    LEISTUNGSKURS("LK"),
    GRUNDKURS("GK");
    private final String kuerzel;

    Kursart(String kuerzel) {
        this.kuerzel = kuerzel;
    }

    public String getKuerzel() {
        return kuerzel;
    }
}
