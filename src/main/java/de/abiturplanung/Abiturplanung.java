package de.abiturplanung;

import de.abiturplanung.gui.MainFrame;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.service.ImportService;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;

public class Abiturplanung {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                starteAnwendung();
            } catch (IOException exception) {
                zeigeStartfehler(exception);
            }
        });
    }

    private static void starteAnwendung() throws IOException {
        Abitur abitur = new Abitur();
        ImportService service = new ImportService(abitur);
        service.importiereSchueler( Path.of("Schueler.csv"));
        service.importiereLehrer(Path.of("Lehrer.csv"));
        service.importiereLeistungsdaten(Path.of("SchuelerLeistungsdaten.dat"));
        service.importiereRaeume(Path.of("Raumliste.csv"));
        MainFrame mainFrame = new MainFrame(abitur);
        mainFrame.setVisible(true);
    }

    private static void zeigeStartfehler(IOException exception) {
        exception.printStackTrace();
        JOptionPane.showMessageDialog(null, "Die Importdateien konnten nicht geladen werden.\n\n" + exception.getMessage(), "Fehler beim Programmstart", JOptionPane.ERROR_MESSAGE );
    }
}