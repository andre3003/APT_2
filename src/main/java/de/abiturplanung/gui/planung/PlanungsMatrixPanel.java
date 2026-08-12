package de.abiturplanung.gui.planung;

import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Pruefung;
import de.abiturplanung.model.Pruefungstag;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlanungsMatrixPanel extends JPanel {

    private static final LocalTime STARTZEIT = LocalTime.of(8, 0);
    private static final LocalTime ENDZEIT = LocalTime.of(18, 0);
    private static final int SPALTEN = 6;

    private static final DateTimeFormatter ZEIT_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private final JPanel[][] slots = new JPanel[21][SPALTEN];
    private final Abitur abitur;
    private final Pruefungstag pruefungstag;

    private final JPanel matrixPanel = new JPanel();
    private Runnable nachBearbeitung = () -> {
    };
    ;

    public PlanungsMatrixPanel(Abitur abitur, Pruefungstag pruefungstag) {
        this.abitur = abitur;
        this.pruefungstag = pruefungstag;
        setLayout(new BorderLayout());
        add(new JScrollPane(matrixPanel), BorderLayout.CENTER);
        aktualisieren();
    }


    public void setNachBearbeitung(Runnable nachBearbeitung) {
        this.nachBearbeitung = nachBearbeitung;
    }

    public LocalDate getDatum() {
        return pruefungstag.getDatum();
    }

    public void aktualisieren() {
        matrixPanel.removeAll();
        matrixPanel.setLayout(new GridBagLayout());

        erzeugeKopfzeile();
        erzeugeLeereMatrix();
        zeigeGeplantePruefungen();

        matrixPanel.revalidate();
        matrixPanel.repaint();
    }

    private void erzeugeKopfzeile() {
        addZelle(new JLabel("Zeit", SwingConstants.CENTER), 0, 0, 70, 30);

        for (int spalte = 1; spalte <= SPALTEN; spalte++) {
            addZelle(new JLabel("Spalte " + spalte, SwingConstants.CENTER), spalte, 0, 180, 30);
        }
    }

    private void erzeugeLeereMatrix() {
        LocalTime zeit = STARTZEIT;
        int zeile = 1;

        while (!zeit.isAfter(ENDZEIT)) {
            JLabel zeitLabel = new JLabel(zeit.format(ZEIT_FORMAT), SwingConstants.CENTER);
            addZelle(zeitLabel, 0, zeile, 70, 65);

            for (int spalte = 1; spalte <= SPALTEN; spalte++) {
                JPanel slot = new JPanel(new BorderLayout());
                slot.setBackground(Color.WHITE);

                LocalTime slotZeit = zeit;
                int slotSpalte = spalte;

                slot.setTransferHandler(new TransferHandler() {

                    @Override
                    public boolean canImport(TransferSupport support) {
                        return slot.getComponentCount() == 0 && support.isDataFlavorSupported(PruefungTransferable.PRUEFUNG_FLAVOR);
                    }

                    @Override
                    public boolean importData(TransferSupport support) {
                        if (!canImport(support)) {
                            return false;
                        }

                        try {
                            Pruefung pruefung = (Pruefung) support.getTransferable().getTransferData(PruefungTransferable.PRUEFUNG_FLAVOR);

                            pruefung.setPruefungstag(pruefungstag.getDatum());
                            pruefung.setBeginn(slotZeit);
                            pruefung.setPlanungsspalte(slotSpalte);

                            aktualisieren();

                            return true;
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            return false;
                        }
                    }
                });

                slots[zeile - 1][spalte - 1] = slot;
                addZelle(slot, spalte, zeile, 180, 65);
            }

            zeit = zeit.plusMinutes(30);
            zeile++;
        }
    }

    private void zeigeGeplantePruefungen() {
        List<Pruefung> pruefungen = new ArrayList<>();

        for (Pruefung pruefung : abitur.getPruefungen()) {
            if (pruefungstag.getDatum().equals(pruefung.getPruefungstag()) && pruefung.getBeginn() != null) {
                pruefungen.add(pruefung);
            }
        }

        pruefungen.sort(Comparator.comparing(Pruefung::getBeginn));

        for (Pruefung pruefung : pruefungen) {
            int zeile = zeileFuerZeit(pruefung.getBeginn());

            if (zeile <= 0) {
                continue;
            }

            int spalte = pruefung.getPlanungsspalte() == null ? findeFreieSpalte(zeile) : pruefung.getPlanungsspalte();

            if (spalte < 1 || spalte > SPALTEN || slots[zeile - 1][spalte - 1].getComponentCount() > 0) {
                spalte = findeFreieSpalte(zeile);
            }

            if (spalte > 0) {
                JPanel slot = slots[zeile - 1][spalte - 1];
                slot.add(new PruefungsKarte(abitur, pruefung, nachBearbeitung), BorderLayout.CENTER);
            }
        }
    }

    private int findeFreieSpalte(int zeile) {
        for (int spalte = 1; spalte <= SPALTEN; spalte++) {
            if (slots[zeile - 1][spalte - 1].getComponentCount() == 0) {
                return spalte;
            }
        }

        return -1;
    }

    private int zeileFuerZeit(LocalTime zeit) {
        if (zeit.isBefore(STARTZEIT) || zeit.isAfter(ENDZEIT)) {
            return -1;
        }

        int minuten = (zeit.getHour() * 60 + zeit.getMinute()) - (STARTZEIT.getHour() * 60 + STARTZEIT.getMinute());

        if (minuten % 30 != 0) {
            return -1;
        }

        return minuten / 30 + 1;
    }

    private void addZelle(Component component, int spalte, int zeile, int breite, int hoehe) {
        JPanel zelle = new JPanel(new BorderLayout());
        zelle.setPreferredSize(new Dimension(breite, hoehe));
        zelle.setMinimumSize(new Dimension(breite, hoehe));
        zelle.setBorder(new MatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        zelle.add(component, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = spalte;
        gbc.gridy = zeile;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = spalte == 0 ? 0 : 1;
        gbc.weighty = 0;

        matrixPanel.add(zelle, gbc);
    }
}