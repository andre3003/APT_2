package de.abiturplanung.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Abitur {

    private final List<Schueler> schueler = new ArrayList<>();

    private final List<Lehrer> lehrer = new ArrayList<>();

    private final List<Kurs> kurse = new ArrayList<>();

    private final List<Pruefung> pruefungen = new ArrayList<>();

    private final List<Raum> raeume = new ArrayList<>();

    private final List<Pruefungstag> pruefungstage = new ArrayList<>();


    /*--------------------------------------------------
     * Hinzufügen
     *--------------------------------------------------*/

    public List<Pruefungstag> getPruefungstage() {
        return Collections.unmodifiableList(pruefungstage);
    }

    public void addPruefungstag(Pruefungstag pruefungstag) {
        pruefungstage.add(pruefungstag);
    }

    public void addSchueler(Schueler schueler) {
        this.schueler.add(schueler);
    }

    public void addLehrer(Lehrer lehrer) {
        this.lehrer.add(lehrer);
    }

    public void addKurs(Kurs kurs) {
        this.kurse.add(kurs);
    }

    public void addRaum(Raum raum) {
        raeume.add(raum);
    }

    public void addPruefung(Pruefung pruefung) {
        pruefungen.add(pruefung);
        pruefung.getSchueler().addPruefung(pruefung);
    }

    /*--------------------------------------------------
     * Zugriff
     *--------------------------------------------------*/

    public List<Schueler> getSchueler() {
        return Collections.unmodifiableList(schueler);
    }

    public List<Lehrer> getLehrer() {
        return Collections.unmodifiableList(lehrer);
    }

    public List<Kurs> getKurse() {
        return Collections.unmodifiableList(kurse);
    }

    public List<Pruefung> getPruefungen() {
        return Collections.unmodifiableList(pruefungen);
    }

    public List<Raum> getRaeume() {
        return Collections.unmodifiableList(raeume);
    }


    /*--------------------------------------------------
     * Suchen
     *--------------------------------------------------*/

    public Schueler findeSchueler(
            String nachname,
            String vorname,
            LocalDate geburtsdatum) {
        for (Schueler schueler : schueler) {
            if (schueler.getNachname().equalsIgnoreCase(nachname)
                    && schueler.getVorname().equalsIgnoreCase(vorname)
                    && schueler.getGeburtsdatum().equals(geburtsdatum)) {
                return schueler;
            }
        }
        return null;
    }


    public Lehrer findeLehrer(String kuerzel) {
        for (Lehrer lehrer : lehrer) {
            if (lehrer.getKuerzel().equalsIgnoreCase(kuerzel)) {
                return lehrer;
            }
        }
        return null;
    }


    public Kurs findeKurs(String bezeichnung) {
        String gesucht = normalisiere(bezeichnung);
        for (Kurs kurs : kurse) {
            if (normalisiere(kurs.getBezeichnung())
                    .equalsIgnoreCase(gesucht)) {
                return kurs;
            }
        }
        return null;
    }


    public Kurs findeOderErzeugeKurs(
            String bezeichnung,
            String fach,
            Lehrer fachlehrer) {

        Kurs kurs = findeKurs(bezeichnung);

        if (kurs != null)
            return kurs;

        kurs = new Kurs(
                normalisiere(bezeichnung),
                fach,
                fachlehrer);

        kurse.add(kurs);

        return kurs;
    }

    public Raum findeRaum(String bezeichnung) {
        for (Raum raum: raeume) {
            if (raum.getBezeichnung().equalsIgnoreCase(bezeichnung)) {
                return raum;
            }
        }
        return null;
    }

    private String normalisiere(String text) {
        return text.trim().replaceAll("\\s+", " ");
    }
}