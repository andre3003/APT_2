package de.abiturplanung.gui.stammdaten;

import de.abiturplanung.gui.dialogs.KursStammdatenDialog;
import de.abiturplanung.gui.dialogs.LehrerStammdatenDialog;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Fach;
import de.abiturplanung.model.Lehrer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class KursStammdatenPanel extends JPanel {
    private final Abitur abitur;
    private final KursTableModel tableModel;
    private final JTable tabelle;
    private JButton btKursAnlegen = new JButton("Neuen Kurs anlegen");
    private Consumer<KursStammdatenDialog.KursEingabe> kusAnlegen;

    public KursStammdatenPanel(Abitur abitur) {
        this.abitur = abitur;
        tableModel = new KursTableModel(abitur);
        setLayout(new BorderLayout());
        tabelle = new JTable(tableModel);
        tabelle.setAutoCreateRowSorter(true);
        tabelle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        konfiguriereSpalten();
        add(new JScrollPane(tabelle), BorderLayout.CENTER);
        btKursAnlegen.addActionListener(this::btKursAnlegenAction);
        JPanel steuerung = new JPanel(new FlowLayout(FlowLayout.LEFT));
        steuerung.add(btKursAnlegen);
        add(steuerung, BorderLayout.NORTH);
    }

    private void btKursAnlegenAction(ActionEvent event) {
        KursStammdatenDialog dialog = new KursStammdatenDialog(null, abitur);
        KursStammdatenDialog.KursEingabe eingabe = dialog.anzeigen();
        kusAnlegen.accept(eingabe);
    }

    public void setKursAnlegen(Consumer<KursStammdatenDialog.KursEingabe> kursAnlegen) {
        this.kusAnlegen = kursAnlegen;
    }

    private void konfiguriereSpalten() {
        JComboBox<Fach> fachComboBox = new JComboBox<>();
        for (Fach fach : abitur.getFaecher()) {
            fachComboBox.addItem(fach);
        }
        tabelle.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(fachComboBox));

        JComboBox<Lehrer> lehrerComboBox = new JComboBox<>();
        for (Lehrer lehrer : abitur.getLehrer()) {
            lehrerComboBox.addItem(lehrer);
        }
        tabelle.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(lehrerComboBox));
    }

    public void setNachAenderung(Consumer<KursTableModel.KursAenderung> aenderung) {
        tableModel.setNachAenderung(aenderung);
    }

    public void ansichtAktualisieren() {
        tableModel.aktualisieren();
    }
}
