package de.abiturplanung.model;

import java.util.Objects;

public class Lehrer {

    private final String kuerzel;

    public Lehrer(String kuerzel) {
        this.kuerzel = kuerzel;
    }

    public String getKuerzel() {
        return kuerzel;
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