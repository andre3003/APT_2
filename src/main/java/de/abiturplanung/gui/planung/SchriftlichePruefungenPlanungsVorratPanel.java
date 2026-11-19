package de.abiturplanung.gui.planung;
import de.abiturplanung.gui.model.SchriftlichePruefungenTableModel;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Kurs;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.util.List;

public class SchriftlichePruefungenPlanungsVorratPanel extends JPanel {
    private final Abitur abitur;
    private SchriftlichePruefungenTableModel tableModel;
    private final List<SchriftlicherPruefungstag> pruefungstage;
    private TableRowSorter<SchriftlichePruefungenTableModel> sorter;
    private SchriftlicherPruefungstag aktuellerPruefungstag;
    private JTable kursTabelle;


    public SchriftlichePruefungenPlanungsVorratPanel(Abitur abitur) {
        this.abitur = abitur;
        this.setLayout(new BorderLayout());
        this.pruefungstage = abitur.getSchriftlichePruefungstage();
        tableModel = new SchriftlichePruefungenTableModel(abitur);
        kursTabelle = new JTable(tableModel);

        kursTabelle.setTransferHandler(new TransferHandler() {
            @Override
            protected Transferable createTransferable(JComponent component) {
                int viewZeile = kursTabelle.getSelectedRow();
                if (viewZeile < 0) {
                    return null;
                }
                int modelZeile = kursTabelle.convertRowIndexToModel(viewZeile);
                return new KursTransferable(tableModel.getKurs(modelZeile));
            }
            @Override
            public int getSourceActions(JComponent component) {
                return COPY;
            }

        });

        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    int modelZeile = table.convertRowIndexToModel(row);
                    Kurs kurs = tableModel.getKurs(modelZeile);

                    if (istKursZugeordnet(kurs)) {
                        component.setBackground(new Color(230, 245, 230));
                    } else {
                        component.setBackground(table.getBackground());
                    }
                    component.setForeground(table.getForeground());
                }

                return component;
            }
        };
        kursTabelle.setDefaultRenderer(Object.class, statusRenderer);
        kursTabelle.setDragEnabled(true);
        sorter = new TableRowSorter<>(tableModel);
        kursTabelle.setRowSorter(sorter);
        kursTabelle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(kursTabelle);
        this.add(scrollPane, BorderLayout.CENTER);
        JLabel titel = new JLabel("""
                <html><center>
                <span style='font-size:18px'><b>Prüfungen im 1. bis 3. Abiturfach</b></span>
                <br><br>
                <span style='font-size:14px'>Prüfungskurse des Tages:</span>
                </center></html>
                """, SwingConstants.CENTER);

        titel.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        this.add(titel, BorderLayout.NORTH);
    }

    public void setzePruefungstag(SchriftlicherPruefungstag pt) {
        aktuellerPruefungstag = pt;
        tableModel.kurseEinlesen(pt);
    }


    private boolean istKursZugeordnet(Kurs kurs) {
        if (aktuellerPruefungstag == null) return false;
        for (RaumbelegungAbiturklausuren belegung : aktuellerPruefungstag.getRaumbelegungen()) {
            if (belegung.getKurse().contains(kurs)) {
                return true;
            }
        }
        return false;
    }
}
