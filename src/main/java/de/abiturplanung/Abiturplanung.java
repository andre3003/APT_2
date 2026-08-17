package de.abiturplanung;
import de.abiturplanung.model.*;
import de.abiturplanung.persistence.Datenbank;

import de.abiturplanung.gui.MainFrame;
import de.abiturplanung.service.ImportService;
import de.config.AppEinstellungen;
import de.config.AppPfade;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
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
        AppPfade.initialisiereVerzeichnisse();

        AppEinstellungen einstellungen = new AppEinstellungen();
        Path letzterPfad = einstellungen.getLetzteDatenbank();
        MainFrame mainFrame = new MainFrame();
        if (letzterPfad != null && Files.exists(letzterPfad)) {
            Datenbank datenbank = new Datenbank(letzterPfad);
            Abitur abitur = datenbank.ladeAbitur();
            mainFrame.setPlanung(abitur, datenbank);
        }
//        einstellungen.setLetzteDatenbank(AppPfade.getDatenVerzeichnis().resolve("Abitur_2027.db"));
        mainFrame.setVisible(true);

    }

    private static void zeigeStartfehler(Exception exception) {
        exception.printStackTrace();
        JOptionPane.showMessageDialog(null, "Die Importdateien konnten nicht geladen werden.\n\n" + exception.getMessage(), "Fehler beim Programmstart", JOptionPane.ERROR_MESSAGE );
    }
}