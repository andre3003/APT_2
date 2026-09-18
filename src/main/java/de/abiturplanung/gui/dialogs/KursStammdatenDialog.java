package de.abiturplanung.gui.dialogs;

import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Fach;
import de.abiturplanung.model.Lehrer;

import javax.swing.*;
import java.awt.*;

public class KursStammdatenDialog extends JDialog {

    private final JTextField bezeichnungFeld = new JTextField(10);
    private final JComboBox<Fach> fachComboBox = new JComboBox<>();
    private final JComboBox<Lehrer> fachlehrerComboBox = new JComboBox<>();

    private KursEingabe ergebnis;

    public KursStammdatenDialog(Window owner, Abitur abitur) {
        super(owner, "Neuen Kurs anlegen", ModalityType.APPLICATION_MODAL);

        fachComboBox.addItem(null);
        for (Fach fach : abitur.getFaecher()) {
            fachComboBox.addItem(fach);
        }

        fachlehrerComboBox.addItem(null);
        for (Lehrer lehrer : abitur.getLehrer()) {
            fachlehrerComboBox.addItem(lehrer);
        }

        JPanel eingabePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        eingabePanel.add(new JLabel("Bezeichnung:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        eingabePanel.add(bezeichnungFeld, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        eingabePanel.add(new JLabel("Fach:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        eingabePanel.add(fachComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        eingabePanel.add(new JLabel("Fachlehrer:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        eingabePanel.add(fachlehrerComboBox, gbc);

        JButton speichernButton = new JButton("Speichern");
        JButton abbrechenButton = new JButton("Abbrechen");

        speichernButton.addActionListener(e -> speichern());
        abbrechenButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(speichernButton);
        buttonPanel.add(abbrechenButton);

        setLayout(new BorderLayout());
        add(eingabePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(speichernButton);

        pack();
        setLocationRelativeTo(owner);
    }

    private void speichern() {
        String bezeichnung = bezeichnungFeld.getText().trim();
        Fach fach = (Fach) fachComboBox.getSelectedItem();
        Lehrer fachlehrer = (Lehrer) fachlehrerComboBox.getSelectedItem();

        if (bezeichnung.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Bitte geben Sie eine Kursbezeichnung ein.",
                    "Unvollständige Eingabe",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (fach == null) {
            JOptionPane.showMessageDialog(this,
                    "Bitte wählen Sie ein Fach aus.",
                    "Unvollständige Eingabe",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (fachlehrer == null) {
            JOptionPane.showMessageDialog(this,
                    "Bitte wählen Sie einen Fachlehrer aus.",
                    "Unvollständige Eingabe",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ergebnis = new KursEingabe(bezeichnung, fach, fachlehrer);
        dispose();
    }

    public KursEingabe anzeigen() {
        setVisible(true);
        return ergebnis;
    }

    public record KursEingabe(String bezeichnung, Fach fach, Lehrer fachlehrer) {}
}