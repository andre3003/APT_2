package de.abiturplanung.gui.dialogs;

import de.abiturplanung.model.Geschlecht;
import de.abiturplanung.Utilities;
import org.apache.xmlbeans.impl.regex.ParseException;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class SchuelerStammdatenDialog extends JDialog {

    private final JTextField schildIdFeld = new JTextField(15);
    private final JTextField nachnameFeld = new JTextField(20);
    private final JTextField vornameFeld = new JTextField(20);
    private final JSpinner geburtsdatumSpinner = new JSpinner(new SpinnerDateModel());
    private final JComboBox<Geschlecht> geschlechtComboBox = new JComboBox<>(Geschlecht.values());
    private SchuelerEingabe ergebnis;

    public SchuelerStammdatenDialog(Window owner) {
        super(owner, "Schüler anlegen", ModalityType.APPLICATION_MODAL);

        JPanel eingabePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        addZeile(eingabePanel, gbc, 0, "Schild-ID:", schildIdFeld);
        addZeile(eingabePanel, gbc, 1, "Nachname:", nachnameFeld);
        addZeile(eingabePanel, gbc, 2, "Vorname:", vornameFeld);
        geburtsdatumSpinner.setEditor(new JSpinner.DateEditor(geburtsdatumSpinner, "dd.MM.yyyy"));
        LocalDate vorauswahl = LocalDate.now().minusYears(18);
        Date startDatum = Date.from(vorauswahl.atStartOfDay(ZoneId.systemDefault()).toInstant());
        geburtsdatumSpinner.setValue(startDatum);
        addZeile(eingabePanel, gbc, 3, "Geburtsdatum:", geburtsdatumSpinner);
        addZeile(eingabePanel, gbc, 4, "Geschlecht:", geschlechtComboBox);

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

    private void addZeile(JPanel panel, GridBagConstraints gbc, int zeile, String beschriftung, Component eingabe) {
        gbc.gridx = 0;
        gbc.gridy = zeile;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(beschriftung), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(eingabe, gbc);
    }

    private void speichern() {
        String schildId = schildIdFeld.getText().trim();
        String nachname = nachnameFeld.getText().trim();
        String vorname = vornameFeld.getText().trim();

        if (schildId.isEmpty() || nachname.isEmpty() || vorname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Schild-ID, Nachname und Vorname müssen angegeben werden.", "Unvollständige Eingabe", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!schildId.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Die Schild-ID muss aus genau vier Ziffern bestehen.", "Ungültige Schild-ID", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date datum = (Date) geburtsdatumSpinner.getValue();
        LocalDate geburtsdatum = datum.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Geschlecht geschlecht = (Geschlecht) geschlechtComboBox.getSelectedItem();
        ergebnis = new SchuelerEingabe(schildId, nachname, vorname, geburtsdatum, geschlecht);
        dispose();
    }

    public SchuelerEingabe anzeigen() {
        setVisible(true);
        return ergebnis;
    }

    public record SchuelerEingabe(String schildId, String nachname, String vorname, LocalDate geburtsdatum, Geschlecht geschlecht) {
    }
}