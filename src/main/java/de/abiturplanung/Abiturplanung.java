package de.abiturplanung;
import de.abiturplanung.model.*;
import de.abiturplanung.persistence.Datenbank;

import de.abiturplanung.gui.MainFrame;
import de.abiturplanung.service.ImportService;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class Abiturplanung {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                starteAnwendung();
            } catch (IOException | SQLException exception) {
                zeigeStartfehler(exception);
            }
        });
    }

    private static void starteAnwendung() throws IOException, SQLException {
        Datenbank datenbank = new Datenbank("Abitur_2027.db");
        datenbank.initialisieren();

        Abitur abitur;

        if (!datenbank.istInitialisiert()) {
            abitur = new Abitur();
            ImportService service = new ImportService(abitur);
            service.importiereSchueler( Path.of("Schueler.csv"));
            service.importiereLehrer(Path.of("Lehrer.csv"));
            service.importiereLeistungsdaten(Path.of("SchuelerLeistungsdaten.dat"));
            service.importiereRaeume(Path.of("Raumliste.csv"));

            //Testdaten:
            abitur.addPruefungstag(new Pruefungstag(LocalDate.of(2027, 5, 20)));
            abitur.addPruefungstag(new Pruefungstag(LocalDate.of(2027, 5, 21)));
            abitur.addPruefungstag(new Pruefungstag(LocalDate.of(2027, 5, 24)));

            datenbank.speichereAbitur(abitur);
            datenbank.setInitialisiert();
        } else {
            abitur = datenbank.ladeAbitur();

        }
        MainFrame mainFrame = new MainFrame(abitur, datenbank);
        mainFrame.setVisible(true);
    }

    private static void zeigeStartfehler(Exception exception) {
        exception.printStackTrace();
        JOptionPane.showMessageDialog(null, "Die Importdateien konnten nicht geladen werden.\n\n" + exception.getMessage(), "Fehler beim Programmstart", JOptionPane.ERROR_MESSAGE );
    }
}