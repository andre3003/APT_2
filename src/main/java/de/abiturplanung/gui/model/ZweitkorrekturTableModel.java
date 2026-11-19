package de.abiturplanung.gui.model;
import de.abiturplanung.gui.stammdaten.FachTableModel;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Fach;
import de.abiturplanung.model.Kurs;
import de.abiturplanung.model.Lehrer;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.function.Consumer;

public class ZweitkorrekturTableModel extends AbstractTableModel {
    public static final int SPALTE_FACH = 0;
    public static final int SPALTE_KURS = 1;
    public static final int SPALTE_ABITURIENTEN = 2;
    public static final int SPALTE_ERSTKORREKTOR= 3;
    public static final int SPALTE_ZWEITKORREKTOR_1 = 4;
    public static final int SPALTE_ZWEITKORREKTOR_2 = 5;
    private static final String[] SPALTENNAMEN = {"Fach", "Kurs", "Abiturienten", "Erstkorrektur", "Zweitkorrektur_1", "Zweitkorrektur_2", };
    private final Abitur abitur;
    private List<Kurs> kurse;
    private final Consumer<ZweitKorrekturAenderung> aktualisiereZweitkorrektur;

    public ZweitkorrekturTableModel(Abitur abitur, Consumer<ZweitKorrekturAenderung> aktualisiereZweitkorrektur){
        this.abitur = abitur;
        kurse = abitur.gibSchriftlicheAbiturkurse();
        this.aktualisiereZweitkorrektur = aktualisiereZweitkorrektur;
    }

    public record ZweitKorrekturAenderung(Kurs kurs, Lehrer zweitkorrektor, int spalte) {}

    @Override
    public int getRowCount() {
        return kurse.size();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == SPALTE_ZWEITKORREKTOR_1 || columnIndex == SPALTE_ZWEITKORREKTOR_2;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Kurs kurs = kurse.get(rowIndex);
        Lehrer lehrer = (Lehrer) value;
        aktualisiereZweitkorrektur.accept(new ZweitKorrekturAenderung(kurs, lehrer, columnIndex));
    }

    @Override
    public int getColumnCount() {
        return SPALTENNAMEN.length;
    }

    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case SPALTE_FACH -> Fach.class;
            case SPALTE_KURS -> String.class;
            case SPALTE_ABITURIENTEN -> Integer.class;
            case SPALTE_ERSTKORREKTOR, SPALTE_ZWEITKORREKTOR_1, SPALTE_ZWEITKORREKTOR_2 -> Lehrer.class;
            default -> Object.class;
        };
    }

    @Override
    public String getColumnName(int column) {
        return SPALTENNAMEN[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Kurs kurs = kurse.get(rowIndex);
        return switch (columnIndex) {
            case SPALTE_FACH -> kurs.getFach();
            case SPALTE_KURS -> kurs.getBezeichnung();
            case SPALTE_ERSTKORREKTOR -> kurs.getFachlehrer();
            case SPALTE_ABITURIENTEN -> kurs.getAnzahlKlausurschreiber();
            case SPALTE_ZWEITKORREKTOR_1 -> kurs.getZweitkorrektor1();
            case SPALTE_ZWEITKORREKTOR_2 -> kurs.getZweitkorrektor2();
            default -> "";
        };
    }

    public void aktualisieren() {
        fireTableDataChanged();
    }
}
