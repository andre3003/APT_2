package de.abiturplanung.gui.planung;

import de.abiturplanung.gui.dialogs.PruefungstagDatumDialog;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Pruefung;
import de.abiturplanung.model.Pruefungstag;
import de.abiturplanung.persistence.Datenbank;
import de.abiturplanung.service.Kollisionspruefer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PruefungstagePanel extends JPanel implements PruefungsKartenAktionen {
    private JTabbedPane tabbedPane = new JTabbedPane();
    private List<PlanungsMatrixPanel> matrixPanels = new ArrayList<>();
    private Abitur abitur;
    private Datenbank datenbank;
    private Kollisionspruefer kollisionspruefer;
    private Pruefung kopiertePruefung;
    private final Runnable planungsvorratAnsichtAktualisieren;

    public PruefungstagePanel(Abitur abitur, Datenbank datenbank, Runnable planungsvorratAktualisieren) {
        this.abitur = abitur;
        this.datenbank = datenbank;
        this.planungsvorratAnsichtAktualisieren = planungsvorratAktualisieren;
        this.kollisionspruefer = new Kollisionspruefer(abitur);
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.addChangeListener(this::selektionenAufheben);
        ansichtAktualisieren();
    }



    private void selektionenAufheben(ChangeEvent e) {
        for (PlanungsMatrixPanel matrixPanel : matrixPanels) {
            matrixPanel.selektionAufheben();
        }
    }

    public void fokussierePruefungInMatrix(Pruefung pruefung) {
        if (pruefung.getPruefungstag() == null) {
            return;
        }
        for (int i = 0; i < abitur.getPruefungstage().size(); i++) {
            Pruefungstag pruefungstag = abitur.getPruefungstage().get(i);
            if (pruefungstag.getDatum().equals(pruefung.getPruefungstag())) {
                tabbedPane.setSelectedIndex(i);
                PlanungsMatrixPanel matrixPanel = matrixPanels.get(i);
                matrixPanel.fokussierePruefung(pruefung);
                return;
            }
        }
    }

    private void fokussierePruefungstagRechts(Pruefungstag pruefungstag) {
        int index = abitur.getPruefungstage().indexOf(pruefungstag);

        if (index >= 0 && index < matrixPanels.size()) {
            tabbedPane.setSelectedIndex(index);
            matrixPanels.get(index).scrollNachRechts();
        }
    }

    //Interface-Methoden:

    @Override
    public void nachBearbeitung(Pruefung pruefung) {
        try {
            datenbank.aktualisierePruefungsplanung(pruefung);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Die Änderungen konnten nicht gespeichert werden.\nStarten Sie die Anwendung neu.", "Datenbankfehler",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        Map<Pruefung, List<Pruefung>> aktuelleKollisionen = kollisionspruefer.findeAlleKollisionen();
        for (PlanungsMatrixPanel panel : matrixPanels) {
            panel.setKollisionen(aktuelleKollisionen);
            panel.ansichtAktualisieren();
        }
        planungsvorratAnsichtAktualisieren.run();
    }

    public void pruefungstagHinzufuegen() {
        LocalDate datum = PruefungstagDatumDialog.anzeigen(this, "Prüfungstag hinzufügen", LocalDate.now());
        if (datum == null) return;

        for (Pruefungstag pruefungstag : abitur.getPruefungstage()) {
            if (pruefungstag.getDatum().equals(datum)) {
                JOptionPane.showMessageDialog(this, "Dieser Prüfungstag existiert bereits.", "Hinweis", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        Pruefungstag pruefungstag = new Pruefungstag(datum, 8);
        try {
            datenbank.fuegePruefungstagHinzu(pruefungstag);
            abitur.addPruefungstag(pruefungstag);
            ansichtAktualisieren();
            ;

        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, "Der Prüfungstag konnte nicht gespeichert werden:\n" + exception.getMessage(), "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void pruefungstagEntfernen() {
        int index = tabbedPane.getSelectedIndex();
        if (index < 0 || index >= abitur.getPruefungstage().size()) {
            return;
        }
        Pruefungstag pruefungstag = abitur.getPruefungstage().get(index);
        int anzahlPruefungen = 0;
        for (Pruefung pruefung : abitur.getPruefungen()) {
            if (pruefungstag.getDatum().equals(pruefung.getPruefungstag())) {
                anzahlPruefungen++;
            }
        }
        try {
            if (anzahlPruefungen == 0) {
                int bestaetigung = JOptionPane.showConfirmDialog(this, "Prüfungstag " + pruefungstag.getDatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " wirklich löschen?", "Prüfungstag löschen", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (bestaetigung == JOptionPane.YES_OPTION) {
                    abitur.loeschePruefungstag(pruefungstag, true);
                    datenbank.loeschePruefungstag(pruefungstag);
                    ansichtAktualisieren();
                }
                return;
            }
            Object[] optionen = {"Kommissionen behalten", "Kommissionen mitlöschen", "Abbrechen"};
            int auswahl = JOptionPane.showOptionDialog(this, "An diesem Prüfungstag sind " + anzahlPruefungen + " Prüfungen geplant.\n\n" + "Planungsdaten werden gelöscht.\n" + "Sollen die bestehenden Kommissionen erhalten bleiben?", "Prüfungstag löschen",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, optionen, optionen[0]);

            if (auswahl == 2 || auswahl == JOptionPane.CLOSED_OPTION) {
                return;
            }
            ArrayList<Pruefung> geaendertePruefungen = new ArrayList<>();
            if (auswahl == 0) {
                geaendertePruefungen = abitur.loeschePruefungstag(pruefungstag, true);

            } else if (auswahl == 1) {
                geaendertePruefungen = abitur.loeschePruefungstag(pruefungstag, false);
            }
            for (Pruefung p : geaendertePruefungen) {
                datenbank.aktualisierePruefungsplanung(p);
            }
            datenbank.loeschePruefungstag(pruefungstag);
            planungsvorratAnsichtAktualisieren.run();
            ansichtAktualisieren();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void planungsspalteHinzufuegen(Pruefungstag pruefungstag) {
        pruefungstag.setAnzahlPlanungsspalten(pruefungstag.getAnzahlPlanungsspalten() + 1);
        try {
            datenbank.aktualisierePruefungstagAnzahlPlanungsspalten(pruefungstag);
            ansichtAktualisieren();
            fokussierePruefungstagRechts(pruefungstag);
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, "Die Planungsspalte konnte nicht gespeichert werden.\nBitte starten Sie die Anwendung neu.", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void planungsspalteEntfernen(Pruefungstag pruefungstag) {
        if (pruefungstag.getAnzahlPlanungsspalten() == 1) {
            return;
        }
        int letzteSpalte = pruefungstag.getAnzahlPlanungsspalten();
        for (Pruefung pruefung : abitur.getPruefungen()) {
            if (pruefungstag.getDatum().equals(pruefung.getPruefungstag()) && pruefung.getPlanungsspalte() == letzteSpalte) {
                JOptionPane.showMessageDialog(this, "Die Planungsspalte konnte nicht entfernt werden.\nSie enthält noch Prüfungen.", "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        pruefungstag.setAnzahlPlanungsspalten(pruefungstag.getAnzahlPlanungsspalten() - 1);
        try {
            datenbank.aktualisierePruefungstagAnzahlPlanungsspalten(pruefungstag);
            ansichtAktualisieren();
            fokussierePruefungstagRechts(pruefungstag);
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, "Die Änderung konnte nicht gespeichert werden.\nBitte starten Sie die Anwendung neu.", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void datumPruefungstagAendern() {
        int index = tabbedPane.getSelectedIndex();
        if (index < 0 || index >= abitur.getPruefungstage().size()) {
            return;
        }
        Pruefungstag pruefungstag = abitur.getPruefungstage().get(index);
        LocalDate altesDatum = pruefungstag.getDatum();
        LocalDate neuesDatum = PruefungstagDatumDialog.anzeigen(this, "Datum des Prüfungstags ändern", altesDatum);
        if (neuesDatum == null || neuesDatum.equals(altesDatum)) {
            return;
        }
        for (Pruefungstag andererTag : abitur.getPruefungstage()) {
            if (andererTag != pruefungstag && andererTag.getDatum().equals(neuesDatum)) {
                JOptionPane.showMessageDialog(this, "Dieser Prüfungstag existiert bereits.", "Hinweis", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        try {
            datenbank.aktualisierePruefungstagDatum(altesDatum, neuesDatum);
            abitur.aenderePruefungstag(pruefungstag, neuesDatum);

            for (Pruefung pruefung : abitur.getPruefungen()) {
                if (altesDatum.equals(pruefung.getPruefungstag())) {
                    pruefung.setPruefungstag(neuesDatum);
                }
            }
            planungsvorratAnsichtAktualisieren.run();
            ansichtAktualisieren();
            int neuerIndex = abitur.getPruefungstage().indexOf(pruefungstag);
            if (neuerIndex >= 0) {
                tabbedPane.setSelectedIndex(neuerIndex);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Das Datum des Prüfungstags konnte nicht geändert werden:\n" + e.getMessage(), "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void planungsdatenKopieren(Pruefung pruefung) {
        kopiertePruefung = pruefung;
    }

    @Override
    public void planungsdatenUebertragen(Pruefung pruefung) {
        if (kopiertePruefung == null) {
            return;
        }
        pruefung.setVorsitz(kopiertePruefung.getVorsitz());
        pruefung.setSchriftfuehrer(kopiertePruefung.getSchriftfuehrer());
        pruefung.setRaum(kopiertePruefung.getRaum());
        nachBearbeitung(pruefung);
    }


    public void ansichtAktualisieren() {
        tabbedPane.removeAll();
        matrixPanels.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        Map<Pruefung, List<Pruefung>> alleKollisionen = kollisionspruefer.findeAlleKollisionen();
        for (Pruefungstag pruefungstag : abitur.getPruefungstage()) {
            PlanungsMatrixPanel matrixPanel = new PlanungsMatrixPanel(abitur, pruefungstag);
            matrixPanel.setzNachSpalteHinzufuegen(this::planungsspalteHinzufuegen);
            matrixPanel.setzNachSpalteEntfernen(this::planungsspalteEntfernen);
            matrixPanels.add(matrixPanel);
            matrixPanel.setKollisionen(alleKollisionen);
            matrixPanel.setzePruefungskartenAktionen(this);
            matrixPanel.ansichtAktualisieren();
            tabbedPane.addTab(pruefungstag.getDatum().format(formatter), matrixPanel);
        }
        revalidate();
        repaint();
    }
}
