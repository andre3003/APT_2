package de.abiturplanung.gui.model;

import de.abiturplanung.model.Abiturfach;
import de.abiturplanung.model.Pruefung;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class PruefungsTableModel extends AbstractTableModel {

    public static final int SPALTE_SCHUELER = 0;
    public static final int SPALTE_FACH = 1;
    public static final int SPALTE_KURS = 2;
    public static final int SPALTE_PRUEFER = 3;
    public static final int SPALTE_STATUS = 4;

    private static final String[] SPALTENNAMEN = {"Schüler", "Fach", "Kurs", "Prüfer", "Status"};

    private final List<Pruefung> pruefungen = new ArrayList<>();

    public PruefungsTableModel(List<Pruefung> allePruefungen) {
        for (Pruefung pruefung : allePruefungen) {
            if (pruefung.getAbiturfach() == Abiturfach.AB4) {
                pruefungen.add(pruefung);
            }
        }
    }

    @Override
    public int getRowCount() {
        return pruefungen.size();
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
        Pruefung pruefung = pruefungen.get(rowIndex);

        return switch (columnIndex) {
            case SPALTE_SCHUELER -> pruefung.getSchueler().getNachname() + ", " + pruefung.getSchueler().getVorname();
            case SPALTE_FACH -> pruefung.getKurs().getFach();
            case SPALTE_KURS -> pruefung.getKurs().getBezeichnung();
            case SPALTE_PRUEFER -> pruefung.getPruefer() == null ? "---" : pruefung.getPruefer().getKuerzel();
            case SPALTE_STATUS -> pruefung.istVollstaendigGeplant() ? "vollständig" : "unvollständig";
            default -> "";
        };
    }

    public Pruefung getPruefung(int modelRow) {
        return pruefungen.get(modelRow);
    }
}