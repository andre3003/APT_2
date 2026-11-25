package de.abiturplanung.formulare.klausurraum;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import de.abiturplanung.Utilities;
import de.abiturplanung.formulare.pdf.PdfUtilities;
import de.abiturplanung.gui.planung.RaumbelegungAbiturklausuren;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Kurs;

import java.io.File;
import java.util.List;

public class PdfSitzplanKlausurraum {

    public static void erstelle(Abitur abitur, RaumbelegungAbiturklausuren belegung, File datei) {
        String pfad = datei.getAbsolutePath();
        try {
            PdfWriter writer = new PdfWriter(pfad);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.add(PdfUtilities.erstelleStempelkopf(abitur.getAbiturjahrgang()));
            Paragraph p1 = new Paragraph("Anwesenheitsliste und Liste der Klausurplätze").setBold().setUnderline();
            document.add(p1);


            float[] spaltenBreiten = {50, 70, 50, 60, 50, 150};
            Table tabelle = new Table(UnitValue.createPercentArray(spaltenBreiten));
            tabelle.addCell(erzeugeDatenzelle("Datum: "));
            tabelle.addCell(erzeugePlatzhalterzelle());
            tabelle.addCell(erzeugeDatenzelle("Raum: "));
            tabelle.addCell(erzeugePlatzhalterzelle());
            tabelle.addCell(erzeugeDatenzelle("Kurs(e): "));
            tabelle.addCell(erzeugePlatzhalterzelle());
//
//            exams.sort(Comparator.comparing(e -> e.getCourse().getName()));
            String datum = Utilities.formatiereDatum(belegung.getPruefungstag().getDatum());
            String raum = belegung.getRaum().getBezeichnung();
            List<Kurs> kurseListe = belegung.getKurse();
            StringBuilder kurse = new StringBuilder();
            for (Kurs k : kurseListe) {
                kurse.append(k.getBezeichnung() + ", ");
            }
            tabelle.getCell(0, 1).add(new Paragraph(datum));
            tabelle.getCell(0, 3).add(new Paragraph(raum));
            tabelle.getCell(0, 5).add(new Paragraph(kurse.toString()));
//            document.add(tabelle);
//            document.add(new Paragraph("\n").setFontSize(5));
//            Paragraph p2 = new Paragraph("Jeder Platz im Prüfungsraum hat eine feste Platznummer. Die Schülerinnen und Schüler setzen sich an die Plätze, die ihnen zugewiesen sind. Die Platznummer ist in der folgenden Liste ersichtlich.");
//            document.add(p2);
//            document.add(new Paragraph("\n").setFontSize(5));
//            float[] columnWidths2 = {50, 130, 130, 40, 100};
//            Table attenadanceTable = new Table(columnWidths2);
//            attenadanceTable.addCell(createHeaderCell("Platz-Nr."));
//            attenadanceTable.addCell(createHeaderCell("Name"));
//            attenadanceTable.addCell(createHeaderCell("Vorname"));
//            attenadanceTable.addCell(createHeaderCell("Kurs/Kursart"));
//            attenadanceTable.addCell(createHeaderCell("Fachlehrer"));
//            ArrayList<String[]> allStundentsOfOneRoomAndOneDay = new ArrayList<>();
//            for (int i = 0; i < exams.size(); i++) {
//                String course = exams.get(i).getCourse().getName() + "-" + exams.get(i).getCourse().getCourseTypeShort();
//                String teacher = exams.get(i).getCourse().getTeacherCode();
//                ArrayList<Student> students = exams.get(i).getListOfAbiturienten();
////                students.sort(Comparator.comparing(s -> s.getName().toLowerCase()));
//                for (int j = 0; j < students.size(); j++) {
//                    String[] data = new String[]{students.get(j).getName(), students.get(j).getFirstName(), course, teacher};
//                    allStundentsOfOneRoomAndOneDay.add(data);
//                }
//            }
//            allStundentsOfOneRoomAndOneDay.sort(Comparator.comparing(a -> a[0].toLowerCase()));
//            for (int j = 0; j < allStundentsOfOneRoomAndOneDay.size(); j++) {
//                attenadanceTable.addCell(new Paragraph(Integer.toString(j + 1)));
//                attenadanceTable.addCell(allStundentsOfOneRoomAndOneDay.get(j)[0]);
//                attenadanceTable.addCell(allStundentsOfOneRoomAndOneDay.get(j)[1]);
//                attenadanceTable.addCell(allStundentsOfOneRoomAndOneDay.get(j)[2]);
//                attenadanceTable.addCell(allStundentsOfOneRoomAndOneDay.get(j)[3]);
//            }
            document.add(tabelle);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Cell createHeaderCell(String text) {
        return new Cell().add(new Paragraph(text).setBold().setFontSize(10))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    private static Cell erzeugeDatenzelle(String text) {
        return new Cell().add(new Paragraph(text).setBold())
                .setBorder(Border.NO_BORDER);
    }

    private static Cell erzeugePlatzhalterzelle() {
        Cell cell = new Cell();
        cell.setBorderTop(Border.NO_BORDER);
        cell.setBorderLeft(Border.NO_BORDER);
        return cell.setBorderRight(Border.NO_BORDER);
    }









        public static Document generateAttendanceList(Document document) {
//            try {
//                document.add(PDFUtils.createStampHeaderWithSchoolName());
//                Paragraph p1 = new Paragraph("Anwesenheitsliste und Liste der Klausurplätze").setBold().setUnderline();
//                document.add(p1);
//
//                float[] columnWidths1 = {50, 70, 50, 60, 50, 150};
//                Table tableDateRoom = new Table(UnitValue.createPercentArray(columnWidths1));
//                tableDateRoom.addCell(createDataCell("Datum: "));
//                tableDateRoom.addCell(PDFUtils.createPlaceHolderCell(1));
//                tableDateRoom.addCell(createDataCell("Raum: "));
//                tableDateRoom.addCell(PDFUtils.createPlaceHolderCell(1));
//                tableDateRoom.addCell(createDataCell("Kurs(e): "));
//                tableDateRoom.addCell(PDFUtils.createPlaceHolderCell(1));
//
//                exams.sort(Comparator.comparing(e -> e.getCourse().getName()));
//                String datum = Utils.germanDateTimeFormatter(exams.get(0).getDate());
//                String raum = exams.get(0).getRoomName();
//                StringBuilder coureses = new StringBuilder();
//                for (int i = 0; i < exams.size(); i++) {
//                    coureses.append(exams.get(i).getCourse().getName());
//                    if (i != exams.size() - 1)
//                        coureses.append(", ");
//                }
//
//                tableDateRoom.getCell(0, 1).add(new Paragraph(datum));
//                tableDateRoom.getCell(0, 3).add(new Paragraph(raum));
//                tableDateRoom.getCell(0, 5).add(new Paragraph(coureses.toString()));
//
//                document.add(tableDateRoom);
//                document.add(new Paragraph("\n").setFontSize(5));
//                Paragraph p2 = new Paragraph("Jeder Platz im Prüfungsraum hat eine feste Platznummer. Die Schülerinnen und Schüler setzen sich an die Plätze, die ihnen zugewiesen sind. Die Platznummer ist in der folgenden Liste ersichtlich.");
//                document.add(p2);
//                document.add(new Paragraph("\n").setFontSize(5));
//                float[] columnWidths2 = {50, 130, 130, 40, 100};
//                Table attenadanceTable = new Table(columnWidths2);
//                attenadanceTable.addCell(createHeaderCell("Platz-Nr."));
//                attenadanceTable.addCell(createHeaderCell("Name"));
//                attenadanceTable.addCell(createHeaderCell("Vorname"));
//                attenadanceTable.addCell(createHeaderCell("Kurs/Kursart"));
//                attenadanceTable.addCell(createHeaderCell("Fachlehrer"));
//                ArrayList<String[]> allStundentsOfOneRoomAndOneDay = new ArrayList<>();
//                for (int i = 0; i < exams.size(); i++) {
//                    String course = exams.get(i).getCourse().getName() + "-" + exams.get(i).getCourse().getCourseTypeShort();
//                    String teacher = exams.get(i).getCourse().getTeacherCode();
//                    ArrayList<Student> students = exams.get(i).getListOfAbiturienten();
////                students.sort(Comparator.comparing(s -> s.getName().toLowerCase()));
//                    for (int j = 0; j < students.size(); j++) {
//                        String[] data = new String[]{students.get(j).getName(), students.get(j).getFirstName(), course, teacher};
//                        allStundentsOfOneRoomAndOneDay.add(data);
//                    }
            return null;
            }
//                allStundentsOfOneRoomAndOneDay.sort(Comparator.comparing(a -> a[0].toLowerCase()));
//                for (int j = 0; j < allStundentsOfOneRoomAndOneDay.size(); j++) {
//                    attenadanceTable.addCell(new Paragraph(Integer.toString(j + 1)));
//                    attenadanceTable.addCell(allStundentsOfOneRoomAndOneDay.get(j)[0]);
//                    attenadanceTable.addCell(allStundentsOfOneRoomAndOneDay.get(j)[1]);
//                    attenadanceTable.addCell(allStundentsOfOneRoomAndOneDay.get(j)[2]);
//                    attenadanceTable.addCell(allStundentsOfOneRoomAndOneDay.get(j)[3]);
//                }
//                document.add(attenadanceTable);
//            } catch (
//                    Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//
        }





