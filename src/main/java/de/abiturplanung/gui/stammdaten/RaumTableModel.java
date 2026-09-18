package de.abiturplanung.gui.stammdaten;

import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Lehrer;
import de.abiturplanung.model.Raum;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RaumTableModel extends AbstractTableModel {
    private final Abitur abitur;
    private static final String[] SPALTEN = {"Bezeichnung", "Kapazität"};
    private List<Raum> raeume;
    private Consumer<RaumTableModel.RaumAenderung> nachAenderung;

    public RaumTableModel(Abitur abitur) {
        this.abitur = abitur;
        aktualisieren();
    }

    public record RaumAenderung(String bezeichnung, int spalte, Object wert) {}

    public void setNachAenderung(Consumer<RaumTableModel.RaumAenderung> nachAenderung) {
        this.nachAenderung = nachAenderung;
    }

    @Override
    public int getRowCount() {
        return raeume.size();
    }

    @Override
    public int getColumnCount() {
        return SPALTEN.length;
    }

    @Override
    public String getColumnName(int column) {
        return SPALTEN[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Raum raum = this.raeume.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> raum.getBezeichnung();
            case 1 -> raum.getKapazitaet();
            default -> null;
        };
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Raum raum = raeume.get(rowIndex);
        if (nachAenderung != null) {
            nachAenderung.accept(new RaumAenderung(raum.getBezeichnung(), columnIndex, value));
        }
    }

    public void aktualisieren() {
        raeume = new ArrayList<>(abitur.getRaeume());
        fireTableDataChanged();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> String.class;
            case 1 -> Integer.class;
            default -> Object.class;
        };
    }
}
