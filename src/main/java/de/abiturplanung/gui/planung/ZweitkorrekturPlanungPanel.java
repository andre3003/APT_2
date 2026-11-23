package de.abiturplanung.gui.planung;

import de.abiturplanung.gui.model.ZweitkorrekturTableModel;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Lehrer;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class ZweitkorrekturPlanungPanel extends JPanel {
    private final Abitur abitur;
    private final JTable tabelle;
    ZweitkorrekturTableModel model;

    public ZweitkorrekturPlanungPanel(Abitur abitur, Consumer<ZweitkorrekturTableModel.ZweitKorrekturAenderung> aktualisiereZweitkorrektur) {
        this.setLayout(new BorderLayout());
        this.abitur = abitur;
        model = new ZweitkorrekturTableModel(abitur, aktualisiereZweitkorrektur);
        tabelle = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tabelle);
        this.add(scrollPane, BorderLayout.CENTER);
        setzeCellEditoren();
    }

    public void setzeCellEditoren() {
        JComboBox<Lehrer> comboBox = new JComboBox<>();
        comboBox.addItem(null);
        for (Lehrer lehrer : abitur.getLehrer()) {
            comboBox.addItem(lehrer);
        }
        tabelle.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(comboBox));
        tabelle.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(comboBox));
    }

    public void aktualisiereAnsicht(){
        model.aktualisieren();;
    }
}
