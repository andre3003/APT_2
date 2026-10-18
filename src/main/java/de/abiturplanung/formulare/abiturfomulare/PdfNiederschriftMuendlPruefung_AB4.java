package de.abiturplanung.formulare.abiturfomulare;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import de.abiturplanung.Utilities;
import de.abiturplanung.formulare.pdf.PdfUtilities;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Pruefung;

public class PdfNiederschriftMuendlPruefung_AB4 {

public static void erstelle (Document document, Abitur abitur, Pruefung pruefung) {
    String datum = Utilities.formatiereDatum(pruefung.getPruefungstag());
    String name = pruefung.getSchueler().getVorname() + " " + pruefung.getSchueler().getNachname();
    String pruefer = pruefung.getPruefer().getAnrede() + " " + pruefung.getPruefer().getNachname() + ", " + pruefung.getPruefer().getAmtsbezeichnung();
    String vorsitz;
    if (pruefung.getVorsitz() != null) {
        vorsitz = pruefung.getVorsitz().getAnrede() + " " + pruefung.getVorsitz().getNachname() + ", " + pruefung.getVorsitz().getAmtsbezeichnung();
    } else {
        vorsitz = "--";
    }
    String protokoll;
    if (pruefung.getSchriftfuehrer() != null) {
        protokoll = pruefung.getSchriftfuehrer().getAnrede() + " " + pruefung.getSchriftfuehrer().getNachname() + ", " + pruefung.getSchriftfuehrer().getAmtsbezeichnung();
    } else {
        protokoll = "--";
    }
    document.add(PdfUtilities.erstelleStempelkopf(abitur.getAbiturjahrgang()));

    Paragraph p1 = new Paragraph().add(new Text("Niederschrift der mündlichen Prüfung \n im 4. Fach\n")
                    .setFontSize(12)
                    .setBold()
                    .setTextAlignment(TextAlignment.LEFT))
            .add(new Text("(§ 38 und § 42 Absatz 4 APO-GOSt)")
                    .setFontSize(10));

    document.add(p1);
    document.add(new Paragraph("\n").setHeight(10));

    float[] columnWidth3 = {20, 110, 20, 110, 30, 30, 110, 30};
    Table datumUndZeit = new Table(columnWidth3);
    datumUndZeit.addCell(erzeugeDatenZelle("am: "));
    Cell datumCell = erzeugePlatzhalterZelle(1);
    datumCell.add(new Paragraph(" " + datum).setFontSize(11));
    datumUndZeit.addCell(datumCell);
    datumUndZeit.addCell(erzeugeDatenZelle("Beginn: "));
    datumUndZeit.addCell(erzeugePlatzhalterZelle(1));
    datumUndZeit.addCell(erzeugeDatenZelle("Uhr"));
    datumUndZeit.addCell(erzeugeDatenZelle("Schluss: "));
    datumUndZeit.addCell(erzeugePlatzhalterZelle(1));
    datumUndZeit.addCell(erzeugeDatenZelle("Uhr"));

    document.add(datumUndZeit);

    document.add(new Paragraph("\n").setHeight(6));

    Paragraph p2 = new Paragraph().add(new Text("Name des Schülers/der Schülerin: ").setBold().setFontSize(11)).add(new Text(name).setFontSize(11));

    document.add(p2);

    Paragraph p3 = new Paragraph("Fachprüfungsausschuss: ").setBold().setFontSize(11);

    document.add(p3);

    float[] spaltenBreite = {120, 400};
    Table fpa = new Table(spaltenBreite);
    fpa.setMarginLeft(70);

    Cell vorsitzCell = new Cell().add(new Paragraph("Vorsitzende/r¹:").setFontSize(11)).setBackgroundColor(ColorConstants.LIGHT_GRAY);

    Cell vorsitzNameCell = new Cell().add(new Paragraph(vorsitz).setFontSize(11));

    fpa.addCell(vorsitzCell);
    fpa.addCell(vorsitzNameCell);

    Cell protokollCell = new Cell().add(new Paragraph("Schriftführer/in¹:").setFontSize(11)).setBackgroundColor(ColorConstants.LIGHT_GRAY);
    Cell protokollName = new Cell().add(new Paragraph(protokoll).setFontSize(11));

    fpa.addCell(protokollCell);
    fpa.addCell(protokollName);

    Cell prueferCell = new Cell().add(new Paragraph("Fachprüfer/in¹:").setFontSize(11)).setBackgroundColor(ColorConstants.LIGHT_GRAY);
    Cell prueferNameCell = new Cell().add(new Paragraph(pruefer).setFontSize(11));

    fpa.addCell(prueferCell);
    fpa.addCell(prueferNameCell);

    document.add(new Paragraph("\n").setHeight(5));
    document.add(fpa);
    document.add(new Paragraph("\n").setHeight(5));
    Paragraph p4 = new Paragraph("Die Niederschrift muss den Prüfungsverlauf in seinen wesentlichen Zügen mit Teilergebnissen und Gesamtergebnis möglichst genau wiedergeben. " +
            "Sie muss Angaben zu folgenden Punkten enthalten:\n" +
            "______________________________________________________________________________________________").setFontSize(10);

    document.add(p4);

    Paragraph p5 = new Paragraph("1.\tAufgabe (ggf. Hinweis auf neuen Prüfungstermin gemäß § 38 Absatz1 Satz 4) \n" +
            "2.\t1. Prüfungsteil: Vortrag der Lösung der vorbereiteten Aufgabe (mit Zeitangabe) \n" +
            "3.\t2. Prüfungsteil: Gang des Prüfungsgespräches \n" +
            "4.\tNote (ggf. mit Tendenz)\n" +
            "5.\tBegründung der erteilten Note \n" +
            "6.\tStimmenverhältnis bei der Abstimmung \n" +
            "7.\tUnterschriften (mit Amtsbezeichnung):").setBold().setFontSize(10).setMultipliedLeading(1.8f);

    document.add(p5);

    Table fussnote = new Table(1);
    Cell fussnotenzelle = new Cell().add(new Paragraph("¹ mit Amtsbezeichnung").setFontSize(9))
            .setBorderBottom(Border.NO_BORDER)
            .setBorderLeft(Border.NO_BORDER)
            .setBorderRight(Border.NO_BORDER)
            .setBorderTop(new SolidBorder(1));
    fussnote.addCell(fussnotenzelle);
    fussnote.setMarginTop(200);
    document.add(fussnote);
}

    private static Cell erzeugeDatenZelle(String text) {
        return new Cell().add(new Paragraph(text)
                        .setFontSize(11))
                .setBorder(Border.NO_BORDER);
    }

    public static Cell erzeugePlatzhalterZelle(int breite) {
        return new Cell()
                .setBorderTop(Border.NO_BORDER)
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(breite));
    }
}
