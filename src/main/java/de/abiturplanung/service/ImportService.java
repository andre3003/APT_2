package de.abiturplanung.service;

import de.abiturplanung.importer.*;
import de.abiturplanung.model.*;

import java.io.IOException;
import java.nio.file.Path;
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



    private void importiereLeistungsdatensatz(SchuelerleistungsDatensatz ds) {
        Schueler schueler = abitur.findeSchueler(ds.getNachname(), ds.getVorname(), ds.getGeburtsdatum());
        Lehrer lehrer = abitur.findeLehrer(ds.getLehrerkuerzel());
        Kurs kurs = abitur.findeOderErzeugeKurs(ds.getKursbezeichnung(), ds.getFach(), lehrer);
        Pruefung pruefung = abitur.findePruefung(schueler, ds.getAbiturfach());

        if (pruefung == null) {
           pruefung = new Pruefung(schueler, kurs, lehrer, ds.getAbiturfach());
            abitur.addPruefung(pruefung);
        } else {
            pruefung.setKurs(kurs);
        }
    }

    public void importiereLeistungsdaten(Path datei) throws IOException {
        LeistungsdatenImporter importer = new LeistungsdatenImporter();
        List<SchuelerleistungsDatensatz> datensaetze = importer.lese(datei);

        for (SchuelerleistungsDatensatz datensatz : datensaetze) {
            importiereLeistungsdatensatz(datensatz);
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