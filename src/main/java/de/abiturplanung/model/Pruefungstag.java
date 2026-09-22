package de.abiturplanung.model;
import java.time.LocalDate;

public class Pruefungstag {

    private LocalDate datum;
    private int anzahlPlanungsspalten;

    public Pruefungstag(LocalDate datum, int spalten) {
        this.datum = datum;
        this.anzahlPlanungsspalten = spalten;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public int getAnzahlPlanungsspalten() {
        return anzahlPlanungsspalten;
    }

    public void setAnzahlPlanungsspalten(int anzahlPlanungsspalten) {
        this.anzahlPlanungsspalten = anzahlPlanungsspalten;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }
}