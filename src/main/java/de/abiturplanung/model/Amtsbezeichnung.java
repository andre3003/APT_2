package de.abiturplanung.model;

public enum Amtsbezeichnung {
    LEHRER("L", "Lehrer", "Lehrerin"),
    STUDIENRAT("StR", "Studienrat", "Studienrätin"),
    OBERSTUDIENRAT("OStR", "Oberstudienrat", "Oberstudienrätin"),
    STUDIENDIREKTOR("StD", "Studiendirektor", "Studiendirektorin"),
    OBERSTUDIENDIREKTOR("OStD", "Oberstudiendirektor", "Oberstudiendirektorin");

    private final String kuerzel;
    private final String maennlich;
    private final String weiblich;

    Amtsbezeichnung(String kuerzel, String maennlich, String weiblich) {
        this.kuerzel = kuerzel;
        this.maennlich = maennlich;
        this.weiblich = weiblich;
    }

    public String getKuerzel() {
        return kuerzel;
    }

    public String getMaennlich() {
        return maennlich;
    }

    public String getWeiblich() {
        return weiblich;
    }

    @Override
    public String toString() {
        return kuerzel;
    }

    public static Amtsbezeichnung ausKuerzel(String kuerzel) {
        for (Amtsbezeichnung amtsbezeichnung : values()) {
            if (amtsbezeichnung.kuerzel.equals(kuerzel)) {
                return amtsbezeichnung;
            }
        }
        return null;
    }

}
