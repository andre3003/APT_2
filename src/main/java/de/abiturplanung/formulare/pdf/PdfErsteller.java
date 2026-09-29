package de.abiturplanung.formulare.pdf;

import de.config.AppPfade;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

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
}
