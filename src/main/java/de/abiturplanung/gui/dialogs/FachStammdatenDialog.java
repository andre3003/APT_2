package de.abiturplanung.gui.dialogs;

import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Fach;

import javax.swing.*;
import java.awt.*;

public class FachStammdatenDialog extends JDialog {

    private final JTextField kuerzelFeld = new JTextField(10);
    private final JTextField bezeichnungFeld = new JTextField(20);
    private final JComboBox<Fach> stammfachComboBox = new JComboBox<>();
    private final JTextField faechergruppeFeld = new JTextField(20);

    private FachEingabe ergebnis;

    public FachStammdatenDialog(Window owner, Abitur abitur) {
        super(owner, "Neues Fach anlegen", ModalityType.APPLICATION_MODAL);

        stammfachComboBox.addItem(null);
        for (Fach fach : abitur.getFaecher()) {
            stammfachComboBox.addItem(fach);
        }

        JPanel eingabePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        eingabePanel.add(new JLabel("Kürzel:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        eingabePanel.add(kuerzelFeld, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        eingabePanel.add(new JLabel("Bezeichnung:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        eingabePanel.add(bezeichnungFeld, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        eingabePanel.add(new JLabel("Stammfach:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        eingabePanel.add(stammfachComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        eingabePanel.add(new JLabel("Fächergruppe:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        eingabePanel.add(faechergruppeFeld, gbc);

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
        String kuerzel = kuerzelFeld.getText().trim();

        if (kuerzel.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Bitte geben Sie ein Fachkürzel ein.",
                    "Unvollständige Eingabe",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bezeichnung = leerZuNull(bezeichnungFeld.getText());
        Fach stammfach = (Fach) stammfachComboBox.getSelectedItem();
        String faechergruppe = leerZuNull(faechergruppeFeld.getText());

        ergebnis = new FachEingabe(kuerzel, bezeichnung, stammfach, faechergruppe);
        dispose();
    }

    private String leerZuNull(String text) {
        String wert = text.trim();
        return wert.isEmpty() ? null : wert;
    }

    public FachEingabe anzeigen() {
        setVisible(true);
        return ergebnis;
    }

    public record FachEingabe(String kuerzel, String bezeichnung, Fach stammfach, String faechergruppe) {}
}