package de.abiturplanung.formulare.pdf;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.borders.Border;
import de.config.AppPfade;

import java.awt.*;

public class PdfUtilities {

    public static Table erstelleDokumentKopf(String titel, int logoBreite, int logoHoehe, String stand) {
        try {
            ImageData logoDaten = ImageDataFactory.create(AppPfade.getLogoPfad().toString());
            Image logo = new Image(logoDaten);
            logo.scaleToFit(logoBreite, logoHoehe);
            logo.setHorizontalAlignment(HorizontalAlignment.LEFT);

            Paragraph titelAbsatz = new Paragraph(titel)
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);

            Table kopf = new Table(new float[]{1, 3});
            kopf.setWidth(UnitValue.createPercentValue(100));

            Cell logoZelle = new Cell().add(logo).setBorder(Border.NO_BORDER);
            logoZelle.setVerticalAlignment(VerticalAlignment.MIDDLE);

            Cell titelZelle = new Cell().add(titelAbsatz).setBorder(Border.NO_BORDER);
            titelZelle.setVerticalAlignment(VerticalAlignment.MIDDLE);
            titelZelle.setTextAlignment(TextAlignment.CENTER);
            titelZelle.add(new Paragraph("Stand: " + stand)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER));

            kopf.addCell(logoZelle);
            kopf.addCell(titelZelle);

            return kopf;
        } catch (Exception e) {
            throw new RuntimeException("Dokumentkopf konnte nicht erstellt werden.", e);
        }
    }
}