package de.abiturplanung.gui.planung;

import de.abiturplanung.gui.dialogs.AbiturklausurDialog;
import de.abiturplanung.gui.dialogs.PruefungstagAnlegenDialog;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.persistence.Datenbank;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SchriftlichePruefungenPanel extends JPanel {
    private final SchriftlichePruefungstagePanel pruefungstagePanel;
    private final SchriftlichePruefungenPlanungsVorratPanel planungsvorratPanel;
    private final Abitur abitur;
    private final Datenbank datenbank;

    public SchriftlichePruefungenPanel(Abitur abitur, Datenbank datenbank) {
        this.abitur = abitur;
        this.datenbank = datenbank;
        this.setLayout(new BorderLayout());
        planungsvorratPanel = new SchriftlichePruefungenPlanungsVorratPanel(abitur);
        pruefungstagePanel = new SchriftlichePruefungstagePanel(abitur, this::setzePruefungstag, this::aktualisiereAbiturklausur, this::speichereRaumbelegung);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, planungsvorratPanel, pruefungstagePanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.32);
        splitPane.setDividerLocation(480);
        this.add(splitPane, BorderLayout.CENTER);
    }

    public void pruefungstagHinzufuegen(LocalDate datum, java.util.List<PruefungstagAnlegenDialog.KlausurEingabe> klausuren) {
        SchriftlicherPruefungstag pruefungstag = new SchriftlicherPruefungstag(datum);
        for (PruefungstagAnlegenDialog.KlausurEingabe klausurEingabe : klausuren) {
            Abiturklausur klausur = new Abiturklausur(klausurEingabe.fach(), klausurEingabe.kursart());
            pruefungstag.addAbiturklausur(klausur);
        }
        try {
            datenbank.fuegeSchriftlichenPruefungstagHinzu(pruefungstag);
            abitur.addSchriftlicherPruefungstag(pruefungstag);
            pruefungstagePanel.ansichtAktualisieren();
            pruefungstagePanel.zeigePruefungstag(pruefungstag);
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, "Der Prüfungstag konnte nicht gespeichert werden:\n" + exception.getMessage(), "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setzePruefungstag(SchriftlicherPruefungstag pruefungstag) {
        planungsvorratPanel.setzePruefungstag(pruefungstag);
    }

    public void aktualisiereAbiturklausur(Abiturklausur klausur, AbiturklausurDialog.Eingaben eingabe) {
        Integer alteBearbeitungszeit = klausur.getBearbeitungszeit();
        Integer alteAuswahlzeit = klausur.getAuswahlzeit();
        List<Hilfsmittel> alteHilfsmittel = new ArrayList<>(klausur.getHilfsmittel());

        klausur.setBearbeitungszeit(eingabe.bearbeitungszeit());
        klausur.setAuswahlzeit(eingabe.auswahlzeit());
        klausur.getHilfsmittel().clear();
        for (Hilfsmittel hilfsmittel : eingabe.hilfsmittel()) {
            klausur.addHilfsmittel(hilfsmittel);
        }
        try {
            datenbank.aktualisiereAbiturklausur(klausur);
        } catch (SQLException | RuntimeException e) {
            klausur.setBearbeitungszeit(alteBearbeitungszeit);
            klausur.setAuswahlzeit(alteAuswahlzeit);
            klausur.getHilfsmittel().clear();
            alteHilfsmittel.forEach(klausur::addHilfsmittel);
            JOptionPane.showMessageDialog(this, "Die Abiturklausur konnte nicht aktualisiert werden:\n" + e.getMessage(), "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void speichereRaumbelegung(RaumbelegungAbiturklausuren belegung) {
        try {
            datenbank.speichereRaumbelegung(belegung);
        } catch (SQLException e) {
            throw new RuntimeException("Raumbelegung konnte nicht gespeichert werden.", e);
        }
    }
}
