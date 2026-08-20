package de.abiturplanung.service;

import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Pruefung;

import java.time.Duration;
import java.util.*;

public class Kollisionspruefer {
    private final Abitur abitur;

    public Kollisionspruefer(Abitur abitur) {
        this.abitur = abitur;
    }

    private boolean zeitlichRelevant(Pruefung a, Pruefung b) {
        if (a.getPruefungstag() == null || b.getPruefungstag() == null
                || a.getBeginn() == null || b.getBeginn() == null) {
            return false;
        }

        // Nur Prüfungen am selben Tag können kollidieren
        if (!a.getPruefungstag().equals(b.getPruefungstag())) {
            return false;
        }

        // Prüfungen innerhalb derselben Planungsspalte
        // gehören organisatorisch zusammen
        if (a.getPlanungsspalte() == b.getPlanungsspalte()) {
            return false;
        }

        long minutenAbstand = Math.abs(Duration.between(a.getBeginn(), b.getBeginn()).toMinutes()); //Math.abs entfernt ein mögliches negatives Vorzeichen bei der Berechnung des Minutenabstandes.

        return minutenAbstand <= 60;
    }

    public boolean pruefeKollision(Pruefung a, Pruefung b) {
        if (!zeitlichRelevant(a, b)) {
            return false;
        }
        return pruefePersonalkollision(a, b) || pruefeRaumKollision(a, b);
    }


    private boolean pruefePersonalkollision(Pruefung a, Pruefung b) {
        Set<String> kommission = new HashSet<>();
        if (a.getPruefer() != null) {
            kommission.add(a.getPruefer().getKuerzel());
        }
        if (a.getVorsitz() != null) {
            kommission.add(a.getVorsitz().getKuerzel());
        }
        if (a.getSchriftfuehrer() != null) {
            kommission.add(a.getSchriftfuehrer().getKuerzel());
        }
        return (b.getPruefer() != null && kommission.contains(b.getPruefer().getKuerzel()))
                || (b.getVorsitz() != null && kommission.contains(b.getVorsitz().getKuerzel()))
                || (b.getSchriftfuehrer() != null && kommission.contains(b.getSchriftfuehrer().getKuerzel()));
    }

    private boolean pruefeRaumKollision(Pruefung a, Pruefung b) {
        return a.getRaum() != null && b.getRaum() != null && a.getRaum().getBezeichnung().equals(b.getRaum().getBezeichnung());
    }


    public List<Pruefung> findeKollisionen(Pruefung pruefung) {
        ArrayList<Pruefung> result = new ArrayList<>();
        List<Pruefung> pruefungen = abitur.getPruefungen();

        for (Pruefung p : pruefungen) {
            if (p != pruefung && pruefeKollision(pruefung, p)) {
                result.add(p);
            }
        }
        return result;
    }

    public Map <Pruefung, List<Pruefung>> findeAlleKollisionen() {
        Map<Pruefung, List<Pruefung>> result = new HashMap<>();

        for (Pruefung pruefung : abitur.getPruefungen()) {
            List<Pruefung> kollisionen = findeKollisionen(pruefung);
            if (!kollisionen.isEmpty()) {
                result.put(pruefung, kollisionen);
            }
        }
        return result;
    }
}