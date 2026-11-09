package de.abiturplanung.gui.planung;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.stream.Collectors;

public class AbiturklausurenTableModel extends AbstractTableModel {
    String [] SPALTENNAMEN = {"Fach", "Kursart", "Bearbeitungszeit", "Auswahlzeit", "Hilfsmittel"};
    private static final int SPALTE_FACH = 0;
    private static final int SPALTE_KURSART = 1;
    private static final int SPALTE_BEARBEITUNGSZEIT = 2;
    private  static final int SPALTE_AUSWAHLZEIT = 3;
    private static final int SPALTE_HILFSMITTEL= 4;

    SchriftlicherPruefungstag pruefungstag;
    List<Abiturklausur> abiturklausuren;

    public AbiturklausurenTableModel(SchriftlicherPruefungstag pruefungstag){
        this.pruefungstag = pruefungstag;
        abiturklausuren = pruefungstag.getAbiturklausuren();

    }

    @Override
    public int getRowCount() {
        return abiturklausuren.size();
    }

    @Override
    public int getColumnCount() {
        return SPALTENNAMEN.length;
    }

    @Override
    public String getColumnName(int column) {
        return SPALTENNAMEN[column];
    }

    public Abiturklausur getAbiturklausur(int index) {
        return abiturklausuren.get(index);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Abiturklausur klausur = abiturklausuren.get(rowIndex);

        return switch (columnIndex) {
            case SPALTE_FACH -> klausur.getFach();
            case SPALTE_KURSART -> klausur.getKursart();
            case SPALTE_BEARBEITUNGSZEIT ->  klausur.getBearbeitungszeit() == null ? "---" : klausur.getBearbeitungszeit();
            case SPALTE_AUSWAHLZEIT -> klausur.getAuswahlzeit() == null ? "---" : klausur.getAuswahlzeit();
            case SPALTE_HILFSMITTEL -> klausur.getHilfsmittel().stream().map(Hilfsmittel::getBezeichnung).collect(Collectors.joining(", "));
                default -> "";
        };
    }
}
