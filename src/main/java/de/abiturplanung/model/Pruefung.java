package de.abiturplanung.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pruefung {

    private final Schueler schueler;

    private final Kurs kurs;

    private Lehrer pruefer;

    private final Abiturfach abiturfach;

    private final Pruefungsform pruefungsform;

    public Pruefung(
            Schueler schueler,
            Kurs kurs,
            Lehrer pruefer,
            Abiturfach abiturfach) {

        this.schueler = schueler;
        this.kurs = kurs;
        this.pruefer = pruefer;
        this.abiturfach = abiturfach;

        if (abiturfach == Abiturfach.AB4)
            pruefungsform = Pruefungsform.MUENDLICH;
        else
            pruefungsform = Pruefungsform.SCHRIFTLICH;
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

    public Pruefungsform getPruefungsform() {
        return pruefungsform;
    }
}