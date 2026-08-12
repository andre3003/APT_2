package de.abiturplanung.gui.planung;

import de.abiturplanung.gui.dialogs.PruefungsDialog;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Lehrer;
import de.abiturplanung.model.Pruefung;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class PruefungsKarte extends JPanel {

    private final Pruefung pruefung;

    public PruefungsKarte(Abitur abitur, Pruefung pruefung, Runnable nachBearbeitung) {
        this.pruefung = pruefung;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(true);
        setBackground(ermittleHintergrundfarbe());
        setBorder(new CompoundBorder(BorderFactory.createLineBorder(ermittleRahmenfarbe(), 2), new EmptyBorder(3, 5, 3, 5)));

        JLabel nameLabel = new JLabel(pruefung.getSchueler().getNachname() + ", " + pruefung.getSchueler().getVorname());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));

        JLabel kursLabel = new JLabel(pruefung.getKurs().getBezeichnung() + " | " + pruefung.getKurs().getFach() + " | " + pruefung.getAbiturfach());
        JLabel planungLabel = new JLabel(planungText());

        add(nameLabel);
        add(kursLabel);
        add(planungLabel);

        setTransferHandler(new TransferHandler() {
            @Override
            protected Transferable createTransferable(JComponent component) {
                return new PruefungTransferable(pruefung);
            }

            @Override
            public int getSourceActions(JComponent component) {
                return MOVE;
            }
        });

        MouseMotionAdapter dragListener = new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                getTransferHandler().exportAsDrag(PruefungsKarte.this, e, TransferHandler.MOVE);
            }
        };

        MouseAdapter doppelklickListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(PruefungsKarte.this);
                    PruefungsDialog dialog = new PruefungsDialog(owner, abitur, pruefung);
                    dialog.setVisible(true);

                    if (dialog.isGespeichert()) {
                        nachBearbeitung.run();
                    }
                }
            }
        };

        addMouseMotionListener(dragListener);
        nameLabel.addMouseMotionListener(dragListener);
        kursLabel.addMouseMotionListener(dragListener);
        planungLabel.addMouseMotionListener(dragListener);

        addMouseListener(doppelklickListener);
        nameLabel.addMouseListener(doppelklickListener);
        kursLabel.addMouseListener(doppelklickListener);
        planungLabel.addMouseListener(doppelklickListener);
    }

    public Pruefung getPruefung() {
        return pruefung;
    }

    private String planungText() {
        return lehrerKuerzel(pruefung.getPruefer())
                + " | " + lehrerKuerzel(pruefung.getSchriftfuehrer())
                + " | " + lehrerKuerzel(pruefung.getVorsitz())
                + " | " + raumText();
    }

    private String lehrerKuerzel(Lehrer lehrer) {
        return lehrer == null ? "---" : lehrer.getKuerzel();
    }

    private String raumText() {
        return pruefung.getRaum() == null ? "---" : pruefung.getRaum().getBezeichnung();
    }

    private Color ermittleHintergrundfarbe() {
        if (pruefung.istVollstaendigGeplant()) {
            return new Color(230, 245, 230);
        }

        return new Color(255, 248, 220);
    }

    private Color ermittleRahmenfarbe() {
        if (pruefung.istVollstaendigGeplant()) {
            return new Color(70, 150, 70);
        }

        return new Color(210, 170, 60);
    }
}