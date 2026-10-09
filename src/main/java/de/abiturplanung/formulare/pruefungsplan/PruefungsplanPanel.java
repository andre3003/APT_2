package de.abiturplanung.formulare.pruefungsplan;

import de.abiturplanung.Utilities;
import de.abiturplanung.formulare.pdf.PdfErsteller;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Pruefung;
import de.abiturplanung.model.Pruefungstag;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PruefungsplanPanel extends JPanel {
    private final Abitur abitur;
    private final PruefungsplanDatenService pruefungsplanDatenService = new PruefungsplanDatenService();
    private JComboBox<Pruefungstag> pruefungstagComboBox;
    private JCheckBox cbPdfDrucken;
    private final JPanel pruefungsplanRasterPanel = new JPanel(new GridBagLayout());
    private static final int SPALTENBREITE = 100;
    private static final int SPALTENHOEHE = 20;

    public PruefungsplanPanel(Abitur abitur) {
        this.abitur = abitur;
        initGui();
    }

    public void initGui() {
        setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(pruefungsplanRasterPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        JPanel steuerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        steuerPanel.add(new JLabel("Prüfungstag:"));

        pruefungstagComboBox = new JComboBox<>();
        pruefungstagComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof Pruefungstag pruefungstag) {
                    setText(pruefungstag.getDatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                }

                return this;
            }
        });

        for (Pruefungstag pruefungstag : abitur.getPruefungstage()) {
            pruefungstagComboBox.addItem(pruefungstag);
        }

        pruefungstagComboBox.addActionListener(e -> aktualisierePruefungsplan());

        steuerPanel.add(pruefungstagComboBox);
        steuerPanel.add(Box.createHorizontalStrut(20));

        JButton btAktuellenPruefungsplanDrucken = new JButton("Aktuellen Prüfungsplan drucken");
        btAktuellenPruefungsplanDrucken.addActionListener(this::btAktuellenPruefungsplanDruckenAction);
        JButton btAllePlaeneDrucken = new JButton("Alle Pläne drucken");
        btAllePlaeneDrucken.addActionListener(this::btAllePlaeneDruckenAction);
        steuerPanel.add(btAktuellenPruefungsplanDrucken);
        steuerPanel.add(btAllePlaeneDrucken);
        cbPdfDrucken = new JCheckBox("PDF nach der Erstellung anzeigen");
        cbPdfDrucken.setSelected(true);
        steuerPanel.add(cbPdfDrucken);
        add(steuerPanel, BorderLayout.NORTH);
        aktualisierePruefungsplan();
    }

    public void aktualisierePruefungsplan() {
        Pruefungstag pruefungstag = (Pruefungstag) pruefungstagComboBox.getSelectedItem();
        if (pruefungstag == null) {
            return;
        }
        List<KommissionsGruppe> gruppen = pruefungsplanDatenService.gibSortierteKommissionsGruppen(abitur, pruefungstag);
        bauePruefungsplan(gruppen);
    }

    public void btAktuellenPruefungsplanDruckenAction(ActionEvent e) {
        Pruefungstag pruefungstag = (Pruefungstag) pruefungstagComboBox.getSelectedItem();
        String datum = Utilities.formatiereDatumKurz(pruefungstag.getDatum());
        String dateiname = "Prüfungsplan " + datum + "pdf";
        PdfErsteller.erstelle(this, dateiname, cbPdfDrucken.isSelected(), zieldatei -> PdfPruefungsplan.erstelle(abitur, (Pruefungstag) pruefungstagComboBox.getSelectedItem(), zieldatei));
    }

    // Der Lambda, der im Aufruf von PDFersteller.erstelle verwendet wird, IST der Consumer, den die Methode Pdfersteller erwartet; PdfErsteller wird ihm später die vom Benutzer gewählte zieldatei geben und ihn dann ausführen.“
//    Der Lambda ist eine Abkürzung. Man könnte den Consumer auch erst gesondert erstellen und dann als solchen übergeben:
//    Consumer<File> pdfErzeugung = new Consumer<File>() {
//        public void accept(File datei) {
//            PdfPruefungsplan.erstelle(abitur, pruefungstag, datei);}};
//    PdfPruefungsplan.erstelle(abitur, (Pruefungstag) pruefungstagComboBox.getSelectedItem(), pdfErzeugnung)

    public void btAllePlaeneDruckenAction(ActionEvent e) {
        PdfErsteller.erstelle(this, "Prüfungsplan, gesamt.pdf", cbPdfDrucken.isSelected(), datei -> PdfPruefungsplan.erstelleGesamtplan(abitur, datei));
    }

    private void bauePruefungsplan(List<KommissionsGruppe> gruppen) {
        pruefungsplanRasterPanel.removeAll();
        if (gruppen.isEmpty()) {
            pruefungsplanRasterPanel.add(new JLabel("Für diesen Prüfungstag sind keine Prüfungen geplant."));
            pruefungsplanRasterPanel.revalidate();
            pruefungsplanRasterPanel.repaint();
            return;
        }
        LocalTime ersteZeit = ermittleErsteZeit(gruppen);
        LocalTime letzteZeit = ermittleLetzteZeit(gruppen);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        // Linke obere Ecke
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        pruefungsplanRasterPanel.add(erstelleZelle("Zeit", true), gbc);
        int kommissionsIndex = 0;
        for (KommissionsGruppe gruppe : gruppen) {
            int ersteSpalte = 1 + kommissionsIndex * 2;
            // Kommissionsinfo über beide Unterspalten
            gbc.gridx = ersteSpalte;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.weightx = 0;
            pruefungsplanRasterPanel.add(erstelleKommissionsInfoPanel(gruppe), gbc);
            // Spaltentitel
            gbc.gridy = 1;
            pruefungsplanRasterPanel.add(erstelleSpatenTitelPanel(), gbc);
            kommissionsIndex++;
        }
        gbc.gridwidth = 1;
        int zeile = 2;
        for (LocalTime zeit = ersteZeit; !zeit.isAfter(letzteZeit); zeit = zeit.plusMinutes(30)) {
            gbc.gridx = 0;
            gbc.gridy = zeile;
            gbc.weightx = 0;
            pruefungsplanRasterPanel.add(erstelleZelle(zeit.format(DateTimeFormatter.ofPattern("HH:mm")), true), gbc);
            kommissionsIndex = 0;
            for (KommissionsGruppe gruppe : gruppen) {
                int vorbereitungsSpalte = 1 + kommissionsIndex * 2;
                int pruefungsSpalte = vorbereitungsSpalte + 1;

                String vorbereitung = "";
                String pruefung = "";

                for (Pruefung p : gruppe.pruefungen()) {
                    if (p.getBeginn().minusMinutes(30).equals(zeit)) {
                        vorbereitung = p.getSchueler().getNachname() + ", " + p.getSchueler().getVorname().charAt(0) + ".";
                    }

                    if (p.getBeginn().equals(zeit)) {
                        pruefung = p.getSchueler().getNachname() + ", " + p.getSchueler().getVorname().charAt(0) + ".";
                    }
                }
                gbc.gridy = zeile;
                gbc.weightx = 0;
                gbc.gridx = vorbereitungsSpalte;
                pruefungsplanRasterPanel.add(erstelleZelle(vorbereitung, false), gbc);
                gbc.gridx = pruefungsSpalte;
                pruefungsplanRasterPanel.add(erstelleZelle(pruefung, false), gbc);
                kommissionsIndex++;
            }
            zeile++;
        }
//sorgt für linksbündig, wenn noch Paltz ist:
        gbc.gridx = 1 + gruppen.size() * 2;
        gbc.gridy = 0;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pruefungsplanRasterPanel.add(Box.createHorizontalGlue(), gbc);
        pruefungsplanRasterPanel.revalidate();
        pruefungsplanRasterPanel.repaint();
    }

    private LocalTime ermittleErsteZeit(List<KommissionsGruppe> gruppen) {
        return gruppen.stream()
                .flatMap(gruppe -> gruppe.pruefungen().stream())
                .map(Pruefung::getBeginn)
                .min(LocalTime::compareTo)
                .orElseThrow()
                .minusMinutes(30);
    }

    private JPanel erstelleKommissionsInfoPanel(KommissionsGruppe gruppe) {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panel.setPreferredSize(new Dimension(SPALTENBREITE * 2, SPALTENHOEHE * 3));
        Pruefung erstePruefung = gruppe.pruefungen().get(0);
        panel.add(new JLabel("Kurs: " + erstePruefung.getKurs().getBezeichnung()));
        panel.add(new JLabel("Komm.: " + gruppe.pruefer() + " / " + gruppe.vorsitz() + " / " + gruppe.schriftfuehrer()));
        panel.add(new JLabel(erstePruefung.getRaum() == null ? "" : erstePruefung.getRaum().toString()));
        return panel;
    }

    private LocalTime ermittleLetzteZeit(List<KommissionsGruppe> gruppen) {
        return gruppen.stream()
                .flatMap(gruppe -> gruppe.pruefungen().stream())
                .map(Pruefung::getBeginn)
                .max(LocalTime::compareTo)
                .orElseThrow();
    }

    private JLabel erstelleZelle(String text, boolean hervorgehoben) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        label.setPreferredSize(new Dimension(SPALTENBREITE, SPALTENHOEHE));

        if (hervorgehoben) {
            label.setFont(label.getFont().deriveFont(Font.BOLD));
        }

        return label;
    }

    public JPanel erstelleSpatenTitelPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel verbereitung = new JLabel("Vorbereitung ");
        panel.add(verbereitung, gbc);
        verbereitung.setPreferredSize(new Dimension(SPALTENBREITE, SPALTENHOEHE));
        gbc.gridx++;
        JLabel pruefungsbeginn = new JLabel("Prüfung");
        pruefungsbeginn.setPreferredSize(new Dimension(SPALTENBREITE, SPALTENHOEHE));
        panel.add(pruefungsbeginn, gbc);
        return panel;
    }

}
