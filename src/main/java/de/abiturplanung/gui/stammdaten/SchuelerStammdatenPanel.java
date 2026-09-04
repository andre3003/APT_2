package de.abiturplanung.gui.stammdaten;

import de.abiturplanung.Utilities;
import de.abiturplanung.gui.dialogs.SchuelerStammdatenDialog;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Geschlecht;
import de.abiturplanung.model.Schueler;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.function.Consumer;

public class SchuelerStammdatenPanel extends JPanel {
    Abitur abitur;
    private JTable schuelerTabelle;
    private SchuelerTableModel tableModel;
    private Consumer schuelerAnlegen;
    private Consumer schuelerLoeschen;

    public SchuelerStammdatenPanel(Abitur abitur) {
        this.abitur = abitur;
        setLayout(new BorderLayout());
        JPanel steuerleiste = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton neuButton = new JButton("Neuen Schüler anlegen");
        neuButton.addActionListener(this::neuAction);
        JButton loeschenButton = new JButton("Schüler Löschen");
        loeschenButton.addActionListener(this::loeschenAction);
        steuerleiste.add(neuButton);
        steuerleiste.add(loeschenButton);
        add(steuerleiste, BorderLayout.NORTH);
        tableModel = new SchuelerTableModel(abitur.getSchuelerList());
        schuelerTabelle = new JTable(tableModel);
        schuelerTabelle.getColumnModel().getColumn(3).setCellRenderer(new LocalDateRenderer());
        schuelerTabelle.getColumnModel().getColumn(3).setCellEditor(new LocalDateEditor());
        add(new JScrollPane(schuelerTabelle), BorderLayout.CENTER);
        JComboBox<Geschlecht> geschlechtComboBox = new JComboBox<>(Geschlecht.values());
        schuelerTabelle.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(geschlechtComboBox));
    }

    private void loeschenAction(ActionEvent actionEvent) {
        int zeile = schuelerTabelle.getSelectedRow();
        if (zeile == -1) {
            JOptionPane.showMessageDialog(this, "Bitte wählen Sie zunächst einen Schüler aus.", "Kein Schüler ausgewählt", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        zeile = schuelerTabelle.convertRowIndexToModel(zeile);
        String schild_ID = (String)  tableModel.getValueAt(zeile,0);
        int bestaetigung = JOptionPane.showConfirmDialog(this, "Den Schüler wirklich löschen?\n Alle zugehörigem Prüfungen werden ebenfalls gelöscht", "Schüler löschen", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (bestaetigung == JOptionPane.YES_OPTION) {
            schuelerLoeschen.accept(schild_ID);
        }
    }

    public void setSchuelerLoeschenAction(Consumer<String> schuelerLoeschen) {
        this.schuelerLoeschen = schuelerLoeschen;
    }

    public void setNachAenderung(Consumer<SchuelerTableModel.SchuelerAenderung> nachAenderung) {
        tableModel.setNachAenderung(nachAenderung);
    }

    public void setSchuelerAnlegen(Consumer<SchuelerStammdatenDialog.SchuelerEingabe> schuelerAnlegen) {
        this.schuelerAnlegen = schuelerAnlegen;
    }


    private static class LocalDateRenderer extends DefaultTableCellRenderer {
        @Override
        protected void setValue(Object value) {
            setText(value instanceof LocalDate datum ? Utilities.formatiereDatum(datum) : "");
        }
    }

    private void neuAction(ActionEvent e) {
        SchuelerStammdatenDialog dialog = new SchuelerStammdatenDialog(SwingUtilities.getWindowAncestor(this));
        SchuelerStammdatenDialog.SchuelerEingabe eingabe = dialog.anzeigen();
        if (eingabe != null && schuelerAnlegen != null) {
            schuelerAnlegen.accept(eingabe);
        }
    }

    public void aktualisieren() {
        tableModel.aktualisieren();
    }

}

