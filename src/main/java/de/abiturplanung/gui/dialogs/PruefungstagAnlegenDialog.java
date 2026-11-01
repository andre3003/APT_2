package de.abiturplanung.gui.dialogs;

import de.abiturplanung.gui.Modul;
import de.abiturplanung.model.Fach;
import de.abiturplanung.model.Kursart;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public final class PruefungstagAnlegenDialog {

    private PruefungstagAnlegenDialog() {}

    public record KlausurEingabe(Fach fach, Kursart kursart) {
        public KlausurEingabe {
            Objects.requireNonNull(fach);
            Objects.requireNonNull(kursart);
        }

        @Override
        public String toString() {
            return fach.getKuerzel() + " – " + kursart.getKuerzel();
        }
    }

    public record PruefungstagEingabe(Modul modul, LocalDate datum, List<KlausurEingabe> klausuren) {
        public PruefungstagEingabe {
            Objects.requireNonNull(modul);
            Objects.requireNonNull(datum);
            klausuren = List.copyOf(klausuren);
        }
    }

    public static PruefungstagEingabe anzeigen(Component parent, LocalDate vorauswahl, List<KlausurEingabe> verfuegbareKlausuren, Modul vorauswahlModul) {
        Objects.requireNonNull(vorauswahl);
        Objects.requireNonNull(verfuegbareKlausuren);

        // Prüfungsart
        JComboBox<Modul> modulCombo = new JComboBox<>(new Modul[]{Modul.MUENDLICH_AB4, Modul.SCHRIFTLICH});
        modulCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value == Modul.MUENDLICH_AB4) setText("Mündliche Prüfungen (AB4)");
                if (value == Modul.SCHRIFTLICH) setText("Schriftliche Prüfungen (AB1–3)");
                return this;
            }
        });

        modulCombo.setSelectedItem(vorauswahlModul);

        // Datum
        Date startDatum = Date.from(vorauswahl.atStartOfDay(ZoneId.systemDefault()).toInstant());
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateModel.setValue(startDatum);

        JSpinner datumSpinner = new JSpinner(dateModel);
        datumSpinner.setEditor(new JSpinner.DateEditor(datumSpinner, "dd.MM.yyyy"));

        // Verfügbare Abiturklausuren
        List<KlausurEingabe> sortierteKlausuren = new ArrayList<>(verfuegbareKlausuren);
        sortierteKlausuren.sort(Comparator.comparing((KlausurEingabe k) -> k.fach().getKuerzel()).thenComparing(k -> k.kursart().name()));

        DefaultListModel<KlausurEingabe> listModel = new DefaultListModel<>();
        sortierteKlausuren.forEach(listModel::addElement);

        JList<KlausurEingabe> klausurListe = new JList<>(listModel);
        klausurListe.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        klausurListe.setVisibleRowCount(8);

        JScrollPane scrollPane = new JScrollPane(klausurListe);
        scrollPane.setPreferredSize(new Dimension(300, 160));

        JLabel klausurLabel = new JLabel("Abiturklausuren an diesem Tag:");

        // Dialoginhalt
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Prüfungsart:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(modulCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Datum:"), gbc);

        gbc.gridx = 1;
        panel.add(datumSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(klausurLabel, gbc);

        gbc.gridy = 3;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, gbc);

        // Klausurauswahl nur bei schriftlichen Prüfungen anzeigen
        Runnable aktualisiereAnzeige = () -> {
            boolean schriftlich = modulCombo.getSelectedItem() == Modul.SCHRIFTLICH;
            klausurLabel.setVisible(schriftlich);
            scrollPane.setVisible(schriftlich);
            panel.revalidate();
            panel.repaint();

            Window window = SwingUtilities.getWindowAncestor(panel);
            if (window != null) window.pack();
        };

        modulCombo.addActionListener(e -> aktualisiereAnzeige.run());
        aktualisiereAnzeige.run();

        // Dialog anzeigen und Eingaben prüfen
        while (true) {
            int ergebnis = JOptionPane.showConfirmDialog(parent, panel, "Prüfungstag hinzufügen",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (ergebnis != JOptionPane.OK_OPTION) return null;

            Modul modul = (Modul) modulCombo.getSelectedItem();
            LocalDate datum = dateModel.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (modul == Modul.SCHRIFTLICH && klausurListe.isSelectionEmpty()) {
                JOptionPane.showMessageDialog(parent, "Bitte mindestens eine Abiturklausur auswählen.",
                        "Eingabe unvollständig", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            List<KlausurEingabe> klausuren = modul == Modul.SCHRIFTLICH
                    ? klausurListe.getSelectedValuesList()
                    : List.of();

            return new PruefungstagEingabe(modul, datum, klausuren);
        }
    }
}