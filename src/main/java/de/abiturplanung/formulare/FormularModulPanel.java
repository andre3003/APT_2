package de.abiturplanung.formulare;

import de.abiturplanung.formulare.abiturfomulare.AbiturformularPanel;
import de.abiturplanung.formulare.pruefungsplan.KommissionenUebersichtPanel;
import de.abiturplanung.formulare.schueleruebersicht.SchueleruebersichtPanel;
import de.abiturplanung.formulare.pruefungsplan.PruefungsplanPanel;
import de.abiturplanung.model.Abitur;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class FormularModulPanel extends JPanel {
    private Abitur abitur;
    private JPanel vorschauPanel;
    JSplitPane splitPane;


    public FormularModulPanel(Abitur abitur) {
        this.abitur = abitur;
        initialisierGui();
    }

    private void initialisierGui() {
        this.setLayout(new BorderLayout());
        JPanel formularTreePanel = new JPanel(new BorderLayout());
        vorschauPanel = new JPanel(new BorderLayout());
        JTree formularTree = erstelleFormularTree();
        formularTree.addTreeSelectionListener(e -> formularAusgewaehlt(formularTree));
        formularTreePanel.add(new JScrollPane(formularTree), BorderLayout.CENTER);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formularTreePanel, vorschauPanel);
        splitPane.setDividerLocation(280);
        this.add(splitPane, BorderLayout.CENTER);
    }

    private JTree erstelleFormularTree() {
        DefaultMutableTreeNode wurzel = new DefaultMutableTreeNode("Formulare");

        DefaultMutableTreeNode interneUebersichten = new DefaultMutableTreeNode("Interne Übersichten");
        interneUebersichten.add(new DefaultMutableTreeNode(new FormularTreeEintrag(FormularTyp.PRUEFUNGSPLAN, "Prüfungsplan")));
        interneUebersichten.add(new DefaultMutableTreeNode(new FormularTreeEintrag(FormularTyp.SCHUELERUEBERSICHT, "Prüfungsübersicht für Schüler")));
        interneUebersichten.add(new DefaultMutableTreeNode(new FormularTreeEintrag(FormularTyp.KOMMISSIONNENUEBERSICHT, "Übersicht über die Kommissionen")));

        DefaultMutableTreeNode abiturFormulare = new DefaultMutableTreeNode("Abitur-Formulare");

        DefaultMutableTreeNode muendlich = new DefaultMutableTreeNode("4. Fach (mündlich)");
        DefaultMutableTreeNode schriftlich = new DefaultMutableTreeNode("Schriftliche Prüfungen");
        DefaultMutableTreeNode zaa = new DefaultMutableTreeNode("Zentraler Abiturausschuss");

        abiturFormulare.add(muendlich);
        muendlich.add(new DefaultMutableTreeNode(new FormularTreeEintrag(FormularTyp.NIEDERSCHRIFTMUENDLICHEPRUEFUNG_AB4, "Niederschrift mündl. Prüfung 4. Fach")));
        muendlich.add(new DefaultMutableTreeNode(new FormularTreeEintrag(FormularTyp.NIEDERSCHRIFT_FPA_AB4, "Niederschrift der Konferenz des FPA")));
        muendlich.add(new DefaultMutableTreeNode(new FormularTreeEintrag(FormularTyp.LISTE_FPA, "Liste der FPAs")));

        abiturFormulare.add(schriftlich);
        abiturFormulare.add(zaa);

        wurzel.add(interneUebersichten);
        wurzel.add(abiturFormulare);

        return new JTree(wurzel);
    }

    private void formularAusgewaehlt(JTree formularTree) {
        DefaultMutableTreeNode knoten = (DefaultMutableTreeNode) formularTree.getLastSelectedPathComponent();
        if (knoten == null) {
            return;
        }
        Object objekt = knoten.getUserObject();
        if (objekt instanceof FormularTreeEintrag formular) {
            if (formular.getTyp() == FormularTyp.PRUEFUNGSPLAN) {
                zeigePruefungsplan();
                return;
            }
            if (formular.getTyp() == FormularTyp.SCHUELERUEBERSICHT) {
                zeigeSchueleruebersicht();
                return;
            }
            if (formular.getTyp() == FormularTyp.NIEDERSCHRIFTMUENDLICHEPRUEFUNG_AB4) {
                zeigeFormularVorschau(FormularTyp.NIEDERSCHRIFTMUENDLICHEPRUEFUNG_AB4);
                return;
            }
            if (formular.getTyp() == FormularTyp.NIEDERSCHRIFT_FPA_AB4) {
                zeigeFormularVorschau(FormularTyp.NIEDERSCHRIFT_FPA_AB4);
                return;
            }
            if (formular.getTyp() == FormularTyp.LISTE_FPA) {
                zeigeFormularVorschau(FormularTyp.LISTE_FPA);
                return;
            }
            if (formular.getTyp() == FormularTyp.KOMMISSIONNENUEBERSICHT){
                zeigeKommissionenUebersicht();
                return;
            }
        }
    }

    private void zeigePruefungsplan() {
        vorschauPanel.removeAll();
        PruefungsplanPanel timelinePanel = new PruefungsplanPanel(abitur);
        vorschauPanel.add(timelinePanel, BorderLayout.CENTER);
        vorschauPanel.revalidate();
        vorschauPanel.repaint();
    }

    private void zeigeSchueleruebersicht() {
        vorschauPanel.removeAll();
        SchueleruebersichtPanel schueleruebersichtPanel = new SchueleruebersichtPanel(abitur);
        vorschauPanel.add(schueleruebersichtPanel, BorderLayout.CENTER);
        vorschauPanel.revalidate();
        vorschauPanel.repaint();
    }

    private void zeigeKommissionenUebersicht() {
        vorschauPanel.removeAll();
        KommissionenUebersichtPanel  kommissionenUebersichtPanel = new KommissionenUebersichtPanel(abitur);
        vorschauPanel.add(kommissionenUebersichtPanel, BorderLayout.CENTER);
        vorschauPanel.revalidate();
        vorschauPanel.repaint();
    }

    private void zeigeFormularVorschau(FormularTyp formularTyp) {
        vorschauPanel.removeAll();
        AbiturformularPanel abiturformularPanel = new AbiturformularPanel(formularTyp, abitur);
        vorschauPanel.add(abiturformularPanel, BorderLayout.CENTER);
        vorschauPanel.revalidate();
        vorschauPanel.repaint();
    }
}
