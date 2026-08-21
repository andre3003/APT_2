package de.abiturplanung.gui.planung;

import de.abiturplanung.model.Pruefung;

public interface PruefungsKartenAktionen {

    public abstract void nachBearbeitung(Pruefung pruefung); //Abstract kann entfallen
    void planungsdatenKopieren(Pruefung pruefung);
    void planungsdatenUebertragen(Pruefung pruefung);
}
