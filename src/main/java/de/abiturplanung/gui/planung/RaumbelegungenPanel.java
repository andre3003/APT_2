package de.abiturplanung.gui.planung;

import javax.swing.*;
import java.awt.*;

public class RaumbelegungenPanel extends JPanel {

    private final SchriftlicherPruefungstag pruefungstag;
    private final JPanel kartenPanel;

    public RaumbelegungenPanel(SchriftlicherPruefungstag pruefungstag) {
        super(new BorderLayout());
        this.pruefungstag = pruefungstag;

        kartenPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        kartenPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel container = new JPanel(new BorderLayout());
        container.add(kartenPanel, BorderLayout.NORTH);

        add(new JScrollPane(container), BorderLayout.CENTER);

        aktualisiereAnzeige();
    }

    public void aktualisiereAnzeige() {
        kartenPanel.removeAll();
        System.out.println(pruefungstag.getRaumbelegungen().size());

        for (RaumbelegungAbiturklausuren belegung : pruefungstag.getRaumbelegungen()) {
            kartenPanel.add(new KlausurRaumKarte(belegung));
        }

        kartenPanel.revalidate();
        kartenPanel.repaint();
    }
}