package de.abiturplanung.gui;

import de.abiturplanung.gui.menue.Hauptmenue;
import de.abiturplanung.gui.menue.HauptmenueAktionen;
import de.abiturplanung.gui.planung.MuendlichePruefungenPanel;
import de.abiturplanung.gui.stammdaten.StammdatenPanel;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Pruefung;
import de.abiturplanung.persistence.Datenbank;
import de.abiturplanung.service.ImportService;
import de.abiturplanung.service.PruefungsfolgenExportService;
import de.abiturplanung.service.PruefungsfolgenImportServide;
import de.config.AppEinstellungen;
import de.config.AppPfade;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class MainFrame extends JFrame implements HauptmenueAktionen {

    private Abitur abitur;
    private Datenbank datenbank;
    private final JLabel statusleiste = new JLabel();
    private final JMenu importMenue = new JMenu("Import");
    private final Hauptmenue hauptmenue = new Hauptmenue(this);
    private final CardLayout modulLayout = new CardLayout();
    private final JPanel modulPanel = new JPanel(modulLayout);

    private MuendlichePruefungenPanel muendlichePruefungenPanel;
    private StammdatenPanel stammdatenPanel;

    public MainFrame(Abitur abitur, Datenbank datenbank) {
        this();
        initialisierePlanung(abitur, datenbank);
    }

    public MainFrame() {
        setTitle("Abiturplanung");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 750);
        setMinimumSize(new Dimension(1000, 550));
        setLocationRelativeTo(null);
        setJMenuBar(hauptmenue.getMenueleiste());
        statusleiste.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(modulPanel);
        zeigeKeinePlanung();
    }

    public void initialisierePlanung(Abitur abitur, Datenbank datenbank) {
        this.abitur = abitur;
        this.datenbank = datenbank;
        importMenue.setEnabled(true);
        modulPanel.removeAll();
        muendlichePruefungenPanel = new MuendlichePruefungenPanel(abitur, datenbank);

        stammdatenPanel = new StammdatenPanel(abitur, datenbank);
        stammdatenPanel.setNachStammdatenAenderung(this::ansichtenAktualisieren);
        modulPanel.add(muendlichePruefungenPanel, "MUENDLICH");
        modulPanel.add(stammdatenPanel, "STAMMDATEN");
        modulLayout.show(modulPanel, "MUENDLICH");
        statusleiste.setText("Datenbank: " + datenbank.getPfad().getFileName() + " | " + abitur.getPruefungen().size() + " Prüfungen geladen");
        add(modulPanel, BorderLayout.CENTER);
        add(statusleiste, BorderLayout.SOUTH);
    }


    private void zeigeKeinePlanung() {
        abitur = null;
        datenbank = null;
        modulPanel.removeAll();
        importMenue.setEnabled(false);
        JLabel hinweis = new JLabel("Keine Planung geöffnet", SwingConstants.CENTER);
        hinweis.setFont(hinweis.getFont().deriveFont(Font.BOLD, 20f));
        modulPanel.add(hinweis, BorderLayout.CENTER);
        statusleiste.setText("Keine Planung geöffnet");
        modulPanel.revalidate();
        modulPanel.repaint();
    }

    //Interface-Methoden des Hauptmenüs:

    @Override
    public void leistungsdatenImportAction() {
        if (abitur == null || datenbank == null) {
            return;
        }
        Path importPfad = AppPfade.getImportVerzeichnis().resolve("SchuelerLeistungsdaten.dat");

        if (!Files.exists(importPfad)) {
            JOptionPane.showMessageDialog(this, "Die Importdatei wurde nicht gefunden:\n" + importPfad, "Importfehler", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            ImportService importService = new ImportService(abitur);
            importService.importiereLeistungsdaten(importPfad);
            datenbank.synchronisiereLeistungsdaten(abitur);
            initialisierePlanung(abitur, datenbank);
            JOptionPane.showMessageDialog(this, "Die Leistungsdaten wurden erfolgreich importiert.", "Import abgeschlossen", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException | SQLException exception) {
            JOptionPane.showMessageDialog(this, "Die Leistungsdaten konnten nicht importiert werden:\n" + exception.getMessage(), "Importfehler", JOptionPane.ERROR_MESSAGE);
        }

    }

    @Override
    public void raeumeImportAction() {
        if (abitur == null || datenbank == null) {
            return;
        }

        Path importPfad = AppPfade.getImportVerzeichnis().resolve("Raumliste.csv");

        if (!Files.exists(importPfad)) {
            JOptionPane.showMessageDialog(this, "Die Importdatei wurde nicht gefunden:\n" + importPfad, "Importfehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ImportService importService = new ImportService(abitur);
            importService.importiereRaeume(importPfad);

            datenbank.synchronisiereRaeume(abitur);

            initialisierePlanung(abitur, datenbank);

            JOptionPane.showMessageDialog(this, "Räume wurden erfolgreich importiert.", "Import abgeschlossen", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException | SQLException exception) {
            JOptionPane.showMessageDialog(this, "Die Raumdaten konnten nicht importiert werden:\n" + exception.getMessage(), "Importfehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void lehrerImportAction() {
        if (abitur == null || datenbank == null) {
            return;
        }

        Path importPfad = AppPfade.getImportVerzeichnis().resolve("Lehrer.csv");

        if (!Files.exists(importPfad)) {
            JOptionPane.showMessageDialog(this, "Die Importdatei wurde nicht gefunden:\n" + importPfad, "Importfehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ImportService importService = new ImportService(abitur);
            importService.importiereLehrer(importPfad);

            datenbank.synchronisiereLehrer(abitur);

            initialisierePlanung(abitur, datenbank);

            JOptionPane.showMessageDialog(this, "Lehrerdaten wurden erfolgreich importiert.", "Import abgeschlossen", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException | SQLException exception) {
            JOptionPane.showMessageDialog(this, "Die Lehrerdaten konnten nicht importiert werden:\n" + exception.getMessage(), "Importfehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void schuelerImportAction() {
        if (abitur == null || datenbank == null) {
            return;
        }

        Path importPfad = AppPfade.getImportVerzeichnis().resolve("Schueler.csv");

        if (!Files.exists(importPfad)) {
            JOptionPane.showMessageDialog(this, "Die Importdatei wurde nicht gefunden:\n" + importPfad, "Importfehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ImportService importService = new ImportService(abitur);
            importService.importiereSchueler(importPfad);

            datenbank.synchronisiereSchueler(abitur);

            initialisierePlanung(abitur, datenbank);

            JOptionPane.showMessageDialog(this, "Schülerdaten wurden erfolgreich importiert.", "Import abgeschlossen", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException | SQLException exception) {
            JOptionPane.showMessageDialog(this, "Die Schülerdaten konnten nicht importiert werden:\n" + exception.getMessage(), "Importfehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void faecherImportAction() {
            if (abitur == null || datenbank == null) {
                return;
            }
            Path importPfad = AppPfade.getImportVerzeichnis().resolve("Faecher.csv");
            if (!Files.exists(importPfad)) {
                JOptionPane.showMessageDialog(this, "Die Importdatei wurde nicht gefunden:\n" + importPfad, "Importfehler", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                ImportService importService = new ImportService(abitur);
                importService.importiereFaecher(importPfad);
                datenbank.synchronisiereFaecher(abitur);
                initialisierePlanung(abitur, datenbank);
                JOptionPane.showMessageDialog(this, "Fächerdaten wurden erfolgreich importiert.", "Import abgeschlossen", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | SQLException | IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(this, "Die Fächerdaten konnten nicht importiert werden:\n" + exception.getMessage(), "Importfehler", JOptionPane.ERROR_MESSAGE);
            }
    }

    @Override
    public void backupErstellenAction() {
        if (datenbank == null) {
            JOptionPane.showMessageDialog(this, "Es ist keine Planung geöffnet.", "Backup erstellen", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Backup erstellen");

        String dateiname = datenbank.getPfad().getFileName().toString();
        String basisname = dateiname.toLowerCase().endsWith(".db") ? dateiname.substring(0, dateiname.length() - 3) : dateiname;

        String backupName = basisname + "_Backup_" + LocalDate.now() + ".db";

        fileChooser.setSelectedFile(new File(backupName));

        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path zielPfad = fileChooser.getSelectedFile().toPath().toAbsolutePath().normalize();

        if (!zielPfad.getFileName().toString().toLowerCase().endsWith(".db")) {
            zielPfad = zielPfad.resolveSibling(zielPfad.getFileName() + ".db");
        }

        if (Files.exists(zielPfad)) {
            int auswahl = JOptionPane.showConfirmDialog(
                    this, "Die Datei existiert bereits.\nSoll sie überschrieben werden?", "Backup überschreiben", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (auswahl != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            Files.copy(datenbank.getPfad(), zielPfad, StandardCopyOption.REPLACE_EXISTING);

            JOptionPane.showMessageDialog(
                    this,
                    "Backup wurde erfolgreich erstellt.\n\n" + "Achtung! Aktuelle Planung bleibt:\n" + datenbank.getPfad().getFileName(), "Backup erstellt", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Das Backup konnte nicht erstellt werden:\n" + exception.getMessage(),
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    public void pruefungstagHinzufuegenAction() {
        muendlichePruefungenPanel.pruefungstagHinzufuegen();
    }

    @Override
    public void pruefungstagEntfernenAction() {
        muendlichePruefungenPanel.pruefungstagEntfernen();
    }

    @Override
    public void datumPruefungstagAendern() {
        muendlichePruefungenPanel.datumPreufungstagAendern();

    }

    @Override
    public void planungOeffnenAction() {
        JFileChooser fileChooser = new JFileChooser(AppPfade.getDatenVerzeichnis().toFile());
        fileChooser.setDialogTitle("Planung öffnen");
        fileChooser.setFileFilter(new FileNameExtensionFilter("APT-Datenbanken (*.db)", "db"));

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path pfad = fileChooser.getSelectedFile().toPath().toAbsolutePath().normalize();

        if (!Files.exists(pfad)) {
            JOptionPane.showMessageDialog(this, "Die ausgewählte Datei existiert nicht.", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Datenbank neueDatenbank = new Datenbank(pfad);
            Abitur neuesAbitur = neueDatenbank.ladeAbitur();

            initialisierePlanung(neuesAbitur, neueDatenbank);

            AppEinstellungen einstellungen = new AppEinstellungen();
            einstellungen.setLetzteDatenbank(pfad);

        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Die Planung konnte nicht geöffnet werden:\n" + exception.getMessage(), "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void neuePlanungAction() {
        JFileChooser fileChooser = new JFileChooser(AppPfade.getDatenVerzeichnis().toFile());
        fileChooser.setDialogTitle("Neue Planung anlegen");
        fileChooser.setFileFilter(new FileNameExtensionFilter("APT-Datenbanken (*.db)", "db"));

        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path pfad = fileChooser.getSelectedFile().toPath().toAbsolutePath().normalize();

        if (!pfad.getFileName().toString().toLowerCase().endsWith(".db")) {
            pfad = pfad.resolveSibling(pfad.getFileName() + ".db");
        }

        if (Files.exists(pfad)) {
            int auswahl = JOptionPane.showConfirmDialog(
                    this, "Die Datei existiert bereits.\nSoll sie überschrieben werden?", "Datei überschreiben", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
            );

            if (auswahl != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            if (Files.exists(pfad)) {
                Files.delete(pfad);
            }

            Datenbank neueDatenbank = new Datenbank(pfad);
            neueDatenbank.initialisieren();

            Abitur neuesAbitur = new Abitur();

            initialisierePlanung(neuesAbitur, neueDatenbank);

            AppEinstellungen einstellungen = new AppEinstellungen();
            einstellungen.setLetzteDatenbank(pfad);

        } catch (IOException | SQLException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Die neue Planung konnte nicht angelegt werden:\n" + exception.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void beendenAction() {
        dispose();
    }

    @Override
    public void vorlagePruefungsfolgeErstellen() {
        File exportOrdner = AppPfade.getExportVerzeichnis().toFile();
        JFileChooser fileChooser = new JFileChooser(exportOrdner);
        fileChooser.setDialogTitle("Vorlage für Prüfungsfolgen speichern");
        fileChooser.setSelectedFile(new File("Pruefungsfolgen.xlsx"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel-Dateien (*.xlsx)", "xlsx"));

        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File datei = fileChooser.getSelectedFile();

        if (!datei.getName().toLowerCase().endsWith(".xlsx")) {
            datei = new File(datei.getParentFile(), datei.getName() + ".xlsx");
        }

        try {
            PruefungsfolgenExportService service = new PruefungsfolgenExportService();
            service.exportiere(abitur, datei);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Die Vorlage konnte nicht gespeichert werden:\n" + e.getMessage(), "Fehler beim Export", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void pruefungsfolgenImportieren() {
        File importOrdner = AppPfade.getImportVerzeichnis().toFile();
        JFileChooser fileChooser = new JFileChooser(importOrdner);
        fileChooser.setDialogTitle("Prüfungsfolgen importieren");
        fileChooser.setSelectedFile(new File("Pruefungsfolgen.xlsx"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel-Dateien (*.xlsx)", "xlsx"));

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File datei = fileChooser.getSelectedFile();

        int count = 0;
        try {
            PruefungsfolgenImportServide service = new PruefungsfolgenImportServide();
            List<Pruefung> geaendertePruefungen = service.importiere(abitur, datei);
            for (Pruefung pruefung : geaendertePruefungen) {
                datenbank.aktualisierePruefungsfolge(pruefung);
                count++;
            }
            muendlichePruefungenPanel.planungsvorratAktualisieren();
            JOptionPane.showMessageDialog(this, count + " Prüfungsfolgen erfolgreich importiert.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Die Daten konnten nicht gespeichert werden:\n" + e.getMessage(), "Fehler beim Export", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void timeLineErzeugen() {
        TimelineFrame timelineFrame = new TimelineFrame(abitur);
        timelineFrame.setVisible(true);
    }

    @Override
    public void zeigeMuendlichePruefungen() {
        modulLayout.show(modulPanel, "MUENDLICH");
    }

    @Override
    public void zeigeStammdaten() {
        modulLayout.show(modulPanel, "STAMMDATEN");
    }

    private void ansichtenAktualisieren() {
        stammdatenPanel.ansichtAktualisieren();
        muendlichePruefungenPanel.ansichtAktualisieren();
    }
}