package de.abiturplanung.gui.planung;

import de.abiturplanung.model.Kurs;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.function.BiConsumer;

public class KlausurRaumKarte extends JPanel {

    private final RaumbelegungAbiturklausuren belegung;
    private final JPanel kursPanel;
    private final BiConsumer<RaumbelegungAbiturklausuren, Kurs> speichereKursZuRaumZuordnung;

    public KlausurRaumKarte(RaumbelegungAbiturklausuren belegung, BiConsumer<RaumbelegungAbiturklausuren, Kurs> speichereKursZuRaumZuordnung) {
        this.speichereKursZuRaumZuordnung = speichereKursZuRaumZuordnung;
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
        this.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDrop() && support.isDataFlavorSupported(KursTransferable.KURS_FLAVOR);
            }

            @Override
            public boolean importData(TransferSupport support) {
                if (!canImport(support)) return false;

                try {
                    Kurs kurs = (Kurs) support.getTransferable().getTransferData(KursTransferable.KURS_FLAVOR);
                    speichereKursZuRaumZuordnung.accept(belegung, kurs);
                    belegung.addKurs(kurs);
                    aktualisiereAnzeige();
                    return true;
                } catch (UnsupportedFlavorException | IOException e) {
                    return false;
                }
            }
        });
        aktualisiereAnzeige();
    }

    public void aktualisiereAnzeige() {
        kursPanel.removeAll();

        if (belegung.getKurse().isEmpty()) {
            kursPanel.add(new JLabel("Noch keine Kurse zugeordnet"));
        } else {
            for (Kurs kurs : belegung.getKurse()) {
                kursPanel.add(new JLabel(kurs.getBezeichnung() + "-" + kurs.getFachlehrer().getKuerzel()));
            }
        }
        kursPanel.revalidate();
        kursPanel.repaint();
    }
}
