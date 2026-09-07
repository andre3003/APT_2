package de.abiturplanung.gui.stammdaten;

import de.abiturplanung.gui.dialogs.SchuelerStammdatenDialog;
import de.abiturplanung.model.*;
import de.abiturplanung.persistence.Datenbank;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class StammdatenPanel extends JPanel {
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final Datenbank datenbank;
    private final Abitur abitur;
    private final SchuelerStammdatenPanel schuelerStammdatenPanel;
    private Runnable nachStammdatenAenderung;

    public StammdatenPanel(Abitur abitur, Datenbank datenbank) {
        this.datenbank = datenbank;
        this.abitur = abitur;
        setLayout(new BorderLayout());
        schuelerStammdatenPanel = new SchuelerStammdatenPanel(abitur);
        schuelerStammdatenPanel.setNachAenderung(this::schuelerAktualisieren);
        schuelerStammdatenPanel.setSchuelerAnlegen(this::neuenSchuelerAnlegen);
        schuelerStammdatenPanel.setSchuelerLoeschenAction(this::schuelerLoeschen);
        schuelerStammdatenPanel.setAbiturfaecherAendern(this::abiturfaecherAendern);
        tabbedPane.addTab("Schüler", schuelerStammdatenPanel);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void schuelerAktualisieren(SchuelerTableModel.SchuelerAenderung aenderung) {
        Schueler schueler = aenderung.schueler();

        String nachname = schueler.getNachname();
        String vorname = schueler.getVorname();
        LocalDate geburtsdatum = schueler.getGeburtsdatum();
        Geschlecht geschlecht = schueler.getGeschlecht();

        switch (aenderung.spalte()) {
            case 1 -> nachname = (String) aenderung.wert();
            case 2 -> vorname = (String) aenderung.wert();
            case 3 -> geburtsdatum = (LocalDate) aenderung.wert();
            case 4 -> geschlecht = (Geschlecht) aenderung.wert();
        }
        abitur.aendereSchueler(schueler, nachname, vorname, geburtsdatum, geschlecht);
        try {
            datenbank.aktualisiereSchueler(schueler);
            nachStammdatenAenderung.run();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Die Änderung konnte nicht gespeichert werden.\nStarten Sie die Anwendung neu.", "Datenbankfehler",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setNachStammdatenAenderung(Runnable nachStammdatenAenderung) {
        this.nachStammdatenAenderung = nachStammdatenAenderung;
    }

    private void neuenSchuelerAnlegen(SchuelerStammdatenDialog.SchuelerEingabe eingabe) {
        if (abitur.findeSchueler(eingabe.schildId()) != null) {
            JOptionPane.showMessageDialog(this, "Ein Schüler mit dieser Schild-ID ist bereits vorhanden.", "Schild-ID bereits vergeben",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Schueler neu = new Schueler(eingabe.schildId());
        neu.setNachname(eingabe.nachname());
        neu.setVorname(eingabe.vorname());
        neu.setGeburtsdatum(eingabe.geburtsdatum());
        neu.setGeschlecht(eingabe.geschlecht());

        List<Pruefung> pruefungen = List.of(
                new Pruefung(neu, eingabe.ab1(), eingabe.ab1().getFachlehrer(), Abiturfach.AB1),
                new Pruefung(neu, eingabe.ab2(), eingabe.ab2().getFachlehrer(), Abiturfach.AB2),
                new Pruefung(neu, eingabe.ab3(), eingabe.ab3().getFachlehrer(), Abiturfach.AB3),
                new Pruefung(neu, eingabe.ab4(), eingabe.ab4().getFachlehrer(), Abiturfach.AB4)
        );

        try {
            datenbank.fuegeSchuelerHinzu(neu, pruefungen);
            abitur.addSchueler(neu);
            abitur.sortiereSchueler();
            for (Pruefung p : pruefungen) {
                abitur.addPruefung(p);
            }
            nachStammdatenAenderung.run();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void schuelerLoeschen(String schild_id) {
        try {
            datenbank.loescheSchueler(schild_id);
            abitur.schuelerLoeschen(schild_id);
            nachStammdatenAenderung.run();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Der Schüler konnte nicht gelöscht werden.\nStarten Sie die Anwendung neu.", "Datenbankfehler",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abiturfaecherAendern(AbiturfaecherPanel.AbiturfaecherEingabe eingabe) {
        System.out.println("TEST");
    }

    public void ansichtAktualisieren() {
        schuelerStammdatenPanel.ansichtAktualisieren();
    }
}
