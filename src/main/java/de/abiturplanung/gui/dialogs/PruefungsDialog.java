package de.abiturplanung.gui.dialogs;

import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Lehrer;
import de.abiturplanung.model.Pruefung;
import de.abiturplanung.model.Raum;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PruefungsDialog extends JDialog {

    private final Pruefung pruefung;

    private final JComboBox<Raum> cmbRaum;
    private final JComboBox<Lehrer> cmbPruefer;
    private final JComboBox<Lehrer> cmbSchriftfuehrer;
    private final JComboBox<Lehrer> cmbVorsitz;

    private boolean gespeichert;

    public PruefungsDialog(JFrame owner,Abitur abitur, Pruefung pruefung) {
        super(owner, "Prüfung", true);
        this.pruefung = pruefung;
        setLayout(new BorderLayout());
        JPanel hauptPanel = new JPanel();
        hauptPanel.setLayout(new BoxLayout(hauptPanel, BoxLayout.Y_AXIS));
        hauptPanel.setBorder( new EmptyBorder(15, 15, 15, 15)
        );

        // ----------------------------------------------------------
        // Stammdaten
        // ----------------------------------------------------------

        JLabel stammdatenTitel = new JLabel("Stammdaten");
        stammdatenTitel.setFont(stammdatenTitel.getFont().deriveFont(Font.BOLD));
        hauptPanel.add(stammdatenTitel);
        hauptPanel.add(Box.createVerticalStrut(8));
        JPanel stammdatenPanel =  new JPanel(new GridLayout(0, 2, 10, 8));
        addZeile(stammdatenPanel, "Schüler:", pruefung.getSchueler().getNachname()  + ", " + pruefung.getSchueler().getVorname() );
        addZeile(stammdatenPanel, "Abiturfach:",  pruefung.getAbiturfach().name());
        addZeile(stammdatenPanel, "Fach:", pruefung.getKurs().getFach() );
        addZeile(stammdatenPanel, "Kurs:", pruefung.getKurs().getBezeichnung());
        addZeile( stammdatenPanel, "Fachlehrer:", lehrerText( pruefung.getKurs().getFachlehrer()));

        addZeile(stammdatenPanel, "Prüfungsform:", pruefung.getPruefungsform().toString() );

        hauptPanel.add(stammdatenPanel);

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

        cmbRaum = erstelleRaumCombo( abitur, pruefung.getRaum() );

        addFeld(planungPanel,                 "Raum:",  cmbRaum );
        cmbPruefer = erstelleLehrerCombo( abitur, pruefung.getPruefer() );

        addFeld(planungPanel, "Prüfer:", cmbPruefer );

        cmbSchriftfuehrer = erstelleLehrerCombo(abitur, pruefung.getSchriftfuehrer() );

        addFeld(planungPanel, "Schriftführer:", cmbSchriftfuehrer );

        cmbVorsitz = erstelleLehrerCombo( abitur, pruefung.getVorsitz() );

        addFeld(planungPanel, "Vorsitz:", cmbVorsitz );

        hauptPanel.add(planungPanel);

        add(hauptPanel, BorderLayout.CENTER );

        // ----------------------------------------------------------
        // Buttons
        // ----------------------------------------------------------

        JButton okButton = new JButton("OK");
        okButton.addActionListener(this::okAction);

        JButton abbrechenButton = new JButton("Abbrechen");
        abbrechenButton.addActionListener(this::abbrechenAction);

        JPanel buttonPanel = new JPanel( new FlowLayout(FlowLayout.RIGHT));

        buttonPanel.add(okButton);
        buttonPanel.add(abbrechenButton);

        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(okButton);

        pack();
        setLocationRelativeTo(owner);
    }

    public boolean isGespeichert() {
        return gespeichert;
    }

    private JComboBox<Raum> erstelleRaumCombo(
            Abitur abitur,
            Raum auswahl) {

        JComboBox<Raum> combo = new JComboBox<>();

        combo.addItem(null);

        for (Raum raum : abitur.getRaeume()) {
            combo.addItem(raum);
        }

        combo.setRenderer(new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,  boolean cellHasFocus) {

                        super.getListCellRendererComponent(list, value, index,  isSelected, cellHasFocus );

                        setText( value == null ? "---" : value.toString());
                        return this;
                    }
                }
        );

        combo.setSelectedItem(auswahl);

        return combo;
    }

    private JComboBox<Lehrer> erstelleLehrerCombo( Abitur abitur, Lehrer auswahl) {

        JComboBox<Lehrer> combo = new JComboBox<>();

        combo.addItem(null);

        for (Lehrer lehrer : abitur.getLehrer()) {
            combo.addItem(lehrer);
        }

        combo.setRenderer(new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus );
                        setText(lehrerText((Lehrer) value));
                        return this;
                    }
                }
        );

        combo.setSelectedItem(auswahl);

        return combo;
    }

    private void okAction(ActionEvent event) {

        pruefung.setRaum((Raum) cmbRaum.getSelectedItem());

        pruefung.setPruefer((Lehrer) cmbPruefer.getSelectedItem());

        pruefung.setSchriftfuehrer((Lehrer) cmbSchriftfuehrer.getSelectedItem());

        pruefung.setVorsitz((Lehrer) cmbVorsitz.getSelectedItem());

        gespeichert = true;

        dispose();
    }

    private void abbrechenAction(ActionEvent event) {
        dispose();
    }

    private void addZeile(JPanel panel, String bezeichnung, String wert) {

        panel.add(new JLabel(bezeichnung));
        panel.add(new JLabel(wert));
    }

    private void addFeld(JPanel panel, String bezeichnung, JComponent feld) {
        panel.add(new JLabel(bezeichnung));
        panel.add(feld);
    }

    private String lehrerText(Lehrer lehrer) {

        if (lehrer == null) {
            return "---";
        }

        return lehrer.getNachname() + " (" + lehrer.getKuerzel() + ")";
    }
}