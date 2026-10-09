package de.abiturplanung.formulare.pruefungsplan;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import de.abiturplanung.Utilities;
import de.abiturplanung.formulare.pdf.PdfUtilities;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Pruefung;
import de.abiturplanung.model.Pruefungstag;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PdfPruefungsplan {
    private static final float KOPF_HOEHE = 45f;
    private static final float ZEILEN_HOEHE = 15f;

    public static void erstelle(Abitur abitur, Pruefungstag pruefungstag, File datei) {
        erstelle(abitur, List.of(pruefungstag), datei);
    }

    public static void erstelleGesamtplan(Abitur abitur, File datei) {
        erstelle(abitur, abitur.getPruefungstage(), datei);
    }

    private static void erstelle(Abitur abitur, List<Pruefungstag> pruefungstage, File datei) {
        PruefungsplanDatenService datenService = new PruefungsplanDatenService();
        String pfad = datei.getAbsolutePath();

        try {
            PdfWriter writer = new PdfWriter(pfad);
            PdfDocument pdf = new PdfDocument(writer);
            pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new SeitenzahlenHandler());
            Document dokument = new Document(pdf, PageSize.A4.rotate());
            dokument.setMargins(15, 20, 35, 20);
            boolean ersteSeite = true;

            for (Pruefungstag pruefungstag : pruefungstage) {
                List<KommissionsGruppe> gruppen = datenService.gibSortierteKommissionsGruppen(abitur, pruefungstag);

                if (gruppen.isEmpty()) {
                    continue;
                }
                LocalTime ersteZeit = ermittleErsteZeit(gruppen);
                LocalTime letzteZeit = ermittleLetzteZeit(gruppen);
                String titel = "Abitur " + abitur.getAbiturjahrgang() + " - Prüfungsplan - " + Utilities.formatiereDatum(pruefungstag.getDatum());
                String stand = Utilities.formatiereDatum(LocalDate.now());

                for (int i = 0; i < gruppen.size(); i += 6) {
                    if (!ersteSeite) {
                        dokument.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                    }
                    int bis = Math.min(i + 6, gruppen.size());
                    List<KommissionsGruppe> seitenGruppen = gruppen.subList(i, bis);
                    dokument.add(PdfUtilities.erstelleDokumentKopf(titel, 90, 90, stand));
                    dokument.add(erstellePruefungsplanBlock(seitenGruppen, ersteZeit, letzteZeit));
                    ersteSeite = false;
                }
            }
            dokument.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    //Das ist die äußere Tabelle einer Seite (6 Prüfungen)
    private static Table erstellePruefungsplanBlock(List<KommissionsGruppe> gruppen, LocalTime ersteZeit, LocalTime letzteZeit) {
        Table block = new Table(new float[]{40, 130, 130, 130, 40, 130, 130, 130});
        block.setBorder(new SolidBorder(0.5f));
        for (int i = 0; i < Math.min(6, gruppen.size()); i++) {
            if (i % 3 == 0) {
                block.addCell(new Cell().add(erstelleZeitachsenContainer(ersteZeit, letzteZeit)).setBorder(Border.NO_BORDER).setPadding(3));
            }
            block.addCell(new Cell().add(erstelleKommission(gruppen.get(i), ersteZeit, letzteZeit)).setBorder(Border.NO_BORDER).setPadding(3));
        }
        return block;
    }

    //Das ist die Tabelle einer Kommission
    private static Table erstelleKommission(KommissionsGruppe gruppe, LocalTime ersteZeit, LocalTime letzteZeit) {
        float[] spaltenBreiten = {58, 58};
        Table tabelle = new Table(spaltenBreiten);
        tabelle.setFixedLayout();
        tabelle.setBorder(Border.NO_BORDER);

        Pruefung erstePruefung = gruppe.pruefungen().get(0);

        String kurs = erstePruefung.getKurs().getBezeichnung();
        String kommission = gruppe.pruefer() + " / " + gruppe.vorsitz() + " / " + gruppe.schriftfuehrer();
        String raum = erstePruefung.getRaum() == null ? "" : erstePruefung.getRaum().toString();

        Paragraph info = new Paragraph(kurs + "\n" + kommission + "\n" + raum)
                .setFontSize(9)
                .setBold()
                .setMargin(3);

        Cell infoZelle = new Cell(1, 2)
                .add(info)
                .setHeight(KOPF_HOEHE)
                .setBorder(new SolidBorder(0.5f))
                .setBorderBottom(Border.NO_BORDER)
                .setPadding(0);

        tabelle.addCell(infoZelle);

        tabelle.addCell(new Cell().add(new Paragraph("Vorber.").setFontSize(9).setBold()).setHeight(ZEILEN_HOEHE));

        tabelle.addCell(new Cell().add(new Paragraph("Prüfung").setFontSize(9).setBold()).setHeight(ZEILEN_HOEHE));

        for (LocalTime zeit = ersteZeit; !zeit.isAfter(letzteZeit); zeit = zeit.plusMinutes(30)) {
            String vorbereitung = "";
            String pruefung = "";

            for (Pruefung p : gruppe.pruefungen()) {
                String schueler = p.getSchueler().getKurzname(14);
                if (p.getBeginn().minusMinutes(30).equals(zeit)) {
                    vorbereitung = schueler;
                }
                if (p.getBeginn().equals(zeit)) {
                    pruefung = schueler;
                }
            }
            tabelle.addCell(new Cell().add(new Paragraph(vorbereitung).setFontSize(9)).setHeight(ZEILEN_HOEHE));

            tabelle.addCell(new Cell().add(new Paragraph(pruefung).setFontSize(9)).setHeight(ZEILEN_HOEHE));
        }

        return tabelle;
    }

    private static Table erstelleZeitachse(LocalTime ersteZeit, LocalTime letzteZeit) {
        Table zeitachse = new Table(1);
        zeitachse.addCell(new Cell().add(new Paragraph("Zeit").setFontSize(10).setBold()).setHeight(ZEILEN_HOEHE));
        for (LocalTime zeit = ersteZeit; !zeit.isAfter(letzteZeit); zeit = zeit.plusMinutes(30)) {
            zeitachse.addCell(new Cell().add(new Paragraph(zeit.toString()).setFontSize(9)).setHeight(ZEILEN_HOEHE));
        }
        return zeitachse;
    }

    private static Table erstelleZeitachsenContainer(LocalTime ersteZeit, LocalTime letzteZeit) {
        Table container = new Table(new float[]{40});
        container.setBorder(Border.NO_BORDER);
        Cell leerZelle = new Cell().setHeight(KOPF_HOEHE).setBorder(Border.NO_BORDER).setPadding(0);
        container.addCell(leerZelle);
        Cell zeitZelle = new Cell().add(erstelleZeitachse(ersteZeit, letzteZeit)).setBorder(Border.NO_BORDER).setPadding(0);
        container.addCell(zeitZelle);
        return container;
    }


    private static LocalTime ermittleErsteZeit(List<KommissionsGruppe> gruppen) {
        return gruppen.stream()
                .flatMap(gruppe -> gruppe.pruefungen().stream())
                .map(Pruefung::getBeginn)
                .min(LocalTime::compareTo)
                .orElseThrow()
                .minusMinutes(30);
    }

    private static LocalTime ermittleLetzteZeit(List<KommissionsGruppe> gruppen) {
        return gruppen.stream()
                .flatMap(gruppe -> gruppe.pruefungen().stream())
                .map(Pruefung::getBeginn)
                .max(LocalTime::compareTo)
                .orElseThrow();
    }

    private static class SeitenzahlenHandler implements IEventHandler {

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent dokumentEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = dokumentEvent.getDocument();
            PdfPage seite = dokumentEvent.getPage();

            int seitenNummer = pdf.getPageNumber(seite);
            Rectangle seitenGroesse = seite.getPageSize();

            Canvas canvas = new Canvas(seite, seitenGroesse);

            canvas.showTextAligned(
                    new Paragraph("Seite " + seitenNummer).setFontSize(9),
                    seitenGroesse.getWidth() / 2,
                    15,
                    TextAlignment.CENTER
            );
            canvas.close();
        }
    }

}