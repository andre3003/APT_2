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
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import de.abiturplanung.Utilities;
import de.abiturplanung.formulare.pdf.PdfUtilities;
import de.abiturplanung.formulare.pruefungsplan.KommissionsGruppe;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Pruefung;
import java.util.List;

public class PdfHinweiseVorsitzFPA_AB4 {

    public static Document erstelle(Document document, Abitur abitur, KommissionsGruppe gruppe) {
        String fach = gruppe.pruefungen().get(0).getKurs().getFach().getBezeichnung();
        String kurs = gruppe.pruefungen().get(0).getKurs().getBezeichnung();
        String datum = Utilities.formatiereDatum(gruppe.pruefungen().get(0).getPruefungstag());
        String vorsitz = gruppe.pruefungen().get(0).getVorsitz().getNachname();
        String pruefer = gruppe.pruefungen().get(0).getPruefer().getNachname();
        String protokoll = gruppe.pruefungen().get(0).getSchriftfuehrer().getNachname();
        String pruefungsraum = gruppe.pruefungen().get(0).getRaum().getBezeichnung();

        String vorbereitungsraum = "MLB1";
        document.add(PdfUtilities.erstelleStempelkopf(abitur.getAbiturjahrgang()));

        Paragraph p1 = new Paragraph().add(new Text("Informationen für die/den Vorsitzende/n des \nFachprüfungsausschusses im 4. Fach\n")
                .setBold()
                .setFontSize(12)).add(new Text("Nummer 37.4.3 VVzAPO-GOSt").setFontSize(10));
        document.add(p1);

        Paragraph p2 = new Paragraph().add(new Text("und Übersicht über die Prüfungsergebnisse")
                        .setBold()
                        .setFontSize(12)).add(new Text("1").setTextRise(6).setFontSize(6))
                .setMarginBottom(10);
        document.add(p2);

        float[] columnWidths2 = {120, 200, 120, 200, 120, 200};
        Table infoTabelle = new Table(columnWidths2);
        infoTabelle.setWidth(UnitValue.createPercentValue(100));

// Spalte 1: Fach
        infoTabelle.addCell(new Cell().add(new Paragraph("Fach:").setBold())
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        infoTabelle.addCell(new Cell().add(new Paragraph(fach)) // Platzhalter für Fach
                .setBorder(Border.NO_BORDER));

        infoTabelle.addCell(new Cell().add(new Paragraph("Kursbezeichnung:").setBold())
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        infoTabelle.addCell(new Cell().add(new Paragraph(kurs)) // Platzhalter für Kursbezeichnung
                .setBorder(Border.NO_BORDER));

        infoTabelle.addCell(new Cell().add(new Paragraph("Prüfungsdatum:").setBold())
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
        infoTabelle.addCell(new Cell().add(new Paragraph(datum))
                .setBorder(Border.NO_BORDER));

        document.add(infoTabelle);

        document.add(new Paragraph("\n").setFontSize(10));

        float[] columnWidths3 = {120, 150, 120, 150};
        Table kommissionsTabelle = new Table(columnWidths3);
        kommissionsTabelle.setWidth(UnitValue.createPercentValue(100));

        kommissionsTabelle.addCell(erstelleTabellenZelle("Prüfungsausschuss", false, 10, true, true));
        kommissionsTabelle.addCell(erstelleTabellenZelle("", false, 10, false, false));
        kommissionsTabelle.addCell(erstelleTabellenZelle("Räume", false, 10, true, true));
        kommissionsTabelle.addCell(erstelleTabellenZelle("", false, 10, false, false));

        kommissionsTabelle.addCell(erstelleTabellenZelle("Vorsitzende/r:", true, 12, false, false).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        kommissionsTabelle.addCell(erstelleTabellenZelle(vorsitz, true, 12, false, false));
        kommissionsTabelle.addCell(erstelleTabellenZelle("Aufenthaltsraum:", true, 12, false, false).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        kommissionsTabelle.addCell(erstelleTabellenZelle("Lernflur", true, 12, false, false));

        kommissionsTabelle.addCell(erstelleTabellenZelle("Fachprüfer/in:", true, 12, false, false).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        kommissionsTabelle.addCell(erstelleTabellenZelle(pruefer, true, 12, false, false));
        kommissionsTabelle.addCell(erstelleTabellenZelle("Vorbereitungsraum:", true, 12, false, false).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        kommissionsTabelle.addCell(erstelleTabellenZelle(vorbereitungsraum, true, 12, false, false));

        kommissionsTabelle.addCell(erstelleTabellenZelle("Schriftführer/in:", true, 12, false, false).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        kommissionsTabelle.addCell(erstelleTabellenZelle(protokoll, true, 12, false, false));
        kommissionsTabelle.addCell(erstelleTabellenZelle("Prüfungsraum:", true, 12, false, false).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        kommissionsTabelle.addCell(erstelleTabellenZelle(pruefungsraum, true, 12, false, false));
        document.add(kommissionsTabelle);

        document.add(new Paragraph("\n").setFontSize(10));

        float[] columnWidths4 = {100, 250, 100, 100};
        Table tabellePruefungsergenisse = new Table(columnWidths4);
        tabellePruefungsergenisse.setWidth(UnitValue.createPercentValue(100));

        tabellePruefungsergenisse.addCell(erstelleSpaltenKopf("Beginn der Prüfung"));
        tabellePruefungsergenisse.addCell(erstelleSpaltenKopf("Name der Schülerin/des Schülers"));
        tabellePruefungsergenisse.addCell(erstelleSpaltenKopf("Note Q2/II\n(ggf. Tendenz)"));
        tabellePruefungsergenisse.addCell(erstelleSpaltenKopf("Prüfungsnote\n(ggf. Tendenz)"));

        List<Pruefung> pruefungen = gruppe.pruefungen();

        for (int i = 0; i < 16; i++) {
            if (i < pruefungen.size()) {
                tabellePruefungsergenisse.addCell(erstelleDatenZelle(pruefungen.get(i).getBeginn().toString()));
                tabellePruefungsergenisse.addCell(erstelleDatenZelle(pruefungen.get(i).getSchueler().getNachname() + ", " + pruefungen.get(i).getSchueler().getVorname()));
            } else {
                tabellePruefungsergenisse.addCell(erstelleDatenZelle(""));
                tabellePruefungsergenisse.addCell(erstelleDatenZelle(""));
            }
            tabellePruefungsergenisse.addCell(erstelleDatenZelle(""));
            tabellePruefungsergenisse.addCell(erstelleDatenZelle(""));
        }

        document.add(tabellePruefungsergenisse);

        Paragraph fussnote = new Paragraph()
                .add(new Text("1").setTextRise(6).setFontSize(8)) // hochgestellte 1
                .add(" Die/Der Vorsitzende des Zentralen Abiturausschusses unterrichtet die Schülerinnen und Schüler gemäß Nummer 38.6.3 VVzAPO-GOSt.")
                .setFontSize(8)
                .setItalic()
                .setMarginTop(15)
                .setTextAlignment(TextAlignment.LEFT);

        document.add(fussnote);
        return document;
    }

    private static Cell erstelleTabellenZelle(String strg, boolean border, int fontSize, boolean bold, boolean underline) {
        Cell cell = new Cell();
        Text text = new Text(strg);
        if (bold) {
            text.setBold();
        }
        if (underline) {
            text.setUnderline();
        }
        Paragraph p = new Paragraph(text).setFontSize(fontSize);
        cell.add(p);
        if (border) {
            cell.setBorder(new SolidBorder(0.5f));
        } else {
            cell.setBorder(Border.NO_BORDER);
        }
        return cell;
    }

    private static Cell erstelleSpaltenKopf(String text) {
        return new Cell().add(new Paragraph(text).setBold().setFontSize(10))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    private static Cell erstelleDatenZelle(String text) {
        return new Cell().add(new Paragraph(text))
                .setMinHeight(18)
                .setTextAlignment(TextAlignment.LEFT);
    }
}
