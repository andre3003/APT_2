package de.abiturplanung.service;

import de.abiturplanung.importer.LehrerDatensatz;
import de.abiturplanung.importer.LehrerImporter;
import de.abiturplanung.importer.SchuelerDatensatz;
import de.abiturplanung.importer.SchuelerImporter;
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
            Lehrer lehrer = findeLehrer(datensatz.getKuerzel());
            if (lehrer == null) {
                lehrer = new Lehrer(datensatz.getKuerzel());
                abitur.getLehrer().add(lehrer);
            }
            lehrer.aktualisiereStammdaten(datensatz);
        }
    }


    public void importiereSchueler(Path datei) throws IOException {

        SchuelerImporter importer = new SchuelerImporter();

        List<SchuelerDatensatz> datensaetze = importer.lese(datei);

        for (SchuelerDatensatz datensatz : datensaetze) {

            Schueler schueler = findeSchueler(datensatz.getSchildId());

            if (schueler == null) {

                schueler = new Schueler(datensatz.getSchildId());

                abitur.getSchueler().add(schueler);

            }
            schueler.aktualisiereStammdaten(datensatz);
        }
    }

    private Schueler findeSchueler(String schildId) {
        for (Schueler schueler : abitur.getSchueler()) {
            if (schueler.getSchildId().equals(schildId)) {
                return schueler;
            }
        }
        return null;
    }

    private Lehrer findeLehrer(String kuerzel) {
        for (Lehrer lehrer : abitur.getLehrer()) {
            if (lehrer.getKuerzel().equals(kuerzel)) {
                return lehrer;
            }
        }
        return null;
    }
}