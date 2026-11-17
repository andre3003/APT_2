package de.abiturplanung.gui.planung;

import de.abiturplanung.model.Kurs;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class KlausurRaumKarte extends JPanel {

    private final RaumbelegungAbiturklausuren belegung;
    private final JPanel kursePanel;
    private final BiConsumer<RaumbelegungAbiturklausuren, Kurs> speichereKursRaumZuordnung;
    private final BiConsumer<RaumbelegungAbiturklausuren, Kurs> entferneKursRaumzuordnung;
    private final Consumer<RaumbelegungAbiturklausuren> loescheRaumbelegungAbiturklausuren;
    private final Consumer<RaumbelegungAbiturklausuren> zeigeKlausurraumDetails;


    public KlausurRaumKarte(RaumbelegungAbiturklausuren belegung, BiConsumer<RaumbelegungAbiturklausuren, Kurs> speichereKursRaumZuordnung, BiConsumer<RaumbelegungAbiturklausuren, Kurs>
            entferneKursRaumzuordnung, Consumer<RaumbelegungAbiturklausuren> loescheRaumbelegungAbiturklausuren, Consumer<RaumbelegungAbiturklausuren> zeigeKlausurraumDetails) {
        this.speichereKursRaumZuordnung = speichereKursRaumZuordnung;
        this.entferneKursRaumzuordnung = entferneKursRaumzuordnung;
        this.loescheRaumbelegungAbiturklausuren = loescheRaumbelegungAbiturklausuren;
        this.zeigeKlausurraumDetails = zeigeKlausurraumDetails;
        this.belegung = belegung;

        setLayout(new BorderLayout(5, 10));
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        setBackground(new Color(237, 242, 247));

        JPanel kopfPanel = new JPanel(new BorderLayout(10, 0));
        kopfPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        kopfPanel.setOpaque(false);
        JLabel lbRaum = new JLabel("Raum: " + belegung.getRaum().getBezeichnung());
        lbRaum.setFont(lbRaum.getFont().deriveFont(Font.BOLD, 15f));
        kopfPanel.add(lbRaum, BorderLayout.CENTER);
        kopfPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    zeigeKlausurraumDetails.accept(belegung);
                }
            }
        });

        JLabel lbBelegung = new JLabel(belegung.getAktuelleBelegung() + " / " + belegung.getRaum().getKapazitaet());
        lbBelegung.setFont(lbRaum.getFont().deriveFont(Font.BOLD, 15f));
        kopfPanel.add(lbBelegung, BorderLayout.EAST);
        add(kopfPanel, BorderLayout.NORTH);

        kursePanel = new JPanel();
        kursePanel.setLayout(new BoxLayout(kursePanel, BoxLayout.Y_AXIS));
        kursePanel.setOpaque(false);
        add(kursePanel, BorderLayout.CENTER);
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
                    speichereKursRaumZuordnung.accept(belegung, kurs);
                    return true;
                } catch (UnsupportedFlavorException | IOException e) {
                    return false;
                }
            }
        });
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem entfernen = new JMenuItem("Klausurraum entfernen");
        entfernen.addActionListener(this::raumkarteLoeschenAction);
        popupMenu.add(entfernen);
        setComponentPopupMenu(popupMenu);

        aktualisiereAnzeige();
    }

    private void raumkarteLoeschenAction(ActionEvent event) {
        loescheRaumbelegungAbiturklausuren.accept(belegung);
    }

    public void aktualisiereAnzeige() {
        kursePanel.removeAll();
        if (belegung.getKurse().isEmpty()) {
            return;
        } else {
            for (Kurs kurs : belegung.getKurse()) {
                kursePanel.add(new KursPanel(kurs));
            }
        }
        kursePanel.revalidate();
        kursePanel.repaint();
    }


    private class KursPanel extends JPanel {
        JLabel lbKursbezeichnung = new JLabel();
        JLabel lbAnzahl = new JLabel();
        Kurs kurs;

        public KursPanel(Kurs kurs) {
            this.setLayout(new BorderLayout(10, 0));
            this.kurs = kurs;
            this.setPreferredSize(new Dimension(400, 30));
            this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            this.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.setOpaque(false);
            this.add(lbKursbezeichnung, BorderLayout.CENTER);
            this.add(lbAnzahl, BorderLayout.EAST);
            if (kurs == null) {
                lbKursbezeichnung.setText("Noch kein Kurs zugeordnet");
            } else {
                lbKursbezeichnung.setText(kurs.getBezeichnung() + "-" + kurs.getFachlehrer().getKuerzel());
                lbAnzahl.setText(String.valueOf(kurs.getAnzahlKlausurschreiber()));
            }

            this.setTransferHandler(new TransferHandler() {
                @Override
                protected Transferable createTransferable(JComponent component) {
                    return new KursTransferable(kurs);
                }

                @Override
                public int getSourceActions(JComponent component) {
                    return COPY;
                }

                @Override
                public boolean canImport(TransferSupport support) {
                    return support.isDrop() && support.isDataFlavorSupported(KursTransferable.KURS_FLAVOR);
                }

                @Override
                public boolean importData(TransferSupport support) {
                    if (!canImport(support)) return false;

                    try {
                        Kurs neuerKurs = (Kurs) support.getTransferable().getTransferData(KursTransferable.KURS_FLAVOR);
                        speichereKursRaumZuordnung.accept(belegung, neuerKurs);
                        return true;
                    } catch (UnsupportedFlavorException | IOException e) {
                        return false;
                    }
                }
            });

            this.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    getTransferHandler().exportAsDrag(KursPanel.this, e, TransferHandler.COPY);
                }
            });
            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem entfernen = new JMenuItem("Kurs entfernen");
            entfernen.addActionListener(this::kursEntfernenAction);
            popupMenu.add(entfernen);
            setComponentPopupMenu(popupMenu);
        }

        private void kursEntfernenAction(ActionEvent actionEvent) {
            entferneKursRaumzuordnung.accept(belegung, kurs);
        }
    }
}
