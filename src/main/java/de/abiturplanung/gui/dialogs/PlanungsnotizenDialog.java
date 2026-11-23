package de.abiturplanung.gui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class PlanungsnotizenDialog extends JDialog {
    private JTextArea textArea;
    private Path notizPfad;
    private String gespeicherterText = "";
    private JButton btSpeichern = new JButton("Speichern");
    private JButton btSchliessen = new JButton("Schließen");

    public PlanungsnotizenDialog(Frame owner, Path dbPfad) {
        super(owner, "Planungsnotizen", false);
        this.setLayout(new BorderLayout());
        this.setSize(600, 600);
        this.setLocationRelativeTo(owner);
        this.setModal(false);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                schliesseDialog();
            }
        });
        String dateiname = dbPfad.getFileName().toString().replaceFirst("\\.db$", "");
        notizPfad = dbPfad.resolveSibling(dateiname + "_notizen.txt");
        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        this.add(scrollPane, BorderLayout.CENTER);
        JPanel steuerung = new JPanel(new FlowLayout());
        btSpeichern.addActionListener(e -> speichereNotizen());
        steuerung.add(btSpeichern);
        btSchliessen.addActionListener(e -> schliesseDialog());
        steuerung.add(btSchliessen);
        this.add(steuerung, BorderLayout.SOUTH);
        notizenLaden();
        gespeicherterText = textArea.getText();
    }

    private void notizenLaden() {
        try {
            if (Files.exists(notizPfad))
                textArea.setText(Files.readString(notizPfad, StandardCharsets.UTF_8));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Notizen konnten nicht geladen werden.");
        }
    }

    private void schliesseDialog() {
        if (!textArea.getText().equals(gespeicherterText)) {
            int auswahl = JOptionPane.showConfirmDialog(this, "Die Notizen wurden verändert. Änderungen speichern?", "Planungsnotizen", JOptionPane.YES_NO_CANCEL_OPTION);
            if (auswahl == JOptionPane.CANCEL_OPTION || auswahl == JOptionPane.CLOSED_OPTION)
                return;
            if (auswahl == JOptionPane.YES_OPTION && !speichereNotizen())
                return;
        }
        dispose();
    }

    private boolean speichereNotizen() {
        try {
            Files.writeString(notizPfad, textArea.getText(), StandardCharsets.UTF_8);
            gespeicherterText = textArea.getText();
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Notizen konnten nicht gespeichert werden:\n" + e.getMessage(), "Fehler beim Speichern", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
}
