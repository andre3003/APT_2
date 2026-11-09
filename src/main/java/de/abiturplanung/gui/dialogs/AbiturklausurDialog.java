package de.abiturplanung.gui.dialogs;

import de.abiturplanung.gui.planung.Abiturklausur;
import de.abiturplanung.gui.planung.Hilfsmittel;
import de.abiturplanung.model.Abitur;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

public class AbiturklausurDialog extends JDialog {
    private final Abiturklausur klausur;
    private final JTextField tfBearbeitungszeit = new JTextField(6);
    private final JTextField tfAuswahlzeit = new JTextField(6);
    private final Map<Hilfsmittel, JCheckBox> checkboxen = new LinkedHashMap<>();
    private Eingaben eingaben;

    public record Eingaben(Integer bearbeitungszeit, Integer auswahlzeit, List<Hilfsmittel> hilfsmittel) {
    }

    public AbiturklausurDialog(Window owner, Abitur abitur, Abiturklausur klausur) {
        super(owner, "Abiturklausur bearbeiten", ModalityType.APPLICATION_MODAL);
        this.klausur = klausur;

        JPanel inhalt = new JPanel(new GridLayout(0, 2, 10, 8));
        inhalt.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        inhalt.add(new JLabel("Fach:"));
        inhalt.add(new JLabel(klausur.getFach().getBezeichnung()));
        inhalt.add(new JLabel("Kursart:"));
        inhalt.add(new JLabel(klausur.getKursart().toString()));

        inhalt.add(new JLabel("Bearbeitungszeit (min):"));
        tfBearbeitungszeit.setText(klausur.getBearbeitungszeit() == null ? "" : klausur.getBearbeitungszeit().toString());
        inhalt.add(tfBearbeitungszeit);

        inhalt.add(new JLabel("Auswahlzeit (min):"));
        tfAuswahlzeit.setText(klausur.getAuswahlzeit() == null ? "" : klausur.getAuswahlzeit().toString());
        inhalt.add(tfAuswahlzeit);

        JPanel hilfsmittelPanel = new JPanel(new GridLayout(0, 1));
        hilfsmittelPanel.setBorder(BorderFactory.createTitledBorder("Erlaubte Hilfsmittel"));

        for (Hilfsmittel hilfsmittel : abitur.getHilfsmittel()) {
            if (!hilfsmittel.isAktiv() && !klausur.getHilfsmittel().contains(hilfsmittel)) continue;

            JCheckBox checkbox = new JCheckBox(hilfsmittel.getBezeichnung());
            checkbox.setSelected(klausur.getHilfsmittel().contains(hilfsmittel));
            checkboxen.put(hilfsmittel, checkbox);
            hilfsmittelPanel.add(checkbox);
        }

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btSpeichern = new JButton("Speichern");
        JButton btAbbrechen = new JButton("Abbrechen");
        buttons.add(btSpeichern);
        buttons.add(btAbbrechen);

        btAbbrechen.addActionListener(e -> dispose());
        btSpeichern.addActionListener(this::btSpeichernAction);

        JPanel gesamt = new JPanel(new BorderLayout(10, 10));
        gesamt.add(inhalt, BorderLayout.NORTH);
        gesamt.add(hilfsmittelPanel, BorderLayout.CENTER);
        gesamt.add(buttons, BorderLayout.SOUTH);

        setContentPane(gesamt);
        pack();
        setLocationRelativeTo(owner);
    }

    private void btSpeichernAction(ActionEvent event) {
        Integer bearbeitungszeit;
        Integer auswahlzeit;

        try {
            bearbeitungszeit = leseMinuten(tfBearbeitungszeit.getText());
            auswahlzeit = leseMinuten(tfAuswahlzeit.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Bitte gültige, nicht negative Minutenwerte eingeben.", "Ungültige Eingabe", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Hilfsmittel> hilfsmittel = new ArrayList<>();

        for (Map.Entry<Hilfsmittel, JCheckBox> eintrag : checkboxen.entrySet()) {
            if (eintrag.getValue().isSelected()) hilfsmittel.add(eintrag.getKey());
        }

        eingaben = new Eingaben(bearbeitungszeit, auswahlzeit, List.copyOf(hilfsmittel));
        dispose();
    }

    private Integer leseMinuten(String text) {
        text = text.trim();
        if (text.isEmpty()) return null;

        int minuten = Integer.parseInt(text);
        if (minuten < 0) throw new NumberFormatException();

        return minuten;
    }

    public Eingaben getEingaben() {
        return eingaben;
    }
}