package de.abiturplanung.formulare.abiturfomulare;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import de.abiturplanung.formulare.pdf.PdfUtilities;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Fachpruefungsausschuss;
import de.abiturplanung.model.Lehrer;

import java.util.List;

public class PdfListeFPAs {

    public static void erstelle(Document document, Abitur abitur, List<Fachpruefungsausschuss> fpas) {
        document.add(PdfUtilities.erstelleStempelkopf(abitur.getAbiturjahrgang()));

        Paragraph p1 = new Paragraph().add(new Text("Liste der Fachprüfungsausschüsse\n")
                .setBold()
                .setFontSize(12)).add(new Text("(Name und Amtsbezeichnung)").setFontSize(10));
        document.add(p1);

        document.add(new Paragraph("\n").setFontSize(5));

        Paragraph p2 = new Paragraph("4. Abiturfach").setBold().setFontSize(10);
        document.add(p2);

        float[] columnWidths2 = {20, 30, 35, 120, 30, 120, 30, 120, 30};
        Table table = new Table(columnWidths2);
        table.setWidth(UnitValue.createPercentValue(100));

        table.addCell(erzeugeSpaltenueberschrift("Nr.", 1));
        table.addCell(erzeugeSpaltenueberschrift("Fach", 1));
        table.addCell(erzeugeSpaltenueberschrift("Kurs", 1));
        table.addCell(erzeugeSpaltenueberschriftMitFussnote("Vorsitzende/r", "1", 2));
        table.addCell(erzeugeSpaltenueberschriftMitFussnote("Fachprüfer/in", "1", 2));
        table.addCell(erzeugeSpaltenueberschriftMitFussnote("Schriftführer/in", "1", 2));

        for (int i = 0; i < fpas.size(); i++) {
            Fachpruefungsausschuss fpa = fpas.get(i);
            table.addCell(erzeugeDatenZelle(String.valueOf(i+1)));
            table.addCell(erzeugeDatenZelle(fpa.fach()));
            table.addCell(erzeugeDatenZelle(fpa.kursart()));
            table.addCell(erzeugeDatenZelle(gibLehrerbezeichnung(abitur, fpa.vorsitz())));
            table.addCell(erzeugeDatenZelle(""));
            table.addCell(erzeugeDatenZelle(gibLehrerbezeichnung(abitur, fpa.pruefer())));
            table.addCell(erzeugeDatenZelle(""));
            table.addCell(erzeugeDatenZelle(gibLehrerbezeichnung(abitur, fpa.schriftfuehrer())));
            table.addCell(erzeugeDatenZelle(""));
        }

        document.add(table);

        Paragraph footnotes = new Paragraph()
                .add(new Text("1").setTextRise(4).setFontSize(6))
                .add(" mit Amtsbezeichnung\n")
                .add(new Text("2").setTextRise(4).setFontSize(6))
                .add(" Kenntnisnahme bitte bestätigen")
                .setFontSize(8)
                .setMarginTop(15)
                .setTextAlignment(TextAlignment.LEFT);

        Table signatureTable = new Table(1);
        signatureTable.setWidth(UnitValue.createPercentValue(40));
        signatureTable.setHorizontalAlignment(HorizontalAlignment.RIGHT);

        Cell lineCell = new Cell()
                .add(new Paragraph(" "))
                .setBorderBottom(new SolidBorder(1))
                .setBorderTop(Border.NO_BORDER)
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER)
                .setHeight(10);
        signatureTable.addCell(lineCell);

        Cell labelCell = new Cell()
                .add(new Paragraph("Vorsitzende/r des Zentralen Abiturausschusses").setFontSize(8))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(Border.NO_BORDER)
                .setPaddingTop(2);
        signatureTable.addCell(labelCell);

        Table abschluss = new Table(1);
        abschluss.setWidth(UnitValue.createPercentValue(100));
        abschluss.setKeepTogether(true);

        Cell abschlussZelle = new Cell().setBorder(Border.NO_BORDER);
        abschlussZelle.add(footnotes);
        abschlussZelle.add(signatureTable);

        abschluss.addCell(abschlussZelle);
        document.add(abschluss);
    }

//    public static Document generateFormularListOfFPAsExtended(Document document, Starter.AbiturPhase type, ArrayList<String[]> fpaData) {
//        document.add(PDFUtils.createStampHeader());
//
//        Paragraph p1 = new Paragraph().add(new Text("Liste der Fachprüfungsausschüsse\n")
//                .setBold()
//                .setFontSize(12)).add(new Text("(Name und Amtsbezeichnung)").setFontSize(10));
//        document.add(p1);
//
//        document.add(new Paragraph("\n").setFontSize(5));
//
//        Paragraph p2 = new Paragraph(type.getExtendedLabel()).setBold().setFontSize(10);
//        document.add(p2);
//
//        float[] columnWidths2 = {20, 30, 35, 110, 30, 110, 30, 110, 30, 60};
//        Table table = new Table(columnWidths2);
//        table.setWidth(UnitValue.createPercentValue(100));
//
//        table.addCell(createHeaderCell("Nr.", 1));
//        table.addCell(createHeaderCell("Fach", 1));
//        table.addCell(createHeaderCell("Kurs", 1));
//        table.addCell(createHeaderCellWithFootnote("Vorsitzende/r", "1", 2));
//        table.addCell(createHeaderCellWithFootnote("Fachprüfer/in", "1", 2));
//        table.addCell(createHeaderCellWithFootnote("Schriftführer/in", "1", 2));
//        table.addCell(createHeaderCell("Datum",1));
//
//        //Leeres Formular:
////        for (int i = 0; i < 15; i++) {
////            for (int j = 0; j < 9; j++) {
////                if (i == 0 && (j == 4 || j == 6 || j == 8)) {
////                    table.addCell(createCenteredFootnoteCell("2"));
////                } else {
////                    table.addCell(createDataCell(""));
////                }
////            }
////        }
//
//        for (int i = 0; i < fpaData.size(); i++) {
//            for (int j = 0; j < fpaData.get(i).length; j++) {
//                if (j == 0) {
//                    table.addCell(createDataCell(String.valueOf(i + 1)));
//                }
//                table.addCell(createDataCell(fpaData.get(i)[j]));
//            }
//        }
//
//        document.add(table);
//
//        Paragraph footnotes = new Paragraph()
//                .add(new Text("1").setTextRise(4).setFontSize(6))
//                .add(" mit Amtsbezeichnung\n")
//                .add(new Text("2").setTextRise(4).setFontSize(6))
//                .add(" Kenntnisnahme bitte bestätigen")
//                .setFontSize(8)
//                .setMarginTop(15)
//                .setTextAlignment(TextAlignment.LEFT);
//        document.add(footnotes);
//
//        Table signatureTable = new Table(1);
//        signatureTable.setWidth(UnitValue.createPercentValue(40));
//        signatureTable.setHorizontalAlignment(HorizontalAlignment.RIGHT);
//
//        Cell lineCell = new Cell()
//                .add(new Paragraph(" "))
//                .setBorderBottom(new SolidBorder(1))
//                .setBorderTop(Border.NO_BORDER)
//                .setBorderLeft(Border.NO_BORDER)
//                .setBorderRight(Border.NO_BORDER)
//                .setHeight(10);
//        signatureTable.addCell(lineCell);
//
//        Cell labelCell = new Cell()
//                .add(new Paragraph("Vorsitzende/r des Zentralen Abiturausschusses").setFontSize(8))
//                .setTextAlignment(TextAlignment.CENTER)
//                .setBorder(Border.NO_BORDER)
//                .setPaddingTop(2);
//        signatureTable.addCell(labelCell);
//
//        document.add(signatureTable);
//
//        return document;
//    }

    private static Cell erzeugeSpaltenueberschrift(String text, int colspan) {
        return new Cell(2, colspan)
                .add(new Paragraph(text).setFontSize(10))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setHeight(20f);
    }

    private static Cell erzeugeSpaltenueberschriftMitFussnote(String text, String footnote, int colspan) {
        Paragraph p = new Paragraph()
                .add(new Text(text).setFontSize(10))
                .add(new Text(footnote).setTextRise(4).setFontSize(6));
        return new Cell(2, colspan)
                .add(p)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setHeight(20f);
    }


    private static Cell erzeugeDatenZelle(String text) {
        return new Cell().add(new Paragraph(text).setFontSize(10).setHeight(20f));
    }

    private static String gibLehrerbezeichnung(Abitur abitur, String kuerzel) {
        Lehrer lehrer = abitur.findeLehrer(kuerzel);
        if (lehrer == null) return "---";
        return lehrer.getNachname() + (lehrer.getAmtsbezeichnung() != null ? ", " + lehrer.getAmtsbezeichnung() : "");
    }
}

