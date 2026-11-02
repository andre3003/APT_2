package de.abiturplanung.gui.planung;

import de.abiturplanung.model.Abitur;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.function.Consumer;

public class SchriftlichePruefungstagePanel extends JPanel {
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final ArrayList<SchriftlicherPruefungstagPlanungsPanel> planungsPanels = new ArrayList<>();
    private final Abitur abitur;
    private final Consumer<SchriftlicherPruefungstag> nachTabWechsel;

    public SchriftlichePruefungstagePanel(Abitur abitur, Consumer<SchriftlicherPruefungstag> nachTabWechsel) {
        this.abitur = abitur;
        this.nachTabWechsel = nachTabWechsel;
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.addChangeListener(this::tabChangeAction);
        ansichtAktualisieren();
    }

    private void tabChangeAction(ChangeEvent changeEvent) {
        SchriftlicherPruefungstagPlanungsPanel panel = (SchriftlicherPruefungstagPlanungsPanel) tabbedPane.getSelectedComponent();
        if (panel != null) {
            nachTabWechsel.accept(panel.getPruefungstag());
        }
    }

    public void ansichtAktualisieren() {
        tabbedPane.removeAll();
        planungsPanels.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        for (SchriftlicherPruefungstag pt : abitur.getSchriftlichePruefungstage()) {
            SchriftlicherPruefungstagPlanungsPanel panel = new SchriftlicherPruefungstagPlanungsPanel(abitur, pt);
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
