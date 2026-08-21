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
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.function.Consumer;
import java.util.List;

public class PruefungsKarte extends JPanel {

    private final Pruefung pruefung;
    private List<Pruefung> kollisionen;
    private JPopupMenu popupMenu;
    private PruefungsKartenAktionen aktionen;


    public PruefungsKarte(Abitur abitur, Pruefung pruefung, PruefungsKartenAktionen aktionen, List<Pruefung> kollisionen){
        this.pruefung = pruefung;
        this.kollisionen = kollisionen;
        this.aktionen = aktionen;
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

        MouseAdapter mausListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(PruefungsKarte.this);
                    PruefungsDialog dialog = new PruefungsDialog(owner, abitur, pruefung);
                    dialog.setVisible(true);
                    if (dialog.isGespeichert()) {
                        aktionen.nachBearbeitung(pruefung);
                    }
                }
            }
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) { //Ginge auch so: SwingUtilities.isRightMouseButton(e)
                    zeigePopup(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {  //Ginge auch so: SwingUtilities.isRightMouseButton(e)
                    zeigePopup(e);
                }
            }
        };

        addMouseMotionListener(dragListener);
        nameLabel.addMouseMotionListener(dragListener);
        kursLabel.addMouseMotionListener(dragListener);
        planungLabel.addMouseMotionListener(dragListener);

        addMouseListener(mausListener);
        nameLabel.addMouseListener(mausListener);
        kursLabel.addMouseListener(mausListener);
        planungLabel.addMouseListener(mausListener);
        if (!kollisionen.isEmpty()) {
            setzeToolTip(nameLabel, kursLabel, planungLabel);
        }
        addPopUpMenu();
    }

    public void addPopUpMenu(){
        popupMenu = new JPopupMenu();
        JMenuItem entplanen = new JMenuItem("Prüfung entplanen");
        entplanen.addActionListener(this::entplanenAction);
        popupMenu.add(entplanen);
        JMenuItem datenKopieren = new JMenuItem("Planungsdaten kopieren");
        datenKopieren.addActionListener(this::datenKopierenAction);
        popupMenu.add(datenKopieren);
        JMenuItem datenUebertragen = new JMenuItem("Daten übertragen");
        datenUebertragen.addActionListener(this::datenUebertragenAction);
        popupMenu.add(datenUebertragen);
    }

    private void datenUebertragenAction(ActionEvent actionEvent) {
        aktionen.planungsdatenUebertragen(pruefung);

    }

    private void datenKopierenAction(ActionEvent actionEvent) {
        aktionen.planungsdatenKopieren(pruefung);
    }

    private void entplanenAction(ActionEvent actionEvent) {
        pruefung.setPruefungstag(null);
        pruefung.setBeginn(null);
        pruefung.setSchriftfuehrer(null);
        pruefung.setVorsitz(null);
        pruefung.setRaum(null);
        pruefung.setPlanungsspalte(0);
        aktionen.nachBearbeitung(pruefung);
    }

    private void zeigePopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            popupMenu.show(e.getComponent(), e.getX()+10, e.getY());
        }
    }

    public void setzeToolTip(JLabel ... labels) {
        StringBuilder tooltip = new StringBuilder();
        tooltip.append("<html>");
        tooltip.append("Kollidierende Prüfungen: <br>");
        for (Pruefung p : kollisionen) {
            tooltip.append(p.getSchueler().getNachname()).append(" - ").append(p.getBeginn()).append(" - Spalte ").append(p.getPlanungsspalte()).append("<br>");

        }
        tooltip.append("</html>");
        this.setToolTipText(tooltip.toString());
        for (JLabel l : labels) {
            l.setToolTipText(tooltip.toString());
        }
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
        if (!kollisionen.isEmpty()) {
            return new Color(249, 115, 115);
        }
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