package de.abiturplanung.gui;

import de.abiturplanung.model.Abitur;
import de.abiturplanung.service.Kollisionspruefer;

import javax.swing.*;
import java.awt.*;

public class MuendlichePruefungenPanel extends JPanel {
    JPanel pruefungsPanel = new JPanel(new BorderLayout(0, 8));
    private final JTabbedPane planungsTabs = new JTabbedPane();
    private final Abitur abitur;
    PlanungsvorratPanel planungsvorratPanel;
    Kollisionspruefer kollisionspruefer;

    public MuendlichePruefungenPanel(Abitur abitur) {
        this.abitur = abitur;
        this.kollisionspruefer = new Kollisionspruefer(abitur);
        this.setLayout(new BorderLayout());
        planungsvorratPanel = new PlanungsvorratPanel(abitur);
        this.add(planungsvorratPanel);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, planungsvorratPanel, planungsTabs);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.32);
        splitPane.setDividerLocation(480);
        this.add(splitPane);
    }

    public void planungsvorratAktualisieren() {
        planungsvorratPanel.aktualisieren();
    }
}
