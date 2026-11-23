package de.abiturplanung.importer;

import de.abiturplanung.model.Abiturfach;
import de.abiturplanung.model.Schueler;

import java.util.ArrayList;
import java.util.List;

public class LeistungsdatenImportbericht {

    private int neueSchriftlichePruefungen;
    private int neueMuendlichePruefungen;
    private int vorhandenePruefungen;

    private final List<String> neueFaecher = new ArrayList<>();
    private final List<String> neueLehrer = new ArrayList<>();
    private final List<String> nichtZugeordneteSchueler = new ArrayList<>();
    private final List<String> fehlendePruefungen = new ArrayList<>();

    public void neuePruefung(Abiturfach abiturfach) {
        if (abiturfach == Abiturfach.AB4) {
            neueMuendlichePruefungen++;
        } else {
            neueSchriftlichePruefungen++;
        }
    }

    public void vorhandenePruefung() {
        vorhandenePruefungen++;
    }

    public void neuesFach(String kuerzel) {
        if (!neueFaecher.contains(kuerzel)) neueFaecher.add(kuerzel);
    }

    public void neuerLehrer(String kuerzel) {
        if (!neueLehrer.contains(kuerzel)) neueLehrer.add(kuerzel);
    }

    public void schuelerNichtGefunden(SchuelerleistungsDatensatz ds) {
        String eintrag = ds.getNachname() + ", " + ds.getVorname() + " (" + ds.getGeburtsdatum() + "), " + ds.getAbiturfach();
        nichtZugeordneteSchueler.add(eintrag);
    }

    public void keinenDatensatzZuSchuelerGefunden(Schueler schueler, Abiturfach abifach){
        String pruefung = schueler.getNachname() + ", " + schueler.getVorname() + "(" + schueler.getGeburtsdatum() + ") " + abifach.name();
        fehlendePruefungen.add(pruefung);
    }

    public int getNeueSchriftlichePruefungen() {
        return neueSchriftlichePruefungen;
    }

    public int getNeueMuendlichePruefungen() {
        return neueMuendlichePruefungen;
    }

    public int getVorhandenePruefungen() {
        return vorhandenePruefungen;
    }

    public List<String> getNeueFaecher() {
        return neueFaecher;
    }

    public List<String> getNeueLehrer() {
        return neueLehrer;
    }

    public List<String> getNichtZugeordneteSchueler() {
        return nichtZugeordneteSchueler;
    }

    public List<String> getFehlendePruefungen() {
        return fehlendePruefungen;
    }

    public String erstelleBerichtstext() {
        StringBuilder sb = new StringBuilder("Importbericht - Leistungsdaten\n");
        sb.append("============================\n\n");
        sb.append("Neu angelegte Prüfungen\n");
        sb.append("Gesamt: ").append(neueSchriftlichePruefungen + neueMuendlichePruefungen).append(" Prüfungen\n");
        sb.append("Davon schriftlich: ").append(neueSchriftlichePruefungen).append(" Prüfungen\n");
        sb.append("Davon mündlich: ").append(neueMuendlichePruefungen).append(" Prüfungen\n");
        sb.append("Bereits vorhandene Prüfungen: ").append(vorhandenePruefungen).append("\n\n");

        sb.append("Ergänzte Stammdaten\n");
        if (neueFaecher.isEmpty() && neueLehrer.isEmpty()) {
            sb.append("Keine neuen Stammdaten angelegt.\n");
        } else {
            if (!neueFaecher.isEmpty()) sb.append("Neue Fächer: ").append(String.join(", ", neueFaecher)).append("\n");
            if (!neueLehrer.isEmpty()) sb.append("Neue Lehrer: ").append(String.join(", ", neueLehrer)).append("\n");
            sb.append("Bitte fehlende Informationen zu Fächern und Lehrern im Stammdatenbereich ergänzen!\n");
        }
        sb.append("\nVerworfene Prüfungsdaten\n");
        if (nichtZugeordneteSchueler.isEmpty()) {
            sb.append("Alle Datensätze konnten zugeordnet werden.\n");
        } else {
            sb.append("ACHTUNG: ").append(nichtZugeordneteSchueler.size()).append(" Prüfungsdatensätze konnten nicht übernommen werden!\n");
            sb.append("Bitte die Schülerstammdaten kontrollieren und den Import wiederholen.\n\n");
            for (String eintrag : nichtZugeordneteSchueler) sb.append(eintrag).append('\n');
        }
        sb.append("\nFehlende Prüfungen\n");
        if (fehlendePruefungen.isEmpty()) {
            sb.append("Alle Schüler der Datenbank haben vier Prüfungen!\n");
        } else {
            sb.append("ACHTUNG: Für ").append(fehlendePruefungen.size()).append(" Prüfungen konnten keine Datensätze gefunden werden!\n");
            sb.append("Bitte die Schülerleistungsdaten überprüfen.\n\n");
            for (String eintrag : fehlendePruefungen) sb.append(eintrag).append('\n');
        }
        return sb.toString();
    }

}