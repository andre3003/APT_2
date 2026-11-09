package de.abiturplanung.gui.planung;

import de.abiturplanung.model.Kurs;
import de.abiturplanung.model.Raum;

import java.util.ArrayList;
import java.util.List;

public class RaumbelegungAbiturklausuren {
    private Long id;
    private SchriftlicherPruefungstag pruefungstag;
    private Raum raum;
    private List<Kurs> kurse = new ArrayList<>();

        public RaumbelegungAbiturklausuren(Long id, SchriftlicherPruefungstag pruefungstag, Raum raum) {
            this.id = id;
            this.pruefungstag = pruefungstag;
            this.raum = raum;
        }

    public void setId(Long id) {
        this.id = id;
    }
    public void addKurs(Kurs kurs) {
        if (!kurse.contains(kurs)) kurse.add(kurs);
    }

    public void removeKurs(Kurs kurs) {
        kurse.remove(kurs);
    }

    public Long getId() {
        return id;
    }

    public SchriftlicherPruefungstag getPruefungstag() {
        return pruefungstag;
    }

    public Raum getRaum() {
        return raum;
    }

    public List<Kurs> getKurse() {
        return kurse;
    }
}
