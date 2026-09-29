package de.abiturplanung.model;
import java.time.LocalDate;
import java.time.LocalTime;

public class Pruefungstag {

    private LocalDate datum;
    private int anzahlPlanungsspalten;
    private LocalTime startzeit;
    private LocalTime endzeit; //Ist der Beginn(!) des letzten Prüfungsblocks

    public Pruefungstag(LocalDate datum, LocalTime starrzeit, LocalTime endzeit, int planungsspalten) {
        this.datum = datum;
        this.startzeit = starrzeit;
        this.endzeit = endzeit;
        this.anzahlPlanungsspalten = planungsspalten;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public LocalTime getStartzeit() {
        return startzeit;
    }

    public void setStartzeit(LocalTime startzeit) {
        this.startzeit = startzeit;
    }

    public LocalTime getEndzeit() {
        return endzeit;
    }

    public void setEndzeit(LocalTime endzeit) {
        this.endzeit = endzeit;
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