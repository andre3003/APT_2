package de.config;

import java.nio.file.Path;
import java.util.prefs.Preferences;

public class AppEinstellungen {

    private static final String KEY_LETZTE_DATENBANK = "letzteDatenbank";

    private final Preferences preferences =
            Preferences.userNodeForPackage(AppEinstellungen.class);

    public void setLetzteDatenbank(Path datenbankPfad) {
        Path appVerzeichnis = AppPfade.getAppVerzeichnis();
        Path absoluterPfad = datenbankPfad.toAbsolutePath().normalize();

        if (absoluterPfad.startsWith(appVerzeichnis)) {
            Path relativerPfad = appVerzeichnis.relativize(absoluterPfad);
            preferences.put(KEY_LETZTE_DATENBANK, relativerPfad.toString());
        } else {
            preferences.put(KEY_LETZTE_DATENBANK, absoluterPfad.toString());
        }
    }

    public Path getLetzteDatenbank() {
        String gespeichert = preferences.get(KEY_LETZTE_DATENBANK, null);

        if (gespeichert == null) {
            return null;
        }

        Path pfad = Path.of(gespeichert);

        if (pfad.isAbsolute()) {
            return pfad;
        }

        return AppPfade.getAppVerzeichnis().resolve(pfad).normalize();
    }
}