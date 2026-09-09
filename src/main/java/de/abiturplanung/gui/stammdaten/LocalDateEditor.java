package de.abiturplanung.gui.stammdaten;
import de.abiturplanung.Utilities;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LocalDateEditor extends DefaultCellEditor {
    private final JTextField textField;

    public LocalDateEditor() {
        super(new JTextField()); //Bei parameterlosen Konstruktoren ruft Java automatisch super auf. Hier geht das natürlich nicht, weil der Konstruktor einen Parameter erwartet.
        textField = (JTextField) this.getComponent(); //Holt sich das Textfield des Ediors und speichert einen Verweis. Anders geht es nicht, weil der Aufruf des Konstruktor der Oberklasse bearbeitet wird, bevor die kokale Variable angelegt wird.
    }


    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        textField.setText(value instanceof LocalDate datum ? Utilities.formatiereDatum(datum) : "");
        return textField;
    }

    @Override
    public Object getCellEditorValue() {
        return Utilities.parseDatum(textField.getText());
    }

    @Override
    public boolean stopCellEditing() {
        try {
            Utilities.parseDatum(textField.getText());
            return super.stopCellEditing();
        } catch (DateTimeParseException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    textField, "Bitte geben Sie ein gültiges Datum im Format TT.MM.JJJJ ein.", "Ungültiges Datum", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
