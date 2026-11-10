package de.abiturplanung.gui.planung;

import de.abiturplanung.gui.dialogs.AbiturklausurDialog;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Kurs;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SchriftlichePruefungstagePanel extends JPanel {
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final ArrayList<SchriftlicherPruefungstagPlanungsPanel> planungsPanels = new ArrayList<>();
    private final Abitur abitur;
    private final Consumer<SchriftlicherPruefungstag> nachTabWechsel;
    private final BiConsumer<Abiturklausur, AbiturklausurDialog.Eingaben> aktualisierAbiturklausur;
    private final Consumer<RaumbelegungAbiturklausuren> speichereRaumbelegung;
    private final BiConsumer<RaumbelegungAbiturklausuren, Kurs> speichereKursZuRaumZuordnnung;

    public SchriftlichePruefungstagePanel(Abitur abitur, Consumer<SchriftlicherPruefungstag> nachTabWechsel, BiConsumer<Abiturklausur, AbiturklausurDialog.Eingaben> aktualisiereAbiturklausur,
                                          Consumer<RaumbelegungAbiturklausuren> speichereRaumbelegung, BiConsumer<RaumbelegungAbiturklausuren, Kurs> speichereKursZuRaumZuordnnung) {
        this.abitur = abitur;
        this.speichereRaumbelegung = speichereRaumbelegung;
        this.aktualisierAbiturklausur = aktualisiereAbiturklausur;
        this.speichereKursZuRaumZuordnnung = speichereKursZuRaumZuordnnung;
        this.nachTabWechsel = nachTabWechsel;
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.addChangeListener(this::tabChangeAction);
        ansichtAktualisieren();
    }

    private void tabChangeAction(ChangeEvent changeEvent) {
        SchriftlicherPruefungstagPlanungsPanel panel = (SchriftlicherPruefungstagPlanungsPanel) tabbedPane.getSelectedComponent();
        if (panel != null) {
            nachTabWechsel.accept(panel.getPruefungstag());
        }
    }

    public void ansichtAktualisieren() {
        tabbedPane.removeAll();
        planungsPanels.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        for (SchriftlicherPruefungstag pt : abitur.getSchriftlichePruefungstage()) {
            SchriftlicherPruefungstagPlanungsPanel panel = new SchriftlicherPruefungstagPlanungsPanel(abitur, pt, aktualisierAbiturklausur, speichereRaumbelegung, speichereKursZuRaumZuordnnung);
            planungsPanels.add(panel);
            tabbedPane.add(pt.getDatum().format(formatter), panel);
        }
    }


    public void zeigePruefungstag(SchriftlicherPruefungstag pruefungstag) {
        int index = abitur.getSchriftlichePruefungstage().indexOf(pruefungstag);
        if (index >= 0 && index < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(index);
        }
    }
}
