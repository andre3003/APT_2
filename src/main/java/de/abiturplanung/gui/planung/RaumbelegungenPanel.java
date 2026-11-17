package de.abiturplanung.gui.planung;

import de.abiturplanung.model.Kurs;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RaumbelegungenPanel extends JPanel {

    private final SchriftlicherPruefungstag pruefungstag;
    private final JPanel kartenPanel;
    private final BiConsumer<RaumbelegungAbiturklausuren, Kurs> speichereKursRaumZuordnung;
    private final BiConsumer<RaumbelegungAbiturklausuren, Kurs> entferneKursRaumzuordnung;
    private final Consumer<RaumbelegungAbiturklausuren> loescheRaumbelegungAbiturklausuren;
    private final Consumer<RaumbelegungAbiturklausuren> zeigeKlausurraumDetails;

    public RaumbelegungenPanel(SchriftlicherPruefungstag pruefungstag, BiConsumer<RaumbelegungAbiturklausuren, Kurs> speichereKursRaumZuordnung, BiConsumer<RaumbelegungAbiturklausuren, Kurs>
            entferneKursRaumZuordnung, Consumer<RaumbelegungAbiturklausuren> loescheRaumbelegungAbiturklausuren, Consumer<RaumbelegungAbiturklausuren> zeigeKlausurraumDetails) {
        super(new BorderLayout());
        this.speichereKursRaumZuordnung = speichereKursRaumZuordnung;
        this.entferneKursRaumzuordnung = entferneKursRaumZuordnung;
        this.loescheRaumbelegungAbiturklausuren = loescheRaumbelegungAbiturklausuren;
        this.zeigeKlausurraumDetails = zeigeKlausurraumDetails;
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
            KlausurRaumKarte karte = new KlausurRaumKarte(belegung, speichereKursRaumZuordnung, entferneKursRaumzuordnung, loescheRaumbelegungAbiturklausuren, zeigeKlausurraumDetails);
            kartenPanel.add(karte);
            karte.aktualisiereAnzeige();
        }
        kartenPanel.revalidate();
        kartenPanel.repaint();
    }
}