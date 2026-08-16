package de.abiturplanung.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Pruefung {

    private final Schueler schueler;
    private final Kurs kurs;

    private Lehrer pruefer;
    private Lehrer schriftfuehrer;
    private Lehrer vorsitz;

    private final Abiturfach abiturfach;
    private final Pruefungsform pruefungsform;

    private LocalDate pruefungstag;
    private LocalTime beginn;
    private Raum raum;
    private Integer planungsspalte;
    private Long pruefungId;

    public Pruefung(Schueler schueler, Kurs kurs, Lehrer pruefer, Abiturfach abiturfach) {
        this.schueler = schueler;
        this.kurs = kurs;
        this.pruefer = pruefer;
        this.abiturfach = abiturfach;

        if (abiturfach == Abiturfach.AB4)
            pruefungsform = Pruefungsform.MUENDLICH;
        else
            pruefungsform = Pruefungsform.SCHRIFTLICH;
    }

    public Long getPruefungId() {
        return pruefungId;
    }

    public void setPruefungId(Long pruefungId) {
        this.pruefungId = pruefungId;
    }

    public Integer getPlanungsspalte() {
        return planungsspalte;
    }

    public void setPlanungsspalte(Integer planungsspalte) {
        this.planungsspalte = planungsspalte;
    }

    public Abiturfach getAbiturfach() {
        return abiturfach;
    }

    public Schueler getSchueler() {
        return schueler;
    }

    public Kurs getKurs() {
        return kurs;
    }

    public Lehrer getPruefer() {
        return pruefer;
    }

    public void setPruefer(Lehrer pruefer) {
        this.pruefer = pruefer;
    }

    public Lehrer getSchriftfuehrer() {
        return schriftfuehrer;
    }

    public void setSchriftfuehrer(Lehrer schriftfuehrer) {
        this.schriftfuehrer = schriftfuehrer;
    }

    public Lehrer getVorsitz() {
        return vorsitz;
    }

    public void setVorsitz(Lehrer vorsitz) {
        this.vorsitz = vorsitz;
    }

    public Pruefungsform getPruefungsform() {
        return pruefungsform;
    }

    public LocalDate getPruefungstag() {
        return pruefungstag;
    }

    public void setPruefungstag(LocalDate pruefungstag) {
        this.pruefungstag = pruefungstag;
    }

    public LocalTime getBeginn() {
        return beginn;
    }

    public void setBeginn(LocalTime beginn) {
        this.beginn = beginn;
    }

    public Raum getRaum() {
        return raum;
    }

    public void setRaum(Raum raum) {
        this.raum = raum;
    }

    public boolean istTerminiert() {
        return pruefungstag != null && beginn != null;
    }

    public boolean istKommissionVollstaendig() {
        return pruefer != null && schriftfuehrer != null && vorsitz != null;
    }

    public boolean istVollstaendigGeplant() {
        return istTerminiert() && istKommissionVollstaendig() && raum != null;
    }
}