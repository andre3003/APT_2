package de.abiturplanung.gui.dialogs;
import de.abiturplanung.model.Pruefung;
import de.abiturplanung.model.Pruefungsform;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PruefungsDialog extends JDialog {

    public PruefungsDialog(JFrame owner, Pruefung pruefung) {
        super(owner, "Prüfung", true);
        setLayout(new BorderLayout());
        JPanel datenPanel = new JPanel(new GridLayout(0, 2, 10, 8));
        datenPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        addZeile(datenPanel, "Schüler:", pruefung.getSchueler().getNachname() + ", " + pruefung.getSchueler().getVorname());
        addZeile(datenPanel, "Abiturfach:", pruefung.getAbiturfach().name());
        addZeile(datenPanel, "Fach:", pruefung.getKurs().getFach());
        addZeile(datenPanel, "Kurs:", pruefung.getKurs().getBezeichnung());
        addZeile(datenPanel, "Prüfer:", pruefung.getPruefer() == null ? "" : pruefung.getPruefer().getNachname() + " (" + pruefung.getPruefer().getKuerzel()  + ")");
        addZeile(datenPanel, "Prüfungsform:", pruefung.getPruefungsform() == Pruefungsform.MUENDLICH ? "mündlich" : "schriftlich" );
        add(datenPanel, BorderLayout.CENTER);

        JButton schliessenButton = new JButton("Schließen");
        schliessenButton.addActionListener(e -> dispose());

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