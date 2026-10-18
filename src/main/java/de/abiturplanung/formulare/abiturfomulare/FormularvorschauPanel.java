package de.abiturplanung.formulare.abiturfomulare;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FormularvorschauPanel extends JPanel {
    private final BufferedImage bild;

    public FormularvorschauPanel(String bildPfad) {
        try {
            bild = ImageIO.read(new File(bildPfad));
        } catch (IOException e) {
            throw new RuntimeException("Vorschaubild konnte nicht geladen werden: " + bildPfad, e);
        }
        setBackground(Color.LIGHT_GRAY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bild == null) return;
        int rand = 15;
        int breite = Math.max(0, getWidth() - 2 * rand);
        int hoehe = Math.max(0, getHeight() - 2 * rand);
        double faktor = Math.min((double) breite / bild.getWidth(), (double) hoehe / bild.getHeight());

        int bildBreite = (int) (bild.getWidth() * faktor);
        int bildHoehe = (int) (bild.getHeight() * faktor);

        int x = rand + (breite - bildBreite) / 2;
        int y = rand + (hoehe - bildHoehe) / 2;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(bild, x, y, bildBreite, bildHoehe, null);
        g2.dispose();
    }
}
