package de.abiturplanung.gui.model;

import de.abiturplanung.gui.planung.Abiturklausur;
import de.abiturplanung.gui.planung.SchriftlicherPruefungstag;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Kurs;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SchriftlichePruefungenTableModel extends AbstractTableModel {
    public static final int SPALTE_FACH = 0;
    public static final int SPALTE_KURS = 1;
    public static final int SPALTE_PRUEFER = 2;
    public static final int SPALTE_ABITURIENTEN = 3;
    private static final String[] SPALTENNAMEN = {"Fach", "Kurs", "Prüfer", "Abiturienten"};
    private List<Kurs> kurse = new ArrayList<>();
    private final Abitur abitur;

    public SchriftlichePruefungenTableModel(Abitur abitur) {
        this.abitur = abitur;
    }

    public void kurseEinlesen(SchriftlicherPruefungstag pt) {
        kurse = pt == null ? new ArrayList<>() : kurseEinesPruefungstagsEinlesen(pt);
        fireTableDataChanged();
    }

    private List<Kurs> kurseEinesPruefungstagsEinlesen(SchriftlicherPruefungstag pt) {
        List<Kurs> alleKurse = abitur.getKurse();
        List<Kurs> result = new ArrayList<>();
        for (Kurs kurs : alleKurse) {
            for (Abiturklausur klausur : pt.getAbiturklausuren()) {
                if (kurs.getFach() == klausur.getFach() && kurs.getKursart() == klausur.getKursart() && !abitur.gibSchriftlichePruefungenEinesKurses(kurs).isEmpty()) {
                    result.add(kurs);
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public int getRowCount() {
        return kurse.size();
    }

    @Override
    public int getColumnCount() {
        return SPALTENNAMEN.length;
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
            case SPALTE_PRUEFER -> kurs.getFachlehrer() == null ? "---" : kurs.getFachlehrer().getKuerzel();
            case SPALTE_ABITURIENTEN -> abitur.gibSchriftlichePruefungenEinesKurses(kurs).size();
            default -> "";
        };
    }
}

