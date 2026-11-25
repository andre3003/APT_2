package de.abiturplanung.formulare.klausurraum;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import de.abiturplanung.Utilities;
import de.abiturplanung.formulare.pdf.PdfUtilities;
import de.abiturplanung.gui.planung.RaumbelegungAbiturklausuren;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Kurs;
import de.abiturplanung.model.Pruefung;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

public class PdfSitzplanKlausurraum {

    public static void erstelle(Abitur abitur, RaumbelegungAbiturklausuren belegung, File datei) {
        String pfad = datei.getAbsolutePath();
        try (PdfWriter writer = new PdfWriter(pfad); PdfDocument pdf = new PdfDocument(writer); Document document = new Document(pdf, PageSize.A4);){
            document.add(PdfUtilities.erstelleStempelkopf(abitur.getAbiturjahrgang()));
            Paragraph p1 = new Paragraph("Anwesenheitsliste und Liste der Klausurplätze").setBold().setUnderline();
            document.add(p1);
            float[] spaltenBreiten = {60, 70, 50, 60, 50, 200};
            Table tabelle = new Table(UnitValue.createPercentArray(spaltenBreiten));
            tabelle.addCell(erzeugeDatenzelle("Datum: "));
            tabelle.addCell(erzeugePlatzhalterzelle());
            tabelle.addCell(erzeugeDatenzelle("Raum: "));
            tabelle.addCell(erzeugePlatzhalterzelle());
            tabelle.addCell(erzeugeDatenzelle("Kurs(e): "));
            tabelle.addCell(erzeugePlatzhalterzelle());
            String datum = Utilities.formatiereDatum(belegung.getPruefungstag().getDatum());
            String raum = belegung.getRaum().getBezeichnung();
            List<Kurs> kurseListe = new ArrayList<>(belegung.getKurse());
            kurseListe.sort(Comparator.comparing(Kurs::getBezeichnung, String.CASE_INSENSITIVE_ORDER));
            StringJoiner kurse = new StringJoiner(", ");
            for (Kurs k : kurseListe) {
                kurse.add(k.getBezeichnung());
            }
            tabelle.getCell(0, 1).add(new Paragraph(datum));
            tabelle.getCell(0, 3).add(new Paragraph(raum));
            tabelle.getCell(0, 5).add(new Paragraph(kurse.toString()));
            document.add(tabelle);

            document.add(new Paragraph("\n").setFontSize(5));
            Paragraph p2 = new Paragraph("Jeder Platz im Prüfungsraum hat eine feste Platznummer. Die Schülerinnen und Schüler setzen sich an die Plätze, die ihnen zugewiesen sind. Die Platznummer ist in der folgenden Liste ersichtlich.");
            document.add(p2);
            document.add(new Paragraph("\n").setFontSize(5));

            float[] columnWidths2 = {40, 160, 160, 60, 70};
            Table datenTabelle = new Table(columnWidths2);
            datenTabelle.addHeaderCell(erzeugeSpaltenkopf("Platz-Nr."));
            datenTabelle.addHeaderCell(erzeugeSpaltenkopf("Name"));
            datenTabelle.addHeaderCell(erzeugeSpaltenkopf("Vorname"));
            datenTabelle.addHeaderCell(erzeugeSpaltenkopf("Kurs"));
            datenTabelle.addHeaderCell(erzeugeSpaltenkopf("Fachlehrer"));
            List<Pruefung> pruefungen = new ArrayList<>(abitur.gibSchriftlichePruefungenEinerRaumbelegung(belegung));
            pruefungen.sort(Comparator.comparing((Pruefung p) -> p.getSchueler().getNachname(), String.CASE_INSENSITIVE_ORDER).
                    thenComparing(p -> p.getSchueler().getVorname(), String.CASE_INSENSITIVE_ORDER));

            int nr = 1;
            for (Pruefung pruefung : pruefungen) {
                String name = pruefung.getSchueler().getNachname();
                String vorname = pruefung.getSchueler().getVorname();
                String kurs = pruefung.getKurs().getBezeichnung();
                String lehrer = pruefung.getKurs().getFachlehrer().getKuerzel();
                datenTabelle.addCell(new Paragraph(Integer.toString(nr++)));
                datenTabelle.addCell(new Paragraph(name));
                datenTabelle.addCell(new Paragraph(vorname));
                datenTabelle.addCell(new Paragraph(kurs));
                datenTabelle.addCell(new Paragraph(lehrer));
            }
            document.add(datenTabelle);
        } catch (Exception e) {
            throw new RuntimeException("Die Anwesenheitsliste konnte nicht erstellt werden.", e);
        }
    }

    private static Cell erzeugeSpaltenkopf(String text) {
        return new Cell().add(new Paragraph(text).setBold().setFontSize(10))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    private static Cell erzeugeDatenzelle(String text) {
        return new Cell().add(new Paragraph(text).setBold())
                .setBorder(Border.NO_BORDER);
    }

    private static Cell erzeugePlatzhalterzelle() {
        Cell cell = new Cell();
        cell.setBorderTop(Border.NO_BORDER);
        cell.setBorderLeft(Border.NO_BORDER);
        return cell.setBorderRight(Border.NO_BORDER);
    }


}





