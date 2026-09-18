package de.abiturplanung.gui.stammdaten;

import de.abiturplanung.gui.dialogs.LehrerStammdatenDialog;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Amtsbezeichnung;
import de.abiturplanung.model.Fach;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class LehrerStammdatenPanel extends JPanel {

    private final Abitur abitur;
    private final LehrerTableModel tableModel;
    private final JTable tabelle;
    private Consumer<String> lehererLoeschen;
    private Consumer<LehrerStammdatenDialog.LehrerEingabe> lehrerAnlegen;

    public LehrerStammdatenPanel(Abitur abitur) {
        this.abitur = abitur;
        setLayout(new BorderLayout());
        tableModel = new LehrerTableModel(abitur);
        tabelle = new JTable(tableModel);
        tabelle.setAutoCreateRowSorter(true);
        tabelle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        konfiguriereSpalten();
        add(new JScrollPane(tabelle), BorderLayout.CENTER);
        JPanel steuerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btLehrerAnlegen = new JButton("Neuen Lehrer anlegen");
        btLehrerAnlegen.addActionListener(this::btLehrerAnlegenAction);
        steuerPanel.add(btLehrerAnlegen);
        JButton btLehrerLoeschen = new JButton("Lehrer löschen");
        btLehrerLoeschen.addActionListener(this::btLehrerLoeschenAction);
        steuerPanel.add(btLehrerLoeschen);
        add(steuerPanel, BorderLayout.NORTH);
    }

    private void konfiguriereSpalten() {
        JComboBox<Amtsbezeichnung> amtsbezeichnungComboBox = new JComboBox<>(Amtsbezeichnung.values());
        tabelle.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(amtsbezeichnungComboBox));

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

    private void btLehrerLoeschenAction(ActionEvent actionEvent) {
        int zeile = tabelle.getSelectedRow();
        if (zeile == -1) {
            JOptionPane.showMessageDialog(this, "Bitte wählen Sie zunächst einen Lehrer aus.", "Kein Lehrer ausgewählt", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        zeile = tabelle.convertRowIndexToModel(zeile);
        String kuerzel = (String)  tableModel.getValueAt(zeile,0);
        int bestaetigung = JOptionPane.showConfirmDialog(this, "Den Lehrer wirklich löschen?\n", "Lehrer löschen", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (bestaetigung == JOptionPane.YES_OPTION) {
            lehererLoeschen.accept(kuerzel);
        }
    }

    private void btLehrerAnlegenAction(ActionEvent actionEvent) {
        LehrerStammdatenDialog dialog = new LehrerStammdatenDialog(SwingUtilities.getWindowAncestor(this), abitur.getFaecher());
        LehrerStammdatenDialog.LehrerEingabe eingabe = dialog.anzeigen();
        if (eingabe != null) {
            lehrerAnlegen.accept(eingabe);
        }
    }

    public void setLehrerLoeschenAction(Consumer<String> lehererLoeschen) {
        this.lehererLoeschen = lehererLoeschen;
    }

    public void setLehrerAnlegenAction(Consumer<LehrerStammdatenDialog.LehrerEingabe> lehrerAnlegen) {
        this.lehrerAnlegen = lehrerAnlegen;
    }

    public void setNachAenderung(Consumer<LehrerTableModel.LehrerAenderung> aenderung) {
        tableModel.setNachAenderung(aenderung);
    }

    public void ansichtAktualisieren() {
        tableModel.aktualisieren();
    }
}