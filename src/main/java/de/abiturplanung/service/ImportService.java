package de.abiturplanung.service;

import de.abiturplanung.importer.*;
import de.abiturplanung.model.*;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ImportService {
    Abitur abitur;

    public ImportService(Abitur abitur) {
        this.abitur = abitur;
    }

    public void importiereLehrer(Path datei) throws IOException {
        LehrerImporter importer = new LehrerImporter();
        List<LehrerDatensatz> datensaetze = importer.lese(datei);
        for (LehrerDatensatz datensatz : datensaetze) {
            Lehrer lehrer = abitur.findeLehrer(datensatz.getKuerzel());
            if (lehrer == null) {
                lehrer = new Lehrer(datensatz.getKuerzel());
                abitur.addLehrer(lehrer);
            }
            lehrer.aktualisiereStammdaten(datensatz.getAnrede(), datensatz.getNachname(), datensatz.getVorname(), Amtsbezeichnung.ausKuerzel(datensatz.getAmtsbez()));
            lehrer.aktualisiereFakultas(erstelleFakultas(datensatz));
        }
    }

    private List<Fach> erstelleFakultas(LehrerDatensatz datensatz) {
        List<Fach> fakultas = new java.util.ArrayList<>();
        fuegeFakultasHinzu(fakultas, datensatz.getFak1());
        fuegeFakultasHinzu(fakultas, datensatz.getFak2());
        fuegeFakultasHinzu(fakultas, datensatz.getFak3());
        fuegeFakultasHinzu(fakultas, datensatz.getFak4());
        return fakultas;
    }

    private void fuegeFakultasHinzu(List<Fach> fakultas, String kuerzel) {
        if (kuerzel == null || kuerzel.isBlank()) {
            return;
        }
        Fach fach = new Fach(kuerzel);
        if (!fakultas.contains(fach)) {
            fakultas.add(fach);
        }
    }

    public void importiereSchueler(Path datei) throws IOException {
        SchuelerImporter importer = new SchuelerImporter();
        List<SchuelerDatensatz> datensaetze = importer.lese(datei);
        for (SchuelerDatensatz datensatz : datensaetze) {
            Schueler schueler = abitur.findeSchueler(datensatz.getNachname(), datensatz.getVorname(), datensatz.getGeburtsdatum());
            if (schueler == null) {
                schueler = new Schueler(datensatz.getSchildId());
                schueler.aktualisiereStammdaten(datensatz);
                abitur.addSchueler(schueler);
            } else {
                schueler.aktualisiereStammdaten(datensatz);
            }
        }
        abitur.sortiereSchueler();
    }

    public void importiereFaecher(Path datei) throws IOException {
        FachImporter importer = new FachImporter();
        List<FachDatensatz> datensaetze = importer.lese(datei);


        // 1. Alle Fächer anlegen bzw. Stammdaten aktualisieren
        for (FachDatensatz datensatz : datensaetze) {
            Fach fach = abitur.findeFach(datensatz.getKuerzel());
            if (fach == null) {
                fach = new Fach(datensatz.getKuerzel());
                abitur.addFach(fach);
            }

            fach.aktualisiereStammdaten(datensatz.getBezeichnung(), datensatz.getFaechergruppe());
        }

        // 2. Stammfächer zuordnen
        for (FachDatensatz datensatz : datensaetze) {
            Fach fach = abitur.findeFach(datensatz.getKuerzel());
            if (datensatz.getStammfachKuerzel() == null) {
                fach.setStammfach(null);
                continue;
            }
            Fach stammfach = abitur.findeFach(datensatz.getStammfachKuerzel());
            if (stammfach == null) {
                System.err.println("Stammfach nicht gefunden: " + datensatz.getStammfachKuerzel() + " für Fach " + datensatz.getKuerzel());
                fach.setStammfach(null);
                continue;
            }
            fach.setStammfach(stammfach);
        }
        abitur.sortiereFaecher();
    }

    private void importiereLeistungsdatensatz(SchuelerleistungsDatensatz ds, LeistungsdatenImportbericht bericht) {
        Schueler schueler = abitur.findeSchueler(ds.getNachname(), ds.getVorname(), ds.getGeburtsdatum());
        if (schueler == null) {
            bericht.schuelerNichtGefunden(ds);
            return;
        }
        Lehrer lehrer = abitur.findeLehrer(ds.getLehrerkuerzel());

        if (lehrer == null && !ds.getLehrerkuerzel().isBlank()) {
            lehrer = new Lehrer(ds.getLehrerkuerzel());
            abitur.addLehrer(lehrer);
            bericht.neuerLehrer(lehrer.getKuerzel());
        }

        Fach fach = abitur.findeFach(ds.getFach());
        if (fach == null) {
            fach = new Fach(ds.getFach());
            abitur.addFach(fach);
            bericht.neuesFach(fach.getKuerzel());
        }

        Kurs kurs = abitur.findeKurs(ds.getKursbezeichnung());
            if (kurs == null) {
                kurs = new Kurs(ds.getKursbezeichnung(), fach, lehrer);
                if (ds.getAbiturfach() == Abiturfach.AB1 || ds.getAbiturfach() == Abiturfach.AB2) {
                    kurs.setKursart(Kursart.LEISTUNGSKURS);
                } else if (ds.getAbiturfach() == Abiturfach.AB3 || ds.getAbiturfach() == Abiturfach.AB4) {
                    kurs.setKursart(Kursart.GRUNDKURS);
                } else {
                    kurs.setKursart(Kursart.NICHT_DEFINIERT);
                }
                abitur.addKurs(kurs);
            }

        Pruefung pruefung = abitur.findePruefung(schueler, ds.getAbiturfach());

        if (pruefung == null) {
           pruefung = new Pruefung(schueler, kurs, lehrer, ds.getAbiturfach());
            abitur.addPruefung(pruefung);
            bericht.neuePruefung(ds.getAbiturfach());
        } else {
            pruefung.setKurs(kurs);
            bericht.vorhandenePruefung();
        }
    }

    public LeistungsdatenImportbericht importiereLeistungsdaten(Path datei) throws IOException {
        LeistungsdatenImporter importer = new LeistungsdatenImporter();
        List<SchuelerleistungsDatensatz> datensaetze = importer.lese(datei, abitur.getAbiturjahrgang());
        LeistungsdatenImportbericht bericht = new LeistungsdatenImportbericht();

        for (SchuelerleistungsDatensatz datensatz : datensaetze) {
            importiereLeistungsdatensatz(datensatz, bericht);
        }
        pruefeFehlendeDatensaetzeNachImport(bericht);
        return bericht;
    }

    public void pruefeFehlendeDatensaetzeNachImport(LeistungsdatenImportbericht bericht) {
        for (Schueler schueler : abitur.getSchuelerList()) {
            for (Abiturfach abiturfach : new Abiturfach[]{Abiturfach.AB1, Abiturfach.AB2, Abiturfach.AB3, Abiturfach.AB4}) {
                if (schueler.getPruefung(abiturfach) == null)
                    bericht.keinenDatensatzZuSchuelerGefunden(schueler, abiturfach);
            }
        }
    }

    public void importiereRaeume(Path datei) throws IOException {
        RaumImporter importer = new RaumImporter();
        List<RaumDatensatz> datensaetze = importer.lese(datei);
        for (RaumDatensatz datensatz : datensaetze) {
            Raum raum = abitur.findeRaum(datensatz.getBezeichnung());
            if (raum == null) {
                raum = new Raum(datensatz.getBezeichnung(), datensatz.getKapazitaet());
                abitur.addRaum(raum);
            } else {
                raum.aktualisiereStammdaten(datensatz.getKapazitaet());
            }
        }
    }
}