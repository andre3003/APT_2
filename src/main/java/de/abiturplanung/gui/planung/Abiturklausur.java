package de.abiturplanung.gui.planung;

import java.util.Objects;

public class Abiturklausur {

    private int id;
    private String fach;
    private String kursart;
    private Integer bearbeitungszeit;
    private Integer auswahlzeit;
    private String hilfsmittel;
    private SchriftlicherPruefungstag pruefungstag;

    public Abiturklausur(int id, String fach, String kursart) {
        this.id = id;
        this.fach = Objects.requireNonNull(fach);
        this.kursart = Objects.requireNonNull(kursart);
    }

    public int getId() {
        return id;
    }

    public String getFach() {
        return fach;
    }

    public String getKursart() {
        return kursart;
    }

    public Integer getBearbeitungszeit() {
        return bearbeitungszeit;
    }

    public void setBearbeitungszeit(Integer bearbeitungszeit) {
        pruefeZeit(bearbeitungszeit);
        this.bearbeitungszeit = bearbeitungszeit;
    }

    public Integer getAuswahlzeit() {
        return auswahlzeit;
    }

    public void setAuswahlzeit(Integer auswahlzeit) {
        pruefeZeit(auswahlzeit);
        this.auswahlzeit = auswahlzeit;
    }

    public String getHilfsmittel() {
        return hilfsmittel;
    }

    public void setHilfsmittel(String hilfsmittel) {
        this.hilfsmittel = hilfsmittel;
    }

    public SchriftlicherPruefungstag getPruefungstag() {
        return pruefungstag;
    }

    void setPruefungstag(SchriftlicherPruefungstag pruefungstag) {
        this.pruefungstag = pruefungstag;
    }

    private void pruefeZeit(Integer minuten) {
        if (minuten != null && minuten < 0) {
            throw new IllegalArgumentException("Eine Zeitangabe darf nicht negativ sein.");
        }
    }
}