package de.abiturplanung.gui.stammdaten;

import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Fach;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.function.Consumer;

public class FachTableModel extends AbstractTableModel {
    private final Abitur abitur;
    private List<Fach> faecher;
    private static final String[] SPALTEN = {"Kürzel", "Bezeichnung", "Stammfach", "Fächergruppe"};
    private Consumer<FachTableModel.FachAenderung> nachAenderung;


    public FachTableModel(Abitur abitur) {
        this.abitur = abitur;
        aktualisieren();
    }

    public record FachAenderung(String kuerzel, int spalte, Object wert){}


    public void setNachAenderung(Consumer<FachTableModel.FachAenderung> nachAenderung) {
        this.nachAenderung = nachAenderung;
    }

    @Override
    public int getRowCount() {
        return faecher.size();
    }

    @Override
    public String getColumnName(int column) {
        return SPALTEN[column];
    }

    @Override
    public int getColumnCount() {
        return SPALTEN.length;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Fach fach = faecher.get(rowIndex);
        if (nachAenderung != null) {
            nachAenderung.accept(new FachTableModel.FachAenderung(fach.getKuerzel(), columnIndex, value));
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Fach fach = faecher.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> fach.getKuerzel();
            case 1 -> fach.getBezeichnung();
            case 2 -> fach.getStammfach();
            case 3 -> fach.getFaechergruppe();
            default -> null;
        };
    }

    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> String.class;
            case 1 -> String.class;
            case 2 -> Fach.class;
            case 3 -> String.class;
            default -> null;
        };
    }

    public void aktualisieren() {
        faecher = abitur.getFaecher();
        fireTableDataChanged();
    }


}
