package de.abiturplanung.service;

import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Pruefung;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class PruefungsfolgenImportServide {


    public List<Pruefung> importiere(Abitur abitur, File datei) {
        List<Pruefung> geaendertePruefungen = new ArrayList<>();
        Map<Long, Pruefung> pruefungMap = new HashMap<>();

        for (Pruefung pruefung : abitur.getPruefungen()) {
            if (pruefung.getPruefungId() != null) {
                pruefungMap.put(pruefung.getPruefungId(), pruefung);
            }
        }

        try (FileInputStream inputStream = new FileInputStream(datei);
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheet("Prüfungsfolgen");

            if (sheet == null) {
                throw new IOException("Die Datei enthält kein Tabellenblatt 'Prüfungsfolgen'.");
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                Cell folgeCell = row.getCell(3);
                Cell idCell = row.getCell(4);

                long pruefungId = (long) idCell.getNumericCellValue();

                Pruefung pruefung = pruefungMap.get(pruefungId);

                if (pruefung == null) {
                    throw new IOException("Keine Prüfung mit der ID " + pruefungId + " gefunden.");
                }

                String pruefungsfolge = null;

                if (folgeCell != null) {
                    pruefungsfolge = folgeCell.getStringCellValue().trim().toUpperCase();

                    if (pruefungsfolge.isEmpty()) {
                        pruefungsfolge = null;
                    }
                }
                String alteFolge = pruefung.getPruefungsFolge();

                if (!Objects.equals(alteFolge, pruefungsfolge)) {
                    pruefung.setPruefungsFolge(pruefungsfolge);
                    geaendertePruefungen.add(pruefung);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    return geaendertePruefungen;
    }
}
