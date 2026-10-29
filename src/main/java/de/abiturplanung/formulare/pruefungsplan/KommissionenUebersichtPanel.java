package de.abiturplanung.formulare.pruefungsplan;

import de.abiturplanung.Utilities;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Pruefung;
import de.abiturplanung.model.Pruefungstag;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class KommissionenUebersichtPanel extends JPanel {
    private final Abitur abitur;
    private final PruefungsplanDatenService pruefungsplanDatenService = new PruefungsplanDatenService();
    private JComboBox<Pruefungstag> pruefungstagComboBox;
    private JCheckBox cbPdfDrucken;
    DefaultTableModel model;

    public KommissionenUebersichtPanel(Abitur abitur) {
        this.abitur = abitur;
        initGui();
    }

    private void initGui() {
        this.setLayout(new BorderLayout());
        String [] spaltenNamen = {"Kommission", "Fach", "Prüfer", "Protokoll", "Vorsitz", "Kurs", "Datum", "Raum", "Beginn", "Schüler"};
        model = new DefaultTableModel(spaltenNamen, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        setzeSpaltenbreiten(table);
        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);
        datenHinzufuegen();
    }

    private void datenHinzufuegen() {
        List<Pruefungstag> pruefungstage = abitur.getPruefungstage();
        for (Pruefungstag pt : pruefungstage) {
            List<KommissionsGruppe> kommissionesgruppen = pruefungsplanDatenService.gibSortierteKommissionsGruppen(abitur, pt);
            for (KommissionsGruppe gruppe : kommissionesgruppen) {
                List<Pruefung> pruefungen = gruppe.pruefungen();
                String fach = pruefungen.get(0).getKurs().getFach().getKuerzel();
                String pruefer = gruppe.pruefer();
                String kommissionsbezeichnung = fach + "-" + pruefer;
                String kurs = pruefungen.get(0).getKurs().getBezeichnung();
                String datum = Utilities.formatiereDatumKurz(pruefungen.get(0).getPruefungstag());
                String raum = pruefungen.get(0).getRaum().getBezeichnung();
                String beginn = pruefungen.get(0).getBeginn().toString();
                String schueler = pruefungen.get(0).getSchueler().getNachname() + ", " + pruefungen.get(0).getSchueler().getVorname();
                String[] datensatz = {kommissionsbezeichnung, fach, pruefer, gruppe.schriftfuehrer(), gruppe.vorsitz(), kurs, datum, raum, beginn, schueler};
                model.addRow(datensatz);
                for (int i = 1; i < pruefungen.size(); i++) {
                    beginn = pruefungen.get(i).getBeginn().toString();
                    schueler = pruefungen.get(i).getSchueler().getNachname() + ", " + pruefungen.get(i).getSchueler().getVorname();
                    datensatz = new String[]{"", "", "", "", "", "", "", "", beginn, schueler};
                    model.addRow(datensatz);
                }
            }
        }
    }

    private void setzeSpaltenbreiten(JTable table){
        int [] spaltenbreiten = {80, 50, 60, 60, 60, 80, 80, 50, 60, 180};
        for (int i = 0; i < spaltenbreiten.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(spaltenbreiten[i]);
        }
    }
}
