package de.abiturplanung.gui.stammdaten;

import de.abiturplanung.gui.dialogs.*;
import de.abiturplanung.model.*;
import de.abiturplanung.persistence.Datenbank;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StammdatenPanel extends JPanel {
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final Datenbank datenbank;
    private final Abitur abitur;
    private final SchuelerStammdatenPanel schuelerStammdatenPanel;
    private final LehrerStammdatenPanel lehrerStammdatenPanel;
    private final RaumStammdatenPanel raumStammdatenPanel;
    private final KursStammdatenPanel kursStammdatenPanel;
    private final FachStammdatenPanel fachStammdatenPanel;
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

        lehrerStammdatenPanel = new LehrerStammdatenPanel(abitur);
        lehrerStammdatenPanel.setNachAenderung(this::lehrerAktualisieren);
        lehrerStammdatenPanel.setLehrerLoeschenAction(this::lehrerLoeschen);
        lehrerStammdatenPanel.setLehrerAnlegenAction(this::neuenLehrerAnlegen);
        tabbedPane.add("Lehrer", lehrerStammdatenPanel);

        raumStammdatenPanel = new RaumStammdatenPanel(abitur);
        raumStammdatenPanel.setNachAenderung(this::raumAendern);
        raumStammdatenPanel.setRaumAnlegenAction(this::neuenRaumAnlegen);
        tabbedPane.add("Räume", raumStammdatenPanel);

        kursStammdatenPanel = new KursStammdatenPanel(abitur);
        kursStammdatenPanel.setNachAenderung(this::kursAendern);
        kursStammdatenPanel.setKursAnlegen(this::neuenKursAnlegen);
        tabbedPane.add("Kurse", kursStammdatenPanel);

        fachStammdatenPanel = new FachStammdatenPanel(abitur);
        fachStammdatenPanel.setNachAenderung(this::fachAendern);
        fachStammdatenPanel.setNeuesFachAnlegen(this::neuesFachAnlegen);
        tabbedPane.add("Fächer", fachStammdatenPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void neuenRaumAnlegen(RaumStammdatenDialog.RaumEingabe raumEingabe) {
        if (abitur.findeRaum(raumEingabe.bezeichnung()) != null) {
            JOptionPane.showMessageDialog(this, "Einen Raum mit dieser Bezeichnung ist bereits vorhanden.", "Raum-Bezeichnung bereits vergeben", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Raum raum = new Raum(raumEingabe.bezeichnung(), raumEingabe.kapazitaet());
         abitur.addRaum(raum);
         abitur.sortiereRaeume();
         try {
             datenbank.fuegeRaumHinzu(raum);
             nachStammdatenAenderung.run();
         } catch (SQLException e) {
             e.printStackTrace();
             JOptionPane.showMessageDialog(this, "Der Raum konnte nicht gespeichert werden.", "Fehler beim Speichern", JOptionPane.ERROR_MESSAGE);
         }
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
            JOptionPane.showMessageDialog(this, "Die Änderung konnte nicht gespeichert werden.\nStarten Sie die Anwendung neu.", "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setNachStammdatenAenderung(Runnable nachStammdatenAenderung) {
        this.nachStammdatenAenderung = nachStammdatenAenderung;
    }

    private void neuenSchuelerAnlegen(SchuelerStammdatenDialog.SchuelerEingabe eingabe) {
        if (abitur.findeSchueler(eingabe.schildId()) != null) {
            JOptionPane.showMessageDialog(this, "Ein Schüler mit dieser Schild-ID ist bereits vorhanden.", "Schild-ID bereits vergeben", JOptionPane.WARNING_MESSAGE);
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

    public void raumAendern(RaumTableModel.RaumAenderung aenderung) {
        Raum raum = abitur.findeRaum(aenderung.bezeichnung());
        raum.aktualisiereStammdaten((Integer) aenderung.wert());
        try {
            datenbank.aktualisiereRaum(raum);
            nachStammdatenAenderung.run();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void kursAendern(KursTableModel.KursAenderung aenderung) {
       Kurs kurs = abitur.findeKurs(aenderung.bezeichnung());
       if (kurs == null) {
           return;
       }
       switch (aenderung.spalte()){
           case 1 -> kurs.setFach((Fach) aenderung.wert());
           case 2 -> kurs.setFachlehrer((Lehrer) aenderung.wert());
       }
       try {
           datenbank.aktualisiereKurs(kurs);
           nachStammdatenAenderung.run();
       } catch (SQLException e) {
           e.printStackTrace();
       }
    }

    private void neuenKursAnlegen(KursStammdatenDialog.KursEingabe eingabe) {
        if (abitur.findeKurs(eingabe.bezeichnung()) != null) {
            JOptionPane.showMessageDialog(this, "Ein Kurs mit dieser Bezeichnung ist bereits vorhanden.", "Kursbezeichnung bereits vergeben", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Kurs neuerKurs = new Kurs(eingabe.bezeichnung(), eingabe.fach(), eingabe.fachlehrer());
        try {
            datenbank.fuegeKursHinzu(neuerKurs);
            abitur.addKurs(neuerKurs);
            nachStammdatenAenderung.run();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Der Kurs konnte nicht gespeichert werden.", "Fehler beim Speichern", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fachAendern(FachTableModel.FachAenderung aenderung) {
        Fach fach = abitur.findeFach(aenderung.kuerzel());
        if (fach == null) {
            return;
        }
        switch (aenderung.spalte()) {
            case 1 -> fach.setBezeichnung((String) aenderung.wert());
            case 2 -> fach.setStammfach((Fach) aenderung.wert());
            case 3 -> fach.setFaechergruppe((String) aenderung.wert());
        }
        try {
            datenbank.aktualisiereFach(fach);
            nachStammdatenAenderung.run();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private void neuesFachAnlegen(FachStammdatenDialog.FachEingabe eingabe) {
        if (abitur.findeFach(eingabe.kuerzel()) != null) {
            JOptionPane.showMessageDialog(this, "Ein Fach mit diesem Kürzel ist bereits vorhanden.", "Fachkürzel bereits vergeben", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Fach  neuesFach = new Fach(eingabe.kuerzel());
        neuesFach.setBezeichnung(eingabe.bezeichnung());
        neuesFach.setStammfach(eingabe.stammfach());
        neuesFach.setFaechergruppe(eingabe.faechergruppe());
        abitur.addFach(neuesFach);
        try {
            datenbank.fuegeFachHinzu(neuesFach);
            abitur.sortiereFaecher();
            nachStammdatenAenderung.run();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Das Fach konnte nicht gespeichert werden.", "Fehler beim Speichern", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abiturfaecherAendern(AbiturfaecherPanel.AbiturfaecherEingabe eingabe) {
        Schueler schueler = eingabe.schueler();
        Map<Pruefung, Kurs> aenderungen = new HashMap<>();
        pruefeAenderung(aenderungen, schueler, Abiturfach.AB1, eingabe.ab1());
        pruefeAenderung(aenderungen, schueler, Abiturfach.AB2, eingabe.ab2());
        pruefeAenderung(aenderungen, schueler, Abiturfach.AB3, eingabe.ab3());
        pruefeAenderung(aenderungen, schueler, Abiturfach.AB4, eingabe.ab4());

        if (aenderungen.isEmpty()) {
            return;
        }
        try {
            //DB aktualisieren:
            datenbank.aktualisierePruefungskurse(aenderungen);
            //Domäne aktualisieren:
            for (Map.Entry<Pruefung, Kurs> aenderung : aenderungen.entrySet()) {
                Pruefung pruefung = aenderung.getKey();
                Kurs neuerKurs = aenderung.getValue();
                pruefung.setKurs(neuerKurs);
                pruefung.setPruefer(neuerKurs.getFachlehrer());
                pruefung.setPruefungstag(null);
                pruefung.setBeginn(null);
                pruefung.setPlanungsspalte(null);
                pruefung.setRaum(null);
                pruefung.setVorsitz(null);
                pruefung.setSchriftfuehrer(null);
            }
            //GUI aktualisieren:
            nachStammdatenAenderung.run();

            JOptionPane.showMessageDialog(this, "Es wurden " + aenderungen.size() + " Prüfungskurse geändert.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Änderungen konnten nicht gespeichert übernommen.", "Änderungen der Prüfungskurse gescheitert.", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void pruefeAenderung(Map<Pruefung, Kurs> aenderungen, Schueler schueler, Abiturfach abiturfach, Kurs neuerKurs) {
        Pruefung pruefung = schueler.getPruefung(abiturfach);
        if (pruefung == null) {
            throw new IllegalStateException("Keine Prüfung für " + abiturfach + " vorhanden.");
        }
        if (!pruefung.getKurs().equals(neuerKurs)) {
            aenderungen.put(pruefung, neuerKurs);
        }
    }

    private void lehrerAktualisieren(LehrerTableModel.LehrerAenderung aenderung) {
        Lehrer lehrer = aenderung.lehrer();
        String nachname = lehrer.getNachname();
        String vorname = lehrer.getVorname();
        Amtsbezeichnung amtsbezeichnung = lehrer.getAmtsbezeichnung();
        List<Fach> fakultas = new ArrayList<>(lehrer.getFakultas());

        switch (aenderung.spalte()) {
            case 1 -> nachname = (String) aenderung.wert();
            case 2 -> vorname = (String) aenderung.wert();
            case 3 -> amtsbezeichnung = (Amtsbezeichnung) aenderung.wert();
            case 4, 5, 6, 7 -> {
                int index = aenderung.spalte() - 4;
                Fach fach = (Fach) aenderung.wert();

                if (fach == null) {
                    if (index < fakultas.size()) {
                        fakultas.remove(index);
                    }
                } else {
                    if (fakultas.contains(fach) && (index >= fakultas.size() || !fach.equals(fakultas.get(index)))) {
                        JOptionPane.showMessageDialog(this, "Das Fach ist bereits als Fakultas eingetragen.", "Ungültige Eingabe", JOptionPane.WARNING_MESSAGE);
                        nachStammdatenAenderung.run();
                        return;
                    }

                    if (index < fakultas.size()) {
                        fakultas.set(index, fach);
                    } else {
                        fakultas.add(fach);
                    }
                }
            }
        }

        if (!abitur.aendereLehrer(lehrer, nachname, vorname, amtsbezeichnung, fakultas)) {
            JOptionPane.showMessageDialog(this, "Der Lehrer konnte nicht aktualisiert werden.", "Fehler", JOptionPane.ERROR_MESSAGE);
            nachStammdatenAenderung.run();
            return;
        }

        try {
            datenbank.aktualisiereLehrer(lehrer);
            nachStammdatenAenderung.run();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Die Änderung konnte nicht gespeichert werden.\nStarten Sie die Anwendung neu.", "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void lehrerLoeschen(String kuerzel) {
        Abitur.LehrerVerwendungen verwendungen = abitur.findeVerwendungen(kuerzel);
        if (verwendungen.istLeer()) {
            try {
                datenbank.loescheLehrer(kuerzel);
                abitur.lehrerLoeschen(kuerzel);
                nachStammdatenAenderung.run();
                return;
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Der Lehrer konnte nicht gelöscht werden.", "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
            }
        }
            StringBuilder meldung = new StringBuilder();
            meldung.append("<html>");
            meldung.append("<b>Der Lehrer ").append(kuerzel).append(" kann nicht gelöscht werden.</b><br><br>");
            meldung.append("Er wird noch verwendet als:<br>");
            if (!verwendungen.kurse().isEmpty()) {
                for (Kurs kurs : verwendungen.kurse()) {meldung.append("&nbsp;&nbsp;• Fachlehrer im Kurs ").append(kurs.getBezeichnung()).append("<br>");
                }
            }
            if (verwendungen.alsPruefer() > 0) {
                meldung.append("&nbsp;&nbsp;• Prüfer in ").append(verwendungen.alsPruefer()).append(" Prüfung(en)<br>");
            }
            if (verwendungen.alsVorsitzender() > 0) {
                meldung.append("&nbsp;&nbsp;• Vorsitzender in ").append(verwendungen.alsVorsitzender()).append(" Prüfung(en)<br>");
            }
            if (verwendungen.alsSchriftfuehrer() > 0) {
                meldung.append("&nbsp;&nbsp;• Schriftführer in ").append(verwendungen.alsSchriftfuehrer()).append(" Prüfung(en)<br>");
            }
            meldung.append("</html>");
            JOptionPane.showMessageDialog(this, meldung.toString(), "Lehrer kann nicht gelöscht werden", JOptionPane.WARNING_MESSAGE);
    }

    public void neuenLehrerAnlegen(LehrerStammdatenDialog.LehrerEingabe eingabe) {
        if (abitur.findeLehrer(eingabe.kuerzel()) != null) {
            JOptionPane.showMessageDialog(this, "Ein Lehrer mit diesem Kürzel ist bereits vorhanden.", "Lehrerkürzel bereits vergeben", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Lehrer neu = new Lehrer(eingabe.kuerzel());
        neu.aktualisiereStammdaten(eingabe.anrede(), eingabe.nachname(), eingabe.vorname(), eingabe.amtsbezeichnung());
        List<Fach> fakultas = new ArrayList<>();
        if (eingabe.fak1() != null) fakultas.add(eingabe.fak1());
        if (eingabe.fak2() != null) fakultas.add(eingabe.fak2());
        if (eingabe.fak3() != null) fakultas.add(eingabe.fak3());
        if (eingabe.fak4() != null) fakultas.add(eingabe.fak4());
        neu.setFakultas(fakultas);

        try {
            datenbank.fuegeLehrerHinzu(neu);
            abitur.addLehrer(neu);
            abitur.sortiereLehrer();
            nachStammdatenAenderung.run();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void ansichtAktualisieren() {
        schuelerStammdatenPanel.ansichtAktualisieren();
        lehrerStammdatenPanel.ansichtAktualisieren();
        raumStammdatenPanel.ansichtAktualisieren();
        kursStammdatenPanel.ansichtAktualisieren();
        fachStammdatenPanel.ansichtAktualisieren();
    }
}
