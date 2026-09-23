package de.abiturplanung.gui.planung;

import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Pruefung;
import de.abiturplanung.model.Pruefungstag;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class PlanungsMatrixPanel extends JPanel {
    private static final LocalTime STARTZEIT = LocalTime.of(8, 0);
    private static final LocalTime ENDZEIT = LocalTime.of(18, 0);
    private static final DateTimeFormatter ZEIT_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private final JPanel[][] slots;
    private final Abitur abitur;
    private final Pruefungstag pruefungstag;
    private final JPanel matrixPanel = new JPanel();
    private JScrollPane scrollPane;
    private PruefungsKartenAktionen aktionen;
    private Map<Pruefung, List<Pruefung>> alleKollisionen = Map.of();
    private final Map<Pruefung, PruefungsKarte> pruefungskarten = new HashMap<>();
    private PruefungsKarte selektierteKarte;
    private Consumer<Pruefungstag> nachSpalteHinzufuegen;
    private Consumer<Pruefungstag> nachSpalteEntfernen;


    public PlanungsMatrixPanel(Abitur abitur, Pruefungstag pruefungstag) {
        this.abitur = abitur;
        this.pruefungstag = pruefungstag;
        slots = new JPanel[21][pruefungstag.getAnzahlPlanungsspalten()];
        setLayout(new BorderLayout());
        scrollPane = new JScrollPane(matrixPanel);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setzePruefungskartenAktionen(PruefungsKartenAktionen aktionen) {
        this.aktionen = aktionen;
    }

    public void setzNachSpalteHinzufuegen(Consumer<Pruefungstag> nachSpalteHinzufuegen) {
        this.nachSpalteHinzufuegen = nachSpalteHinzufuegen;
    }

    public void setzNachSpalteEntfernen(Consumer<Pruefungstag> nachSpalteEntfernen) {
        this.nachSpalteEntfernen = nachSpalteEntfernen;
    }

    public LocalDate getDatum() {
        return pruefungstag.getDatum();
    }



    private void erzeugeKopfzeile() {
        addZelle(new JLabel("Zeit", SwingConstants.CENTER), 0, 0, 70, 30);
        int spalten = pruefungstag.getAnzahlPlanungsspalten();
        for (int spalte = 1; spalte <= spalten; spalte++) {
            addZelle(new JLabel("Spalte " + spalte, SwingConstants.CENTER), spalte, 0, 180, 30);
        }
    }

    private void erzeugeLeereMatrix() {
        LocalTime zeit = STARTZEIT;
        int zeile = 1;
        int spalten = pruefungstag.getAnzahlPlanungsspalten();

        while (!zeit.isAfter(ENDZEIT)) {
            LocalTime vorbereitungsbeginn = zeit.minusMinutes(30);
            LocalTime pruefungsende = zeit.plusMinutes(30);

            String zeitText = String.format(
                    "<html><div style='text-align:center;'>Vorber: %s - %s<br>----------------<br>Prüfg: %s - %s</div></html>",
                    vorbereitungsbeginn.format(ZEIT_FORMAT),
                    zeit.format(ZEIT_FORMAT),
                    zeit.format(ZEIT_FORMAT),
                    pruefungsende.format(ZEIT_FORMAT)
            );

            JLabel zeitLabel = new JLabel(zeitText, SwingConstants.CENTER);

            JPanel zeitPanel = new JPanel(new BorderLayout());
            zeitPanel.setBackground(getBackground());
            zeitPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            zeitPanel.add(zeitLabel, BorderLayout.CENTER);

            JPanel zeitContainer = new JPanel(new BorderLayout());
            zeitContainer.setBackground(getBackground());
            zeitContainer.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
            zeitContainer.add(zeitPanel, BorderLayout.CENTER);

            addZelle(zeitContainer, 0, zeile, 150, 65);

            for (int spalte = 1; spalte <= spalten; spalte++) {
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
                            aktionen.nachBearbeitung(pruefung);
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
        addSteuerPanel();
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

    public void addSteuerPanel() {
        JPanel steuerpanel = new JPanel();
        steuerpanel.setLayout(new BoxLayout(steuerpanel, BoxLayout.Y_AXIS));
        JButton btSpalteHinzufuegen = erzeugeSteuerButton("+","Planungsspalte hinzufügen");
        btSpalteHinzufuegen.addActionListener(this::btSpalteHinzufuegenAction);
        JButton btSpalteEntfernen = erzeugeSteuerButton("-", "Planungsspalte entfernen");
        btSpalteEntfernen.addActionListener(this::btSpalteEntfernenAction);
        steuerpanel.add(Box.createVerticalGlue());
        steuerpanel.add(btSpalteHinzufuegen);
        steuerpanel.add(btSpalteEntfernen);
        steuerpanel.add(Box.createVerticalGlue());
        this.add(steuerpanel, BorderLayout.EAST);
    }

    private JButton erzeugeSteuerButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 22f));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);
        return button;
    }

    private void btSpalteHinzufuegenAction(ActionEvent event) {
        nachSpalteHinzufuegen.accept(pruefungstag);
    }

    private void btSpalteEntfernenAction(ActionEvent event) {
        nachSpalteEntfernen.accept(pruefungstag);
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

            int spalten = pruefungstag.getAnzahlPlanungsspalten();

            if (spalte < 1 || spalte > spalten || slots[zeile - 1][spalte - 1].getComponentCount() > 0) {
                spalte = findeFreieSpalte(zeile);
            }

            if (spalte > 0) {
                JPanel slot = slots[zeile - 1][spalte - 1];
                List<Pruefung> kollisionen = alleKollisionen.getOrDefault(pruefung, List.of()); //Liefert die Liste der Kollisionen der Prüfung dieser Karte; getOrDefault sorgt dafür, dass wir im Falle von null (Prüfung nicht in der Map) eine leere Liste bekommen und nicht null
                PruefungsKarte karte = new PruefungsKarte(abitur, pruefung, aktionen, kollisionen);
                pruefungskarten.put(pruefung, karte);
                slot.add(karte, BorderLayout.CENTER);
            }
        }
    }


    private int findeFreieSpalte(int zeile) {
        int spalten = pruefungstag.getAnzahlPlanungsspalten();
        for (int spalte = 1; spalte <= spalten; spalte++) {
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


    public void setKollisionen(Map<Pruefung, List<Pruefung>> alleKollisionen) {
        this.alleKollisionen = alleKollisionen;
    }

    public void fokussierePruefung(Pruefung pruefung) {
        PruefungsKarte karte = pruefungskarten.get(pruefung); //prüfungskarten ist die Map, die für jede Prüfung speichert, welche Karte sie darstellt.
        if (karte == null) {
            return;
        }
        if (selektierteKarte != null) {
            selektierteKarte.setSelektiert(false);
        }
        Rectangle bereich = SwingUtilities.convertRectangle(karte.getParent(), karte.getBounds(), matrixPanel);
        matrixPanel.scrollRectToVisible(bereich);
        selektierteKarte = karte;
        selektierteKarte.setSelektiert(true);
    }

    public void scrollNachRechts() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
            horizontalScrollBar.setValue(horizontalScrollBar.getMaximum());
        });
    }

    public void selektionAufheben() {
        if (selektierteKarte != null) {
            selektierteKarte.setSelektiert(false);
            selektierteKarte = null;
        }
    }

    public void ansichtAktualisieren() {
        selektierteKarte = null;
        pruefungskarten.clear();
        matrixPanel.removeAll();
        matrixPanel.setLayout(new GridBagLayout());
        erzeugeKopfzeile();
        erzeugeLeereMatrix();
        zeigeGeplantePruefungen();
        matrixPanel.revalidate();
        matrixPanel.repaint();
    }
}