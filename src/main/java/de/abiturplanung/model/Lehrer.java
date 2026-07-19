package de.abiturplanung.model;

import de.abiturplanung.importer.LehrerDatensatz;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lehrer {
    private String kuerzel;
    private String anrede;
    private String nachname;
    private String vorname;
    private String amtsbez;
    private List<String> fakultas = new ArrayList<>();

    public Lehrer(String kuerzel) {
        this.kuerzel = kuerzel;
    }

    public void aktualisiereStammdaten(LehrerDatensatz datensatz) {
        anrede = datensatz.getAnrede();
        nachname = datensatz.getNachname();
        vorname = datensatz.getVorname();
        fakultas.clear();
        if (!datensatz.getFak1().isEmpty()) {
            fakultas.add(datensatz.getFak1());
        }
        if (!datensatz.getFak2().isEmpty()) {
            fakultas.add(datensatz.getFak2());
        }
        if (!datensatz.getFak3().isEmpty()) {
            fakultas.add(datensatz.getFak3());
        }
        if (!datensatz.getFak4().isEmpty()) {
            fakultas.add(datensatz.getFak4());
        }
        amtsbez = datensatz.getAmtsbez();
    }

    public String getKuerzel() {
        return kuerzel;
    }

    public String getAnrede() {
        return anrede;
    }

    public String getNachname() {
        return nachname;
    }

    public String getVorname() {
        return vorname;
    }

    public String getAmtsbez() {
        return amtsbez;
    }

    public List<String> getFakultas() {
        return fakultas;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Lehrer lehrer)) return false;
        return Objects.equals(kuerzel, lehrer.kuerzel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kuerzel);
    }
}