package de.abiturplanung.gui.model;

import de.abiturplanung.gui.planung.Abiturklausur;
import de.abiturplanung.gui.planung.SchriftlicherPruefungstag;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Kurs;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchriftlichePruefungenTableModel extends AbstractTableModel {
    public static final int SPALTE_FACH = 0;
    public static final int SPALTE_KURS = 1;
    public static final int SPALTE_PRUEFER = 2;
    private static final String[] SPALTENNAMEN = {"Fach", "Kurs", "Prüfer", "Folge", "Status"};

    private List<Kurs> kurse = new ArrayList<>();
    private final Abitur abitur;
    private Map<LocalDate, List<Kurs>> kursePrepruefungstageMap = new HashMap();

    public SchriftlichePruefungenTableModel(Abitur abitur) {
        this.abitur = abitur;
    }

//    private void kurseEinlesen(List<Kurs> alleKurse) {
//        kurse.clear();
//        for (Kurs kurs : alleKurse) {
//            for (Abiturklausur klausur : pruefungstag.getAbiturklausuren()) {
//                if (kurs.getFach() == klausur.getFach() && kurs.getKursart() == klausur.getKursart()) {
//                    kurse.add(kurs);
//                }
//            }
//        }
//    }

    private List<Kurs> kurseEinesPruefungstags(SchriftlicherPruefungstag pt) {
//        kurse.clear();
        List<Kurs> alleKurse = abitur.getKurse();
        List<Kurs> result = new ArrayList<>();
        for (Kurs kurs : alleKurse) {
            for (Abiturklausur klausur : pt.getAbiturklausuren()) {
                if (kurs.getFach() == klausur.getFach() && kurs.getKursart() == klausur.getKursart()) {
                    result.add(kurs);
                }
            }
        }
        return result;
    }

    private void kurseZuPruefungstage() {
        for (SchriftlicherPruefungstag pruefungstag : abitur.getSchriftlichePruefungstage()) {
            kursePrepruefungstageMap.put(pruefungstag.getDatum(), kurseEinesPruefungstags(pruefungstag));
        }
    }

    public List<Kurs> getAlleKurse() {
        return kurse;
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
            default -> "";
        };
    }
}

