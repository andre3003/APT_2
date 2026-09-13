package de.abiturplanung.gui.stammdaten;

import de.abiturplanung.model.Abitur;

import javax.swing.*;
import java.awt.*;

public class LehrerStammdatenPanel extends JPanel {

    private final Abitur abitur;

    public LehrerStammdatenPanel(Abitur abitur) {
        this.abitur = abitur;
        setLayout(new BorderLayout());
        // Tabelle kommt als Nächstes
    }
}

