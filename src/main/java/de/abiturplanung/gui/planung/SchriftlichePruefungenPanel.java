package de.abiturplanung.gui.planung;

import javax.swing.*;
import java.awt.*;

public class SchriftlichePruefungenPanel extends JPanel {
    private final SchriftlichePruefungenPlanungsPanel  preufungstagePanel;
    private final SchriftlichePruefungenPlanungsVorratPanel  planungsvorratPanel;


    public SchriftlichePruefungenPanel() {
        this.setLayout(new BorderLayout());
        preufungstagePanel = new SchriftlichePruefungenPlanungsPanel();
        planungsvorratPanel = new SchriftlichePruefungenPlanungsVorratPanel();
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, planungsvorratPanel, preufungstagePanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.32);
        splitPane.setDividerLocation(480);
        this.add(splitPane, BorderLayout.CENTER);
    }

}
