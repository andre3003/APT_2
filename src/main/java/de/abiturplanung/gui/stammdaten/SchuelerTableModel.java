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
    private Consumer<SchuelerAenderung> nachAenderung;
    public record SchuelerAenderung(Schueler schueler, int spalte, Object wert) {}


    public SchuelerTableModel(List<Schueler> schueler) {
        this.schueler = schueler;
    }

    public void setNachAenderung(Consumer<SchuelerAenderung> nachAenderung) {
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
        if (nachAenderung != null) {
            nachAenderung.accept(new SchuelerAenderung(aktuellerSchueler, columnIndex, value));
        }
    }

    public void aktualisieren() {
        fireTableDataChanged();
    }

    public Schueler getSchueler(int modelRow) {
        return schueler.get(modelRow);
    }
}