package de.abiturplanung.formulare.pdf;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.properties.AreaBreakType;
import de.config.AppPfade;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.List;

public class PdfErsteller {

    public static File waehleZieldatei(Component parent, String dateiname) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(AppPfade.getPdfVerzeichnis().toFile());
        fileChooser.setSelectedFile(new File(dateiname));

        if (fileChooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File datei = fileChooser.getSelectedFile();

        if (!datei.getName().toLowerCase().endsWith(".pdf")) {
            datei = new File(datei.getParentFile(), datei.getName() + ".pdf");
        }

        return datei;
    }

    public static void oeffnePdf(File datei) throws IOException {
        Desktop.getDesktop().open(datei);
    }

    public static void erstelle(Component parent, String dateiname, boolean anschliessendOeffnen, Consumer<File> pdfErzeugung) {
        File datei = waehleZieldatei(parent, dateiname);
        if (datei == null) {
            return;
        }
        pdfErzeugung.accept(datei);
        if (anschliessendOeffnen) {
            try {
                oeffnePdf(datei);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static <T> void erstelleMehrseitigesFormularDokument(File datei, List<T> elemente, BiConsumer<Document, T> formularErsteller) {
        try (Document dokument = new Document(new PdfDocument(new PdfWriter(datei)), PageSize.A4)) {
            for (int i = 0; i < elemente.size(); i++) {
                if (i > 0) dokument.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                formularErsteller.accept(dokument, elemente.get(i));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("PDF konnte nicht erstellt werden: " + datei, e);
        }
    }
}
