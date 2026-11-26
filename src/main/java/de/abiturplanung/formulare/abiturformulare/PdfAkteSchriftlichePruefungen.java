package de.abiturplanung.formulare.abiturformulare;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import de.abiturplanung.model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

public class PdfAkteSchriftlichePruefungen {

    public static void erstelle(Document dokument, Abitur abitur, Kurs kurs) {
        dokument.setLeftMargin(30f);
        dokument.setRightMargin(30f);
        erstelleDeckblatt(dokument, abitur, kurs);
        dokument.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        erstelleNotenuebersicht(dokument, abitur, kurs);
        erstelleNotenspiegel(dokument);
    }

    private static void erstelleDeckblatt(Document dokument, Abitur abitur, Kurs kurs) {
        String fach = kurs.getFach().getBezeichnung() == null ? "--" : kurs.getFach().getBezeichnung();
        String kursart = kurs.getKursart() == null ? "--" : kurs.getKursart().toString();
        String kursbezeichnung = kurs.getBezeichnung();

        // Überschriften
        dokument.add(new Paragraph("\n").setHeight(90));
        dokument.add(new Paragraph("ABITURPRÜFUNG " + abitur.getAbiturjahrgang()).setFontSize(18).setBold().setTextAlignment(TextAlignment.CENTER));

        dokument.add(new Paragraph("\n").setHeight(20));
        dokument.add(new Paragraph("Schriftliche Prüfungsarbeiten").setFontSize(16).setBold().setTextAlignment(TextAlignment.CENTER));

        dokument.add(new Paragraph("\n").setHeight(40));

        // Kursdaten
        dokument.add(erzeugeKursdatenAbsatz("Fach:   ", fach));
        dokument.add(erzeugeKursdatenAbsatz("Kursart:   ", kursart));
        dokument.add(erzeugeKursdatenAbsatz("Kursbezeichnung:   ", kursbezeichnung));

        dokument.add(new Paragraph("\n").setHeight(80));

        // Namen der Korrektoren


//        StringJoiner kokorrektoren = new StringJoiner(" / ");
//        if (kurs.getZweitkorrektor1() != null)
//            kokorrektoren.add(formatiereLehrer(kurs.getZweitkorrektor1()));


        // Unterschriften der Korrektoren
        Table unterschriften = new Table(UnitValue.createPercentArray(new float[]{250, 50, 250}));
        unterschriften.setWidth(UnitValue.createPercentValue(80));
        unterschriften.setHorizontalAlignment(HorizontalAlignment.CENTER);
        unterschriften.setBorder(Border.NO_BORDER);

        String fachlehrer = formatiereLehrer(kurs.getFachlehrer());
        unterschriften.addCell(erzeugeUnterschriftszelle(fachlehrer, "Fachlehrer/in¹"));
        unterschriften.addCell(new Cell().setBorder(Border.NO_BORDER));
        String kokorrektor = formatiereLehrer(kurs.getZweitkorrektor1());
//        System.out.println(kokorrektor);
        unterschriften.addCell(erzeugeUnterschriftszelle(kokorrektor, "Kokorrektor/in¹"));

        dokument.add(unterschriften);
        dokument.add(new Paragraph("\n").setHeight(60));

        // Unterschrift des ZAA-Vorsitzenden
        String vorsitzender = formatiereLehrer(abitur.getSchuldaten().getZaaVorsitzender());

        dokument.add(new Paragraph("_____________________________________")
                .setTextAlignment(TextAlignment.CENTER));
        dokument.add(new Paragraph(vorsitzender + " (Vorsitzender des Zentralen Abiturausschusses¹)")
                .setFontSize(8).setTextAlignment(TextAlignment.CENTER));

        dokument.add(new Paragraph("\n").setHeight(60));

        // Fußnote
        dokument.add(new Paragraph("____________________"));
        dokument.add(new Paragraph("¹ mit Amtsbezeichnung").setFontSize(8));
    }

    private static Paragraph erzeugeKursdatenAbsatz(String bezeichnung, String wert) {
        return new Paragraph()
                .add(new Text(bezeichnung).setBold())
                .add(new Text(wert))
                .setFontSize(14)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginLeft(120);
    }

    private static String formatiereLehrer(Lehrer lehrer) {
        if (lehrer == null) return "";
        String amt = lehrer.getAmtsbezeichnung() == null ? "" : lehrer.getAmtsbezeichnung().toString();
        return lehrer.getNachname() + (amt.isEmpty() ? "" : ", " + amt);
    }

    private static Cell erzeugeUnterschriftszelle(String name, String funktion) {
        Cell zelle = new Cell().setBorder(Border.NO_BORDER);

        zelle.add(new Paragraph("_____________________________")
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5));

        zelle.add(new Paragraph(name + " (" + funktion + ")")
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER));
        return zelle;
    }

    private static void erstelleNotenuebersicht(Document dokument, Abitur abitur, Kurs kurs) {
        float[] columnWidths2 = {30, 180, 70, 80, 3, 30, 180, 70, 80};
        Table notentabelle = new Table(columnWidths2);
        notentabelle.setWidth(UnitValue.createPercentValue(100));
        notentabelle.addHeaderCell(erzeugeSpaltekhopf("Nr."));
        notentabelle.addHeaderCell(erzeugeSpaltekhopf("Name (alphabetisch)"));
        notentabelle.addHeaderCell(erzeugeSpaltekhopf("Note (mit Tendenz)"));
        notentabelle.addHeaderCell(erzeugeSpaltekhopf("Seite"));
        Cell separator = new Cell(0, 1).setBorder(new SolidBorder(0.5f));
        separator.setBackgroundColor(ColorConstants.BLACK);
        notentabelle.addHeaderCell(separator);

        notentabelle.addHeaderCell(erzeugeSpaltekhopf("Nr."));
        notentabelle.addHeaderCell(erzeugeSpaltekhopf("Name (alphabetisch)"));
        notentabelle.addHeaderCell(erzeugeSpaltekhopf("Note (mit Tendenz)"));
        notentabelle.addHeaderCell(erzeugeSpaltekhopf("Seite"));

        List<Pruefung> pruefungen = new ArrayList<>(abitur.ermittelechriftlichePruefungenEinesKurses(kurs));
        pruefungen.sort(Comparator.comparing((Pruefung p) -> p.getSchueler().getNachname()).thenComparing(p -> p.getSchueler().getVorname()));

        for (int i = 0; i < 20; i++) {
            // Linke Tabellenhälfte
            notentabelle.addCell(erzeugeDatenZelle(Integer.toString(i + 1)));
            notentabelle.addCell(erzeugeNamenszelle(pruefungen, i));
            notentabelle.addCell(erzeugeDatenZelle(""));
            notentabelle.addCell(erzeugeDatenZelle(""));

            // Trennspalte
            notentabelle.addCell(erzeugeTrennzelle());

            // Rechte Tabellenhälfte
            notentabelle.addCell(erzeugeDatenZelle(Integer.toString(i + 21)));
            notentabelle.addCell(erzeugeNamenszelle(pruefungen, i + 20));
            notentabelle.addCell(erzeugeDatenZelle(""));
            notentabelle.addCell(erzeugeDatenZelle(""));
        }
        dokument.add(notentabelle);
        dokument.add(new Paragraph("\n").setHeight(60));
        if (pruefungen.size() > 40)
            throw new IllegalStateException("Der Kurs " + kurs.getBezeichnung() + " hat mehr als 40 Klausurschreiber.");
    }

    private static void erstelleNotenspiegel(Document dokument) {
        Table notenSpiegel = new Table(UnitValue.createPercentArray(16)); //createPercentArray erzeugt 16 gleich große Spalten
        notenSpiegel.setWidth(UnitValue.createPercentValue(100));

        String[] spaltenBezeichner = {"+", "1", "-", "+", "2", "-", "+", "3", "-", "+", "4", "-", "+", "5", "-", "6"};
        for (String header : spaltenBezeichner) {
            notenSpiegel.addCell(erzeugeSpaltekhopf(header));
        }
        for (int i = 0; i < 2 * spaltenBezeichner.length; i++) {
            notenSpiegel.addCell(erzeugeDatenZelle("").setHeight(15f));
        }
        dokument.add(notenSpiegel);
    }

    private static Cell erzeugeSpaltekhopf(String text) {
        return new Cell().add(new Paragraph(text).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(11);
    }

    private static Cell erzeugeDatenZelle(String text) {
        return new Cell().add(new Paragraph(text))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(new SolidBorder(0.5f))
                .setHeight(22);
    }

    private static Cell erzeugeNamenszelle(List<Pruefung> pruefungen, int index) {
        String name = index < pruefungen.size() ? pruefungen.get(index).getSchueler().getKurzname(20) : "";
        return erzeugeDatenZelle(name).setTextAlignment(TextAlignment.LEFT);
    }

    private static Cell erzeugeTrennzelle() {
        return new Cell().setBorder(new SolidBorder(0.5f)).setBackgroundColor(ColorConstants.BLACK);
    }
}
