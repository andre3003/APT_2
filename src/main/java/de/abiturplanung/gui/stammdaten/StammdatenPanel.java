package de.abiturplanung.gui.stammdaten;

import de.abiturplanung.gui.dialogs.SchuelerStammdatenDialog;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Schueler;
import de.abiturplanung.persistence.Datenbank;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class StammdatenPanel extends JPanel {
    private final JTabbedPane tabbedPane = new JTabbedPane();
    Datenbank datenbank;
    Abitur abitur;

    public StammdatenPanel(Abitur abitur, Datenbank datenbank) {
        this.datenbank = datenbank;
        this.abitur = abitur;
        setLayout(new BorderLayout());
        SchuelerStammdatenPanel panel = new SchuelerStammdatenPanel(abitur);
        panel.setNachAenderung(this::schuelerAktualisieren);
        panel.setSchuelerAnlegen(this::neuenSchuelerAnlegen);
        tabbedPane.addTab("Schüler", panel);
        add(tabbedPane, BorderLayout.CENTER);
    }


    private void schuelerAktualisieren(Schueler schueler) {
        try {
            datenbank.aktualisiereSchueler(schueler);
        } catch (SQLException e) {JOptionPane.showMessageDialog(this, "Die Änderung konnte nicht gespeichert werden.\n" + "Starten Sie die Anwendung neu.",
                    "Datenbankfehler",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void neuenSchuelerAnlegen(SchuelerStammdatenDialog.SchuelerEingabe eingabe) {
        //ID checken fehlt! Datenchek insgesamt fehlt! Schülerliste wird nicht neu sortiert.
        if (abitur.findeSchueler(eingabe.schildId()) != null) {JOptionPane.showMessageDialog(this, "Ein Schüler mit dieser Schild-ID ist bereits vorhanden.",
                "Schild-ID bereits vergeben",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Schueler neu = new Schueler(eingabe.schildId());
        neu.setNachname(eingabe.nachname());
        neu.setVorname(eingabe.vorname());
        neu.setGeburtsdatum(eingabe.geburtsdatum());
        neu.setGeschlecht(eingabe.geschlecht());
        try {
            datenbank.fuegeSchuelerHinzu(neu);
            abitur.addSchueler(neu);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
