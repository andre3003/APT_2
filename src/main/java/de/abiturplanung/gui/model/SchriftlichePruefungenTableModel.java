package de.abiturplanung.gui.model;

import de.abiturplanung.gui.planung.Abiturklausur;
import de.abiturplanung.gui.planung.SchriftlicherPruefungstag;
import de.abiturplanung.model.Kurs;
import de.abiturplanung.model.Pruefung;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SchriftlichePruefungenTableModel extends AbstractTableModel {
    public static final int SPALTE_FACH = 0;
    public static final int SPALTE_KURS = 1;
    public static final int SPALTE_PRUEFER = 2;
    public static final int SPALTE_FOLGE = 3;
    public static final int SPALTE_STATUS = 4;
    private static final String[] SPALTENNAMEN = {"Fach", "Kurs", "Prüfer", "Folge", "Status"};

    List<Kurs> kurse = new ArrayList<>();
    SchriftlicherPruefungstag pruefungstag;

    public SchriftlichePruefungenTableModel(List<Kurs> alleKurse, SchriftlicherPruefungstag pruefungstag) {
        this.pruefungstag = pruefungstag;
        pruefungenEinlesen(alleKurse);
    }

    private void pruefungenEinlesen(List<Kurs> alleKurse) {
        kurse.clear();
        for (Kurs kurs : alleKurse) {
            for (Abiturklausur klausur : pruefungstag.getAbiturklausuren()) {
                if (kurs.getFach() == klausur.getFach() && kurs.getKursart() == klausur.getKursart()) {
                    kurse.add(kurs);
                }
            }
        }
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
//                case SPALTE_FOLGE -> pruefung.getPruefungsFolge() == null ? "" : pruefung.getPruefungsFolge();
//                case SPALTE_STATUS -> pruefung.istVollstaendigGeplant() ? "vollständig" : "unvollständig";
            default -> "";
        };
    }
}

