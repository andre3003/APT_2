package de.abiturplanung.gui.planung;

import de.abiturplanung.model.Kurs;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

public class RaumbelegungenPanel extends JPanel {

    private final SchriftlicherPruefungstag pruefungstag;
    private final JPanel kartenPanel;
    private final BiConsumer<RaumbelegungAbiturklausuren, Kurs> speichereKursZuRaumZuordnung;

    public RaumbelegungenPanel(SchriftlicherPruefungstag pruefungstag, BiConsumer<RaumbelegungAbiturklausuren, Kurs> speichereKursZuRaumZuordnung) {
        super(new BorderLayout());
        this.speichereKursZuRaumZuordnung =speichereKursZuRaumZuordnung;
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
        for (RaumbelegungAbiturklausuren belegung : pruefungstag.getRaumbelegungen()) {
            kartenPanel.add(new KlausurRaumKarte(belegung,speichereKursZuRaumZuordnung));
        }
        kartenPanel.revalidate();
        kartenPanel.repaint();
    }
}