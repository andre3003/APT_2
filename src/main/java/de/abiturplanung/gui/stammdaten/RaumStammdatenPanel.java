package de.abiturplanung.gui.stammdaten;

import de.abiturplanung.gui.dialogs.RaumStammdatenDialog;
import de.abiturplanung.model.Abitur;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class RaumStammdatenPanel extends JPanel {
    private final Abitur abitur;
    private final RaumTableModel tableModel;
    private final JTable tabelle;
    private final JButton btRaumHinzufuegen = new JButton("Raum hinzufügen");
    private Consumer<RaumStammdatenDialog.RaumEingabe> raumAnlegen;

    public RaumStammdatenPanel(Abitur abitur) {
        this.abitur = abitur;
        setLayout(new BorderLayout());
        tableModel = new RaumTableModel(abitur);
        tabelle = new JTable(tableModel);
        tabelle.setAutoCreateRowSorter(true);
        tabelle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        btRaumHinzufuegen.addActionListener(this::raumHinzufuegenAction);
        add(new JScrollPane(tabelle), BorderLayout.CENTER);
        JPanel steuernung =new JPanel(new FlowLayout(FlowLayout.LEFT));
        steuernung.add(btRaumHinzufuegen);
        add(steuernung, BorderLayout.NORTH);
    }

    public void setNachAenderung(Consumer<RaumTableModel.RaumAenderung> aenderung) {
        tableModel.setNachAenderung(aenderung);
    }

    public void raumHinzufuegenAction(ActionEvent event) {
        RaumStammdatenDialog dialog = new RaumStammdatenDialog(SwingUtilities.getWindowAncestor(this));
        RaumStammdatenDialog.RaumEingabe eingabe = dialog.anzeigen();
        if (eingabe != null) {
            raumAnlegen.accept(eingabe);
        }
    }

    public void setRaumAnlegenAction(Consumer<RaumStammdatenDialog.RaumEingabe> raumAnlegen){
        this.raumAnlegen = raumAnlegen;
    }

    public void ansichtAktualisieren() {
        tableModel.aktualisieren();
    }
}
