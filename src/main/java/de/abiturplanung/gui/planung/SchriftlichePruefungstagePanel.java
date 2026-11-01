package de.abiturplanung.gui.planung;

import de.abiturplanung.model.Abitur;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SchriftlichePruefungstagePanel extends JPanel {
    private JTabbedPane tabbedPane = new JTabbedPane();
    private ArrayList<SchriftlicherPruefungstagPlanungsPanel> planungsPanels = new ArrayList<>();
    private final Abitur abitur;

    public SchriftlichePruefungstagePanel(Abitur abitur) {
        this.abitur = abitur;
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        ansichtAktualisieren();
    }

    public void ansichtAktualisieren() {
        tabbedPane.removeAll();
        planungsPanels.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        for (SchriftlicherPruefungstag pt : abitur.getSchriftlichePruefungstage()) {
            SchriftlicherPruefungstagPlanungsPanel panel = new SchriftlicherPruefungstagPlanungsPanel(pt);
            planungsPanels.add(panel);
            tabbedPane.add(pt.getDatum().format(formatter), panel);
        }
    }


    public void zeigePruefungstag(SchriftlicherPruefungstag pruefungstag) {
        int index = abitur.getSchriftlichePruefungstage().indexOf(pruefungstag);
        if (index >= 0 && index < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(index);
        }
    }
}
