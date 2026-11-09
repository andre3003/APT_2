package de.abiturplanung.gui.planung;

public class Hilfsmittel {
        private Long id;
        private String bezeichnung;
        private boolean aktiv;

    public Hilfsmittel(Long id, String bezeichnung, boolean aktiv) {
        this.id = id;
        this.bezeichnung = bezeichnung;
        this.aktiv = aktiv;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public boolean isAktiv() {
        return aktiv;
    }

    public void setAktiv(boolean aktiv) {
        this.aktiv = aktiv;
    }
}
