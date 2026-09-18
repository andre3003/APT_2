package de.abiturplanung.gui.stammdaten;
import de.abiturplanung.model.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class KursTableModel extends AbstractTableModel {
    private final Abitur abitur;
    private static final String[] SPALTEN= {"Bezeichnung", "Fach", "Fachlehrer" };
    private List<Kurs> kurse;
    private Consumer<KursTableModel.KursAenderung> nachAenderung;

    public KursTableModel(Abitur abitur) {
        this.abitur = abitur;
        aktualisieren();
    }

    public record KursAenderung(String bezeichnung, int spalte, Object wert) {};

    public void setNachAenderung(Consumer<KursTableModel.KursAenderung> nachAenderung) {
        this.nachAenderung = nachAenderung;
    }

    @Override
    public int getRowCount() {
        return kurse.size();
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
    public int getColumnCount() {
        return SPALTEN.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Kurs kurs = this.kurse.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> kurs.getBezeichnung();
            case 1 -> kurs.getFach();
            case 2 -> kurs.getFachlehrer();
            default -> null;
        };
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Kurs kurs = kurse.get(rowIndex);
        if (nachAenderung != null) {
            nachAenderung.accept(new KursTableModel.KursAenderung(kurs.getBezeichnung(), columnIndex, value));
        }
    }

    public void aktualisieren() {
        kurse = new ArrayList<>(abitur.getKurse());
        fireTableDataChanged();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> String.class;
            case 1 -> Fach.class;
            case 2 -> Lehrer.class;
            default -> Object.class;
        };
    }
}
