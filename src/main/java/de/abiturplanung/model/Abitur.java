package de.abiturplanung.model;

import java.time.LocalDate;
import java.util.*;

public class Abitur {

    private final List<Schueler> schuelerList = new ArrayList<>();

    private final List<Lehrer> lehrer = new ArrayList<>();

    private final List<Kurs> kurse = new ArrayList<>();

    private final List<Pruefung> pruefungen = new ArrayList<>();

    private final List<Raum> raeume = new ArrayList<>();

    private final List<Fach> faecher = new ArrayList<>();

    private final List<Pruefungstag> pruefungstage = new ArrayList<>();

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

    public void aendereSchueler(Schueler schueler, String nachname, String vorname, LocalDate geburtsdatum, Geschlecht geschlecht) {
        schueler.aktualisiereStammdaten(nachname, vorname, geburtsdatum, geschlecht);
        schuelerList.sort(Comparator.comparing(Schueler::getNachname, String.CASE_INSENSITIVE_ORDER).thenComparing(Schueler::getVorname, String.CASE_INSENSITIVE_ORDER).thenComparing(Schueler::getSchildId));
    }

    public boolean aendereLehrer(Lehrer lehrer, String nachname, String vorname, Amtsbezeichnung amtsbezeichnung, List<Fach> fakultas) {
        Lehrer l = findeLehrer(lehrer.getKuerzel());

        if (l == null) {
            return false;
        }
        l.aktualisiereStammdaten(l.getAnrede(), nachname, vorname, amtsbezeichnung);
        l.aktualisiereFakultas(fakultas);
        sortiereLehrer();
        return true;
    }

    public void lehrerLoeschen(String kuerzel) {
        Lehrer l = findeLehrer(kuerzel);
        lehrer.remove(l);
        sortiereLehrer();
    }

    public void addLehrer(Lehrer lehrer) {
        this.lehrer.add(lehrer);
    }

    public void addFach(Fach fach) {
        faecher.add(fach);
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

    public List<Fach> getFaecher() {
        return Collections.unmodifiableList(faecher);
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

    public Fach findeFach(String kuerzel) {
        for (Fach fach : faecher) {
            if (fach.getKuerzel().equalsIgnoreCase(kuerzel)) {
                return fach;
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

    public Kurs findeOderErzeugeKurs(String bezeichnung, String fachKuerzel, Lehrer fachlehrer) {
        Fach fach = findeFach(fachKuerzel);

        if (fach == null) {
            throw new IllegalArgumentException("Fach nicht gefunden: " + fachKuerzel);
        }

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
        schuelerList.sort(Comparator.comparing(Schueler::getNachname, String.CASE_INSENSITIVE_ORDER).thenComparing(Schueler::getVorname, String.CASE_INSENSITIVE_ORDER));
    }

    public void sortiereFaecher() {
        faecher.sort(Comparator.comparing(Fach::getKuerzel));
    }

    public void sortiereLehrer() {
        lehrer.sort(Comparator
                .comparing(Lehrer::getNachname, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Lehrer::getVorname, String.CASE_INSENSITIVE_ORDER));
    }

    public record LehrerVerwendungen(
            List<Kurs> kurse,
            int alsPruefer,
            int alsVorsitzender,
            int alsSchriftfuehrer) {

        public boolean istLeer() {
            return kurse.isEmpty()
                    && alsPruefer == 0
                    && alsVorsitzender == 0
                    && alsSchriftfuehrer == 0;
        }
    }

    public LehrerVerwendungen findeVerwendungen(String kuerzel) {
        Lehrer lehrer = findeLehrer(kuerzel);

        List<Kurs> verwendeteKurse = new ArrayList<>();
        int alsPruefer = 0;
        int alsVorsitzender = 0;
        int alsSchriftfuehrer = 0;

        for (Kurs kurs : kurse) {
            if (lehrer.equals(kurs.getFachlehrer())) {
                verwendeteKurse.add(kurs);
            }
        }

        for (Pruefung pruefung : getPruefungen()) {
            if (lehrer.equals(pruefung.getPruefer())) {
                alsPruefer++;
            }
            if (lehrer.equals(pruefung.getVorsitz())) {
                alsVorsitzender++;
            }
            if (lehrer.equals(pruefung.getSchriftfuehrer())) {
                alsSchriftfuehrer++;
            }
        }
        return new LehrerVerwendungen(
                verwendeteKurse,
                alsPruefer,
                alsVorsitzender,
                alsSchriftfuehrer);
    }
}