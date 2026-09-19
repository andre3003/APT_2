package de.abiturplanung.gui.stammdaten;

import de.abiturplanung.gui.dialogs.FachStammdatenDialog;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Fach;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class FachStammdatenPanel extends JPanel {
    private final Abitur abitur;
    private final JTable tabelle;
    private final FachTableModel fachTableModel;
    private JButton btFachHinzufuegen;
    private Consumer<FachStammdatenDialog.FachEingabe> neuesFachAnlegen;

    public FachStammdatenPanel(Abitur abitur) {
        this.abitur = abitur;
        setLayout(new BorderLayout());
        fachTableModel = new FachTableModel(abitur);
        tabelle = new JTable(fachTableModel);
        tabelle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelle.setAutoCreateRowSorter(true);
        btFachHinzufuegen = new JButton("Neues Fach hinzufügen");
        btFachHinzufuegen.addActionListener(this::btFachHinzufuegenAction);
        JPanel steuerung = new JPanel(new FlowLayout(FlowLayout.LEFT));
        steuerung.add(btFachHinzufuegen);
        add(steuerung, BorderLayout.NORTH);
        konfiguriereSpalten();
        add(new JScrollPane(tabelle), BorderLayout.CENTER);
    }

    public void setNachAenderung(Consumer<FachTableModel.FachAenderung> nachAenderung) {
        fachTableModel.setNachAenderung(nachAenderung);
    }

    public void setNeuesFachAnlegen(Consumer<FachStammdatenDialog.FachEingabe> neuesFachAnlegen) {
        this.neuesFachAnlegen = neuesFachAnlegen;
    }

    private void btFachHinzufuegenAction(ActionEvent e) {
        FachStammdatenDialog dialog = new FachStammdatenDialog(null, abitur);
        FachStammdatenDialog.FachEingabe eingabe = dialog.anzeigen();
        if (eingabe == null) {
            return;
        }
        neuesFachAnlegen.accept(eingabe);
    }

    public void konfiguriereSpalten() {
        JComboBox<Fach> fachComboBox = new JComboBox<>();
        fachComboBox.addItem(null);
        for (Fach fach : abitur.getFaecher()) {
            fachComboBox.addItem(fach);
        }
        tabelle.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(fachComboBox));
    }

    public void ansichtAktualisieren() {
        fachTableModel.aktualisieren();
        konfiguriereSpalten();
    }
}
