package de.abiturplanung.formulare;

public class FormularTreeEintrag {

    private final String bezeichnung;
    private final FormularTyp typ;

    public FormularTreeEintrag(FormularTyp typ, String bezeichnung) {
        this.bezeichnung = bezeichnung;
        this.typ = typ;
    }

    public FormularTyp getTyp() {
        return typ;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    @Override
    public String toString() {
        return bezeichnung;
    }
}