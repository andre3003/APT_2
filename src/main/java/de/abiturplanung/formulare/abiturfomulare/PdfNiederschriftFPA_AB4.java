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
import de.abiturplanung.formulare.pruefungsplan.KommissionsGruppe;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Kurs;
import de.abiturplanung.model.Lehrer;

public class PdfNiederschriftFPA_AB4 {

    public static void erstelle(Document document, Abitur abitur, KommissionsGruppe kommission) {
        document.add(PdfUtilities.erstelleStempelkopf(abitur.getAbiturjahrgang()));
        Paragraph p1 = new Paragraph().add(new Text("Niederschrift über die Konferenz des Fachprüfungsausschusses\nim 4. Facn\n")
                .setBold()
                .setFontSize(12)).add(new Text("§ 27 Absatz 8, § 37 Absatz 4 APO-GOSt und Nummer 37.4.2, Nummer 37.4.3 und Nummer 37.4.4 VVzAPO-GOSt\n" +
                "sowie § 37 Absatz 5 APO-GOSt, § 38 Absatz 1, Absatz 3, Absatz 4; § 42 APO-GOSt").setFontSize(9));
        document.add(p1);
        float[] columnWidths2 = {130, 140, 130, 140}; // Anpassbar nach Wunsch
        Table tableMeta = new Table(UnitValue.createPointArray(columnWidths2));
        tableMeta.setWidth(UnitValue.createPercentValue(100));
        tableMeta.setMarginTop(5);
        tableMeta.setMarginBottom(5);

        tableMeta.addCell(erzeugeLabelZelle("Fach:"));
        tableMeta.addCell(erzeugePlatzhalterZelle(1));
        tableMeta.addCell(erzeugeLabelZelle("Kursart:"));
        tableMeta.addCell(erzeugePlatzhalterZelle(1));
        tableMeta.addCell(erzeugeLabelZelle("Datum der Konferenz:"));
        tableMeta.addCell(erzeugePlatzhalterZelle(1));
        tableMeta.addCell(erzeugeLabelZelle("Beginn der Konferenz:"));
        tableMeta.addCell(erzeugePlatzhalterZelle(1));

        //Daten einfügen:
        Kurs kurs = kommission.pruefungen().get(0).getKurs();
        tableMeta.getCell(0,1).add(new Paragraph(kurs.getFach().getBezeichnung()).setFontSize(11));
        tableMeta.getCell(0,3).add(new Paragraph(kurs.getKursart().toString()).setFontSize(11));
        document.add(tableMeta);

        Paragraph p2 = new Paragraph("Anwesend:").setFontSize(11);
        p2.setMarginTop(5);

        document.add(p2);

        float[] columnWidths3 = {120, 320};
        Table fpaTable = new Table(UnitValue.createPointArray(columnWidths3));
        fpaTable.setWidth(UnitValue.createPercentValue(60));
        fpaTable.setHorizontalAlignment(HorizontalAlignment.LEFT);
        fpaTable.setMarginLeft(80);
        fpaTable.setMarginBottom(15);

        fpaTable.addCell(erzeugeLabelZelle2("Vorsitzende/r:"));
        fpaTable.addCell(new Cell());

        fpaTable.addCell(erzeugeLabelZelle2("Fachprüfer/in:"));
        fpaTable.addCell(new Cell());

        fpaTable.addCell(erzeugeLabelZelle2("Schriftführer/in:"));
        fpaTable.addCell(new Cell());

        //Daten einfügen:

        Lehrer lehrer = abitur.findeLehrer(kommission.vorsitz());
        String text = lehrer != null ?  lehrer.gibVollstaendigenNamen() : "---";
        fpaTable.getCell(0,1).add(new Paragraph(text).setFontSize(11));

        lehrer = abitur.findeLehrer(kommission.pruefer());
        text = lehrer != null ?  lehrer.gibVollstaendigenNamen() : "---";
        fpaTable.getCell(1,1).add(new Paragraph(text).setFontSize(11));

        lehrer = abitur.findeLehrer(kommission.schriftfuehrer());
        text = lehrer != null ?  lehrer.gibVollstaendigenNamen() : "---";
        fpaTable.getCell(2,1).add(new Paragraph(text).setFontSize(11));

        document.add(fpaTable);

        float[] columnWidths4 = {20, 800};
        Table paragraphTable = new Table(columnWidths4);
        paragraphTable.addCell(new Cell().add(new Paragraph("1.").setFontSize(10)).setBorder(Border.NO_BORDER));
        paragraphTable.addCell(erzeugeParagraphZelle("Die/Der Vorsitzende hat die Anwesenden auf ihre Verschwiegenheitspflicht über alle Prüfungsvorgänge hingewiesen. (§ 27 Absatz 8 APO-GOSt)"));
        paragraphTable.addCell(new Cell().add(new Paragraph("2.").setFontSize(10)).setBorder(Border.NO_BORDER));
        paragraphTable.addCell(erzeugeParagraphZelle("Die/Der Vorsitzende hat die Anwesenden über den Leistungsstand der Prüflinge informiert. (Nummer 37.4.3 VVzAPO-GOSt)"));
        paragraphTable.addCell(new Cell().add(new Paragraph("3.").setFontSize(10)).setBorder(Border.NO_BORDER));
        paragraphTable.addCell(erzeugeParagraphZelle("Die Fachprüferin/Der Fachprüfer hat jedem Mitglied des Fachprüfungsausschusses alle Prüfungsaufgaben und den Erwartungshorizont ausgehändigt. Sie/Er hat erläutert, welche inhaltlichen und methodischen Voraussetzungen die Prüflinge für die Lösungen der Aufgaben aus dem Unterricht mitbringen müssen. (Nummer 37.4.4 VVzAPO-GOSt) Die unterrichtlichen Voraussetzungen von Wiederholerinnen und Wiederholern wurden geprüft."));
        paragraphTable.addCell(new Cell().add(new Paragraph("4.").setFontSize(10)).setBorder(Border.NO_BORDER));
        paragraphTable.addCell(erzeugeParagraphZelle("Bei den vorgelegten Aufgaben handelt es sich um eine neue, begrenzte Aufgabe, die sich nicht auf das Sachgebiet eines Kurshalbjahres beschränkt. Sie stellt keine Wiederholung der Inhalte einer anderen in der Qualifikationsphase und im Abiturbereich bereits erbrachten Leistung dar. (§ 38 Absatz 1 und 3)"));
        paragraphTable.addCell(new Cell().add(new Paragraph("5.").setFontSize(10)).setBorder(Border.NO_BORDER));
        paragraphTable.addCell(erzeugeParagraphZelle("Die Fachprüferin/Der Fachprüfer hat eine Ersatzaufgabe mit Erwartungshorizont vorbereitet und dem Prüfungsausschuss vorgelegt. (§ 38 Absatz 1 Satz 4 APO-GOSt)"));
        paragraphTable.addCell(new Cell().add(new Paragraph("6.").setFontSize(10)).setBorder(Border.NO_BORDER));
        paragraphTable.addCell(erzeugeParagraphZelle("Die/Der Vorsitzende hat nach Beratung des Ausschusses entschieden, welche Änderungen in den Prüfungsaufgaben²/im Erwartungshorizont² vorzunehmen sind/dass eine neue Prüfungsaufgabe vorgelegt werden muss.² Der Fachprüfungsausschuss tagt – im Falle einer Neukonzeption – erneut:"));

        document.add(paragraphTable);

        float[] dateTimeCols = {30, 80, 30, 80, 150}; // flexibel anpassbar
        Table dateTimeTable = new Table(UnitValue.createPointArray(dateTimeCols));
        dateTimeTable.setHorizontalAlignment(HorizontalAlignment.LEFT);
        dateTimeTable.setMarginBottom(10);

        dateTimeTable.addCell(erzeugeLabelZelle("Datum:"));
        dateTimeTable.addCell(erzeugePlatzhalterZelle(1));
        dateTimeTable.addCell(erzeugeLabelZelle("Uhrzeit:"));
        dateTimeTable.addCell(erzeugePlatzhalterZelle(1));
        dateTimeTable.addCell(new Cell().add(new Paragraph("² (§ 37 Absatz 4 APO-GOSt)").setFontSize(8)).setBorder(Border.NO_BORDER));
        dateTimeTable.setMarginLeft(17);

        document.add(dateTimeTable);

        Table endTable = new Table(UnitValue.createPercentArray(new float[]{30, 50, 30}));
        endTable.setHorizontalAlignment(HorizontalAlignment.LEFT);
        endTable.setMarginBottom(10);
        endTable.setMarginTop(10);
        endTable.addCell(erzeugeLabelZelle("Ende der Konferenz:"));
        endTable.addCell(erzeugePlatzhalterZelle(1));
        endTable.addCell(new Cell().add(new Paragraph("Uhr").setFontSize(11)).setBorder(Border.NO_BORDER));
        document.add(endTable);

        float[] columnWidth4 = {200, 200, 200};
        Table signatureTable = new Table(UnitValue.createPointArray(columnWidth4));
        signatureTable.setWidth(UnitValue.createPercentValue(100));
        signatureTable.setMarginTop(25);

        for (int i = 0; i < 3; i++) {
            signatureTable.addCell(erzeugePlatzhalterZelle(1).setMinHeight(10));
        }

        signatureTable.addCell(erzeugeLabelZelle("Vorsitzende/r"));
        signatureTable.addCell(erzeugeLabelZelle("Fachprüfer/in"));
        signatureTable.addCell(erzeugeLabelZelle("Schriftführer/in"));
        document.add(signatureTable);

        Paragraph footnotes = new Paragraph()
                .setFontSize(8)
                .setMarginTop(15)
                .add(new Text("¹ mit Amtsbezeichnung\n"))
                .add(new Text("² Nicht-Zutreffendes bitte streichen"));

        document.add(footnotes);
    }

    private static Cell erzeugeDatenZelle(String text) {
        return new Cell().add(new Paragraph(text)
                        .setFontSize(11))
                .setBorder(Border.NO_BORDER);
    }

    private static Cell erzeugePlatzhalterZelle(int breite) {
        return new Cell()
                .setBorderTop(Border.NO_BORDER)
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(breite));
    }

    private static Cell erzeugeLabelZelle(String text) {
        return new Cell()
                .add(new Paragraph(text).setFontSize(11))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    private static Cell erzeugeParagraphZelle(String text) {
        return new Cell().add(new Paragraph(text)
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.JUSTIFIED))
                .setBorder(Border.NO_BORDER);
    }

    private static Cell erzeugeLabelZelle2(String text) {
        return new Cell()
                .add(new Paragraph(text).setFontSize(11))
                .setTextAlignment(TextAlignment.LEFT)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }


}
