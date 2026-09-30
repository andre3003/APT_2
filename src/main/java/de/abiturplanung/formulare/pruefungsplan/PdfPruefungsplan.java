package de.abiturplanung.formulare.pruefungsplan;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
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

    public static void erstelle(Abitur abitur, Pruefungstag pruefungstag, File datei) {
        PruefungsplanDatenService datenService = new PruefungsplanDatenService();
        List<KommissionsGruppe> gruppen = datenService.gibSortierteKommissionsGruppen(abitur, pruefungstag);
        List<KommissionsGruppe> testGruppen = gruppen.subList(0, Math.min(2, gruppen.size()));

        LocalTime ersteZeit = ermittleErsteZeit(gruppen);
        LocalTime letzteZeit = ermittleLetzteZeit(gruppen);

        try {
            PdfWriter writer = new PdfWriter(datei.getAbsolutePath());
            PdfDocument pdf = new PdfDocument(writer);
            Document dokument = new Document(pdf, PageSize.A4.rotate());

            dokument.setMargins(10, 20, 10, 20);

            String titel = "Abitur " + abitur.getAbiturjahrgang()+ " - Prüfungsplan - " + Utilities.formatiereDatum(pruefungstag.getDatum());

            String stand = Utilities.formatiereDatum(LocalDate.now());

            dokument.add(PdfUtilities.erstelleDokumentKopf(titel, stand));

//            dokument.add(erstelleKommission(gruppen.get(0), ersteZeit, letzteZeit));
            dokument.add(erstelleKommissionsBlock(testGruppen, ersteZeit, letzteZeit));
            dokument.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static Table erstelleKommission(KommissionsGruppe gruppe, LocalTime ersteZeit, LocalTime letzteZeit) {
        float zellenHoehe = 15f;
        float[] spaltenBreiten = {58, 58};

        Table tabelle = new Table(spaltenBreiten);
        tabelle.setFixedLayout();
        tabelle.setBorder(Border.NO_BORDER);

        Pruefung erstePruefung = gruppe.pruefungen().get(0);

        String kurs = erstePruefung.getKurs().getBezeichnung();
        String kommission = gruppe.pruefer() + " / " + gruppe.vorsitz() + " / " + gruppe.schriftfuehrer();
        String raum = erstePruefung.getRaum() == null ? "" : erstePruefung.getRaum().toString();

        Paragraph info = new Paragraph("Kurs: " + kurs + "\nKomm.: " + kommission + "\n" + raum).setFontSize(9).setBold();

        Cell infoZelle = new Cell(1, 2)
                .add(info)
                .setBorder(Border.NO_BORDER);

        tabelle.addCell(infoZelle);

        tabelle.addCell(new Cell()
                .add(new Paragraph("Vorbereitung").setFontSize(9).setBold())
                .setHeight(zellenHoehe));

        tabelle.addCell(new Cell()
                .add(new Paragraph("Prüfung").setFontSize(9).setBold())
                .setHeight(zellenHoehe));

        for (LocalTime zeit = ersteZeit; !zeit.isAfter(letzteZeit); zeit = zeit.plusMinutes(30)) {
            String vorbereitung = "";
            String pruefung = "";

            for (Pruefung p : gruppe.pruefungen()) {
                String schueler = p.getSchueler().getNachname() + ", " + p.getSchueler().getVorname().charAt(0) + ".";

                if (p.getBeginn().minusMinutes(30).equals(zeit)) {
                    vorbereitung = schueler;
                }

                if (p.getBeginn().equals(zeit)) {
                    pruefung = schueler;
                }
            }

            tabelle.addCell(new Cell()
                    .add(new Paragraph(vorbereitung).setFontSize(9))
                    .setHeight(zellenHoehe));

            tabelle.addCell(new Cell()
                    .add(new Paragraph(pruefung).setFontSize(9))
                    .setHeight(zellenHoehe));
        }

        return tabelle;
    }

    private static Table erstelleKommissionsBlock(List<KommissionsGruppe> gruppen, LocalTime ersteZeit, LocalTime letzteZeit) {
        Table block = new Table(new float[]{1, 1, 1});
        block.setWidth(UnitValue.createPercentValue(100));
        block.setBorder(Border.NO_BORDER);

        for (KommissionsGruppe gruppe : gruppen) {
            block.addCell(new Cell()
                    .add(erstelleKommission(gruppe, ersteZeit, letzteZeit))
                    .setBorder(Border.NO_BORDER)
                    .setPadding(3));
        }
        return block;
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

}