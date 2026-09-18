package de.abiturplanung.gui.dialogs;

import javax.swing.*;
import java.awt.*;

public class RaumStammdatenDialog extends JDialog {

    private final JTextField bezeichnungFeld = new JTextField(10);
    private final JSpinner kapazitaetSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));

    private RaumEingabe ergebnis;

    public RaumStammdatenDialog(Window owner) {
        super(owner, "Neuen Raum anlegen", ModalityType.APPLICATION_MODAL);

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
        eingabePanel.add(new JLabel("Kapazität:"), gbc);

        gbc.gridx = 1;
        eingabePanel.add(kapazitaetSpinner, gbc);

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

        if (bezeichnung.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bitte geben Sie eine Raumbezeichnung ein.", "Unvollständige Eingabe", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int kapazitaet = (Integer) kapazitaetSpinner.getValue();

        ergebnis = new RaumEingabe(bezeichnung, kapazitaet);
        dispose();
    }

    public RaumEingabe anzeigen() {
        setVisible(true);
        return ergebnis;
    }

    public record RaumEingabe(String bezeichnung, int kapazitaet) {}
}
