package de.abiturplanung.formulare.abiturfomulare;

import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Abiturfach;
import de.abiturplanung.model.Fachpruefungsausschuss;
import de.abiturplanung.model.Pruefung;

import java.util.*;

public class AbiturfomulareDatenService {


    public static List<Pruefung> gibPruefungenAB4FuerFormulardruck(Abitur abitur) {
        return abitur.getPruefungen().stream()
                .filter(p -> p.getAbiturfach() == Abiturfach.AB4)
                .filter(p -> p.getPruefungstag() != null)
                .sorted(Comparator.comparing(Pruefung::getPruefungstag)
                        .thenComparing(p -> p.getPruefer().getNachname())
                        .thenComparing(p -> p.getSchueler().getNachname())
                        .thenComparing(p -> p.getSchueler().getVorname()))
                .toList();
    }

    public static List<Fachpruefungsausschuss> gibFachpruefungsausschuesseAB4(Abitur abitur) {
        List<Pruefung> pruefungen = abitur.gibPruefungenAB4Sortiert();
        Set<Fachpruefungsausschuss> fpas = new HashSet<>();

        for (Pruefung p : pruefungen) {
            if (p.getVorsitz() == null || p.getSchriftfuehrer() == null) continue;
            Fachpruefungsausschuss fpa = new Fachpruefungsausschuss(p.getKurs().getFach().getKuerzel(), p.getKurs().getKursart().getKuerzel(), p.getVorsitz().getKuerzel(),
                    p.getPruefer().getKuerzel(), p.getSchriftfuehrer().getKuerzel());
            fpas.add(fpa);
        }
        return fpas.stream()
                .sorted(Comparator.comparing(Fachpruefungsausschuss::fach)
                        .thenComparing(Fachpruefungsausschuss::kursart)
                        .thenComparing(Fachpruefungsausschuss::pruefer))
                .toList();
    }
}
