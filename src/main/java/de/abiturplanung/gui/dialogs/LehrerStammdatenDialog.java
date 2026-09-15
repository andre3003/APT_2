package de.abiturplanung.gui.dialogs;
import de.abiturplanung.model.Amtsbezeichnung;
import de.abiturplanung.model.Fach;
import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LehrerStammdatenDialog extends JDialog {

    private final List<Fach> faecher;

    private final JTextField kuerzelFeld = new JTextField(10);
    private final JComboBox<String> anredeComboBox = new JComboBox<>(new String[]{"Herr", "Frau"});
    private final JTextField nachnameFeld = new JTextField(20);
    private final JTextField vornameFeld = new JTextField(20);
    private final JComboBox<Amtsbezeichnung> amtsbezeichnungComboBox = new JComboBox<>(Amtsbezeichnung.values());

    private final JComboBox<Fach> fak1ComboBox = new JComboBox<>();
    private final JComboBox<Fach> fak2ComboBox = new JComboBox<>();
    private final JComboBox<Fach> fak3ComboBox = new JComboBox<>();
    private final JComboBox<Fach> fak4ComboBox = new JComboBox<>();

    private LehrerEingabe ergebnis;

    public LehrerStammdatenDialog(Window owner, List<Fach> faecher) {
        super(owner, "Lehrer anlegen", ModalityType.APPLICATION_MODAL);
        this.faecher = faecher;

        setLayout(new BorderLayout(10, 10));

        JPanel eingabePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addZeile(eingabePanel, gbc, 0, "Kürzel:", kuerzelFeld);
        addZeile(eingabePanel, gbc, 1, "Anrede:", anredeComboBox);
        addZeile(eingabePanel, gbc, 2, "Nachname:", nachnameFeld);
        addZeile(eingabePanel, gbc, 3, "Vorname:", vornameFeld);
        addZeile(eingabePanel, gbc, 4, "Amtsbezeichnung:", amtsbezeichnungComboBox);
        addZeile(eingabePanel, gbc, 5, "Fakultas 1:", fak1ComboBox);
        addZeile(eingabePanel, gbc, 6, "Fakultas 2:", fak2ComboBox);
        addZeile(eingabePanel, gbc, 7, "Fakultas 3:", fak3ComboBox);
        addZeile(eingabePanel, gbc, 8, "Fakultas 4:", fak4ComboBox);

        fuelleFachComboBox(fak1ComboBox);
        fuelleFachComboBox(fak2ComboBox);
        fuelleFachComboBox(fak3ComboBox);
        fuelleFachComboBox(fak4ComboBox);

        JButton speichernButton = new JButton("Speichern");
        JButton abbrechenButton = new JButton("Abbrechen");

        speichernButton.addActionListener(e -> speichern());
        abbrechenButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(speichernButton);
        buttonPanel.add(abbrechenButton);

        add(eingabePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(speichernButton);

        pack();
        setLocationRelativeTo(owner);
    }

    private void addZeile(JPanel panel, GridBagConstraints gbc, int zeile, String text, JComponent komponente) {
        gbc.gridx = 0;
        gbc.gridy = zeile;
        gbc.weightx = 0;
        panel.add(new JLabel(text), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(komponente, gbc);
    }

    private void fuelleFachComboBox(JComboBox<Fach> comboBox) {
        comboBox.addItem(null);

        for (Fach fach : faecher) {
            comboBox.addItem(fach);
        }
    }

    private void speichern() {
        String kuerzel = kuerzelFeld.getText().trim();
        String nachname = nachnameFeld.getText().trim();
        String vorname = vornameFeld.getText().trim();

        if (kuerzel.isEmpty() || nachname.isEmpty() || vorname.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Kürzel, Nachname und Vorname müssen angegeben werden.",
                    "Unvollständige Eingabe",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Fach fak1 = (Fach) fak1ComboBox.getSelectedItem();
        Fach fak2 = (Fach) fak2ComboBox.getSelectedItem();
        Fach fak3 = (Fach) fak3ComboBox.getSelectedItem();
        Fach fak4 = (Fach) fak4ComboBox.getSelectedItem();


        Set<Fach> fakultas = new HashSet<>(); //Hashset erlaubt nicht, ein Element zweimal hinzuzufügen; add liefert in dem Fall false!

        if ((fak1 != null && !fakultas.add(fak1)) || (fak2 != null && !fakultas.add(fak2)) || (fak3 != null && !fakultas.add(fak3)) || (fak4 != null && !fakultas.add(fak4))) {
            JOptionPane.showMessageDialog(this,
                    "Eine Fakultas darf nicht mehrfach ausgewählt werden.", "Ungültige Eingabe", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ergebnis = new LehrerEingabe(
                kuerzel,
                (String) anredeComboBox.getSelectedItem(),
                nachname,
                vorname,
                (Amtsbezeichnung) amtsbezeichnungComboBox.getSelectedItem(),
                fak1,
                fak2,
                fak3,
                fak4);
        dispose();
    }

    public LehrerEingabe anzeigen() {
        setVisible(true);
        return ergebnis;
    }

    public record LehrerEingabe(String kuerzel, String anrede, String nachname, String vorname,
                                Amtsbezeichnung amtsbezeichnung, Fach fak1, Fach fak2, Fach fak3, Fach fak4) {
    }
}