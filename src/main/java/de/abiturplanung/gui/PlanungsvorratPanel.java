package de.abiturplanung.gui;

import de.abiturplanung.gui.model.PruefungsTableModel;
import de.abiturplanung.gui.planung.PruefungTransferable;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Pruefung;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class PlanungsvorratPanel extends JPanel {
    Abitur abitur;
    private PruefungsTableModel tableModel;
    private TableRowSorter<PruefungsTableModel> sorter;
    private JTextField txtSuche;
    private JComboBox<String> cmbFach;
    private JComboBox<String> cmbKurs;
    private JComboBox<String> cmbStatus;
    private JCheckBox chkVollstaendigeAusblenden;

    public PlanungsvorratPanel(Abitur abitur) {
        this.abitur = abitur;
        initGui();
    }

    public void initGui() {
        this.setLayout(new BorderLayout(0, 0));
        tableModel = new PruefungsTableModel(abitur.getPruefungen());
        JTable pruefungstabelle = erstellePruefungenTabelle();
        JPanel kopfPanel = erstelleKopfPanel();
        this.add(kopfPanel, BorderLayout.NORTH);
        this.add(new JScrollPane(pruefungstabelle), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel erstelleKopfPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel ueberschrift = new JLabel("Prüfungen im 4. Abiturfach");
        ueberschrift.setFont(ueberschrift.getFont().deriveFont(Font.BOLD, 18f));
        panel.add(ueberschrift);
        panel.add(erstelleFilterPanel());
        return panel;
    }

    private JPanel erstelleFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        txtSuche = new JTextField(12);
        cmbFach = new JComboBox<>();
        cmbFach.addItem("Alle");
        cmbKurs = new JComboBox<>();
        cmbKurs.addItem("Alle");
        cmbStatus = new JComboBox<>(new String[]{"Alle", "unvollständig", "vollständig"});
        chkVollstaendigeAusblenden = new JCheckBox("Vollständige ausblenden");
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
        txtSuche.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterAktualisieren();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterAktualisieren();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterAktualisieren();
            }
        });
        cmbFach.addActionListener(e -> filterAktualisieren());
        cmbKurs.addActionListener(e -> filterAktualisieren());
        cmbStatus.addActionListener(e -> filterAktualisieren());
        chkVollstaendigeAusblenden.addActionListener(e -> filterAktualisieren());

        JLabel ueberschrift = new JLabel("Prüfungen im 4. Abiturfach");
        ueberschrift.setFont(ueberschrift.getFont().deriveFont(Font.BOLD, 18f));
        filterPanel.add(new JLabel("Suche:"));
        filterPanel.add(txtSuche);
        filterPanel.add(new JLabel("Fach:"));
        filterPanel.add(cmbFach);
        filterPanel.add(new JLabel("Kurs:"));
        filterPanel.add(cmbKurs);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(cmbStatus);
        filterPanel.add(chkVollstaendigeAusblenden);
        return filterPanel;
    }

    public void filterAktualisieren() {
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
    }

    private JTable erstellePruefungenTabelle() {
        JTable tabelle = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        tabelle.setRowSorter(sorter);
        tabelle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelle.setFillsViewportHeight(true);
        tabelle.setRowHeight(24);
        tabelle.setDragEnabled(true);
        sorter.setSortsOnUpdates(true);
        tabelle.setTransferHandler(new TransferHandler() {
            @Override
            protected Transferable createTransferable(JComponent component) {
                int viewZeile = tabelle.getSelectedRow();

                if (viewZeile < 0) {
                    return null;
                }

                int modelZeile = tabelle.convertRowIndexToModel(viewZeile);
                return new PruefungTransferable(tableModel.getPruefung(modelZeile));
            }

            @Override
            public int getSourceActions(JComponent component) {
                return MOVE;
            }
        });

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

        tabelle.setDefaultRenderer(Object.class, statusRenderer);
        return tabelle;
    }

    public void aktualisieren() {
        tableModel.fireTableDataChanged();
    }
}
