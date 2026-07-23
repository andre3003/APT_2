package de.abiturplanung.gui;
import de.abiturplanung.gui.dialogs.PruefungsDialog;
import de.abiturplanung.gui.model.PruefungsTableModel;
import de.abiturplanung.model.Abitur;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(Abitur abitur) {
        setTitle("Abiturplanung");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setMinimumSize(new Dimension(800, 450));
        setLocationRelativeTo(null);
        setJMenuBar(erstelleMenueleiste());
        PruefungsTableModel tableModel = new PruefungsTableModel(abitur.getPruefungen());
        JTable pruefungstabelle = new JTable(tableModel);
        pruefungstabelle.setAutoCreateRowSorter(true);
        pruefungstabelle.setSelectionMode( ListSelectionModel.SINGLE_SELECTION);
        pruefungstabelle.setFillsViewportHeight(true);
        pruefungstabelle.setRowHeight(24);
        pruefungstabelle.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int zeile = pruefungstabelle.rowAtPoint(e.getPoint());
                    if (zeile >= 0) {
                        zeile = pruefungstabelle.convertRowIndexToModel(zeile);
                        PruefungsTableModel model = (PruefungsTableModel) pruefungstabelle.getModel();
                        new PruefungsDialog(MainFrame.this, abitur, model.getPruefung(zeile)).setVisible(true);
                    }
                }
            }
        });

        JLabel ueberschrift = new JLabel("Prüfungen");
        ueberschrift.setFont(ueberschrift.getFont().deriveFont(Font.BOLD, 18f));

        JPanel inhalt = new JPanel(new BorderLayout(0, 8));
        inhalt.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inhalt.add(ueberschrift, BorderLayout.NORTH);
        inhalt.add(new JScrollPane(pruefungstabelle), BorderLayout.CENTER);

        JLabel statusleiste = new JLabel(abitur.getPruefungen().size() + " Prüfungen geladen");
        statusleiste.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(inhalt, BorderLayout.CENTER);
        add(statusleiste, BorderLayout.SOUTH);
    }

    private JMenuBar erstelleMenueleiste() {
        JMenuBar menueleiste = new JMenuBar();
        JMenu dateiMenue = new JMenu("Datei");
        JMenuItem beendenEintrag = new JMenuItem("Beenden");
        beendenEintrag.addActionListener(event -> dispose());
        dateiMenue.add(beendenEintrag);
        menueleiste.add(dateiMenue);
        return menueleiste;
    }
}