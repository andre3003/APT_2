package de.abiturplanung.gui.planung;

import de.abiturplanung.model.Abitur;
import de.abiturplanung.persistence.Datenbank;

import javax.swing.*;
import java.awt.*;

public class MuendlichePruefungenPanel extends JPanel {
    private final PruefungstagePanel pruefungstagePanel;
    PlanungsvorratPanel planungsvorratPanel;

    public MuendlichePruefungenPanel(Abitur abitur, Datenbank datenbank) {
        this.setLayout(new BorderLayout());
        planungsvorratPanel = new PlanungsvorratPanel(abitur);
        pruefungstagePanel = new PruefungstagePanel(abitur, datenbank, planungsvorratPanel::aktualisieren);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, planungsvorratPanel, pruefungstagePanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.32);
        splitPane.setDividerLocation(480);
        this.add(splitPane);
    }

    public void planungsvorratAktualisieren() {
        planungsvorratPanel.aktualisieren();
    }

    public void pruefungstagHinzufuegen() {
        pruefungstagePanel.pruefungstagHinzufuegen();
    }

    public void pruefungstagEntfernen() {
        pruefungstagePanel.pruefungstagEntfernen();
    }
}
