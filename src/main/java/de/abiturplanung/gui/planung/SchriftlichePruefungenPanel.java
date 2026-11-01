package de.abiturplanung.gui.planung;

import de.abiturplanung.gui.dialogs.PruefungstagAnlegenDialog;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.persistence.Datenbank;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;

public class SchriftlichePruefungenPanel extends JPanel {
    private final SchriftlichePruefungstagePanel pruefungstagePanel;
    private final SchriftlichePruefungenPlanungsVorratPanel  planungsvorratPanel;
    private final Abitur abitur;
    private final Datenbank datenbank;


    public SchriftlichePruefungenPanel(Abitur abitur, Datenbank datenbank) {
        this.abitur = abitur;
        this.datenbank = datenbank;
        this.setLayout(new BorderLayout());
        pruefungstagePanel = new SchriftlichePruefungstagePanel(abitur);
        planungsvorratPanel = new SchriftlichePruefungenPlanungsVorratPanel(abitur);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, planungsvorratPanel, pruefungstagePanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.32);
        splitPane.setDividerLocation(480);
        this.add(splitPane, BorderLayout.CENTER);
    }

    public void pruefungstagHinzufuegen(LocalDate datum, java.util.List<PruefungstagAnlegenDialog.KlausurEingabe> klausuren) {
        SchriftlicherPruefungstag pruefungstag = new SchriftlicherPruefungstag(datum);
        for (PruefungstagAnlegenDialog.KlausurEingabe klausurEingabe : klausuren) {
            Abiturklausur klausur = new Abiturklausur(klausurEingabe.fach(), klausurEingabe.kursart());
            pruefungstag.addAbiturklausur(klausur);
        }
        try {
            datenbank.fuegeSchriftlichenPruefungstagHinzu(pruefungstag);
            abitur.addSchriftlicherPruefungstag(pruefungstag);
            pruefungstagePanel.ansichtAktualisieren();
            pruefungstagePanel.zeigePruefungstag(pruefungstag);
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, "Der Prüfungstag konnte nicht gespeichert werden:\n" + exception.getMessage(), "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
        }
    }
}
