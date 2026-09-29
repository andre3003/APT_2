package de.abiturplanung.formulare;

import de.abiturplanung.Utilities;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Pruefung;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SchueleruebersichtPanel extends JPanel {
    private final Abitur abitur;
    private JTable tabelle;
    private static final String VORBEREITUNGSRAUM = "MLB2"; //Dummy-Wert

    public SchueleruebersichtPanel(Abitur abitur) {
        this.abitur = abitur;
        initialisiereGui();
    }

    private void initialisiereGui() {
        setLayout(new BorderLayout());
        tabelle = erstelleTabelle();
        JScrollPane scrollPane = new JScrollPane(tabelle);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JTable erstelleTabelle() {
        String[] spalten = {"Name", "Vorname", "Kurs", "Prüfer", "Protokoll", "Vorsitz", "Tag", "Prfg-Beginn", "Raum", "Vorbereitung"};
        DefaultTableModel model = new DefaultTableModel(spalten, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        for (Pruefung p : abitur.gibPruefungenAB4Sortiert()) {
            String pruefer = p.getPruefer() != null ? p.getPruefer().getKuerzel() : "---";
            String schriftfuehrer = p.getSchriftfuehrer() != null ? p.getSchriftfuehrer().getKuerzel() : "---";
            String vorsitz = p.getVorsitz() != null ? p.getVorsitz().getKuerzel() : "---";
            String tag = p.getPruefungstag() != null ? Utilities.formatiereDatumKurz(p.getPruefungstag()) : "---";
            String beginn = p.getBeginn() != null ? p.getBeginn().toString() : "---";
            String raum = p.getRaum() != null ? p.getRaum().getBezeichnung() : "---";


            Object[] row = {
                    p.getSchueler().getNachname(),
                    p.getSchueler().getVorname(),
                    p.getKurs().getBezeichnung(),
                    pruefer,
                    schriftfuehrer,
                    vorsitz,
                    tag,
                    beginn,
                    raum,
                    VORBEREITUNGSRAUM
            };
            model.addRow(row);
        }
        return new JTable(model);
    }
}
