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

public class KlausurRaumKarte extends JPanel {

    private final RaumbelegungAbiturklausuren belegung;
    private final JPanel kursePanel;
    private final BiConsumer<RaumbelegungAbiturklausuren, Kurs> speichereKursZuRaumZuordnung;
    JPopupMenu popupMenu;

    public KlausurRaumKarte(RaumbelegungAbiturklausuren belegung, BiConsumer<RaumbelegungAbiturklausuren, Kurs> speichereKursZuRaumZuordnung) {
        this.speichereKursZuRaumZuordnung = speichereKursZuRaumZuordnung;
        this.belegung = belegung;
//        this.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                super.mouseClicked(e);
////                Component c = e.
////                System.out.println(c.toString());
//                System.out.println(e.getSource().toString());
//
//            }
//        });

        setLayout(new BorderLayout(5, 10));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel titel = new JLabel("Raum " + belegung.getRaum().getBezeichnung());
        titel.setFont(titel.getFont().deriveFont(Font.BOLD, 15f));
        add(titel, BorderLayout.NORTH);

        kursePanel = new JPanel();
        kursePanel.setLayout(new BoxLayout(kursePanel, BoxLayout.Y_AXIS));
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
        JLabel lbText = new JLabel();
        Kurs kurs;

        public KursPanel(Kurs kurs) {
            this.kurs = kurs;
            this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            this.setPreferredSize(new Dimension(400, 30));
            this.add(lbText);
            if (kurs == null) {
                lbText.setText("Noch kein Kurs zugeordnet");
            } else {
                lbText.setText(kurs.getBezeichnung() + "-" + kurs.getFachlehrer().getKuerzel());
            }
//            this.addMouseListener(new MouseAdapter() {
//                public void mouseClicked(MouseEvent e) {
//                    System.out.println(e.getSource().toString());
//                }
//            });
            this.setTransferHandler(new TransferHandler() {
                                        @Override
                                        protected Transferable createTransferable(JComponent component) {
                                            return new KursTransferable(kurs);
                                        }

                                        @Override
                                        public int getSourceActions(JComponent component) {
                                            return MOVE;
                                        }

                                        @Override
                                        protected void exportDone(JComponent source, Transferable data, int action) {
                                            super.exportDone(source, data, action);
                                            System.out.println(kurs.getBezeichnung());
                                            belegung.removeKurs(kurs);
                                            aktualisiereAnzeige();
                                        }
                                    }
            );

            MouseMotionAdapter dragListener = new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    getTransferHandler().exportAsDrag(KursPanel.this, e, TransferHandler.MOVE);

                }
            };
            this.addMouseMotionListener(dragListener);

            popupMenu = new JPopupMenu();
            JMenuItem entfernen = new JMenuItem("Kurs entfernen");
            entfernen.addActionListener(this::kursEntfernenAction);
            popupMenu.add(entfernen);
            this.add(popupMenu);

            MouseAdapter mausListener = new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) {  //Ginge auch so: SwingUtilities.isRightMouseButton(e)
                        zeigePopup(e);
                    }
                }
            };
            this.addMouseListener(mausListener);

        }

        private void zeigePopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popupMenu.show(e.getComponent(), e.getX() + 10, e.getY());
            }
        }

        private void kursEntfernenAction(ActionEvent actionEvent) {
            belegung.removeKurs(kurs);
            aktualisiereAnzeige();
        }
    }
}
