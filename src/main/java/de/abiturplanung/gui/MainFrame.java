package de.abiturplanung.gui;

import de.abiturplanung.gui.dialogs.PruefungsDialog;
import de.abiturplanung.gui.model.PruefungsTableModel;
import de.abiturplanung.gui.planung.PlanungsMatrixPanel;
import de.abiturplanung.gui.planung.PruefungTransferable;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Pruefung;
import de.abiturplanung.model.Pruefungstag;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class MainFrame extends JFrame {

    private final Abitur abitur;
    List<PlanungsMatrixPanel> matrixPanels = new ArrayList<>();

    public MainFrame(Abitur abitur) {
        this.abitur = abitur;

        setTitle("Abiturplanung");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 750);
        setMinimumSize(new Dimension(1000, 550));
        setLocationRelativeTo(null);
        setJMenuBar(erstelleMenueleiste());

        PruefungsTableModel tableModel = new PruefungsTableModel(abitur.getPruefungen());
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

        JTabbedPane planungsTabs = new JTabbedPane();

        for (Pruefungstag pruefungstag : abitur.getPruefungstage()) {
            PlanungsMatrixPanel matrixPanel = new PlanungsMatrixPanel(abitur, pruefungstag);
            matrixPanels.add(matrixPanel);
            planungsTabs.addTab(pruefungstag.getDatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), matrixPanel);
        }

        Runnable ansichtAktualisieren = () -> {
            tableModel.fireTableDataChanged();
            for (PlanungsMatrixPanel matrixPanel : matrixPanels) {
                matrixPanel.aktualisieren();
            }
        };

        for (PlanungsMatrixPanel matrixPanel : matrixPanels) {
            matrixPanel.setNachBearbeitung(ansichtAktualisieren);
        }

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pruefungsPanel, planungsTabs);
        splitPane.setResizeWeight(0.32);
        splitPane.setDividerLocation(480);
        splitPane.setOneTouchExpandable(true);

        JLabel statusleiste = new JLabel(tableModel.getRowCount() + " Prüfungen im 4. Abiturfach");
        statusleiste.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        add(splitPane, BorderLayout.CENTER);
        add(statusleiste, BorderLayout.SOUTH);
    }

    private JMenuBar erstelleMenueleiste() {
        JMenuBar menueleiste = new JMenuBar();
        JMenu dateiMenue = new JMenu("Datei");

        JMenuItem beendenEintrag = new JMenuItem("Beenden");
        beendenEintrag.addActionListener(event -> dispose());

        dateiMenue.add(beendenEintrag);
        menueleiste.add(dateiMenue);

        return menueleiste;
    }
}