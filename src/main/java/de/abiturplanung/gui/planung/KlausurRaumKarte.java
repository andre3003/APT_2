package de.abiturplanung.gui.planung;

import de.abiturplanung.model.Kurs;

import javax.swing.*;
import java.awt.*;

public class KlausurRaumKarte extends JPanel {

        private final RaumbelegungAbiturklausuren belegung;
        private final JPanel kursPanel;

        public KlausurRaumKarte(RaumbelegungAbiturklausuren belegung) {
            this.belegung = belegung;

            setLayout(new BorderLayout(5, 10));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
            ));

            JLabel titel = new JLabel("Raum " + belegung.getRaum().getBezeichnung());
            titel.setFont(titel.getFont().deriveFont(Font.BOLD, 15f));
            add(titel, BorderLayout.NORTH);

            kursPanel = new JPanel();
            kursPanel.setLayout(new BoxLayout(kursPanel, BoxLayout.Y_AXIS));
            add(kursPanel, BorderLayout.CENTER);
            aktualisiereAnzeige();
        }

        public void aktualisiereAnzeige() {
            kursPanel.removeAll();

            if (belegung.getKurse().isEmpty()) {
                kursPanel.add(new JLabel("Noch keine Kurse zugeordnet"));
            } else {
                for (Kurs kurs : belegung.getKurse()) {
                    kursPanel.add(new JLabel(kurs.getBezeichnung()));
                }
            }
            kursPanel.revalidate();
            kursPanel.repaint();
        }
    }
