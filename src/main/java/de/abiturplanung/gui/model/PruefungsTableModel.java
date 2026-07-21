package de.abiturplanung.gui.model;
import de.abiturplanung.model.Pruefung;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class PruefungsTableModel extends AbstractTableModel {

    private static final String[] SPALTENNAMEN = {
            "Schüler",
            "Abiturfach",
            "Fach",
            "Kurs",
            "Prüfer",
            "Prüfungsform"
    };

    private final List<Pruefung> pruefungen;

    public PruefungsTableModel(List<Pruefung> pruefungen) {
        this.pruefungen = pruefungen;
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
            case 0 -> schuelername(pruefung);
            case 1 -> pruefung.getAbiturfach();
            case 2 -> pruefung.getKurs().getFach();
            case 3 -> pruefung.getKurs().getBezeichnung();
            case 4 -> pruefername(pruefung);
            case 5 -> pruefung.getPruefungsform();
            default -> "";
        };
    }

    public Pruefung getPruefung(int modelRow) {
        return pruefungen.get(modelRow);
    }

    private String schuelername(Pruefung pruefung) {
        return pruefung.getSchueler().getNachname()
                + ", "
                + pruefung.getSchueler().getVorname();
    }

    private String pruefername(Pruefung pruefung) {
        if (pruefung.getPruefer() == null) {
            return "";
        }
        return pruefung.getPruefer().getNachname() + " (" + pruefung.getPruefer().getKuerzel() + ")";
    }
}