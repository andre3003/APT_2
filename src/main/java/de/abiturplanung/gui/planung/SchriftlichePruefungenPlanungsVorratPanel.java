package de.abiturplanung.gui.planung;

import de.abiturplanung.gui.model.SchriftlichePruefungenTableModel;
import de.abiturplanung.model.Abitur;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SchriftlichePruefungenPlanungsVorratPanel extends JPanel {
    private SchriftlichePruefungenTableModel tableModel;
    private final List<SchriftlicherPruefungstag> pruefungstage;


    public  SchriftlichePruefungenPlanungsVorratPanel(Abitur abitur) {
        this.setLayout(new BorderLayout());
        this.pruefungstage = abitur.getSchriftlichePruefungstage();
        tableModel = new SchriftlichePruefungenTableModel(abitur.getKurse(), pruefungstage.get(0));
        JTable kursTabelle = new JTable(tableModel);
        this.add(kursTabelle, BorderLayout.CENTER);
    }

}
