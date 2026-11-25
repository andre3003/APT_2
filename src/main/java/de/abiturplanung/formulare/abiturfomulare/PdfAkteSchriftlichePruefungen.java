package de.abiturplanung.formulare.abiturfomulare;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Kurs;
import de.abiturplanung.model.Lehrer;

import java.util.StringJoiner;


public class PdfAkteSchriftlichePruefungen {

    public static void erstelle(Document dokument, Abitur abitur, Kurs kurs) {
        erstelleDeckblatt(dokument, abitur, kurs);
        dokument.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        erstelleNotenuebersicht(dokument, abitur, kurs);
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
//        dokument.add(erzeugeKursdatenAbsatz("Fach:   ", "fach"));
        dokument.add(erzeugeKursdatenAbsatz("Kursart:   ", kursart));
//        dokument.add(erzeugeKursdatenAbsatz("Kursart:   ", "kursart"));
        dokument.add(erzeugeKursdatenAbsatz("Kursbezeichnung:   ", kursbezeichnung));
//        dokument.add(erzeugeKursdatenAbsatz("Kursbezeichnung:   ", "bezeichnung"));

        dokument.add(new Paragraph("\n").setHeight(80));

        // Namen der Korrektoren
        String fachlehrer = formatiereLehrer(kurs.getFachlehrer());

        StringJoiner kokorrektoren = new StringJoiner(" / ");
        if (kurs.getZweitkorrektor1() != null)
            kokorrektoren.add(formatiereLehrer(kurs.getZweitkorrektor1()));
        if (kurs.getZweitkorrektor2() != null)
            kokorrektoren.add(formatiereLehrer(kurs.getZweitkorrektor2()));

        // Unterschriften der Korrektoren
        Table unterschriften = new Table(UnitValue.createPercentArray(new float[]{250, 50, 250}));
        unterschriften.setWidth(UnitValue.createPercentValue(80));
        unterschriften.setHorizontalAlignment(HorizontalAlignment.CENTER);
        unterschriften.setBorder(Border.NO_BORDER);

        unterschriften.addCell(erzeugeUnterschriftszelle(fachlehrer, "Fachlehrer/in¹"));
        unterschriften.addCell(new Cell().setBorder(Border.NO_BORDER));
        unterschriften.addCell(erzeugeUnterschriftszelle(kokorrektoren.toString(), "Kokorrektor/in¹"));

        dokument.add(unterschriften);
        dokument.add(new Paragraph("\n").setHeight(60));

        // Unterschrift des ZAA-Vorsitzenden
//        String vorsitzender = formatiereLehrer(abitur.getZaaVorsitzender());
        String vorsitzender = "Dummy";

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
        return lehrer.getNachname() + ", " + lehrer.getAmtsbezeichnung();
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
    }


}
