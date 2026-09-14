package de.abiturplanung.gui.stammdaten;

import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Fach;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.function.Consumer;

public class LehrerStammdatenPanel extends JPanel {

    private final Abitur abitur;
    private final LehrerTableModel tableModel;
    private final JTable tabelle;

    public LehrerStammdatenPanel(Abitur abitur) {
        this.abitur = abitur;
        setLayout(new BorderLayout());
        tableModel = new LehrerTableModel(abitur);
        tabelle = new JTable(tableModel);
        tabelle.setAutoCreateRowSorter(true);
        tabelle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        konfiguriereSpalten();
        add(new JScrollPane(tabelle), BorderLayout.CENTER);
    }

    private void konfiguriereSpalten() {
        JComboBox<Fach> fachComboBox = new JComboBox<>();
        fachComboBox.addItem(null);

        for (Fach fach : abitur.getFaecher()) {
            fachComboBox.addItem(fach);
        }
        DefaultCellEditor fachEditor = new DefaultCellEditor(fachComboBox);
        for (int spalte = 4; spalte <= 7; spalte++) {
            TableColumn tableColumn = tabelle.getColumnModel().getColumn(spalte);
            tableColumn.setCellEditor(fachEditor);
        }
    }

    public void setNachAenderung(Consumer<LehrerTableModel.LehrerAenderung> aenderung) {
        tableModel.setNachAenderung(aenderung);
    }

    public void ansichtAktualisieren() {
        tableModel.aktualisieren();
    }
}