package de.abiturplanung.gui.planung;

import de.abiturplanung.gui.model.PruefungsTableModel;
import de.abiturplanung.gui.model.SchriftlichePruefungenTableModel;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Kurs;
import de.abiturplanung.model.Pruefungstag;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class SchriftlichePruefungenPlanungsVorratPanel extends JPanel {
    private final Abitur abitur;
    private SchriftlichePruefungenTableModel tableModel;
    private final List<SchriftlicherPruefungstag> pruefungstage;
    private TableRowSorter<SchriftlichePruefungenTableModel> sorter;


    public SchriftlichePruefungenPlanungsVorratPanel(Abitur abitur) {
        this.abitur = abitur;
        this.setLayout(new BorderLayout());
        this.pruefungstage = abitur.getSchriftlichePruefungstage();
        tableModel = new SchriftlichePruefungenTableModel(abitur);
        JTable kursTabelle = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        kursTabelle.setRowSorter(sorter);
        kursTabelle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(kursTabelle);
        this.add(scrollPane, BorderLayout.CENTER);
        JLabel titel = new JLabel("""
                <html><center>
                <span style='font-size:18px'><b>Prüfungen im 1. bis 3. Abiturfach</b></span>
                <br><br>
                <span style='font-size:14px'>Prüfungskurse des Tages:</span>
                </center></html>
                """, SwingConstants.CENTER);

        titel.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        this.add(titel, BorderLayout.NORTH);
    }

    public void setzePruefungstag(SchriftlicherPruefungstag pt) {
        tableModel.kurseEinlesen(pt);
    }
}
