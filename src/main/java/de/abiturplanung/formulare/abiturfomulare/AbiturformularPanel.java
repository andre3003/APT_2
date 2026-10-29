package de.abiturplanung.formulare.abiturfomulare;

import de.abiturplanung.formulare.FormularTyp;
import de.abiturplanung.formulare.pdf.PdfErsteller;
import de.abiturplanung.formulare.pruefungsplan.KommissionsGruppe;
import de.abiturplanung.formulare.pruefungsplan.PruefungsplanDatenService;
import de.abiturplanung.model.Abitur;
import de.abiturplanung.model.Fachpruefungsausschuss;
import de.abiturplanung.model.Pruefung;
import de.abiturplanung.model.Pruefungstag;
import de.config.AppPfade;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AbiturformularPanel extends JPanel {
    private Abitur abitur;
    private FormularTyp formularTyp;
    private JCheckBox cbPdfOeffnen;

    public AbiturformularPanel(FormularTyp formularTyp, Abitur abitur) {
        this.abitur = abitur;
        this.formularTyp = formularTyp;
        this.setLayout(new BorderLayout());
        JPanel steuerung = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btDruck = new JButton("PDF erstellen");
        btDruck.addActionListener(this::btDruckAction);
        cbPdfOeffnen = new JCheckBox("PDF nach Erstellung anzeigen");
        cbPdfOeffnen.setSelected(true);
        steuerung.add(btDruck);
        steuerung.add(cbPdfOeffnen);
        this.add(steuerung, BorderLayout.NORTH);
        Path bildpfad = AppPfade.getRessourcenVerzeichnis().resolve(formularTyp.name() + ".png");
        FormularvorschauPanel formularVorschauPanel = new FormularvorschauPanel(bildpfad.toString());
        this.add(formularVorschauPanel, BorderLayout.CENTER);
    }

    private void btDruckAction(ActionEvent e) {
        if (formularTyp == FormularTyp.NIEDERSCHRIFTMUENDLICHEPRUEFUNG_AB4) {
            List<Pruefung> pruefungen = AbiturfomulareDatenService.gibPruefungenAB4FuerFormulardruck(abitur);
            PdfErsteller.erstelle(this, "Niederschriften_AB4.pdf", cbPdfOeffnen.isSelected(),
                    datei -> PdfErsteller.erstelleMehrseitigesFormularDokument(datei, pruefungen, (dokument, pruefung) -> PdfNiederschriftMuendlPruefung_AB4.erstelle(dokument, abitur, pruefung)));
        }
        if (formularTyp == FormularTyp.NIEDERSCHRIFT_FPA_AB4) {
            PruefungsplanDatenService datenService = new PruefungsplanDatenService();
            List<KommissionsGruppe> gruppen = new ArrayList<>();
            for (Pruefungstag tag : abitur.getPruefungstage()) {
                gruppen.addAll(datenService.gibSortierteKommissionsGruppen(abitur, tag));
            }
            PdfErsteller.erstelle(this, "Niedrschriften Konferenz FPA_AB4.pdf", cbPdfOeffnen.isSelected(),
                    datei -> PdfErsteller.erstelleMehrseitigesFormularDokument(datei, gruppen, (dokument, gruppe) -> PdfNiederschriftFPA_AB4.erstelle(dokument, abitur, gruppe)));
        }
        if (formularTyp == FormularTyp.LISTE_FPA) {
            List<Fachpruefungsausschuss> fpas = AbiturfomulareDatenService.gibFachpruefungsausschuesseAB4(abitur);
            PdfErsteller.erstelle(this, "Liste der FPAs.pdf", cbPdfOeffnen.isSelected(),
                    datei -> PdfErsteller.erstelleFormularDokument(datei,
                            dokument -> PdfListeFPAs.erstelle(dokument, abitur, fpas)));
        }
    }
}
