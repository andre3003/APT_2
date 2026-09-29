package de.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AppPfade {

    private static final Path APP_VERZEICHNIS = Path.of("").toAbsolutePath().normalize();

    public static Path getAppVerzeichnis() {
        return APP_VERZEICHNIS;
    }

    public static Path getDatenVerzeichnis() {
        return APP_VERZEICHNIS.resolve("Daten");
    }

    public static Path getImportVerzeichnis() {
        return APP_VERZEICHNIS.resolve("Import");
    }

    public static Path getExportVerzeichnis() {
        return APP_VERZEICHNIS.resolve("Export");
    }

    public static void initialisiereVerzeichnisse() throws IOException {
        Files.createDirectories(getDatenVerzeichnis());
        Files.createDirectories(getImportVerzeichnis());
        Files.createDirectories(getExportVerzeichnis());
        Files.createDirectories(getRessourcenVerzeichnis());
        Files.createDirectories(getPdfVerzeichnis());

    }

    public static Path getRessourcenVerzeichnis() {
        return APP_VERZEICHNIS.resolve("Ressourcen");
    }

    public static Path getPdfVerzeichnis() {
        return APP_VERZEICHNIS.resolve("PDF");
    }

    public static Path getLogoPfad() {
        return getRessourcenVerzeichnis().resolve("schullogo.png");
    }
}