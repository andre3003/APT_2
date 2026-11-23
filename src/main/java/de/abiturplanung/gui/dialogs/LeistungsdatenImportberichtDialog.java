package de.abiturplanung.gui.dialogs;

import de.abiturplanung.gui.MainFrame;
import de.abiturplanung.importer.LeistungsdatenImportbericht;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class LeistungsdatenImportberichtDialog extends JDialog {

    LeistungsdatenImportbericht bericht;

    public LeistungsdatenImportberichtDialog(MainFrame mainFrame, LeistungsdatenImportbericht bericht) {
        super(mainFrame, ModalityType.APPLICATION_MODAL);
        this.bericht = bericht;
        this.setSize(new Dimension(700, 700));
        this.setLocationRelativeTo(mainFrame);
        createGUI();
    }

    private void createGUI() {
        JPanel main = new JPanel(new BorderLayout());
        this.add(main, BorderLayout.CENTER);

        //NOrth:
        JPanel north = new JPanel();
        JLabel titel = new JLabel("Importbericht - Leistungsdaten");
        titel.setFont(titel.getFont().deriveFont(Font.BOLD, 18f));
        north.add(titel);
        main.add(north, BorderLayout.NORTH);

        ///Center:
        JPanel center = new JPanel(new GridBagLayout());
        main.add(center);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 10, 5, 10);

        StringBuilder sb = new StringBuilder();
        sb.append("Gesamt: ").append(bericht.getNeueMuendlichePruefungen() + bericht.getNeueSchriftlichePruefungen()).append(" Prüfungen neu angelegt");
        sb.append("\nDavon schriftlich: ").append(bericht.getNeueSchriftlichePruefungen()).append(" Pruefungen");
        sb.append("\nDavon mündlich: ").append(bericht.getNeueMuendlichePruefungen()).append(" Prüfungen");
        sb.append("\nBereits vorhandene Prüfungen: ").append(bericht.getVorhandenePruefungen()).append(" Prüfungen");
        center.add(erstelleDatenPanel("Neu angelegte Prüfungen: ", sb.toString(), false), gbc);

        gbc.gridy++;

        sb = new StringBuilder();

        if (bericht.getNeueFaecher().isEmpty() && bericht.getNeueLehrer().isEmpty()) {
            sb.append("Keine neuen Stammdaten angelegt.");
        } else {
            if (!bericht.getNeueFaecher().isEmpty()) {
                sb.append("Neue Fächer: ").append(String.join(", ", bericht.getNeueFaecher()));
            }
            if (!bericht.getNeueLehrer().isEmpty()) {
                if (!sb.isEmpty()) sb.append("\n");
                sb.append("Neue Lehrer: ").append(String.join(", ", bericht.getNeueLehrer()));
            }
            sb.append("\nBitte unbedingt fehlende Informationen zu Fächern und Lehrern im Stammdatenbereich ergänzen!");
        }
        center.add(erstelleDatenPanel("Ergänzte Stammdaten:", sb.toString(), false), gbc);

        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        sb = new StringBuilder();

        if (!bericht.getNichtZugeordneteSchueler().isEmpty()) {
            int anzahl = bericht.getNichtZugeordneteSchueler().size();
            sb.append("ACHTUNG: ").append(anzahl).append(" Prüfungsdatensätze (mündlich und schriftlich) konnten nicht übernommen werden!\n");
            sb.append("Bitte die Schülerstammdaten kontrollieren und den Import wiederholen.\n\n");
            for (String schueler : bericht.getNichtZugeordneteSchueler()) {
                sb.append(schueler + "\n");
            }
        } else {
            sb.append("Alle Datensätze konnten zugeordnet werden.");
        }
        center.add(erstelleDatenPanel("Verworfene Prüfungsdaten:", sb.toString(), true), gbc);

        gbc.gridy++;
        sb = new StringBuilder();
        if (!bericht.getFehlendePruefungen().isEmpty()) {
            int anzahl = bericht.getFehlendePruefungen().size();
            sb.append("ACHTUNG: Für ").append(anzahl).append(" Prüfungen (mündlich und schriftlich) konnten keine Datensätze gefunden werden!\n");
            sb.append("Bitte die Schülerleistungsdaten überprüfen.\n\n");
            for (String schueler : bericht.getFehlendePruefungen()) {
                sb.append(schueler + "\n");
            }
        } else {
            sb.append("Alle Schüler der Datenbank haben vier Prüfungen!");
        }

        center.add(erstelleDatenPanel("Fehlende Prüfungen:", sb.toString(), true), gbc);

        JPanel steuerung = new JPanel(new FlowLayout());
        JButton btspeichern = new JButton("Speichern");
        btspeichern.addActionListener(this::btSpeichernAction);
        steuerung.add(btspeichern);
        JButton btSchliessen = new JButton("Schließen");
        btSchliessen.addActionListener(this::btSchliessenAction);
        steuerung.add(btSchliessen);
        main.add(steuerung, BorderLayout.SOUTH);
    }

    private void btSchliessenAction(ActionEvent event) {
        dispose();
    }

    private JPanel erstelleDatenPanel(String titel, String text, boolean scroll) {
        JPanel panel = new JPanel(new BorderLayout());
        TitledBorder border = BorderFactory.createTitledBorder(titel);
        border.setTitleFont(panel.getFont().deriveFont(Font.BOLD, 14f));
        panel.setBorder(border);
        JTextArea textArea = new JTextArea(text);
        textArea.setFont(textArea.getFont().deriveFont(14f));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        if (scroll) {
            textArea.setCaretPosition(0);
            panel.add(new JScrollPane(textArea));
        } else {
            panel.add(textArea);
        }
        return panel;
    }

    private void btSpeichernAction(ActionEvent event) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Importbericht speichern");
        chooser.setSelectedFile(new java.io.File("Importbericht_Leistungsdaten.txt"));
        chooser.setCurrentDirectory(new File("."));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        Path datei = chooser.getSelectedFile().toPath();
        if (!datei.getFileName().toString().toLowerCase(java.util.Locale.ROOT).endsWith(".txt"))
            datei = datei.resolveSibling(datei.getFileName() + ".txt");

        if (Files.exists(datei)) {
            int antwort = JOptionPane.showConfirmDialog(this,
                    "Die Datei existiert bereits. Soll sie überschrieben werden?",
                    "Datei überschreiben?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (antwort != JOptionPane.YES_OPTION) return;
        }

        try {
            Files.writeString(datei, bericht.erstelleBerichtstext(), StandardCharsets.UTF_8);
            JOptionPane.showMessageDialog(this, "Importbericht gespeichert:\n" + datei,
                    "Speichern erfolgreich", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(this, "Der Importbericht konnte nicht gespeichert werden:\n" + exception.getMessage(),
                    "Fehler beim Speichern", JOptionPane.ERROR_MESSAGE);
        }
    }
}
