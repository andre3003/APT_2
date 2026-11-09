package de.abiturplanung.gui.planung;

import de.abiturplanung.gui.dialogs.AbiturklausurDialog;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Raum;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.List;
import java.util.function.Consumer;

public class SchriftlicherPruefungstagPlanungsPanel extends JPanel {
    private final SchriftlicherPruefungstag pruefungstag;
    private final Abitur abitur;
    private JTable klausurenTabelle;
    private AbiturklausurenTableModel model;
    private final BiConsumer<Abiturklausur, AbiturklausurDialog.Eingaben> aktualisiereKlausur;
    private final Consumer<RaumbelegungAbiturklausuren> speichereRaumbelegung;
    private RaumbelegungenPanel raumbelegungenPanel;

    public SchriftlicherPruefungstagPlanungsPanel(Abitur abitur, SchriftlicherPruefungstag pruefungstag, BiConsumer<Abiturklausur, AbiturklausurDialog.Eingaben> aktualisiereklausur,
                                                  Consumer<RaumbelegungAbiturklausuren> speichereRaumbelegung) {
        this.speichereRaumbelegung = speichereRaumbelegung;
        this.pruefungstag = pruefungstag;
        this.aktualisiereKlausur = aktualisiereklausur;
        this.abitur = abitur;
        erstelleGUI();
    }

    public void erstelleGUI() {
        this.setLayout(new BorderLayout());
        JPanel planungsPanel = new JPanel();
        JPanel steuerung = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btNeuerKlsusurRaum = new JButton("Klausur-Raum anlegen");
        btNeuerKlsusurRaum.addActionListener(this::btKlausurRaumAction);
        steuerung.add(btNeuerKlsusurRaum);
        this.add(steuerung, BorderLayout.SOUTH);
        this.add(planungsPanel, BorderLayout.CENTER);
        model = new AbiturklausurenTableModel(pruefungstag);
        klausurenTabelle = new JTable(model);
        setzeSpaltenbreiten();
        int hoehe = klausurenTabelle.getTableHeader().getPreferredSize().height + klausurenTabelle.getRowHeight() * model.getRowCount() + 3;
        klausurenTabelle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mausKlickTabelle(e);
            }
        });
        JPanel klausurenPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(klausurenTabelle);
        scrollPane.setPreferredSize(new Dimension(0, hoehe));
        klausurenPanel.add(scrollPane, BorderLayout.CENTER);
        JLabel titel = new JLabel("""
                <html><center>
                <span style='font-size:16px'><b>Klausuren des Prüfungstages</b></span>
                </center></html>
                """, SwingConstants.CENTER);
        titel.setBorder(BorderFactory.createEmptyBorder(6, 10, 4, 10));
        klausurenPanel.add(titel, BorderLayout.NORTH);

        this.add(klausurenPanel, BorderLayout.NORTH);
        raumbelegungenPanel = new RaumbelegungenPanel(pruefungstag);
        this.add(raumbelegungenPanel, BorderLayout.CENTER);
    }

    private void setzeSpaltenbreiten() {
        int[] SPALTENBREITEN = {70, 120, 110, 110, 350};
        for (int i = 0; i < SPALTENBREITEN.length; i++) {
            klausurenTabelle.getColumnModel().getColumn(i).setPreferredWidth(SPALTENBREITEN[i]);
        }
    }

    private void btKlausurRaumAction(ActionEvent event) {
        Raum[] raeume = ermitteleRaeume();
        if (raeume.length == 0) {
            JOptionPane.showMessageDialog(this, "Es sind keine weiteren Räume verfügbar.");
            return;
        }
        Raum raum = (Raum) JOptionPane.showInputDialog(
                this,
                "Bitte einen Raum auswählen:",
                "Klausur-Raum anlegen",
                JOptionPane.PLAIN_MESSAGE,
                null,
                raeume,
                raeume.length > 0 ? raeume[0] : null
        );
        if (raum == null) return;
        RaumbelegungAbiturklausuren raumbelegung = new RaumbelegungAbiturklausuren(null, pruefungstag, raum);
        pruefungstag.addRaumbelegung(raumbelegung);
        speichereRaumbelegung.accept(raumbelegung);
        raumbelegungenPanel.aktualisiereAnzeige();
    }

    public SchriftlicherPruefungstag getPruefungstag() {
        return pruefungstag;
    }

    public Raum[] ermitteleRaeume() {
        List<Raum> freieRaeume = new ArrayList<>();
        for (Raum raum : abitur.getRaeume()) {
            boolean bereitsVerwendet = false;
            for (RaumbelegungAbiturklausuren belegung : pruefungstag.getRaumbelegungen()) {
                if (belegung.getRaum() == raum) {
                    bereitsVerwendet = true;
                    break;
                }
            }
            if (!bereitsVerwendet) freieRaeume.add(raum);
        }
        return  freieRaeume.toArray(new Raum[0]);
    }


    public void mausKlickTabelle(MouseEvent e) {
        if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
            int zeile = klausurenTabelle.rowAtPoint(e.getPoint());
            if (zeile < 0) return;
            int modelZeile = klausurenTabelle.convertRowIndexToModel(zeile);
            Abiturklausur klausur = model.getAbiturklausur(modelZeile);
            Window owner = SwingUtilities.getWindowAncestor(klausurenTabelle);
            AbiturklausurDialog dialog = new AbiturklausurDialog(owner, abitur, klausur);
            dialog.setVisible(true);
            AbiturklausurDialog.Eingaben eingaben = dialog.getEingaben();
            if (eingaben == null) return; // Abgebrochen
            aktualisiereKlausur.accept(klausur, eingaben);
            model.fireTableRowsUpdated(modelZeile, modelZeile);
        }
    }
}

