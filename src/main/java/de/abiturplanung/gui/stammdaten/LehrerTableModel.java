package de.abiturplanung.gui.stammdaten;

import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Amtsbezeichnung;
import de.abiturplanung.model.Fach;
import de.abiturplanung.model.Lehrer;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LehrerTableModel extends AbstractTableModel {

    private static final String[] SPALTEN = {
            "Kürzel",
            "Nachname",
            "Vorname",
            "Amtsbezeichnung",
            "Fakultas 1",
            "Fakultas 2",
            "Fakultas 3",
            "Fakultas 4"
    };

    private final Abitur abitur;
    private List<Lehrer> lehrer;
    private Consumer<LehrerAenderung> nachAenderung;

    public LehrerTableModel(Abitur abitur) {
        this.abitur = abitur;
        aktualisieren();
    }

    public record LehrerAenderung(Lehrer lehrer, int spalte, Object wert) {
    }

    @Override
    public int getRowCount() {
        return lehrer.size();
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
    public Object getValueAt(int rowIndex, int columnIndex) {
        Lehrer lehrer = this.lehrer.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> lehrer.getKuerzel();
            case 1 -> lehrer.getNachname();
            case 2 -> lehrer.getVorname();
            case 3 -> lehrer.getAmtsbezeichnung();
            case 4 -> getFakultaet(lehrer, 0);
            case 5 -> getFakultaet(lehrer, 1);
            case 6 -> getFakultaet(lehrer, 2);
            case 7 -> getFakultaet(lehrer, 3);
            default -> null;
        };
    }

    private Fach getFakultaet(Lehrer lehrer, int index) {
        if (index >= lehrer.getFakultas().size()) {
            return null;
        }
        return lehrer.getFakultas().get(index);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Lehrer aktuellerLehrer = lehrer.get(rowIndex);
        if (nachAenderung != null) {
            nachAenderung.accept(new LehrerAenderung(aktuellerLehrer, columnIndex, value));
        }
    }

    public Lehrer getLehrer(int rowIndex) {
        return lehrer.get(rowIndex);
    }

    public void setNachAenderung(Consumer<LehrerAenderung> nachAenderung) {
        this.nachAenderung = nachAenderung;
    }

    public void aktualisieren() {
        lehrer = new ArrayList<>(abitur.getLehrer());
        fireTableDataChanged();
    }


    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0, 1, 2 -> String.class;
            case 3 -> Amtsbezeichnung.class;
            case 4, 5, 6, 7 -> Fach.class;
            default -> Object.class;
        };
    }
}