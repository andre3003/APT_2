package de.abiturplanung.model;

import java.util.Objects;

public class Kurs {

    private final String bezeichnung;
    private Fach fach;
    private Lehrer fachlehrer;
    private int anzahlKlausurschreiber;
    private Lehrer zweitkorrektor1;
    private Lehrer zweitkorrektor2;
    private Kursart kursart = Kursart.NICHT_DEFINIERT;


    public Kurs(String bezeichnung, Fach fach, Lehrer fachlehrer) {
        this.bezeichnung = bezeichnung;
        this.fach = fach;
        this.fachlehrer = fachlehrer;
    }

    public Lehrer getZweitkorrektor1() {
        return zweitkorrektor1;
    }

    public void setZweitkorrektor1(Lehrer zweitkorrektor1) {
        this.zweitkorrektor1 = zweitkorrektor1;
    }

    public Lehrer getZweitkorrektor2() {
        return zweitkorrektor2;
    }

    public void setZweitkorrektor2(Lehrer zweitkorrektor2) {
        this.zweitkorrektor2 = zweitkorrektor2;
    }

    public int getAnzahlKlausurschreiber() {
        return anzahlKlausurschreiber;
    }

    public void setAnzahlKlausurschreiber(int anzahlKlausurschreiber) {
        this.anzahlKlausurschreiber = anzahlKlausurschreiber;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public Fach getFach() {
        return fach;
    }

    public Lehrer getFachlehrer() {
        return fachlehrer;
    }

    public void setFach(Fach fach) {
        this.fach = fach;
    }

    public void setFachlehrer(Lehrer fachlehrer) {
        this.fachlehrer = fachlehrer;
    }

    public void setKursart(Kursart kursart){
        this.kursart = kursart;
    }

    public Kursart getKursart() {
        return kursart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Kurs kurs)) return false;
        return Objects.equals(bezeichnung, kurs.bezeichnung);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bezeichnung);
    }
}