package de.abiturplanung.gui.stammdaten;

import de.abiturplanung.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.function.Consumer;

public class AbiturfaecherPanel extends JPanel {
    private final JComboBox<Kurs> ab1ComboBox = new JComboBox<>();
    private final JComboBox<Kurs> ab2ComboBox = new JComboBox<>();
    private final JComboBox<Kurs> ab3ComboBox = new JComboBox<>();
    private final JComboBox<Kurs> ab4ComboBox = new JComboBox<>();
    private Schueler schueler;
    private JButton uebernehmenButton;
    public record AbiturfaecherEingabe(Schueler schueler, Kurs ab1, Kurs ab2, Kurs ab3, Kurs ab4) {}
    private Consumer<AbiturfaecherEingabe> uebernehmen;


    public AbiturfaecherPanel(List<Kurs> kurse) {
        JPanel eingabePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
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
        uebernehmenButton = new JButton("Änderungen übernehmen");
        uebernehmenButton.addActionListener(this::uebernehmenButtonAction);
        uebernehmenButton.setEnabled(false);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(uebernehmenButton);
        setLayout(new BorderLayout());
        add(eingabePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void uebernehmenButtonAction(ActionEvent actionEvent) {
        if (schueler == null || uebernehmen == null) {
            return;
        }
        AbiturfaecherEingabe eingabe = new AbiturfaecherEingabe(
                schueler,
                (Kurs) ab1ComboBox.getSelectedItem(),
                (Kurs) ab2ComboBox.getSelectedItem(),
                (Kurs) ab3ComboBox.getSelectedItem(),
                (Kurs) ab4ComboBox.getSelectedItem());

        uebernehmen.accept(eingabe);
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

    public void setSchueler(Schueler schueler) {
        this.schueler = schueler;

        if (schueler == null) {
            ab1ComboBox.setSelectedItem(null);
            ab2ComboBox.setSelectedItem(null);
            ab3ComboBox.setSelectedItem(null);
            ab4ComboBox.setSelectedItem(null);
            uebernehmenButton.setEnabled(false);
            return;
        }

        ab1ComboBox.setSelectedItem(schueler.getPruefung(Abiturfach.AB1).getKurs());
        ab2ComboBox.setSelectedItem(schueler.getPruefung(Abiturfach.AB2).getKurs());
        ab3ComboBox.setSelectedItem(schueler.getPruefung(Abiturfach.AB3).getKurs());
        ab4ComboBox.setSelectedItem(schueler.getPruefung(Abiturfach.AB4).getKurs());
        uebernehmenButton.setEnabled(true);
    }

    public void setUebernehmen(Consumer<AbiturfaecherEingabe> uebernehmen) {
        this.uebernehmen = uebernehmen;
    }
}
