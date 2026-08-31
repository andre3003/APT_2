package de.abiturplanung.gui.stammdaten;
import de.abiturplanung.Utilities;
import de.abiturplanung.model.Geschlecht;
import de.abiturplanung.model.Schueler;
import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class SchuelerTableModel extends AbstractTableModel {

    private final List<Schueler> schueler;
    private final String[] spaltennamen = {"Schild-ID", "Nachname", "Vorname", "Geburtsdatum", "Geschlecht"};
    private Consumer<Schueler> nachAenderung;


    public SchuelerTableModel(List<Schueler> schueler) {
        this.schueler = schueler;
    }

    public void setNachAenderung(Consumer<Schueler> nachAenderung) {
        this.nachAenderung = nachAenderung;
    }

    @Override
    public int getRowCount() {
        return schueler.size();
    }

    @Override
    public int getColumnCount() {
        return spaltennamen.length;
    }

    @Override
    public String getColumnName(int column) {
        return spaltennamen[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Schueler s = schueler.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> s.getSchildId();
            case 1 -> s.getNachname();
            case 2 -> s.getVorname();
            case 3 -> s.getGeburtsdatum();
            case 4 -> s.getGeschlecht();
            default -> null;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Schueler aktuellerSchueler = schueler.get(rowIndex);
        switch (columnIndex) {
            case 1 -> aktuellerSchueler.setNachname((String) value);
            case 2 -> aktuellerSchueler.setVorname((String) value);
            case 3 -> aktuellerSchueler.setGeburtsdatum((LocalDate) value);
            case 4 -> aktuellerSchueler.setGeschlecht((Geschlecht) value);
        }
        fireTableCellUpdated(rowIndex, columnIndex);
        if (nachAenderung != null) {
            nachAenderung.accept(aktuellerSchueler);
        }
    }
}