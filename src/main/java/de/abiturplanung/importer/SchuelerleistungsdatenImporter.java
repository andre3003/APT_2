package de.abiturplanung.importer;

import de.abiturplanung.model.Abitur;
import de.abiturplanung.service.ImportService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SchuelerleistungsdatenImporter {

    public Abitur importiere(Path datei) throws IOException {
        List<String> zeilen = Files.readAllLines(
                datei,
                StandardCharsets.UTF_8
        );

        Abitur abitur = new Abitur();
        ImportService importService = new ImportService(abitur);

        for (int i = 1; i < zeilen.size(); i++) {
            String zeile = zeilen.get(i);

            if (zeile.isBlank()) {
                continue;
            }

            SchuelerleistungsDatensatz datensatz = parse(zeile);
            importService.verarbeite(datensatz);
        }

        return abitur;
    }

    public void importiereLeistungsdaten(Path datei) throws IOException {

        List<String> zeilen = leseDatei(datei);

        for (int i = 1; i < zeilen.size(); i++) {

            String zeile = zeilen.get(i);

            if (zeile.isBlank()) {
                continue;
            }

            SchuelerleistungsDatensatz datensatz =
                    parseLeistungsZeile(zeile);

            // nächste Ausbaustufe

        }
    }

    private List<String> leseDatei(Path datei) throws IOException {

        return Files.readAllLines(datei, StandardCharsets.UTF_8);

    }
}