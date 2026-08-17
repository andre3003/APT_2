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
            lehrer.aktualisiereStammdaten(datensatz);
        }
    }

    public void importiereSchueler(Path datei) throws IOException {
        SchuelerImporter importer = new SchuelerImporter();
        List<SchuelerDatensatz> datensaetze = importer.lese(datei);
        for (SchuelerDatensatz datensatz : datensaetze) {
            Schueler schueler = abitur.findeSchueler(datensatz.getNachname(), datensatz.getVorname(), datensatz.getGeburtsdatum());
            if (schueler == null) {
                schueler = new Schueler(datensatz.getSchildId());
                abitur.addSchueler(schueler);
            }
            schueler.aktualisiereStammdaten(datensatz);
        }
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