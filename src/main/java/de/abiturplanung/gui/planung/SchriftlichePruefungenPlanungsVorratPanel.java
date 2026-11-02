package de.abiturplanung.gui.planung;

import de.abiturplanung.gui.model.PruefungsTableModel;
import de.abiturplanung.gui.model.SchriftlichePruefungenTableModel;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Kurs;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class SchriftlichePruefungenPlanungsVorratPanel extends JPanel {
   private final Abitur abitur;
    private SchriftlichePruefungenTableModel tableModel;
    private final List<SchriftlicherPruefungstag> pruefungstage;
    private JComboBox<String> cmbPreufungstag;
    private TableRowSorter<SchriftlichePruefungenTableModel> sorter;


    public  SchriftlichePruefungenPlanungsVorratPanel(Abitur abitur) {
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
        this.add(erstelleFilterPanel(), BorderLayout.NORTH);
    }

    public JPanel erstelleFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Pruefungstag: ");
        cmbPreufungstag = new JComboBox<>();

        for (SchriftlicherPruefungstag pruefungstag : abitur.getSchriftlichePruefungstage()) {
            cmbPreufungstag.addItem(pruefungstag.getDatum().toString());
        }
        filterPanel.add(label);
        filterPanel.add(cmbPreufungstag);
        return filterPanel;
    }

    public void filterAktualisieren() {
        cmbPreufungstag.getSelectedIndex();
        RowFilter.
    }

}
