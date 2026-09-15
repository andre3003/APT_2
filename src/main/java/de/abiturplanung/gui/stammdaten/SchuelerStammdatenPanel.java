package de.abiturplanung.gui.stammdaten;

import de.abiturplanung.gui.dialogs.SchuelerStammdatenDialog;
import de.abiturplanung.model.Abitur;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class SchuelerStammdatenPanel extends JPanel {
    private final Abitur abitur;
    private final SchuelerStammdatenTabellePanel stammdatenTabellePanel;
    private final AbiturfaecherPanel abiturfaecherPanel;

    public SchuelerStammdatenPanel(Abitur abitur) {
        this.abitur = abitur;
        stammdatenTabellePanel = new SchuelerStammdatenTabellePanel(abitur);
        abiturfaecherPanel = new AbiturfaecherPanel(abitur.getKurse());
        stammdatenTabellePanel.setNachAuswahl(abiturfaecherPanel::setSchueler);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, stammdatenTabellePanel, abiturfaecherPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.8);

        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
    }

    public void setSchuelerLoeschenAction(Consumer<String> schuelerLoeschen) {
        stammdatenTabellePanel.setSchuelerLoeschenAction(schuelerLoeschen);
    }

    public void setNachAenderung(Consumer<SchuelerTableModel.SchuelerAenderung> nachAenderung) {
        stammdatenTabellePanel.setNachAenderung(nachAenderung);
    }

    public void setSchuelerAnlegen(Consumer<SchuelerStammdatenDialog.SchuelerEingabe> schuelerAnlegen) {
        stammdatenTabellePanel.setSchuelerAnlegen(schuelerAnlegen);
    }

    public void setAbiturfaecherAendern(Consumer<AbiturfaecherPanel.AbiturfaecherEingabe> abiturfaecherAendern) {
        abiturfaecherPanel.setUebernehmen(abiturfaecherAendern);
    }

    public void ansichtAktualisieren() {
        stammdatenTabellePanel.ansichtAktualisieren();
    }
}

