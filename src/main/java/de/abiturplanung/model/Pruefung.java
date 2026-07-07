package de.abiturplanung.model;

public class Pruefung {

    private final Schueler pruefling;
    private final Kurs kurs;
    private Lehrer pruefer;

    public Pruefung(Schueler pruefling, Kurs kurs) {
        this.pruefling = pruefling;
        this.kurs = kurs;
        this.pruefer = kurs.getKurslehrer();
    }

    public Schueler getPruefling() {
        return pruefling;
    }

    public Kurs getKurs() {
        return kurs;
    }

    public Lehrer getPruefer() {
        return pruefer;
    }
}