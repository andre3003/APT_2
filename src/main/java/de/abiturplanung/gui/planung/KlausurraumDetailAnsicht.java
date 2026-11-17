package de.abiturplanung.gui.planung;

import de.abiturplanung.model.Pruefung;

import javax.swing.*;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class KlausurraumDetailAnsicht extends JDialog {

    public KlausurraumDetailAnsicht(Window owner, RaumbelegungAbiturklausuren belegung, List<Pruefung> pruefungen) {
        super(owner, "Klausurraum " + belegung.getRaum().getBezeichnung(), ModalityType.MODELESS);
        setLayout(new BorderLayout(10, 10));

        // Kopfbereich
        JPanel kopfPanel = new JPanel(new BorderLayout());
        JLabel lbTitel = new JLabel("Klausurraum " + belegung.getRaum().getBezeichnung());
        lbTitel.setFont(lbTitel.getFont().deriveFont(Font.BOLD, 16f));

        JLabel lbInfo = new JLabel(belegung.getPruefungstag().getDatum() + "   |   "
                + belegung.getAktuelleBelegung() + " / " + belegung.getRaum().getKapazitaet());

        kopfPanel.add(lbTitel, BorderLayout.WEST);
        kopfPanel.add(lbInfo, BorderLayout.EAST);
        kopfPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        add(kopfPanel, BorderLayout.NORTH);

        // Tabelle
        String[] spalten = {"Platz", "Nachname", "Vorname", "Kurs", "Lehrer"};
        DefaultTableModel model = new DefaultTableModel(spalten, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        int platz = 1;
        for (Pruefung pruefung : pruefungen) {
            model.addRow(new Object[]{
                    platz++,
                    pruefung.getSchueler().getNachname(),
                    pruefung.getSchueler().getVorname(),
                    pruefung.getKurs().getBezeichnung(),
                    pruefung.getKurs().getFachlehrer().getKuerzel()
            });
        }

        JTable tabelle = new JTable(model);
        tabelle.setRowHeight(24);
        tabelle.setFillsViewportHeight(true);
        tabelle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelle.setAutoCreateRowSorter(true);

        add(new JScrollPane(tabelle), BorderLayout.CENTER);

        // Schaltflächen
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btDrucken = new JButton("Drucken");
        JButton btSchliessen = new JButton("Schließen");

        btDrucken.setEnabled(false); // PDF-Anbindung folgt später
        btSchliessen.addActionListener(e -> dispose());

        buttonPanel.add(btDrucken);
        buttonPanel.add(btSchliessen);
        add(buttonPanel, BorderLayout.SOUTH);

        // Fenster
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setSize(750, 550);
        setLocationRelativeTo(owner);
    }
}

