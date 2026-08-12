package de.abiturplanung.model;
import java.time.LocalDate;

public class Pruefungstag {

    private LocalDate datum;

    public Pruefungstag(LocalDate datum) {
        this.datum = datum;
    }

    public LocalDate getDatum() {
        return datum;
    }
}