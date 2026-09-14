package de.abiturplanung.gui.menue;

import javax.swing.*;

public class Hauptmenue {

    private final JMenuBar menueleiste;

    private final HauptmenueAktionen aktionen;


    public Hauptmenue(HauptmenueAktionen aktionen) {
        this.aktionen = aktionen;
        this.menueleiste = erstelleMenueleiste();
    }

    public JMenuBar getMenueleiste() {
        return menueleiste;
    }


    private JMenuBar erstelleMenueleiste() {
        JMenuBar menueleiste = new JMenuBar();
        JMenu dateiMenue = new JMenu("Datei");

        JMenuItem neuePlanungEintrag = new JMenuItem("Neue Planung...");
        neuePlanungEintrag.addActionListener(e -> aktionen.neuePlanungAction()); //Java kann den Typ ActionEvent aus addActionListener() ableiten, deshalb genügt e.

        dateiMenue.add(neuePlanungEintrag);
        dateiMenue.addSeparator();

        JMenuItem planungOeffnenEintrag = new JMenuItem("Planung öffnen...");
        planungOeffnenEintrag.addActionListener(e -> aktionen.planungOeffnenAction());

        dateiMenue.add(planungOeffnenEintrag);

        JMenuItem backupEintrag = new JMenuItem("Backup erstellen...");
        backupEintrag.addActionListener(e -> aktionen.backupErstellenAction());

        dateiMenue.addSeparator();
        dateiMenue.add(backupEintrag);

        JMenuItem beendenEintrag = new JMenuItem("Beenden");

        beendenEintrag.addActionListener(e -> aktionen.beendenAction());

        dateiMenue.addSeparator();

        dateiMenue.add(beendenEintrag);
        menueleiste.add(dateiMenue);

        JMenu planungMenue = new JMenu("Planung");

        JMenuItem pruefungstagHinzufuegen = new JMenuItem("Prüfungstag hinzufügen");
        pruefungstagHinzufuegen.addActionListener(e -> aktionen.pruefungstagHinzufuegenAction());

        JMenuItem pruefungstagEntfernen = new JMenuItem("Prüfungstag entfernen");
        pruefungstagEntfernen.addActionListener(e -> aktionen.pruefungstagEntfernenAction());

        planungMenue.add(pruefungstagHinzufuegen);
        planungMenue.addSeparator();
        planungMenue.add(pruefungstagEntfernen);
        planungMenue.addSeparator();

        JMenuItem datumAendern = new JMenuItem("Datum des aktuellen PT ändern");
        datumAendern.addActionListener(e -> aktionen.datumPruefungstagAendern());
        planungMenue.add(datumAendern);
        planungMenue.addSeparator();
        menueleiste.add(planungMenue);
        JMenu pruefungsfolge = new JMenu("Prüfungsfolge");
        JMenuItem vorlagePruegfungsfolgeErstellen = new JMenuItem("Vorlage erstellen");
        vorlagePruegfungsfolgeErstellen.addActionListener(e -> aktionen.vorlagePruefungsfolgeErstellen());
        pruefungsfolge.add(vorlagePruegfungsfolgeErstellen);
        pruefungsfolge.addSeparator();
        JMenuItem folgenimportieren = new JMenuItem("Folgen Importieren");
        folgenimportieren.addActionListener(e -> aktionen.pruefungsfolgenImportieren());
        pruefungsfolge.add(folgenimportieren);
        planungMenue.add(pruefungsfolge);
        menueleiste.add(planungMenue);
        JMenu ansichtMenue = new JMenu("Ansicht");
        JMenuItem timeLineAnsicht = new JMenuItem("Timeline");
        timeLineAnsicht.addActionListener(e -> aktionen.timeLineErzeugen());
        ansichtMenue.add(timeLineAnsicht);

        menueleiste.add(ansichtMenue);

        JMenu module = new JMenu("Module");

        JMenuItem muendlichePruefungen = new JMenuItem("Mündliche Prüfungen");
        muendlichePruefungen.addActionListener(e -> aktionen.zeigeMuendlichePruefungen());
        module.add(muendlichePruefungen);
        module.addSeparator();

        JMenuItem stammdaten = new JMenuItem("Stammdaten");
        stammdaten.addActionListener(e -> aktionen.zeigeStammdaten());
        module.add(stammdaten);
        menueleiste.add(module);
        JMenu importMenue = new JMenu("Import");

        JMenuItem schuelerImportEintrag = new JMenuItem("Schüler laden");
        schuelerImportEintrag.addActionListener(e -> aktionen.schuelerImportAction());

        JMenuItem lehrerImportEintrag = new JMenuItem("Lehrer laden");
        lehrerImportEintrag.addActionListener(e -> aktionen.lehrerImportAction());

        JMenuItem faecherImportEintrag = new JMenuItem("Fächer laden");
        faecherImportEintrag.addActionListener(e -> aktionen.faecherImportAction());

        JMenuItem raeumeImportEintrag = new JMenuItem("Räume laden");
        raeumeImportEintrag.addActionListener(e -> aktionen.raeumeImportAction());

        JMenuItem leistungsdatenImportEintrag = new JMenuItem("Leistungsdaten laden");
        leistungsdatenImportEintrag.addActionListener(e -> aktionen.leistungsdatenImportAction());


        importMenue.add(schuelerImportEintrag);
        importMenue.addSeparator();
        importMenue.add(lehrerImportEintrag);
        importMenue.addSeparator();
        importMenue.add(faecherImportEintrag);
        importMenue.addSeparator();
        importMenue.add(raeumeImportEintrag);
        importMenue.addSeparator();
        importMenue.add(leistungsdatenImportEintrag);

        menueleiste.add(importMenue);

        return menueleiste;
    }

}
