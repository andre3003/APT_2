package de.abiturplanung.persistence;

import de.abiturplanung.model.*;

import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Datenbank {

    private final Path pfad;
    private final String url;

    public Datenbank(Path pfad) {
        this.pfad = pfad.toAbsolutePath().normalize();
        this.url = "jdbc:sqlite:" + this.pfad;
    }

    public Path getPfad() {
        return pfad;
    }

    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(url);

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }

        return connection;
    }

    //Initialiserung ___________

    public void initialisieren() throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS app_info (
                        schluessel TEXT PRIMARY KEY,
                        wert TEXT
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS schueler (
                        schild_id TEXT PRIMARY KEY,
                        nachname TEXT NOT NULL,
                        vorname TEXT NOT NULL,
                        geburtsdatum TEXT,
                        geschlecht TEXT
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS lehrer (
                        kuerzel TEXT PRIMARY KEY,
                        anrede TEXT,
                        nachname TEXT NOT NULL,
                        vorname TEXT NOT NULL,
                        amtsbezeichnung TEXT
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS fach (
                        kuerzel TEXT PRIMARY KEY,
                        bezeichnung TEXT,
                        stammfach_kuerzel TEXT,
                        faechergruppe TEXT,
                        FOREIGN KEY (stammfach_kuerzel) REFERENCES fach(kuerzel)
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS lehrer_fakultaet (
                        lehrer_kuerzel TEXT NOT NULL,
                        fach TEXT NOT NULL,
                    
                        PRIMARY KEY (lehrer_kuerzel, fach),
                    
                        FOREIGN KEY (lehrer_kuerzel)
                            REFERENCES lehrer(kuerzel)
                            ON DELETE CASCADE
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS raum (
                        bezeichnung TEXT PRIMARY KEY,
                        kapazitaet INTEGER
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS kurs (
                        bezeichnung TEXT PRIMARY KEY,
                        fach TEXT NOT NULL,
                        fachlehrer_kuerzel TEXT,
                    
                        FOREIGN KEY (fachlehrer_kuerzel)
                            REFERENCES lehrer(kuerzel)
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS pruefung (
                        pruefung_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        schueler_id TEXT NOT NULL,
                        kurs_bezeichnung TEXT NOT NULL,
                        abiturfach TEXT NOT NULL,
                        pruefungsform TEXT NOT NULL,
                        pruefungsfolge TEXT,
                    
                        FOREIGN KEY (schueler_id)
                            REFERENCES schueler(schild_id)
                             ON DELETE CASCADE,
                    
                        FOREIGN KEY (kurs_bezeichnung)
                            REFERENCES kurs(bezeichnung)
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS pruefungstag (
                        pruefungstag_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        datum TEXT NOT NULL UNIQUE
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS pruefungsplanung (
                        pruefung_id INTEGER PRIMARY KEY,
                        pruefungstag_id INTEGER,
                        beginn TEXT,
                        planungsspalte INTEGER,
                        raum_bezeichnung TEXT,
                        pruefer_kuerzel TEXT,
                        schriftfuehrer_kuerzel TEXT,
                        vorsitz_kuerzel TEXT,
                    
                        FOREIGN KEY (pruefung_id)
                            REFERENCES pruefung(pruefung_id)
                            ON DELETE CASCADE,
                    
                        FOREIGN KEY (pruefungstag_id)
                            REFERENCES pruefungstag(pruefungstag_id),
                    
                        FOREIGN KEY (raum_bezeichnung)
                            REFERENCES raum(bezeichnung),
                    
                        FOREIGN KEY (pruefer_kuerzel)
                            REFERENCES lehrer(kuerzel),
                    
                        FOREIGN KEY (schriftfuehrer_kuerzel)
                            REFERENCES lehrer(kuerzel),
                    
                        FOREIGN KEY (vorsitz_kuerzel)
                            REFERENCES lehrer(kuerzel)
                    )
                    """);
        }
    }

    private void speicherePruefungsplanung(Connection connection, long pruefungId, Pruefung pruefung) throws SQLException {
        String sql = """
                INSERT INTO pruefungsplanung (pruefung_id, pruefungstag_id, beginn, planungsspalte, raum_bezeichnung, pruefer_kuerzel, schriftfuehrer_kuerzel, vorsitz_kuerzel) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, pruefungId);

            setPruefungstagId(statement, 2, connection, pruefung);
            setLocalTime(statement, 3, pruefung.getBeginn());
            setInteger(statement, 4, pruefung.getPlanungsspalte());
            setRaum(statement, 5, pruefung.getRaum());
            setLehrer(statement, 6, pruefung.getPruefer());
            setLehrer(statement, 7, pruefung.getSchriftfuehrer());
            setLehrer(statement, 8, pruefung.getVorsitz());

            statement.executeUpdate();
        }
    }

    private void setLocalTime(PreparedStatement statement, int index, LocalTime wert) throws SQLException {
        if (wert == null) {
            statement.setNull(index, Types.VARCHAR);
        } else {
            statement.setString(index, wert.toString());
        }
    }

    private void setInteger(PreparedStatement statement, int index, Integer wert) throws SQLException {
        if (wert == null) {
            statement.setNull(index, Types.INTEGER);
        } else {
            statement.setInt(index, wert);
        }
    }

    private void setLehrer(PreparedStatement statement, int index, Lehrer lehrer) throws SQLException {
        if (lehrer == null) {
            statement.setNull(index, Types.VARCHAR);
        } else {
            statement.setString(index, lehrer.getKuerzel());
        }
    }

    private void setRaum(PreparedStatement statement, int index, Raum raum) throws SQLException {
        if (raum == null) {
            statement.setNull(index, Types.VARCHAR);
        } else {
            statement.setString(index, raum.getBezeichnung());
        }
    }

    private void setPruefungstagId(PreparedStatement statement, int index, Connection connection, Pruefung pruefung) throws SQLException {
        if (pruefung.getPruefungstag() == null) {
            statement.setNull(index, Types.INTEGER);
            return;
        }

        String sql = "SELECT pruefungstag_id FROM pruefungstag WHERE datum = ?";

        try (PreparedStatement select = connection.prepareStatement(sql)) {
            select.setString(1, pruefung.getPruefungstag().toString());

            try (ResultSet resultSet = select.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("Prüfungstag nicht gefunden: " + pruefung.getPruefungstag());
                }

                statement.setInt(index, resultSet.getInt("pruefungstag_id"));
            }
        }
    }

    //Landen_______

    public Abitur ladeAbitur() throws SQLException {
        Abitur abitur = new Abitur();

        Map<String, Schueler> schuelerMap = new HashMap<>(); //Die Maps sind nur für den Ladevorgang, um Datensätze anhand des Schlüssels leicht identifizieren zu können.
        Map<String, Lehrer> lehrerMap = new HashMap<>();
        Map<String, Raum> raumMap = new HashMap<>();
        Map<String, Kurs> kursMap = new HashMap<>();
        Map<Long, Pruefungstag> pruefungstagMap = new HashMap<>();
        Map<Long, Pruefung> pruefungMap = new HashMap<>();

        try (Connection connection = getConnection()) {
            ladeFaecher(connection, abitur);
            ladeSchueler(connection, abitur, schuelerMap);
            ladeLehrer(connection, abitur, lehrerMap);
            ladeFakultas(connection, abitur, lehrerMap);
            ladeRaeume(connection, abitur, raumMap);
            ladeKurse(connection, abitur, lehrerMap, kursMap);
            ladePruefungstage(connection, abitur, pruefungstagMap);
            ladePruefungen(connection, abitur, schuelerMap, kursMap, pruefungMap);
            ladePruefungsplanung(connection, pruefungMap, pruefungstagMap, raumMap, lehrerMap);
        }
        return abitur;
    }

    private void ladeSchueler(Connection connection, Abitur abitur, Map<String, Schueler> schuelerMap) throws SQLException {
        String sql = "SELECT schild_id, nachname, vorname, geburtsdatum, geschlecht FROM schueler";

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String schildId = resultSet.getString("schild_id");
                String nachname = resultSet.getString("nachname");
                String vorname = resultSet.getString("vorname");

                LocalDate geburtsdatum = null;
                String geburtsdatumText = resultSet.getString("geburtsdatum");

                if (geburtsdatumText != null) {
                    geburtsdatum = LocalDate.parse(geburtsdatumText);
                }

                Geschlecht geschlecht = null;
                String geschlechtText = resultSet.getString("geschlecht");

                if (geschlechtText != null) {
                    geschlecht = Geschlecht.valueOf(geschlechtText);
                }

                Schueler schueler = new Schueler(schildId);
                schueler.aktualisiereStammdaten(nachname, vorname, geburtsdatum, geschlecht);

                abitur.addSchueler(schueler);
                schuelerMap.put(schildId, schueler);
            }
        }
    }

    private void ladeLehrer(Connection connection, Abitur abitur, Map<String, Lehrer> lehrerMap) throws SQLException {
        String sql = "SELECT kuerzel, anrede, nachname, vorname, amtsbezeichnung FROM lehrer";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String kuerzel = resultSet.getString("kuerzel");
                Amtsbezeichnung amtsbezeichnung = Amtsbezeichnung.ausKuerzel(resultSet.getString("amtsbezeichnung"));
                Lehrer lehrer = new Lehrer(kuerzel);
                lehrer.aktualisiereStammdaten(resultSet.getString("anrede"), resultSet.getString("nachname"), resultSet.getString("vorname"), amtsbezeichnung);
                abitur.addLehrer(lehrer);
                lehrerMap.put(kuerzel, lehrer);
            }
        }
        abitur.sortiereLehrer();
    }

    public void loescheLehrer(String kuerzel) throws SQLException {
        String sql = "DELETE FROM Lehrer WHERE kuerzel = ?";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, kuerzel);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Schüler mit Schild-ID " + kuerzel + " konnte nicht gelöscht werden.");
            }
        }
    }

    private void ladeFaecher(Connection connection, Abitur abitur) throws SQLException {
        String sql = "SELECT kuerzel, bezeichnung, stammfach_kuerzel, faechergruppe FROM fach";

        Map<String, String> stammfachKuerzel = new HashMap<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                String kuerzel = resultSet.getString("kuerzel");
                String bezeichnung = resultSet.getString("bezeichnung");
                String stammfach = resultSet.getString("stammfach_kuerzel");
                String faechergruppe = resultSet.getString("faechergruppe");

                Fach fach = new Fach(kuerzel);
                fach.aktualisiereStammdaten(bezeichnung, faechergruppe);
                abitur.addFach(fach);

                if (stammfach != null) {
                    stammfachKuerzel.put(kuerzel, stammfach);
                }
            }
        }

        for (Map.Entry<String, String> eintrag : stammfachKuerzel.entrySet()) {
            Fach fach = abitur.findeFach(eintrag.getKey());
            Fach stammfach = abitur.findeFach(eintrag.getValue());

            if (fach == null || stammfach == null) {
                throw new SQLException(
                        "Stammfachzuordnung konnte nicht geladen werden: "
                                + eintrag.getKey() + " -> " + eintrag.getValue());
            }

            fach.setStammfach(stammfach);
        }

        abitur.sortiereFaecher();
    }

    private void ladeFakultas(Connection connection, Abitur abitur, Map<String, Lehrer> lehrerMap) throws SQLException {
        String sql = "SELECT lehrer_kuerzel, fach FROM lehrer_fakultaet";
        Map<Lehrer, List<Fach>> fakultas = new HashMap<>();

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String kuerzel = resultSet.getString("lehrer_kuerzel");
                String fachKuerzel = resultSet.getString("fach");
                Lehrer lehrer = lehrerMap.get(kuerzel);
                if (lehrer == null) {
                    throw new SQLException("Lehrer für Fakultas nicht gefunden: " + kuerzel);
                }
                Fach fach = abitur.findeFach(fachKuerzel);
                if (fach == null) {
                    System.err.println("Fakultas ignoriert: Fach nicht gefunden: " + fachKuerzel + " bei Lehrer " + kuerzel);
                    continue;
                }
                fakultas.computeIfAbsent(lehrer, k -> new ArrayList<>()).add(fach);
            }
        }

        for (Map.Entry<Lehrer, List<Fach>> eintrag : fakultas.entrySet()) {
            eintrag.getKey().aktualisiereFakultas(eintrag.getValue());
        }
    }

    private void ladeRaeume(Connection connection, Abitur abitur, Map<String, Raum> raumMap) throws SQLException {
        String sql = "SELECT bezeichnung, kapazitaet FROM raum";

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String bezeichnung = resultSet.getString("bezeichnung");
                int kapazitaet = resultSet.getInt("kapazitaet");

                Raum raum = new Raum(bezeichnung, kapazitaet);

                abitur.addRaum(raum);
                raumMap.put(bezeichnung, raum);
            }
        }
        abitur.sortiereRaeume();
    }

    private void ladeKurse(Connection connection, Abitur abitur, Map<String, Lehrer> lehrerMap, Map<String, Kurs> kursMap) throws SQLException {
        String sql = "SELECT bezeichnung, fach, fachlehrer_kuerzel FROM kurs";

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String bezeichnung = resultSet.getString("bezeichnung");
                String fach = resultSet.getString("fach");
                String fachlehrerKuerzel = resultSet.getString("fachlehrer_kuerzel");

                Lehrer fachlehrer = fachlehrerKuerzel == null ? null : lehrerMap.get(fachlehrerKuerzel);

                if (fachlehrerKuerzel != null && fachlehrer == null) {
                    throw new SQLException("Fachlehrer für Kurs " + bezeichnung + " nicht gefunden: " + fachlehrerKuerzel);
                }
                Fach kursFach = abitur.findeFach(fach);
                if (kursFach == null) {
                    throw new SQLException("Fach für Kurs nicht gefunden: " + fach);
                }
                Kurs kurs = new Kurs(bezeichnung, kursFach, fachlehrer);
                abitur.addKurs(kurs);
                kursMap.put(bezeichnung, kurs);
            }
        }
    }

    private void ladePruefungstage(Connection connection, Abitur abitur, Map<Long, Pruefungstag> pruefungstagMap) throws SQLException {
        String sql = "SELECT pruefungstag_id, datum FROM pruefungstag ORDER BY datum";

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                long id = resultSet.getLong("pruefungstag_id");
                LocalDate datum = LocalDate.parse(resultSet.getString("datum"));

                Pruefungstag pruefungstag = new Pruefungstag(datum);

                abitur.addPruefungstag(pruefungstag);
                pruefungstagMap.put(id, pruefungstag);
            }
        }
    }

    private void ladePruefungen(Connection connection, Abitur abitur, Map<String, Schueler> schuelerMap, Map<String, Kurs> kursMap, Map<Long, Pruefung> pruefungMap) throws SQLException {
        String sql = "SELECT pruefung_id, schueler_id, kurs_bezeichnung, abiturfach, pruefungsform, pruefungsfolge FROM pruefung";

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                long pruefungId = resultSet.getLong("pruefung_id");
                String schuelerId = resultSet.getString("schueler_id");
                String kursBezeichnung = resultSet.getString("kurs_bezeichnung");
                String pruefungsfolge = resultSet.getString("pruefungsfolge");

                Schueler schueler = schuelerMap.get(schuelerId);
                Kurs kurs = kursMap.get(kursBezeichnung);

                if (schueler == null) {
                    throw new SQLException("Schüler für Prüfung nicht gefunden: " + schuelerId);
                }

                if (kurs == null) {
                    throw new SQLException("Kurs für Prüfung nicht gefunden: " + kursBezeichnung);
                }

                Abiturfach abiturfach = Abiturfach.valueOf(resultSet.getString("abiturfach"));
                Pruefung pruefung = new Pruefung(schueler, kurs, null, abiturfach); //null bei Prüfer, weil der Prüfer erst mit den Prüfungsdaten geladen wird!
                pruefung.setPruefungId(pruefungId);
                pruefung.setPruefungsFolge(pruefungsfolge);
                abitur.addPruefung(pruefung);
                pruefungMap.put(pruefungId, pruefung);
            }
        }
    }

    private void ladePruefungsplanung(Connection connection, Map<Long, Pruefung> pruefungMap, Map<Long, Pruefungstag> pruefungstagMap, Map<String, Raum> raumMap, Map<String, Lehrer> lehrerMap) throws SQLException {

        String sql = """
                SELECT pruefung_id, pruefungstag_id, beginn, planungsspalte, raum_bezeichnung, pruefer_kuerzel, schriftfuehrer_kuerzel, vorsitz_kuerzel FROM pruefungsplanung
                """;

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                long pruefungId = resultSet.getLong("pruefung_id");
                Pruefung pruefung = pruefungMap.get(pruefungId);

                if (pruefung == null) {
                    throw new SQLException("Prüfung für Prüfungsplanung nicht gefunden: " + pruefungId);
                }

                long pruefungstagId = resultSet.getLong("pruefungstag_id");

                if (!resultSet.wasNull()) {
                    Pruefungstag pruefungstag = pruefungstagMap.get(pruefungstagId);

                    if (pruefungstag == null) {
                        throw new SQLException("Prüfungstag für Prüfungsplanung nicht gefunden: " + pruefungstagId);
                    }

                    pruefung.setPruefungstag(pruefungstag.getDatum());
                }

                String beginnText = resultSet.getString("beginn");

                if (beginnText != null) {
                    pruefung.setBeginn(LocalTime.parse(beginnText));
                }

                int planungsspalte = resultSet.getInt("planungsspalte");

                if (!resultSet.wasNull()) {
                    pruefung.setPlanungsspalte(planungsspalte);
                }

                String raumBezeichnung = resultSet.getString("raum_bezeichnung");

                if (raumBezeichnung != null) {
                    Raum raum = raumMap.get(raumBezeichnung);

                    if (raum == null) {
                        throw new SQLException("Raum für Prüfungsplanung nicht gefunden: " + raumBezeichnung);
                    }

                    pruefung.setRaum(raum);
                }

                String prueferKuerzel = resultSet.getString("pruefer_kuerzel");

                if (prueferKuerzel != null) {
                    Lehrer pruefer = lehrerMap.get(prueferKuerzel);

                    if (pruefer == null) {
                        throw new SQLException("Prüfer nicht gefunden: " + prueferKuerzel);
                    }

                    pruefung.setPruefer(pruefer);
                }

                String schriftfuehrerKuerzel = resultSet.getString("schriftfuehrer_kuerzel");

                if (schriftfuehrerKuerzel != null) {
                    Lehrer schriftfuehrer = lehrerMap.get(schriftfuehrerKuerzel);

                    if (schriftfuehrer == null) {
                        throw new SQLException("Schriftführer nicht gefunden: " + schriftfuehrerKuerzel);
                    }

                    pruefung.setSchriftfuehrer(schriftfuehrer);
                }

                String vorsitzKuerzel = resultSet.getString("vorsitz_kuerzel");

                if (vorsitzKuerzel != null) {
                    Lehrer vorsitz = lehrerMap.get(vorsitzKuerzel);

                    if (vorsitz == null) {
                        throw new SQLException("Vorsitz nicht gefunden: " + vorsitzKuerzel);
                    }

                    pruefung.setVorsitz(vorsitz);
                }
            }
        }
    }

    public void aktualisierePruefungsplanung(Pruefung pruefung) throws SQLException {
        if (pruefung.getPruefungId() == null) {
            throw new IllegalStateException("Prüfung wurde noch nicht in der Datenbank gespeichert.");
        }

        String sql = """
                UPDATE pruefungsplanung SET pruefungstag_id = ?, beginn = ?, planungsspalte = ?, raum_bezeichnung = ?, pruefer_kuerzel = ?, schriftfuehrer_kuerzel = ?, vorsitz_kuerzel = ? WHERE pruefung_id = ?
                """;

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            setPruefungstagId(statement, 1, connection, pruefung);
            setLocalTime(statement, 2, pruefung.getBeginn());
            setInteger(statement, 3, pruefung.getPlanungsspalte());
            setRaum(statement, 4, pruefung.getRaum());
            setLehrer(statement, 5, pruefung.getPruefer());
            setLehrer(statement, 6, pruefung.getSchriftfuehrer());
            setLehrer(statement, 7, pruefung.getVorsitz());
            statement.setLong(8, pruefung.getPruefungId());

            statement.executeUpdate();
        }
    }

    public void synchronisiereSchueler(Abitur abitur) throws SQLException {
        String sql = """
                INSERT INTO schueler (schild_id, nachname, vorname, geburtsdatum, geschlecht) VALUES (?, ?, ?, ?, ?) ON CONFLICT(schild_id) DO UPDATE SET
                nachname = excluded.nachname, vorname = excluded.vorname,
                geburtsdatum = excluded.geburtsdatum,
                geschlecht = excluded.geschlecht
                """;

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Schueler schueler : abitur.getSchuelerList()) {
                statement.setString(1, schueler.getSchildId());
                statement.setString(2, schueler.getNachname());
                statement.setString(3, schueler.getVorname());

                if (schueler.getGeburtsdatum() == null) {
                    statement.setNull(4, Types.VARCHAR);
                } else {
                    statement.setString(4, schueler.getGeburtsdatum().toString());
                }

                statement.setString(5, schueler.getGeschlecht() == null ? null : schueler.getGeschlecht().name());
                statement.addBatch();
            }

            statement.executeBatch();
        }
    }

    public void aktualisiereSchueler(Schueler schueler) throws SQLException {
        String sql = """
                UPDATE schueler
                SET nachname = ?, vorname = ?, geburtsdatum = ?, geschlecht = ?
                WHERE schild_id = ?
                """;

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, schueler.getNachname());
            statement.setString(2, schueler.getVorname());

            if (schueler.getGeburtsdatum() == null) {
                statement.setNull(3, Types.VARCHAR);
            } else {
                statement.setString(3, schueler.getGeburtsdatum().toString());
            }

            statement.setString(4, schueler.getGeschlecht() == null ? null : schueler.getGeschlecht().name());
            statement.setString(5, schueler.getSchildId());

            int anzahl = statement.executeUpdate();

            if (anzahl != 1) {
                throw new SQLException("Schüler konnte nicht eindeutig aktualisiert werden.");
            }
        }
    }

    public void aktualisiereKurs(Kurs kurs) throws SQLException {
        String sql = """
                UPDATE kurs SET fach = ?, fachlehrer_kuerzel = ? WHERE bezeichnung = ?
                """;
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, kurs.getFach().getKuerzel());
            statement.setString(2, kurs.getFachlehrer().getKuerzel());
            statement.setString(3, kurs.getBezeichnung());
            statement.executeUpdate();
        }
    }

    public void aktualisiereRaum(Raum raum) throws SQLException {
        String sql = "UPDATE Raum SET kapazitaet = ? WHERE bezeichnung = ?";
        try (Connection connection = getConnection();PreparedStatement statement = connection.prepareStatement(sql);){
            statement.setInt(1, raum.getKapazitaet());
            statement.setString(2, raum.getBezeichnung());
            statement.executeUpdate();
        }
    }

    public void aktualisiereLehrer(Lehrer lehrer) throws SQLException {
        String sqlLehrer = """
                UPDATE lehrer
                SET nachname = ?, vorname = ?, amtsbezeichnung = ?
                WHERE kuerzel = ?
                """;

        String sqlFakultaetLoeschen = """
                DELETE FROM lehrer_fakultaet
                WHERE lehrer_kuerzel = ?
                """;

        String sqlFakultaetEinfuegen = """
                INSERT INTO lehrer_fakultaet (lehrer_kuerzel, fach)
                VALUES (?, ?)
                """;

        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement statement = connection.prepareStatement(sqlLehrer)) {
                    statement.setString(1, lehrer.getNachname());
                    statement.setString(2, lehrer.getVorname());
                    statement.setString(3, lehrer.getAmtsbezeichnung() == null ? null : lehrer.getAmtsbezeichnung().getKuerzel());
                    statement.setString(4, lehrer.getKuerzel());
                    statement.executeUpdate();
                }

                try (PreparedStatement statement = connection.prepareStatement(sqlFakultaetLoeschen)) {
                    statement.setString(1, lehrer.getKuerzel());
                    statement.executeUpdate();
                }

                try (PreparedStatement statement = connection.prepareStatement(sqlFakultaetEinfuegen)) {
                    for (Fach fach : lehrer.getFakultas()) {
                        statement.setString(1, lehrer.getKuerzel());
                        statement.setString(2, fach.getKuerzel());
                        statement.addBatch();
                    }

                    statement.executeBatch();
                }
                connection.commit();

            } catch (SQLException e) {
                e.printStackTrace();
                connection.rollback();
                throw e;
            }
        }
    }

    private void fuegeSchuelerDatensatzHinzu(Connection connection, Schueler schueler) throws SQLException {
        String sql = """
                INSERT INTO schueler (schild_id, nachname, vorname, geburtsdatum, geschlecht) VALUES (?, ?, ?, ?, ?)""";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, schueler.getSchildId());
            statement.setString(2, schueler.getNachname());
            statement.setString(3, schueler.getVorname());

            if (schueler.getGeburtsdatum() == null) {
                statement.setNull(4, Types.VARCHAR);
            } else {
                statement.setString(4, schueler.getGeburtsdatum().toString());
            }

            statement.setString(5, schueler.getGeschlecht() == null ? null : schueler.getGeschlecht().name());

            if (statement.executeUpdate() != 1) {
                throw new SQLException("Schüler konnte nicht eindeutig eingefügt werden.");
            }
        }
    }

    public void fuegeSchuelerHinzu(Schueler schueler, List<Pruefung> pruefungen) throws SQLException {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            try {
                fuegeSchuelerDatensatzHinzu(connection, schueler);
                for (Pruefung pruefung : pruefungen) {
                    fuegePruefungHinzu(connection, pruefung);
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    public void fuegeLehrerHinzu(Lehrer lehrer) throws SQLException {
        String lehrerSql = """
                INSERT INTO lehrer (kuerzel, anrede, nachname, vorname, amtsbezeichnung)
                VALUES (?, ?, ?, ?, ?)
                """;

        String fakultasSql = """
                INSERT INTO lehrer_fakultaet (lehrer_kuerzel, fach)
                VALUES (?, ?)
                """;

        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement statement = connection.prepareStatement(lehrerSql)) {
                    statement.setString(1, lehrer.getKuerzel());
                    statement.setString(2, lehrer.getAnrede());
                    statement.setString(3, lehrer.getNachname());
                    statement.setString(4, lehrer.getVorname());
                    statement.setString(5, lehrer.getAmtsbezeichnung() == null ? null : lehrer.getAmtsbezeichnung().getKuerzel());
                    statement.executeUpdate();
                }

                try (PreparedStatement statement = connection.prepareStatement(fakultasSql)) {
                    for (Fach fach : lehrer.getFakultas()) {
                        statement.setString(1, lehrer.getKuerzel());
                        statement.setString(2, fach.getKuerzel());
                        statement.addBatch();
                    }
                    statement.executeBatch();
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
                throw e;
            }
        }
    }

    private void fuegePruefungHinzu(Connection connection, Pruefung pruefung) throws SQLException {
        String sql = """
                INSERT INTO pruefung (schueler_id, kurs_bezeichnung, abiturfach, pruefungsform, pruefungsfolge) VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, pruefung.getSchueler().getSchildId());
            statement.setString(2, pruefung.getKurs().getBezeichnung());
            statement.setString(3, pruefung.getAbiturfach().name());
            statement.setString(4, pruefung.getPruefungsform().name());
            statement.setString(5, pruefung.getPruefungsFolge());

            if (statement.executeUpdate() != 1) {
                throw new SQLException("Prüfung konnte nicht eindeutig eingefügt werden.");
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Keine ID für neue Prüfung erzeugt.");
                }

                long pruefungId = keys.getLong(1);
                pruefung.setPruefungId(pruefungId);

                speicherePruefungsplanung(connection, pruefungId, pruefung);
            }
        }
    }

    public void fuegeRaumHinzu(Raum raum) throws SQLException{
        String sql = """
                INSERT INTO raum (bezeichnung, kapazitaet) Values (?, ?);
                """;
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, raum.getBezeichnung());
            statement.setInt(2, raum.getKapazitaet());
            statement.execute();
        }
    }

    public void fuegeKursHinzu(Kurs kurs) throws SQLException {
        String sql = """
                INSERT INTO kurs (bezeichnung, fach, fachlehrer_kuerzel) Values (?, ?, ?);
                """;
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, kurs.getBezeichnung());
            statement.setString(2, kurs.getFach().getKuerzel());
            statement.setString(3, kurs.getFachlehrer().getKuerzel());
            statement.execute();
        }
    }

    public void aktualisiereFach(Fach fach) throws SQLException{
        String sql = """
                UPDATE fach SET bezeichnung = ?, stammfach_kuerzel = ?, faechergruppe = ? WHERE kuerzel = ?;
                """;

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, fach.getBezeichnung() == null ? null : fach.getBezeichnung());
            statement.setString(2, fach.getStammfach() == null ? null : fach.getStammfach().getKuerzel());
            statement.setString(3, fach.getFaechergruppe() == null ? null : fach.getFaechergruppe());
            statement.setString(4, fach.getKuerzel());
            statement.executeUpdate();
        }
    }

    public void fuegeFachHinzu(Fach fach) throws SQLException {
        String sql = """
                INSERT INTO fach (kuerzel, bezeichnung, stammfach_kuerzel, faechergruppe) VALUES (?, ?, ?, ?);
                """;

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, fach.getKuerzel());
            statement.setString(2, fach.getBezeichnung() == null ? null : fach.getBezeichnung());
            statement.setString(3, fach.getStammfach() == null ? null : fach.getStammfach().getKuerzel());
            statement.setString(4, fach.getFaechergruppe() == null ? null : fach.getFaechergruppe());
            statement.executeUpdate();
        }
    }

    public void loescheSchueler(String schild_id) throws SQLException {
        String sql = "DELETE FROM schueler WHERE schild_id = ?";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, schild_id);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Schüler mit Schild-ID " + schild_id + " konnte nicht gelöscht werden.");
            }
        }
    }

    public void synchronisiereLehrer(Abitur abitur) throws SQLException {
        String lehrerSql = """
                INSERT INTO lehrer
                (kuerzel, anrede, nachname, vorname, amtsbezeichnung) VALUES (?, ?, ?, ?, ?) ON CONFLICT(kuerzel) DO UPDATE SET
                    anrede = excluded.anrede,
                    nachname = excluded.nachname,
                    vorname = excluded.vorname,
                    amtsbezeichnung = excluded.amtsbezeichnung
                """;

        String fakultasLoeschenSql = "DELETE FROM lehrer_fakultaet WHERE lehrer_kuerzel = ?";

        String fakultasEinfuegenSql = """
                INSERT INTO lehrer_fakultaet (lehrer_kuerzel, fach) VALUES (?, ?)
                """;

        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement lehrerStatement = connection.prepareStatement(lehrerSql);
                 PreparedStatement fakultasLoeschenStatement = connection.prepareStatement(fakultasLoeschenSql);
                 PreparedStatement fakultasEinfuegenStatement = connection.prepareStatement(fakultasEinfuegenSql)) {

                for (Lehrer lehrer : abitur.getLehrer()) {
                    lehrerStatement.setString(1, lehrer.getKuerzel());
                    lehrerStatement.setString(2, lehrer.getAnrede());
                    lehrerStatement.setString(3, lehrer.getNachname());
                    lehrerStatement.setString(4, lehrer.getVorname());
                    lehrerStatement.setString(5, lehrer.getAmtsbezeichnung() == null ? null : lehrer.getAmtsbezeichnung().getKuerzel());
                    lehrerStatement.executeUpdate();

                    fakultasLoeschenStatement.setString(1, lehrer.getKuerzel());
                    fakultasLoeschenStatement.executeUpdate();

                    for (Fach fakultas : lehrer.getFakultas()) {
                        if (fakultas != null) {
                            fakultasEinfuegenStatement.setString(1, lehrer.getKuerzel());
                            fakultasEinfuegenStatement.setString(2, fakultas.getKuerzel());
                            fakultasEinfuegenStatement.addBatch();
                        }
                    }

                    fakultasEinfuegenStatement.executeBatch();
                }

                connection.commit();

            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    public void synchronisiereFaecher(Abitur abitur) throws SQLException {
        String fachSql = """
                INSERT INTO fach
                (kuerzel, bezeichnung, stammfach_kuerzel, faechergruppe)
                VALUES (?, ?, NULL, ?)
                ON CONFLICT(kuerzel) DO UPDATE SET
                    bezeichnung = excluded.bezeichnung,
                    stammfach_kuerzel = NULL,
                    faechergruppe = excluded.faechergruppe
                """;

        String stammfachSql = """
                UPDATE fach
                SET stammfach_kuerzel = ?
                WHERE kuerzel = ?
                """;

        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement fachStatement = connection.prepareStatement(fachSql);
                 PreparedStatement stammfachStatement = connection.prepareStatement(stammfachSql)) {

                for (Fach fach : abitur.getFaecher()) {
                    fachStatement.setString(1, fach.getKuerzel());
                    fachStatement.setString(2, fach.getBezeichnung());
                    fachStatement.setString(3, fach.getFaechergruppe());
                    fachStatement.addBatch();
                }

                fachStatement.executeBatch();

                for (Fach fach : abitur.getFaecher()) {
                    if (fach.getStammfach() != null) {
                        stammfachStatement.setString(1, fach.getStammfach().getKuerzel());
                        stammfachStatement.setString(2, fach.getKuerzel());
                        stammfachStatement.addBatch();
                    }
                }

                stammfachStatement.executeBatch();

                connection.commit();

            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    public void synchronisiereRaeume(Abitur abitur) throws SQLException {
        String raeumeSql = """
                INSERT INTO raum (bezeichnung, kapazitaet) VALUES (?, ?) ON CONFLICT(bezeichnung) DO UPDATE SET
                    kapazitaet = excluded.kapazitaet
                """;

        try (Connection connection = getConnection(); PreparedStatement raeumeStatement = connection.prepareStatement(raeumeSql)) {
            for (Raum raum : abitur.getRaeume()) {
                raeumeStatement.setString(1, raum.getBezeichnung());
                raeumeStatement.setInt(2, raum.getKapazitaet());
                raeumeStatement.addBatch();
            }
            raeumeStatement.executeBatch();
        }
    }

    private void synchronisiereKurse(Connection connection, Abitur abitur) throws SQLException {
        String sql = """
                INSERT INTO kurs (bezeichnung, fach, fachlehrer_kuerzel) VALUES (?, ?, ?) ON CONFLICT(bezeichnung) DO UPDATE SET
                    fach = excluded.fach,
                    fachlehrer_kuerzel = excluded.fachlehrer_kuerzel
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Kurs kurs : abitur.getKurse()) {
                statement.setString(1, kurs.getBezeichnung());
                statement.setString(2, kurs.getFach().getKuerzel());

                if (kurs.getFachlehrer() == null) {
                    statement.setNull(3, Types.VARCHAR);
                } else {
                    statement.setString(3, kurs.getFachlehrer().getKuerzel());
                }
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void synchronisierePruefungen(Connection connection, Abitur abitur) throws SQLException {
        String updateSql = """
                UPDATE pruefung SET schueler_id = ?, kurs_bezeichnung = ?, abiturfach = ?, pruefungsform = ?, pruefungsfolge = ? WHERE pruefung_id = ?
                """;
        try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {

            for (Pruefung pruefung : abitur.getPruefungen()) {
                if (pruefung.getPruefungId() != null) {
                    updateStatement.setString(1, pruefung.getSchueler().getSchildId());
                    updateStatement.setString(2, pruefung.getKurs().getBezeichnung());
                    updateStatement.setString(3, pruefung.getAbiturfach().name());
                    updateStatement.setString(4, pruefung.getPruefungsform().name());
                    updateStatement.setString(5, pruefung.getPruefungsFolge());
                    updateStatement.setLong(6, pruefung.getPruefungId());

                    updateStatement.executeUpdate();

                } else {
                    fuegePruefungHinzu(connection, pruefung);
                }
            }
        }
    }

    public void synchronisiereLeistungsdaten(Abitur abitur) throws SQLException {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try {
                synchronisiereKurse(connection, abitur);
                synchronisierePruefungen(connection, abitur);
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    public void fuegePruefungstagHinzu(Pruefungstag pruefungstag) throws SQLException {
        String sql = """
                INSERT INTO pruefungstag (datum) VALUES (?) ON CONFLICT(datum) DO NOTHING
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pruefungstag.getDatum().toString());
            statement.executeUpdate();
        }
    }

    public void loeschePruefungstag(Pruefungstag pruefungstag) throws SQLException {
        String sql = "DELETE FROM pruefungstag WHERE datum = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pruefungstag.getDatum().toString());
            statement.executeUpdate();
        }
    }

    public void aktualisierePruefungstagDatum(LocalDate altesDatum, LocalDate neuesDatum) throws SQLException {
        String sql = "UPDATE pruefungstag SET datum = ? WHERE datum = ?";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, neuesDatum.toString());
            statement.setString(2, altesDatum.toString());
            statement.executeUpdate();
        }
    }

    public void aktualisierePruefungsfolge(Pruefung pruefung) {
        String sql = """
                UPDATE pruefung SET pruefungsfolge = ? WHERE pruefung_id = ?
                """;
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pruefung.getPruefungsFolge());
            statement.setLong(2, pruefung.getPruefungId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void aktualisierePruefungskurse(Map<Pruefung, Kurs> aenderungen) throws SQLException {
        String sql = """
                UPDATE pruefung
                SET kurs_bezeichnung = ?
                WHERE pruefung_id = ?
                """;

        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (Map.Entry<Pruefung, Kurs> eintrag : aenderungen.entrySet()) {
                    statement.setString(1, eintrag.getValue().getBezeichnung());
                    statement.setLong(2, eintrag.getKey().getPruefungId());
                    statement.addBatch();
                }

                int[] ergebnisse = statement.executeBatch();

                for (int ergebnis : ergebnisse) {
                    if (ergebnis == 0) {
                        throw new SQLException("Eine Prüfung konnte nicht aktualisiert werden.");
                    }
                }

                for (Map.Entry<Pruefung, Kurs> eintrag : aenderungen.entrySet()) {
                    setzePruefungsplanungZurueck(connection, eintrag.getKey(), eintrag.getValue());
                }

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    private void setzePruefungsplanungZurueck(Connection connection, Pruefung pruefung, Kurs neuerKurs) throws SQLException {
        String sql = """
                UPDATE pruefungsplanung
                SET pruefungstag_id = NULL,
                    beginn = NULL,
                    planungsspalte = NULL,
                    raum_bezeichnung = NULL,
                    pruefer_kuerzel = ?,
                    schriftfuehrer_kuerzel = NULL,
                    vorsitz_kuerzel = NULL
                WHERE pruefung_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setLehrer(statement, 1, neuerKurs.getFachlehrer());
            statement.setLong(2, pruefung.getPruefungId());

            if (statement.executeUpdate() != 1) {
                throw new SQLException("Die Prüfungsplanung konnte nicht zurückgesetzt werden.");
            }
        }
    }


}
