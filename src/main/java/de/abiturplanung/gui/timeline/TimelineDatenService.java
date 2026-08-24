package de.abiturplanung.gui.timeline;

import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Kurs;
import de.abiturplanung.model.Pruefung;
import de.abiturplanung.model.Pruefungstag;

import java.util.*;

public class TimelineDatenService {
    private record KommissionsSchluessel(String pruefer, String vorsitz, String schriftfuehrer) {
    }

    private Map<KommissionsSchluessel, List<Pruefung>> gibKommissionsGruppen(Abitur abitur, Pruefungstag pt) {
        Map<KommissionsSchluessel, List<Pruefung>> map = new HashMap<>();

        for (Pruefung p : abitur.getPruefungen()) {
            if (!pt.getDatum().equals(p.getPruefungstag())) {
                continue;
            }
            KommissionsSchluessel schluessel = new KommissionsSchluessel(
                    p.getPruefer().getKuerzel(),
                    p.getVorsitz().getKuerzel(),
                    p.getSchriftfuehrer().getKuerzel());

            if (map.containsKey(schluessel)) {
                map.get(schluessel).add(p);
            } else {
                List<Pruefung> neueGruppe = new ArrayList<>();
                neueGruppe.add(p);
                map.put(schluessel, neueGruppe);
            }
        }
        return map;
    }

    public List<KommissionsGruppe> gibSortierteKommissionsGruppen(Abitur abitur, Pruefungstag pt) {
        List<KommissionsGruppe> result = new ArrayList<>();
        Map<KommissionsSchluessel, List<Pruefung>> map = gibKommissionsGruppen(abitur, pt);
        for (Map.Entry<KommissionsSchluessel, List<Pruefung>> entry : map.entrySet()) {
            KommissionsSchluessel schluessel = entry.getKey();
            List<Pruefung> pruefungen = entry.getValue();
            pruefungen.sort(Comparator.comparing(Pruefung::getBeginn));
            KommissionsGruppe gruppe = new KommissionsGruppe(schluessel.pruefer(), schluessel.vorsitz(), schluessel.schriftfuehrer(), pruefungen);
            result.add(gruppe);
        }
        result.sort(Comparator.comparing(KommissionsGruppe::pruefer).thenComparing(gruppe -> gruppe.pruefungen().get(0).getKurs().getFach()).thenComparing(gruppe -> gruppe.pruefungen().get(0).getBeginn()));
        //Wichtig: gruppe ist hier ein fre gewählter Name für den Parameter, den wir dem Lambda geben. Der Typ (Kommissionsgruppe) wird aus dem Kontext erschlossen.
        // An dem Parameter können wir in dem Lambda dann die Methoden aufrufen, die wir brauchen, um die Information zu erhalten, die wir für das Sortieren brauchen (Fach und Beginn).
        // Beim Prüfer brauchen wir kein Lambda. Wir übergeben einfach die Methodenreferenz auf eine Methode, die der Parametertyp (Kommissionsgruppe) unmittelbar kennt.
        return result;
    }
}

