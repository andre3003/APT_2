package de.abiturplanung.gui.planung;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SchriftlicherPruefungstag {

    private Long id;
    private LocalDate datum;
    private final List<Abiturklausur> abiturklausuren = new ArrayList<>();

    public SchriftlicherPruefungstag(LocalDate datum) {
        this.datum = Objects.requireNonNull(datum);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public void setDatum(LocalDate datum) {
        this.datum = Objects.requireNonNull(datum);
    }

    public List<Abiturklausur> getAbiturklausuren() {
        return Collections.unmodifiableList(abiturklausuren);
    }

    public void addAbiturklausur(Abiturklausur klausur) {
        Objects.requireNonNull(klausur);

        if (klausur.getPruefungstag() != null && klausur.getPruefungstag() != this) {
            throw new IllegalArgumentException("Die Abiturklausur gehört bereits zu einem anderen Prüfungstag.");
        }

        if (!abiturklausuren.contains(klausur)) {
            abiturklausuren.add(klausur);
            klausur.setPruefungstag(this);
        }
    }

    public void removeAbiturklausur(Abiturklausur klausur) {
        if (abiturklausuren.remove(klausur)) {
            klausur.setPruefungstag(null);
        }
    }
}
