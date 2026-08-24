package de.abiturplanung.service;

import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Abiturfach;
import de.abiturplanung.model.Pruefung;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class PruefungsfolgenExportService {

    public void exportiere(Abitur abitur, File datei) throws IOException {
        List<Pruefung> pruefungen = abitur.getPruefungen().stream()
                .filter(p -> p.getAbiturfach() == Abiturfach.AB4)
                .sorted(Comparator
                        .comparing((Pruefung p) -> p.getPruefer() == null ? "" : p.getPruefer().getKuerzel())
                        .thenComparing(p -> p.getKurs().getBezeichnung())
                        .thenComparing(p -> p.getSchueler().getNachname())
                        .thenComparing(p -> p.getSchueler().getVorname()))
                .toList();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Prüfungsfolgen");

            CellStyle kopfStyle = erstelleKopfStyle(workbook);
            CellStyle normalStyle = erstelleNormalStyle(workbook);
            CellStyle editierbarStyle = erstelleEditierbarStyle(workbook);
            CellStyle gruppenStartStyle = erstelleGruppenStartStyle(workbook);
            CellStyle gruppenStartEditierbarStyle = erstelleGruppenStartEditierbarStyle(workbook);

            erzeugeKopfzeile(sheet, kopfStyle);

            String letzterPruefer = null;
            int zeilennummer = 1;

            for (Pruefung pruefung : pruefungen) {
                String pruefer = pruefung.getPruefer() == null ? "" : pruefung.getPruefer().getKuerzel();
                boolean neuerPruefer = !pruefer.equals(letzterPruefer);

                Row row = sheet.createRow(zeilennummer++);

                Cell prueferCell = row.createCell(0);
                prueferCell.setCellValue(neuerPruefer ? pruefer : "");

                Cell schuelerCell = row.createCell(1);
                schuelerCell.setCellValue(pruefung.getSchueler().getNachname() + ", " + pruefung.getSchueler().getVorname());

                Cell kursCell = row.createCell(2);
                kursCell.setCellValue(pruefung.getKurs().getBezeichnung());

                Cell folgeCell = row.createCell(3);
                folgeCell.setCellValue(pruefung.getPruefungsFolge() == null ? "" : pruefung.getPruefungsFolge());

                Cell idCell = row.createCell(4);
                if (pruefung.getPruefungId() != null) {
                    idCell.setCellValue(pruefung.getPruefungId());
                }

                prueferCell.setCellStyle(neuerPruefer ? gruppenStartStyle : normalStyle);
                schuelerCell.setCellStyle(neuerPruefer ? gruppenStartStyle : normalStyle);
                kursCell.setCellStyle(neuerPruefer ? gruppenStartStyle : normalStyle);
                folgeCell.setCellStyle(neuerPruefer ? gruppenStartEditierbarStyle : editierbarStyle);
                idCell.setCellStyle(neuerPruefer ? gruppenStartStyle : normalStyle);

                letzterPruefer = pruefer;
            }

            sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0, pruefungen.size(), 0, 4));
            sheet.createFreezePane(0, 1);

            sheet.setColumnWidth(0, 10 * 256);
            sheet.setColumnWidth(1, 32 * 256);
            sheet.setColumnWidth(2, 15 * 256);
            sheet.setColumnWidth(3, 18 * 256);
            sheet.setColumnWidth(4, 12 * 256);

            sheet.protectSheet("APT");
//            sheet.lockAutoFilter(false);
//            sheet.lockSort(false);

            try (FileOutputStream outputStream = new FileOutputStream(datei)) {
                workbook.write(outputStream);
            }
        }
    }

    private void erzeugeKopfzeile(Sheet sheet, CellStyle style) {
        Row row = sheet.createRow(0);
        String[] spalten = {"Prüfer", "Schüler", "Kurs", "Prüfungsfolge", "P-ID"};

        for (int i = 0; i < spalten.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(spalten[i]);
            cell.setCellStyle(style);
        }
    }

    private CellStyle erstelleKopfStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);

        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setLocked(true);

        return style;
    }

    private CellStyle erstelleNormalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setLocked(true);
        return style;
    }

    private CellStyle erstelleEditierbarStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setLocked(false);
        return style;
    }

    private CellStyle erstelleGruppenStartStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setLocked(true);
        return style;
    }

    private CellStyle erstelleGruppenStartEditierbarStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setLocked(false);
        return style;
    }
}