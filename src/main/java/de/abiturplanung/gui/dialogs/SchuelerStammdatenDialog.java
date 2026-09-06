package de.abiturplanung.gui.dialogs;

import de.abiturplanung.model.Geschlecht;
import de.abiturplanung.Utilities;
import de.abiturplanung.model.Kurs;
import de.abiturplanung.model.Kursart;
import org.apache.xmlbeans.impl.regex.ParseException;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.*;
import java.util.List;

public class SchuelerStammdatenDialog extends JDialog {

    private final JTextField schildIdFeld = new JTextField(15);
    private final JTextField nachnameFeld = new JTextField(20);
    private final JTextField vornameFeld = new JTextField(20);
    private final JSpinner geburtsdatumSpinner = new JSpinner(new SpinnerDateModel());
    private final JComboBox<Geschlecht> geschlechtComboBox = new JComboBox<>(Geschlecht.values());
    private SchuelerEingabe ergebnis;
    private final JComboBox<Kurs> ab1ComboBox = new JComboBox<>();
    private final JComboBox<Kurs> ab2ComboBox = new JComboBox<>();
    private final JComboBox<Kurs> ab3ComboBox = new JComboBox<>();
    private final JComboBox<Kurs> ab4ComboBox = new JComboBox<>();



    public SchuelerStammdatenDialog(Window owner, List<Kurs> kurse) {
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


        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 4, 4, 4);
        JLabel abiturLabel = new JLabel("Abiturfächer");
        abiturLabel.setFont(abiturLabel.getFont().deriveFont(Font.BOLD));
        eingabePanel.add(abiturLabel, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(4, 4, 4, 4);

        addZeile(eingabePanel, gbc, 6, "AB1:", ab1ComboBox);
        addZeile(eingabePanel, gbc, 7, "AB2:", ab2ComboBox);
        addZeile(eingabePanel, gbc, 8, "AB3:", ab3ComboBox);
        addZeile(eingabePanel, gbc, 9, "AB4:", ab4ComboBox);

        ab1ComboBox.addItem(null);
        ab2ComboBox.addItem(null);
        ab3ComboBox.addItem(null);
        ab4ComboBox.addItem(null);

        ab1ComboBox.setRenderer(kursRenderer);
        ab2ComboBox.setRenderer(kursRenderer);
        ab3ComboBox.setRenderer(kursRenderer);
        ab4ComboBox.setRenderer(kursRenderer);

        for (Kurs kurs : kurse) {
            if (kurs.getKursart() == Kursart.LEISTUNGSKURS) {
                ab1ComboBox.addItem(kurs);
                ab2ComboBox.addItem(kurs);
            } else if (kurs.getKursart() == Kursart.GRUNDKURS) {
                ab3ComboBox.addItem(kurs);
                ab4ComboBox.addItem(kurs);
            }
        }

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
        Kurs ab1 = (Kurs) ab1ComboBox.getSelectedItem();
        Kurs ab2 = (Kurs) ab2ComboBox.getSelectedItem();
        Kurs ab3 = (Kurs) ab3ComboBox.getSelectedItem();
        Kurs ab4 = (Kurs) ab4ComboBox.getSelectedItem();

        if (schildId.isEmpty() || nachname.isEmpty() || vorname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Schild-ID, Nachname und Vorname müssen angegeben werden.", "Unvollständige Eingabe", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!schildId.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Die Schild-ID muss aus genau vier Ziffern bestehen.", "Ungültige Schild-ID", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (ab1 == null || ab2 == null || ab3 == null || ab4 == null) {
            JOptionPane.showMessageDialog(this, "Alle vier Abiturfächer müssen angegeben werden.", "Unvollständige Eingabe", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date datum = (Date) geburtsdatumSpinner.getValue();
        LocalDate geburtsdatum = datum.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Geschlecht geschlecht = (Geschlecht) geschlechtComboBox.getSelectedItem();
        ergebnis = new SchuelerEingabe(schildId, nachname, vorname, geburtsdatum, geschlecht, ab1, ab2, ab3, ab4);
        dispose();
    }

    public SchuelerEingabe anzeigen() {
        setVisible(true);
        return ergebnis;
    }

    public record SchuelerEingabe(String schildId, String nachname, String vorname, LocalDate geburtsdatum, Geschlecht geschlecht, Kurs ab1, Kurs ab2, Kurs ab3, Kurs ab4) {
    }

    private final ListCellRenderer<Object> kursRenderer = new DefaultListCellRenderer() {//Anonyme Unterklasse
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Kurs kurs) {
                setText(kurs.getBezeichnung());
            } else {
                setText("<bitte auswählen>");
            }

            return this;
        }
    };
}