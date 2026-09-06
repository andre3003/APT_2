package de.abiturplanung.model;

import de.abiturplanung.model.Lehrer;

public class Kurs {

    private final String bezeichnung;
    private String fach;
    private Lehrer fachlehrer;

    public Kurs(String bezeichnung, String fach, Lehrer fachlehrer) {
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

    public void setFachlehrer(Lehrer fachlehrer) {
        this.fachlehrer = fachlehrer;
    }

    public void setFach(String fach) {
        this.fach = fach;
    }

    public Kursart getKursart() {
        String[] teile = bezeichnung.trim().split("\\s+");

        if (teile.length < 2) {
            throw new IllegalStateException("Ungültige Kursbezeichnung: " + bezeichnung);
        }

        return switch (teile[1].charAt(0)) {
            case 'L' -> Kursart.LEISTUNGSKURS;
            case 'G' -> Kursart.GRUNDKURS;
            default -> throw new IllegalStateException("Unbekannte Kursart in Kursbezeichnung: " + bezeichnung);
        };
    }

}