package de.abiturplanung.gui;

import de.abiturplanung.gui.dialogs.PruefungsDialog;
import de.abiturplanung.gui.model.PruefungsTableModel;
import de.abiturplanung.gui.planung.PlanungsMatrixPanel;
import de.abiturplanung.gui.planung.PruefungTransferable;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Pruefung;
import de.abiturplanung.model.Pruefungstag;
import de.abiturplanung.persistence.Datenbank;
import de.config.AppEinstellungen;
import de.config.AppPfade;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.function.Consumer;

public class MainFrame extends JFrame {

    private Abitur abitur;
    List<PlanungsMatrixPanel> matrixPanels = new ArrayList<>();
    private final JTabbedPane planungsTabs = new JTabbedPane();
    private PruefungsTableModel tableModel;
    private Datenbank datenbank;
    private final JPanel arbeitsbereich = new JPanel(new BorderLayout());
    private final JLabel statusleiste = new JLabel();

//    public MainFrame(Abitur abitur, Datenbank datenbank) {
//        this.abitur = abitur;
//        this.datenbank = datenbank;
//        tableModel = new PruefungsTableModel(abitur.getPruefungen());
//        setTitle("Abiturplanung");
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setSize(1400, 750);
//        setMinimumSize(new Dimension(1000, 550));
//        setLocationRelativeTo(null);
//        setJMenuBar(erstelleMenueleiste());
//
//        JTable pruefungstabelle = new JTable(tableModel);
//        TableRowSorter<PruefungsTableModel> sorter = new TableRowSorter<>(tableModel);
//
//        pruefungstabelle.setRowSorter(sorter);
//        pruefungstabelle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        pruefungstabelle.setFillsViewportHeight(true);
//        pruefungstabelle.setRowHeight(24);
//        pruefungstabelle.setDragEnabled(true);
//
//        sorter.setSortsOnUpdates(true);
//
//        pruefungstabelle.setTransferHandler(new TransferHandler() {
//            @Override
//            protected Transferable createTransferable(JComponent component) {
//                int viewZeile = pruefungstabelle.getSelectedRow();
//
//                if (viewZeile < 0) {
//                    return null;
//                }
//
//                int modelZeile = pruefungstabelle.convertRowIndexToModel(viewZeile);
//                return new PruefungTransferable(tableModel.getPruefung(modelZeile));
//            }
//
//            @Override
//            public int getSourceActions(JComponent component) {
//                return MOVE;
//            }
//        });
//
//        pruefungstabelle.addMouseListener(new java.awt.event.MouseAdapter() {
//            @Override
//            public void mouseClicked(java.awt.event.MouseEvent e) {
//                if (e.getClickCount() != 2) {
//                    return;
//                }
//
//                int viewZeile = pruefungstabelle.rowAtPoint(e.getPoint());
//
//                if (viewZeile < 0) {
//                    return;
//                }
//
//                int modelZeile = pruefungstabelle.convertRowIndexToModel(viewZeile);
//                PruefungsDialog dialog = new PruefungsDialog(MainFrame.this, abitur, tableModel.getPruefung(modelZeile));
//                dialog.setVisible(true);
//
//                if (dialog.isGespeichert()) {
//                    tableModel.fireTableRowsUpdated(modelZeile, modelZeile);
//                }
//            }
//        });
//
//        // ----------------------------------------------------------
//        // Farbliche Statusanzeige
//        // ----------------------------------------------------------
//
//        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
//            @Override
//            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//
//                if (!isSelected) {
//                    int modelZeile = table.convertRowIndexToModel(row);
//                    Pruefung pruefung = tableModel.getPruefung(modelZeile);
//
//                    if (pruefung.istVollstaendigGeplant()) {
//                        component.setBackground(new Color(230, 245, 230));
//                    } else {
//                        component.setBackground(new Color(255, 248, 220));
//                    }
//
//                    component.setForeground(Color.BLACK);
//                }
//
//                return component;
//            }
//        };
//
//        pruefungstabelle.setDefaultRenderer(Object.class, statusRenderer);
//
//        // ----------------------------------------------------------
//        // Filter
//        // ----------------------------------------------------------
//
//        JTextField txtSuche = new JTextField(12);
//
//        JComboBox<String> cmbFach = new JComboBox<>();
//        cmbFach.addItem("Alle");
//
//        JComboBox<String> cmbKurs = new JComboBox<>();
//        cmbKurs.addItem("Alle");
//
//        JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Alle", "unvollständig", "vollständig"});
//
//        JCheckBox chkVollstaendigeAusblenden = new JCheckBox("Vollständige ausblenden");
//
//        TreeSet<String> faecher = new TreeSet<>();
//        TreeSet<String> kurse = new TreeSet<>();
//
//        for (int i = 0; i < tableModel.getRowCount(); i++) {
//            Pruefung pruefung = tableModel.getPruefung(i);
//            faecher.add(pruefung.getKurs().getFach());
//            kurse.add(pruefung.getKurs().getBezeichnung());
//        }
//
//        for (String fach : faecher) {
//            cmbFach.addItem(fach);
//        }
//
//        for (String kurs : kurse) {
//            cmbKurs.addItem(kurs);
//        }
//
//        Runnable filterAktualisieren = () -> {
//            List<RowFilter<PruefungsTableModel, Integer>> filter = new ArrayList<>();
//
//            String suche = txtSuche.getText().trim();
//
//            if (!suche.isEmpty()) {
//                filter.add(RowFilter.regexFilter("(?i)" + Pattern.quote(suche)));
//            }
//
//            String fach = (String) cmbFach.getSelectedItem();
//
//            if (fach != null && !fach.equals("Alle")) {
//                filter.add(RowFilter.regexFilter("^" + Pattern.quote(fach) + "$", PruefungsTableModel.SPALTE_FACH));
//            }
//
//            String kurs = (String) cmbKurs.getSelectedItem();
//
//            if (kurs != null && !kurs.equals("Alle")) {
//                filter.add(RowFilter.regexFilter("^" + Pattern.quote(kurs) + "$", PruefungsTableModel.SPALTE_KURS));
//            }
//
//            String status = (String) cmbStatus.getSelectedItem();
//
//            if (status != null && !status.equals("Alle")) {
//                filter.add(RowFilter.regexFilter("^" + Pattern.quote(status) + "$", PruefungsTableModel.SPALTE_STATUS));
//            }
//
//            if (chkVollstaendigeAusblenden.isSelected()) {
//                filter.add(RowFilter.notFilter(RowFilter.regexFilter("^vollständig$", PruefungsTableModel.SPALTE_STATUS)));
//            }
//
//            sorter.setRowFilter(filter.isEmpty() ? null : RowFilter.andFilter(filter));
//        };
//
//        txtSuche.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                filterAktualisieren.run();
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                filterAktualisieren.run();
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                filterAktualisieren.run();
//            }
//        });
//
//        cmbFach.addActionListener(e -> filterAktualisieren.run());
//        cmbKurs.addActionListener(e -> filterAktualisieren.run());
//        cmbStatus.addActionListener(e -> filterAktualisieren.run());
//        chkVollstaendigeAusblenden.addActionListener(e -> filterAktualisieren.run());
//
//        // ----------------------------------------------------------
//        // Linker Arbeitsvorrat
//        // ----------------------------------------------------------
//
//        JLabel ueberschrift = new JLabel("Prüfungen im 4. Abiturfach");
//        ueberschrift.setFont(ueberschrift.getFont().deriveFont(Font.BOLD, 18f));
//
//        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
//        filterPanel.add(new JLabel("Suche:"));
//        filterPanel.add(txtSuche);
//        filterPanel.add(new JLabel("Fach:"));
//        filterPanel.add(cmbFach);
//        filterPanel.add(new JLabel("Kurs:"));
//        filterPanel.add(cmbKurs);
//        filterPanel.add(new JLabel("Status:"));
//        filterPanel.add(cmbStatus);
//        filterPanel.add(chkVollstaendigeAusblenden);
//
//        JPanel kopfPanel = new JPanel();
//        kopfPanel.setLayout(new BoxLayout(kopfPanel, BoxLayout.Y_AXIS));
//        kopfPanel.add(ueberschrift);
//        kopfPanel.add(Box.createVerticalStrut(5));
//        kopfPanel.add(filterPanel);
//
//        JPanel pruefungsPanel = new JPanel(new BorderLayout(0, 8));
//        pruefungsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//        pruefungsPanel.add(kopfPanel, BorderLayout.NORTH);
//        pruefungsPanel.add(new JScrollPane(pruefungstabelle), BorderLayout.CENTER);
//
//        // ----------------------------------------------------------
//        // Matrix
//        // ----------------------------------------------------------
//
//        // ----------------------------------------------------------
//// Matrix
//// ----------------------------------------------------------
//
//
//
//        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pruefungsPanel, planungsTabs);
//        splitPane.setResizeWeight(0.32);
//        splitPane.setDividerLocation(480);
//        splitPane.setOneTouchExpandable(true);
//
//        JLabel statusleiste = new JLabel(tableModel.getRowCount() + " Prüfungen im 4. Abiturfach");
//        statusleiste.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
//        planungsTabsAktualisieren();
//        add(splitPane, BorderLayout.CENTER);
//        add(statusleiste, BorderLayout.SOUTH);
//    }

    public MainFrame(Abitur abitur, Datenbank datenbank) {
        this();
        setPlanung(abitur, datenbank);
    }



    private void planungsTabsAktualisieren() {
        planungsTabs.removeAll();
        matrixPanels.clear();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        for (Pruefungstag pruefungstag : abitur.getPruefungstage()) {
            PlanungsMatrixPanel matrixPanel = new PlanungsMatrixPanel(abitur, pruefungstag);
            matrixPanels.add(matrixPanel);

            Consumer<Pruefung> ansichtAktualisieren = pruefung -> {
                try {
                    datenbank.aktualisierePruefungsplanung(pruefung);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Die Planungsdaten konnten nicht gespeichert werden:\n" + e.getMessage(), "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
                }

                tableModel.fireTableDataChanged();

                for (PlanungsMatrixPanel panel : matrixPanels) {
                    panel.aktualisieren();
                }
            };

            matrixPanel.setNachBearbeitung(ansichtAktualisieren);
            planungsTabs.addTab(pruefungstag.getDatum().format(formatter), matrixPanel);
        }

        revalidate();
        repaint();
    }

    public MainFrame() {
        setTitle("Abiturplanung");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 750);
        setMinimumSize(new Dimension(1000, 550));
        setLocationRelativeTo(null);

        setJMenuBar(erstelleMenueleiste());

        statusleiste.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        add(arbeitsbereich, BorderLayout.CENTER);
        add(statusleiste, BorderLayout.SOUTH);

        zeigeKeinePlanung();
    }

    private JMenuBar erstelleMenueleiste() {
        JMenuBar menueleiste = new JMenuBar();
        JMenu dateiMenue = new JMenu("Datei");

        JMenuItem neuePlanungEintrag = new JMenuItem("Neue Planung...");
        neuePlanungEintrag.addActionListener(this::neuePlanungAction);

        dateiMenue.add(neuePlanungEintrag);
        dateiMenue.addSeparator();

        JMenuItem planungOeffnenEintrag = new JMenuItem("Planung öffnen...");
        planungOeffnenEintrag.addActionListener(this::planungOeffnenAction);

        dateiMenue.add(planungOeffnenEintrag);

        JMenuItem backupEintrag = new JMenuItem("Backup erstellen...");
        backupEintrag.addActionListener(this::backupErstellenAction);

        dateiMenue.addSeparator();
        dateiMenue.add(backupEintrag);

        JMenuItem beendenEintrag = new JMenuItem("Beenden");
        beendenEintrag.addActionListener(event -> dispose());
        dateiMenue.addSeparator();
        dateiMenue.add(beendenEintrag);
        menueleiste.add(dateiMenue);

        JMenu planungMenue = new JMenu("Planung");

        JMenuItem pruefungstagHinzufuegen = new JMenuItem("Prüfungstag hinzufügen");
        pruefungstagHinzufuegen.addActionListener(this::pruefungstagHinzufuegenAction);

        JMenuItem pruefungstagEntfernen = new JMenuItem("Prüfungstag entfernen");
        pruefungstagEntfernen.addActionListener(this::pruefungstagEntfernenAction);

        planungMenue.add(pruefungstagHinzufuegen);
        planungMenue.add(pruefungstagEntfernen);

        menueleiste.add(planungMenue);

        return menueleiste;
    }

    private void backupErstellenAction(ActionEvent event) {
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

    private void planungOeffnenAction(ActionEvent event) {
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

            setPlanung(neuesAbitur, neueDatenbank);

            AppEinstellungen einstellungen = new AppEinstellungen();
            einstellungen.setLetzteDatenbank(pfad);

        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Die Planung konnte nicht geöffnet werden:\n" + exception.getMessage(), "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void neuePlanungAction(ActionEvent event) {
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

            setPlanung(neuesAbitur, neueDatenbank);

            AppEinstellungen einstellungen = new AppEinstellungen();
            einstellungen.setLetzteDatenbank(pfad);

        } catch (IOException | SQLException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Die neue Planung konnte nicht angelegt werden:\n" + exception.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void zeigeKeinePlanung() {
        abitur = null;
        datenbank = null;

        arbeitsbereich.removeAll();

        JLabel hinweis = new JLabel("Keine Planung geöffnet", SwingConstants.CENTER);
        hinweis.setFont(hinweis.getFont().deriveFont(Font.BOLD, 20f));

        arbeitsbereich.add(hinweis, BorderLayout.CENTER);
        statusleiste.setText("Keine Planung geöffnet");

        arbeitsbereich.revalidate();
        arbeitsbereich.repaint();
    }

    public void setPlanung(Abitur abitur, Datenbank datenbank) {
        this.abitur = abitur;
        this.datenbank = datenbank;

        arbeitsbereich.removeAll();

        JPanel planungsbereich = erstellePlanungsbereich();
        arbeitsbereich.add(planungsbereich, BorderLayout.CENTER);

        statusleiste.setText("Datenbank: " + datenbank.getPfad().getFileName() + " | " + abitur.getPruefungen().size() + " Prüfungen geladen");

        arbeitsbereich.revalidate();
        arbeitsbereich.repaint();
    }

    private JPanel erstellePlanungsbereich() {
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


    private void pruefungstagHinzufuegenAction(ActionEvent event) {
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

        abitur.addPruefungstag(new Pruefungstag(datum));
        planungsTabsAktualisieren();
    }

    private void pruefungstagEntfernenAction(ActionEvent event) {
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

        if (anzahlPruefungen == 0) {
            int bestaetigung = JOptionPane.showConfirmDialog(
                    this,
                    "Prüfungstag " + pruefungstag.getDatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " wirklich löschen?",
                    "Prüfungstag löschen",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (bestaetigung == JOptionPane.YES_OPTION) {
                abitur.removePruefungstag(pruefungstag, true);
                planungsTabsAktualisieren();
            }

            return;
        }

        Object[] optionen = {"Kommissionen behalten", "Kommissionen mitlöschen", "Abbrechen"};

        int auswahl = JOptionPane.showOptionDialog(
                this,
                "An diesem Prüfungstag sind " + anzahlPruefungen + " Prüfungen geplant.\n\n" + "Planungsdaten werden gelöscht.\n" + "Sollen die bestehenden Kommissionen erhalten bleiben?","Prüfungstag löschen",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                optionen,
                optionen[0]
        );

        if (auswahl == 0) {
            abitur.removePruefungstag(pruefungstag, true);
            planungsTabsAktualisieren();
            tableModel.fireTableDataChanged();
        } else if (auswahl == 1) {
            abitur.removePruefungstag(pruefungstag, false);
            planungsTabsAktualisieren();
            tableModel.fireTableDataChanged();
        }
    }

}