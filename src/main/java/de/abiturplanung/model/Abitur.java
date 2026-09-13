package de.abiturplanung.model;

import java.time.LocalDate;
import java.util.*;

public class Abitur {

    private final List<Schueler> schuelerList = new ArrayList<>();

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
        pruefungstage.sort(Comparator.comparing(Pruefungstag::getDatum));
    }

    public void aenderePruefungstag(Pruefungstag pruefungstag, LocalDate date) {
        pruefungstag.setDatum(date);
        pruefungstage.sort(Comparator.comparing(Pruefungstag::getDatum));
    }

    public ArrayList<Pruefung> loeschePruefungstag(Pruefungstag pruefungstag, boolean kommissionenBehalten) {
        ArrayList<Pruefung> result = new ArrayList<>();
        LocalDate datum = pruefungstag.getDatum();
        for (Pruefung pruefung : pruefungen) {
            if (datum.equals(pruefung.getPruefungstag())) {
                pruefung.setPruefungstag(null);
                pruefung.setBeginn(null);
                pruefung.setPlanungsspalte(null);
                pruefung.setRaum(null);

                if (!kommissionenBehalten) {
                    pruefung.setPruefer(null);
                    pruefung.setSchriftfuehrer(null);
                    pruefung.setVorsitz(null);
                }
                result.add(pruefung);
            }

        }
        pruefungstage.remove(pruefungstag);
        return result;
    }

    public void schuelerLoeschen(String schildId) {
        Schueler schueler = findeSchueler(schildId);
        if (schueler == null) {
            return;
        }
        pruefungen.removeIf(pruefung -> pruefung.getSchueler().equals(schueler));
        schuelerList.remove(schueler);
    }

    public void addSchueler(Schueler neuerSchueler) {
        schuelerList.add(neuerSchueler);
    }

    public void aendereSchueler(Schueler schueler, String nachname, String vorname, LocalDate
            geburtsdatum, Geschlecht geschlecht) {
        schueler.aktualisiereStammdaten(nachname, vorname, geburtsdatum, geschlecht);
        schuelerList.sort(Comparator
                .comparing(Schueler::getNachname, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Schueler::getVorname, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Schueler::getSchildId));
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

    public List<Schueler> getSchuelerList() {
        return Collections.unmodifiableList(schuelerList);
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

    public Schueler findeSchueler(String nachname, String vorname, LocalDate geburtsdatum) {
        for (Schueler schueler : schuelerList) {
            if (schueler.getNachname().equalsIgnoreCase(nachname) && schueler.getVorname().equalsIgnoreCase(vorname) && Objects.equals(schueler.getGeburtsdatum(), geburtsdatum)) {
                return schueler;
            }
        }
        return null;
    }

    public Schueler findeSchueler(String schildId) {
        for (Schueler schueler : schuelerList) {
            if (schueler.getSchildId().equals(schildId)) {
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
            if (normalisiere(kurs.getBezeichnung()).equalsIgnoreCase(gesucht)) {
                return kurs;
            }
        }
        return null;
    }

    public Kurs findeOderErzeugeKurs(String bezeichnung, String fach, Lehrer fachlehrer) {
        Kurs kurs = findeKurs(bezeichnung);
        if (kurs != null) {
            kurs.setFach(fach);
            kurs.setFachlehrer(fachlehrer);
            return kurs;
        }

        kurs = new Kurs(normalisiere(bezeichnung), fach, fachlehrer);
        kurse.add(kurs);
        return kurs;
    }

    public Raum findeRaum(String bezeichnung) {
        for (Raum raum : raeume) {
            if (raum.getBezeichnung().equalsIgnoreCase(bezeichnung)) {
                return raum;
            }
        }
        return null;
    }

    public Pruefung findePruefung(Schueler schueler, Abiturfach abiturfach) {
        for (Pruefung pruefung : pruefungen) {
            if (pruefung.getSchueler().getSchildId().equals(schueler.getSchildId()) && pruefung.getAbiturfach() == abiturfach) {
                return pruefung;
            }
        }
        return null;
    }

    private String normalisiere(String text) {
        return text.trim().replaceAll("\\s+", " ");
    }

    public void sortiereSchueler() {
        schuelerList.sort(Comparator
                .comparing(Schueler::getNachname, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Schueler::getVorname, String.CASE_INSENSITIVE_ORDER));
    }


}