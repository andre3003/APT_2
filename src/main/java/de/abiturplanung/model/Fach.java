package de.abiturplanung.model;

import java.util.Objects;

public class Fach {

    private final String kuerzel;

    public Fach(String kuerzel) {
        this.kuerzel = kuerzel;
    }

    public String getKuerzel() {
        return kuerzel;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Fach fach)) return false;
        return Objects.equals(kuerzel, fach.kuerzel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kuerzel);
    }
}