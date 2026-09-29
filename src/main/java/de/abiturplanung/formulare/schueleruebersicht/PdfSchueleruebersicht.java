package de.abiturplanung.formulare.schueleruebersicht;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import de.abiturplanung.Utilities;
import de.abiturplanung.formulare.pdf.PdfUtilities;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Pruefung;
import java.io.File;
import java.time.LocalDate;
import static de.abiturplanung.formulare.schueleruebersicht.SchueleruebersichtPanel.VORBEREITUNGSRAUM;

public class PdfSchueleruebersicht {

    private static DeviceRgb kopfFarbe = new DeviceRgb(204, 204, 255);

    public static void erstelle(Abitur abitur, File datei) {
        String pfad = datei.getAbsolutePath();
        try {
            PdfWriter writer = new PdfWriter(pfad);
            PdfDocument pdf = new PdfDocument(writer);
            pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new FusszeilenHandler());
            Document dokument = new Document(pdf, PageSize.A4.rotate());

            dokument.setMargins(25, 20, 40, 20);

            String titel = "Abitur " + abitur.getAbiturjahrgang() + " - Alphabetische Übersicht der Prüfungen im 4. Fach";

            String stand = Utilities.formatiereDatum(LocalDate.now());

            dokument.add(PdfUtilities.erstelleDokumentKopf(titel, stand));
            dokument.add(erstelleTabelle(abitur));
            dokument.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Table erstelleTabelle(Abitur abitur) {
        String[] spalten = {"Name", "Vorname", "Kurs", "Prüfer", "Protokoll", "Vorsitz", "Tag", "Prfg-Beginn", "Raum", "Vorbereitung"};
        Table tabelle = new Table(spalten.length);
        tabelle.setWidth(UnitValue.createPercentValue(100));
        for (String spalte : spalten) {
            Cell zelle = new Cell().add(new Paragraph(spalte).setBold().setFontSize(10))
                    .setBackgroundColor(kopfFarbe);
            tabelle.addHeaderCell(zelle);
        }

        for (Pruefung p : abitur.gibPruefungenAB4Sortiert()) {
            String tag = p.getPruefungstag() == null ? "---" : Utilities.formatiereDatumKurz(p.getPruefungstag());
            String beginn = p.getBeginn() == null ? "---" : p.getBeginn().toString();

            Object[] zeile = {
                    p.getSchueler().getNachname(),
                    p.getSchueler().getVorname(),
                    p.getKurs().getBezeichnung(),
                    p.getPruefer() == null ? "---" : p.getPruefer().getKuerzel(),
                    p.getSchriftfuehrer() == null ? "---" : p.getSchriftfuehrer().getKuerzel(),
                    p.getVorsitz() == null ? "---" : p.getVorsitz().getKuerzel(),
                    tag,
                    beginn,
                    p.getRaum() == null ? "---" : p.getRaum(),
                    VORBEREITUNGSRAUM
            };

            for (Object wert : zeile) {
                tabelle.addCell(new Cell().add(new Paragraph(String.valueOf(wert)).setFontSize(10)));
            }
        }

        return tabelle;
    }

    private static class FusszeilenHandler implements IEventHandler {

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent dokumentEvent = (PdfDocumentEvent) event;
            PdfPage seite = dokumentEvent.getPage();
            Rectangle seitenGroesse = seite.getPageSize();

            PdfCanvas pdfCanvas = new PdfCanvas(
                    seite.newContentStreamAfter(),
                    seite.getResources(),
                    dokumentEvent.getDocument()
            );

            Canvas canvas = new Canvas(pdfCanvas, seitenGroesse);

            Paragraph fusszeile = new Paragraph()
                    .add("Die Prüflinge halten sich eine Stunde vor Prüfungsbeginn auf dem Lernflur abrufbereit!\n")
                    .add("Ausnahme: Prüflinge mit Prüfungsbeginn um 08.30 Uhr müssen erst ab 07.45 Uhr anwesend sein.")
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER);

            canvas.showTextAligned(
                    fusszeile,
                    seitenGroesse.getWidth() / 2,
                    18,
                    TextAlignment.CENTER
            );

            canvas.close();
        }
    }
}