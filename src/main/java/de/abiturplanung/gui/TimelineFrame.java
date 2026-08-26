package de.abiturplanung.gui;

import de.abiturplanung.gui.timeline.KommissionsGruppe;
import de.abiturplanung.gui.timeline.TimelineDatenService;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Pruefung;
import de.abiturplanung.model.Pruefungstag;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class TimelineFrame extends JFrame {
    private final Abitur abitur;
    private final TimelineDatenService timelineDatenService = new TimelineDatenService();
    private JComboBox<Pruefungstag> pruefungstagComboBox;
    private JPanel timelinePanel = new JPanel(new GridBagLayout());

    public TimelineFrame(Abitur abitur) {
        this.abitur = abitur;
        setTitle("Timeline");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        initGui();
    }

    public void initGui() {
        JScrollPane scrollPane = new JScrollPane(timelinePanel);
        add(scrollPane, BorderLayout.CENTER);
        pruefungstagComboBox = new JComboBox<>();
        pruefungstagComboBox.addActionListener(e -> aktualisiereTimeline());
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
        add(pruefungstagComboBox, BorderLayout.NORTH);
        aktualisiereTimeline();
    }

    private void aktualisiereTimeline() {
        Pruefungstag pruefungstag = (Pruefungstag) pruefungstagComboBox.getSelectedItem();

        if (pruefungstag == null) {
            return;
        }
        List<KommissionsGruppe> gruppen = timelineDatenService.gibSortierteKommissionsGruppen(abitur, pruefungstag);
        baueTimeline(gruppen);
    }

    private void baueTimeline(List<KommissionsGruppe> gruppen) {
        timelinePanel.removeAll();
        if (gruppen.isEmpty()) {
            timelinePanel.add(new JLabel("Für diesen Prüfungstag sind keine Prüfungen geplant."));
            timelinePanel.revalidate();
            timelinePanel.repaint();
            return;
        }

        LocalTime ersteZeit = ermittleErsteZeit(gruppen);
        LocalTime letzteZeit = ermittleLetzteZeit(gruppen);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // Linke obere Ecke
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        timelinePanel.add(erstelleZelle("Zeit", true), gbc);

        int kommissionsIndex = 0;

        for (KommissionsGruppe gruppe : gruppen) {
            int ersteSpalte = 1 + kommissionsIndex * 2;

            // Kommissionsinfo über beide Unterspalten
            gbc.gridx = ersteSpalte;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.weightx = 1.0;
            timelinePanel.add(erstelleKommissionsInfoPanel(gruppe), gbc);

            // Spaltentitel
            gbc.gridy = 1;
            timelinePanel.add(erstelleSpatenTitelPanel(), gbc);

            kommissionsIndex++;
        }

        gbc.gridwidth = 1;

        int zeile = 2;

        for (LocalTime zeit = ersteZeit; !zeit.isAfter(letzteZeit); zeit = zeit.plusMinutes(30)) {
            gbc.gridx = 0;
            gbc.gridy = zeile;
            gbc.weightx = 0;
            timelinePanel.add(erstelleZelle(zeit.format(DateTimeFormatter.ofPattern("HH:mm")), true), gbc);

            kommissionsIndex = 0;

            for (KommissionsGruppe gruppe : gruppen) {
                int vorbereitungsSpalte = 1 + kommissionsIndex * 2;
                int pruefungsSpalte = vorbereitungsSpalte + 1;

                String vorbereitung = "";
                String pruefung = "";

                for (Pruefung p : gruppe.pruefungen()) {
                    if (p.getBeginn().minusMinutes(30).equals(zeit)) {
                        vorbereitung = p.getSchueler().getNachname() + ", " + p.getSchueler().getVorname();
                    }

                    if (p.getBeginn().equals(zeit)) {
                        pruefung = p.getSchueler().getNachname() + ", " + p.getSchueler().getVorname();
                    }
                }

                gbc.gridy = zeile;
                gbc.weightx = 1.0;

                gbc.gridx = vorbereitungsSpalte;
                timelinePanel.add(erstelleZelle(vorbereitung, false), gbc);

                gbc.gridx = pruefungsSpalte;
                timelinePanel.add(erstelleZelle(pruefung, false), gbc);

                kommissionsIndex++;
            }

            zeile++;
        }
        timelinePanel.revalidate();
        timelinePanel.repaint();
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
        Pruefung erstePruefung = gruppe.pruefungen().get(0);
        panel.add(new JLabel("Kurs: " + erstePruefung.getKurs().getBezeichnung()));
        panel.add(new JLabel("Kommission: " + gruppe.pruefer() + " / " + gruppe.vorsitz() + " / " + gruppe.schriftfuehrer()));
        panel.add(new JLabel("Raum: " + erstePruefung.getRaum()));
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
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JLabel verbereitung = new JLabel("Vorbereitung ");
        panel.add(verbereitung, gbc);
        gbc.gridx++;
        JLabel bruefungsBeginn = new JLabel("Prüfung");
        panel.add(bruefungsBeginn, gbc);
        return panel;
    }
}
