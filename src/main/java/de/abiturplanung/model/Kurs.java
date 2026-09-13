package de.abiturplanung.model;
import java.util.Objects;

public class Kurs {

    private final String bezeichnung;
    private String fachbezeichnung;
    private Lehrer fachlehrer;

    public Kurs(String bezeichnung, String fach, Lehrer fachlehrer) {
        this.bezeichnung = bezeichnung;
        this.fachbezeichnung = fach;
        this.fachlehrer = fachlehrer;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public String getFach() {
        return fachbezeichnung;
    }

    public Fach getFachObjekt() {
        return Fach.ausFachbezeichnung(fachbezeichnung);
    }

    public Lehrer getFachlehrer() {
        return fachlehrer;
    }

    public void setFachlehrer(Lehrer fachlehrer) {
        this.fachlehrer = fachlehrer;
    }

    public void setFach(String fach) {
        this.fachbezeichnung = fach;
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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Kurs kurs)) return false; //Pattern Matching: Die moderne Java-Schreibweise kombiniert Typprüfung und Cast: Prüfe, ob o ein Kurs ist. Wenn ja, stelle mir dieses Objekt zugleich als Variable kurs vom Typ Kurs zur Verfügung.
        return Objects.equals(bezeichnung, kurs.bezeichnung);
    }

    //Zwei Objekte, für die equals() true ergibt, müssen denselben Hashcode besitzen. Deshalb:
    @Override
    public int hashCode() {
        return Objects.hash(bezeichnung);
    }

}