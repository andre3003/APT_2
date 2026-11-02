package de.abiturplanung.gui.planung;

import de.abiturplanung.gui.model.SchriftlichePruefungenTableModel;
import de.abiturplanung.model.Abitur;

import javax.swing.*;
import java.awt.*;

public class SchriftlicherPruefungstagPlanungsPanel extends JPanel {
    private final SchriftlicherPruefungstag pruefungstag;

    public SchriftlicherPruefungstagPlanungsPanel(Abitur abitur, SchriftlicherPruefungstag pruefungstag) {
        this.pruefungstag = pruefungstag;
    }

    public SchriftlicherPruefungstag getPruefungstag() {
        return pruefungstag;
    }
}
