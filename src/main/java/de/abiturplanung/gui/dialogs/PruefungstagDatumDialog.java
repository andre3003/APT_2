package de.abiturplanung.gui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class PruefungstagDatumDialog {

    private PruefungstagDatumDialog() {}

    public static LocalDate anzeigen(Component parent, String titel, LocalDate vorauswahl) {
        Date startDatum = Date.from(vorauswahl.atStartOfDay(ZoneId.systemDefault()).toInstant());

        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateModel.setValue(startDatum);

        JSpinner datumSpinner = new JSpinner(dateModel);
        datumSpinner.setEditor(new JSpinner.DateEditor(datumSpinner, "dd.MM.yyyy"));

        int ergebnis = JOptionPane.showConfirmDialog(parent, datumSpinner, titel, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ergebnis != JOptionPane.OK_OPTION) return null;

        Date ausgewaehlt = dateModel.getDate();
        return ausgewaehlt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}