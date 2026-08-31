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
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class SchuelerStammdatenPanel extends JPanel {
    Abitur abitur;
    private JTable schuelerTabelle;
    private SchuelerTableModel tableModel;
    private Consumer schuelerAnlegen;

    public SchuelerStammdatenPanel(Abitur abitur) {
        this.abitur = abitur;
        setLayout(new BorderLayout());
        JPanel steuerleiste = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton neuButton = new JButton("Neuen Schüler anlegen");
        neuButton.addActionListener(this::neuAction);
        JButton loeschenButton = new JButton("Schüler Löschen");
        steuerleiste.add(neuButton);
        steuerleiste.add(loeschenButton);
        add(steuerleiste, BorderLayout.NORTH);
        tableModel = new SchuelerTableModel(abitur.getSchueler());
        schuelerTabelle = new JTable(tableModel);
        schuelerTabelle.getColumnModel().getColumn(3).setCellRenderer(new LocalDateRenderer());
        schuelerTabelle.getColumnModel().getColumn(3).setCellEditor(new LocalDateEditor());
        add(new JScrollPane(schuelerTabelle), BorderLayout.CENTER);
        JComboBox<Geschlecht> geschlechtComboBox = new JComboBox<>(Geschlecht.values());
        schuelerTabelle.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(geschlechtComboBox));
    }

    public void setNachAenderung(Consumer<Schueler> nachAenderung) {
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

}

