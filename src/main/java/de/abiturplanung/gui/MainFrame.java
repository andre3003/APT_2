package de.abiturplanung.gui;

import de.abiturplanung.gui.dialogs.PruefungsDialog;
import de.abiturplanung.gui.menue.Hauptmenue;
import de.abiturplanung.gui.menue.HauptmenueAktionen;
import de.abiturplanung.gui.model.PruefungsTableModel;
import de.abiturplanung.gui.planung.PlanungsMatrixPanel;
import de.abiturplanung.gui.planung.PruefungTransferable;
import de.abiturplanung.gui.planung.PruefungsKartenAktionen;
import de.abiturplanung.gui.timeline.KommissionsGruppe;
import de.abiturplanung.gui.timeline.TimelineDatenService;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Pruefung;
import de.abiturplanung.model.Pruefungstag;
import de.abiturplanung.persistence.Datenbank;
import de.abiturplanung.service.ImportService;
import de.abiturplanung.service.Kollisionspruefer;
import de.abiturplanung.service.PruefungsfolgenExportService;
import de.abiturplanung.service.PruefungsfolgenImportServide;
import de.config.AppEinstellungen;
import de.config.AppPfade;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class MainFrame extends JFrame implements PruefungsKartenAktionen, HauptmenueAktionen {

    private Abitur abitur;
    private Datenbank datenbank;
    private Kollisionspruefer kollisionspruefer;
    List<PlanungsMatrixPanel> matrixPanels = new ArrayList<>();
    private final JTabbedPane planungsTabs = new JTabbedPane();
    private PruefungsTableModel tableModel;
    private final JPanel arbeitsbereich = new JPanel(new BorderLayout());
    private final JLabel statusleiste = new JLabel();
    private final JMenu importMenue = new JMenu("Import");
    private Pruefung kopiertePruefung = null;
    private final Hauptmenue hauptmenue = new Hauptmenue(this);

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
        add(arbeitsbereich, BorderLayout.CENTER);
        add(statusleiste, BorderLayout.SOUTH);
        zeigeKeinePlanung();
    }

    private void planungsTabsAktualisieren() {
        planungsTabs.removeAll();
        matrixPanels.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        Map<Pruefung, List<Pruefung>> alleKollisionen = kollisionspruefer.findeAlleKollisionen();

        for (Pruefungstag pruefungstag : abitur.getPruefungstage()) {
            PlanungsMatrixPanel matrixPanel = new PlanungsMatrixPanel(abitur, pruefungstag);
            matrixPanels.add(matrixPanel);
            matrixPanel.setKollisionen(alleKollisionen);
            matrixPanel.setzePruefungskartenAktionen(this);
            matrixPanel.aktualisieren();
            planungsTabs.addTab(pruefungstag.getDatum().format(formatter), matrixPanel);
        }
        revalidate();
        repaint();
    }

    private void zeigeKeinePlanung() {
        abitur = null;
        datenbank = null;
        kollisionspruefer = null;
        arbeitsbereich.removeAll();
        importMenue.setEnabled(false);
        JLabel hinweis = new JLabel("Keine Planung geöffnet", SwingConstants.CENTER);
        hinweis.setFont(hinweis.getFont().deriveFont(Font.BOLD, 20f));
        arbeitsbereich.add(hinweis, BorderLayout.CENTER);
        statusleiste.setText("Keine Planung geöffnet");
        arbeitsbereich.revalidate();
        arbeitsbereich.repaint();
    }

    public void initialisierePlanung(Abitur abitur, Datenbank datenbank) {
        this.abitur = abitur;
        this.datenbank = datenbank;
        this.kollisionspruefer = new Kollisionspruefer(abitur);
        importMenue.setEnabled(true);

        arbeitsbereich.removeAll();

        JPanel planungsbereich = erstellePlanungsbereich();
        arbeitsbereich.add(planungsbereich, BorderLayout.CENTER);

        statusleiste.setText("Datenbank: " + datenbank.getPfad().getFileName() + " | " + abitur.getPruefungen().size() + " Prüfungen geladen");

        arbeitsbereich.revalidate();
        arbeitsbereich.repaint();
    }

    private void verarbeitePlanungsaenderung(Pruefung pruefung) {
        try {
            datenbank.aktualisierePruefungsplanung(pruefung);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Die Änderungen konnten nicht gespeichert werden.\n" + "Starten Sie die Anwendung neu.", "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        tableModel.fireTableDataChanged();

        Map<Pruefung, List<Pruefung>> aktuelleKollisionen = kollisionspruefer.findeAlleKollisionen();

        for (PlanungsMatrixPanel panel : matrixPanels) {
            panel.setKollisionen(aktuelleKollisionen);
            panel.aktualisieren();
        }
    }

    private JPanel erstellePlanungsbereich() { //Das ist die Tabelle mit den Prüfungen
        JPanel panel = new JPanel(new BorderLayout());

        tableModel = new PruefungsTableModel(abitur.getPruefungen());

        JTable pruefungstabelle = new JTable(tableModel);

        TableRowSorter<PruefungsTableModel> sorter = new TableRowSorter<>(tableModel);

        pruefungstabelle.setRowSorter(sorter);
        pruefungstabelle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pruefungstabelle.setFillsViewportHeight(true);
        pruefungstabelle.setRowHeight(24);
        pruefungstabelle.setDragEnabled(true);

        sorter.setSortsOnUpdates(true);

        pruefungstabelle.setTransferHandler(new TransferHandler() {
            @Override
            protected Transferable createTransferable(JComponent component) {
                int viewZeile = pruefungstabelle.getSelectedRow();

                if (viewZeile < 0) {
                    return null;
                }

                int modelZeile = pruefungstabelle.convertRowIndexToModel(viewZeile);
                return new PruefungTransferable(tableModel.getPruefung(modelZeile));
            }

            @Override
            public int getSourceActions(JComponent component) {
                return MOVE;
            }
        });

        pruefungstabelle.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() != 2) {
                    return;
                }

                int viewZeile = pruefungstabelle.rowAtPoint(e.getPoint());

                if (viewZeile < 0) {
                    return;
                }

                int modelZeile = pruefungstabelle.convertRowIndexToModel(viewZeile);
                PruefungsDialog dialog = new PruefungsDialog(MainFrame.this, abitur, tableModel.getPruefung(modelZeile));
                dialog.setVisible(true);

                if (dialog.isGespeichert()) {
                    tableModel.fireTableRowsUpdated(modelZeile, modelZeile);
                }
            }
        });

        // ----------------------------------------------------------
        // Farbliche Statusanzeige
        // ----------------------------------------------------------

        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    int modelZeile = table.convertRowIndexToModel(row);
                    Pruefung pruefung = tableModel.getPruefung(modelZeile);

                    if (pruefung.istVollstaendigGeplant()) {
                        component.setBackground(new Color(230, 245, 230));
                    } else {
                        component.setBackground(new Color(255, 248, 220));
                    }

                    component.setForeground(Color.BLACK);
                }

                return component;
            }
        };

        pruefungstabelle.setDefaultRenderer(Object.class, statusRenderer);

        // ----------------------------------------------------------
        // Filter
        // ----------------------------------------------------------

        JTextField txtSuche = new JTextField(12);

        JComboBox<String> cmbFach = new JComboBox<>();
        cmbFach.addItem("Alle");

        JComboBox<String> cmbKurs = new JComboBox<>();
        cmbKurs.addItem("Alle");

        JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Alle", "unvollständig", "vollständig"});

        JCheckBox chkVollstaendigeAusblenden = new JCheckBox("Vollständige ausblenden");

        TreeSet<String> faecher = new TreeSet<>();
        TreeSet<String> kurse = new TreeSet<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Pruefung pruefung = tableModel.getPruefung(i);
            faecher.add(pruefung.getKurs().getFach());
            kurse.add(pruefung.getKurs().getBezeichnung());
        }

        for (String fach : faecher) {
            cmbFach.addItem(fach);
        }

        for (String kurs : kurse) {
            cmbKurs.addItem(kurs);
        }

        Runnable filterAktualisieren = () -> {
            List<RowFilter<PruefungsTableModel, Integer>> filter = new ArrayList<>();

            String suche = txtSuche.getText().trim();

            if (!suche.isEmpty()) {
                filter.add(RowFilter.regexFilter("(?i)" + Pattern.quote(suche)));
            }

            String fach = (String) cmbFach.getSelectedItem();

            if (fach != null && !fach.equals("Alle")) {
                filter.add(RowFilter.regexFilter("^" + Pattern.quote(fach) + "$", PruefungsTableModel.SPALTE_FACH));
            }

            String kurs = (String) cmbKurs.getSelectedItem();

            if (kurs != null && !kurs.equals("Alle")) {
                filter.add(RowFilter.regexFilter("^" + Pattern.quote(kurs) + "$", PruefungsTableModel.SPALTE_KURS));
            }

            String status = (String) cmbStatus.getSelectedItem();

            if (status != null && !status.equals("Alle")) {
                filter.add(RowFilter.regexFilter("^" + Pattern.quote(status) + "$", PruefungsTableModel.SPALTE_STATUS));
            }

            if (chkVollstaendigeAusblenden.isSelected()) {
                filter.add(RowFilter.notFilter(RowFilter.regexFilter("^vollständig$", PruefungsTableModel.SPALTE_STATUS)));
            }

            sorter.setRowFilter(filter.isEmpty() ? null : RowFilter.andFilter(filter));
        };

        txtSuche.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterAktualisieren.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterAktualisieren.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterAktualisieren.run();
            }
        });

        cmbFach.addActionListener(e -> filterAktualisieren.run());
        cmbKurs.addActionListener(e -> filterAktualisieren.run());
        cmbStatus.addActionListener(e -> filterAktualisieren.run());
        chkVollstaendigeAusblenden.addActionListener(e -> filterAktualisieren.run());

        // ----------------------------------------------------------
        // Linker Arbeitsvorrat
        // ----------------------------------------------------------

        JLabel ueberschrift = new JLabel("Prüfungen im 4. Abiturfach");
        ueberschrift.setFont(ueberschrift.getFont().deriveFont(Font.BOLD, 18f));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        filterPanel.add(new JLabel("Suche:"));
        filterPanel.add(txtSuche);
        filterPanel.add(new JLabel("Fach:"));
        filterPanel.add(cmbFach);
        filterPanel.add(new JLabel("Kurs:"));
        filterPanel.add(cmbKurs);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(cmbStatus);
        filterPanel.add(chkVollstaendigeAusblenden);

        JPanel kopfPanel = new JPanel();
        kopfPanel.setLayout(new BoxLayout(kopfPanel, BoxLayout.Y_AXIS));
        kopfPanel.add(ueberschrift);
        kopfPanel.add(Box.createVerticalStrut(5));
        kopfPanel.add(filterPanel);

        JPanel pruefungsPanel = new JPanel(new BorderLayout(0, 8));
        pruefungsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pruefungsPanel.add(kopfPanel, BorderLayout.NORTH);
        pruefungsPanel.add(new JScrollPane(pruefungstabelle), BorderLayout.CENTER);

        // ----------------------------------------------------------
        // Matrix
        // ----------------------------------------------------------

        // ----------------------------------------------------------
// Matrix
// ----------------------------------------------------------
        planungsTabsAktualisieren();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pruefungsPanel, planungsTabs);
        splitPane.setResizeWeight(0.32);
        splitPane.setDividerLocation(480);
        splitPane.setOneTouchExpandable(true);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
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
            datenbank.aktualisiereLeistungsdaten(abitur);
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

            datenbank.aktualisiereRaeume(abitur);

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

            datenbank.aktualisiereLehrer(abitur);

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

            datenbank.aktualisiereSchueler(abitur);

            initialisierePlanung(abitur, datenbank);

            JOptionPane.showMessageDialog(this, "Schülerdaten wurden erfolgreich importiert.", "Import abgeschlossen", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException | SQLException exception) {
            JOptionPane.showMessageDialog(this, "Die Schülerdaten konnten nicht importiert werden:\n" + exception.getMessage(), "Importfehler", JOptionPane.ERROR_MESSAGE);
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
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner datumSpinner = new JSpinner(dateModel);
        datumSpinner.setEditor(new JSpinner.DateEditor(datumSpinner, "dd.MM.yyyy"));

        int ergebnis = JOptionPane.showConfirmDialog(this, datumSpinner, "Prüfungstag hinzufügen", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (ergebnis != JOptionPane.OK_OPTION) {
            return;
        }

        Date ausgewaehlt = dateModel.getDate();
        LocalDate datum = ausgewaehlt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        for (Pruefungstag pruefungstag : abitur.getPruefungstage()) {
            if (pruefungstag.getDatum().equals(datum)) {
                JOptionPane.showMessageDialog(this, "Dieser Prüfungstag existiert bereits.", "Hinweis", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        Pruefungstag pruefungstag = new Pruefungstag(datum);

        try {
            datenbank.speicherePruefungstag(pruefungstag);
            abitur.addPruefungstag(pruefungstag);
            planungsTabsAktualisieren();

        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, "Der Prüfungstag konnte nicht gespeichert werden:\n" + exception.getMessage(), "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void pruefungstagEntfernenAction() {
        int index = planungsTabs.getSelectedIndex();
        if (index < 0 || index >= abitur.getPruefungstage().size()) {
            return;
        }
        Pruefungstag pruefungstag = abitur.getPruefungstage().get(index);
        int anzahlPruefungen = 0;
        for (Pruefung pruefung : abitur.getPruefungen()) {
            if (pruefungstag.getDatum().equals(pruefung.getPruefungstag())) {
                anzahlPruefungen++;
            }
        }

        try {
            if (anzahlPruefungen == 0) {
                int bestaetigung = JOptionPane.showConfirmDialog(this, "Prüfungstag " + pruefungstag.getDatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " wirklich löschen?", "Prüfungstag löschen", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (bestaetigung == JOptionPane.YES_OPTION) {
                    abitur.removePruefungstag(pruefungstag, true);
                    planungsTabsAktualisieren();
                    datenbank.loeschePruefungstag(pruefungstag);
                }
                return;
            }

            Object[] optionen = {"Kommissionen behalten", "Kommissionen mitlöschen", "Abbrechen"};
            int auswahl = JOptionPane.showOptionDialog(this, "An diesem Prüfungstag sind " + anzahlPruefungen + " Prüfungen geplant.\n\n" + "Planungsdaten werden gelöscht.\n" + "Sollen die bestehenden Kommissionen erhalten bleiben?", "Prüfungstag löschen",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, optionen, optionen[0]);

            if (auswahl == 2 || auswahl == JOptionPane.CLOSED_OPTION) {
                return;
            }
            ArrayList<Pruefung> geaendertePruefungen = new ArrayList<>();
            if (auswahl == 0) {
                geaendertePruefungen = abitur.removePruefungstag(pruefungstag, true);

            } else if (auswahl == 1) {
                geaendertePruefungen = abitur.removePruefungstag(pruefungstag, false);
            }
            for (Pruefung p : geaendertePruefungen) {
                datenbank.aktualisierePruefungsplanung(p);
            }
            planungsTabsAktualisieren();
            tableModel.fireTableDataChanged();
            datenbank.loeschePruefungstag(pruefungstag);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        System.out.println("TEST");
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
            tableModel.fireTableDataChanged();
            JOptionPane.showMessageDialog(this, count + " Prüfungsfolgen erfolgreich importiert.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Die Daten konnten nicht gespeichert werden:\n" + e.getMessage(), "Fehler beim Export", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void timeLineErzeugen() {
        TimelineDatenService service = new TimelineDatenService();
        List<KommissionsGruppe> gruppen = service.gibSortierteKommissionsGruppen(abitur, abitur.getPruefungstage().get(0));

        for (KommissionsGruppe gruppe : gruppen) {
            System.out.print(gruppe.pruefer() + " | " + gruppe.vorsitz() + " | "  + gruppe.schriftfuehrer() + "\n");
            List<Pruefung> pruefungen = gruppe.pruefungen();

            for (Pruefung p : pruefungen) {
                System.out.println("      " + p.getBeginn() + " " + p.getSchueler().getNachname() + " " + p.getSchueler().getVorname());
            }
        }
    }

    //Interface-Methoden für die Planungsaktionen:
    @Override
    public void nachBearbeitung(Pruefung pruefung) {
        verarbeitePlanungsaenderung(pruefung);
    }

    @Override
    public void planungsdatenKopieren(Pruefung pruefung) {
        kopiertePruefung = pruefung;
    }

    @Override
    public void planungsdatenUebertragen(Pruefung pruefung) {
        if (kopiertePruefung == null) {
            return;
        }
        pruefung.setVorsitz(kopiertePruefung.getVorsitz());
        pruefung.setSchriftfuehrer(kopiertePruefung.getSchriftfuehrer());
        pruefung.setRaum(kopiertePruefung.getRaum());
        verarbeitePlanungsaenderung(pruefung);
    }

}