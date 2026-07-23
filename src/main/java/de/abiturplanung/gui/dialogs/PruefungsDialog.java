package de.abiturplanung.gui.dialogs;

import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Pruefung;
import de.abiturplanung.model.Raum;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PruefungsDialog extends JDialog {
   private final Abitur abitur;


    public PruefungsDialog(JFrame owner, Abitur abitur, Pruefung pruefung) {
        super(owner, "Prüfung", true);
        setLayout(new BorderLayout());
        JPanel hauptPanel = new JPanel();
        hauptPanel.setLayout(new BoxLayout(hauptPanel, BoxLayout.Y_AXIS));
        hauptPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        this.abitur = abitur;

        // ----------------------------------------------------------
        // Stammdaten
        // ----------------------------------------------------------

        JLabel stammdatenTitel = new JLabel("Stammdaten");
        stammdatenTitel.setFont(stammdatenTitel.getFont().deriveFont(Font.BOLD));
        hauptPanel.add(stammdatenTitel);
        hauptPanel.add(Box.createVerticalStrut(8));

        JPanel stammdatenPanel = new JPanel(new GridLayout(0, 2, 10, 8));
        addZeile(stammdatenPanel, "Schüler:", pruefung.getSchueler().getNachname()  + ", " + pruefung.getSchueler().getVorname());
        addZeile(stammdatenPanel, "Abiturfach:", pruefung.getAbiturfach().name());
        addZeile(stammdatenPanel, "Fach:", pruefung.getKurs().getFach() );
        addZeile(stammdatenPanel, "Kurs:", pruefung.getKurs().getBezeichnung());
        addZeile(stammdatenPanel, "Prüfer:", pruefung.getPruefer().getNachname() + " (" + pruefung.getPruefer().getKuerzel() + ")" );
        addZeile(stammdatenPanel, "Prüfungsform:", pruefung.getPruefungsform().toString() );
        hauptPanel.add(stammdatenPanel);

        // ----------------------------------------------------------
        // Abstand
        // ----------------------------------------------------------

        hauptPanel.add(Box.createVerticalStrut(20));
        hauptPanel.add(new JSeparator());
        hauptPanel.add(Box.createVerticalStrut(20));

        // ----------------------------------------------------------
        // Planung
        // ----------------------------------------------------------

        JLabel planungTitel = new JLabel("Planung");
        planungTitel.setFont(planungTitel.getFont().deriveFont(Font.BOLD));
        hauptPanel.add(planungTitel);
        hauptPanel.add(Box.createVerticalStrut(8));

        JPanel planungPanel = new JPanel(new GridLayout(0, 2, 10, 8));
        addZeile(planungPanel, "Prüfungstag:", pruefung.getPruefungstag() == null ? "---" : pruefung.getPruefungstag().toString());
        addZeile(planungPanel, "Beginn:", pruefung.getBeginn() == null ? "---" : pruefung.getBeginn().toString());
        planungPanel.add(new JLabel("Raum:"));
        JComboBox<Raum> cmbRaum = new JComboBox<>();
        for (Raum raum : abitur.getRaeume()) {
            cmbRaum.addItem(raum);
        }
        cmbRaum.setSelectedItem(pruefung.getRaum());
        planungPanel.add(cmbRaum);
        hauptPanel.add(planungPanel);
        add(hauptPanel, BorderLayout.CENTER);

        // ----------------------------------------------------------
        // Button
        // ----------------------------------------------------------

        JButton schliessenButton = new JButton("Schließen");
        schliessenButton.addActionListener(g -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(schliessenButton);

        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    private void addZeile(JPanel panel, String bezeichnung, String wert) {
        panel.add(new JLabel(bezeichnung));
        panel.add(new JLabel(wert));
    }
}