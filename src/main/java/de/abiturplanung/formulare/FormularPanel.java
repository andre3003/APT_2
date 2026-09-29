package de.abiturplanung.formulare;

import de.abiturplanung.formulare.schueleruebersicht.SchueleruebersichtPanel;
import de.abiturplanung.formulare.timeline.TimelinePanel;
import de.abiturplanung.model.Abitur;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class FormularPanel extends JPanel {
    private Abitur abitur;
    private JPanel vorschauPanel;
    JSplitPane splitPane;


    public FormularPanel(Abitur abitur) {
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
        interneUebersichten.add(new DefaultMutableTreeNode(new FormularTreeEintrag(FormularTyp.TIMELINE, "Prüfungsplan (Timeline)")));
        interneUebersichten.add(new DefaultMutableTreeNode(new FormularTreeEintrag(FormularTyp.SCHUELERUEBERSICHT, "Prüfungsübersicht für Schüler")));

        DefaultMutableTreeNode offizielleFormulare = new DefaultMutableTreeNode("Offizielle Formulare");

        DefaultMutableTreeNode muendlich = new DefaultMutableTreeNode("Mündliche Prüfungen");
        DefaultMutableTreeNode schriftlich = new DefaultMutableTreeNode("Schriftliche Prüfungen");
        DefaultMutableTreeNode zaa = new DefaultMutableTreeNode("Zentraler Abiturausschuss");

        offizielleFormulare.add(muendlich);
        offizielleFormulare.add(schriftlich);
        offizielleFormulare.add(zaa);

        wurzel.add(interneUebersichten);
        wurzel.add(offizielleFormulare);

        return new JTree(wurzel);
    }

    private void formularAusgewaehlt(JTree formularTree) {
        DefaultMutableTreeNode knoten = (DefaultMutableTreeNode) formularTree.getLastSelectedPathComponent();
        if (knoten == null) {
            return;
        }
        Object objekt = knoten.getUserObject();
        if (objekt instanceof FormularTreeEintrag formular) {
            if (formular.getTyp() == FormularTyp.TIMELINE) {
                zeigeTimeline();
                return;
            }
            if (formular.getTyp() == FormularTyp.SCHUELERUEBERSICHT) {
                zeigeSchueleruebersicht();
                return;
            }
        }
    }

    private void zeigeTimeline() {
        vorschauPanel.removeAll();
        TimelinePanel timelinePanel = new TimelinePanel(abitur);
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
}
