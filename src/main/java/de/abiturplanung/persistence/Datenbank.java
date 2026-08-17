package de.abiturplanung.persistence;

import de.abiturplanung.model.*;

import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
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
                    
                        FOREIGN KEY (schueler_id)
                            REFERENCES schueler(schild_id),
                    
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

    public boolean istInitialisiert() throws SQLException {
        String sql = "SELECT wert FROM app_info WHERE schluessel = 'initialisiert'";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            if (resultSet.next()) {
                return Boolean.parseBoolean(resultSet.getString("wert"));
            }

            return false;
        }
    }

    public void setInitialisiert() throws SQLException {
        String sql = """
                INSERT INTO app_info (schluessel, wert)
                VALUES ('initialisiert', 'true')
                ON CONFLICT(schluessel) DO UPDATE SET wert = 'true'
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    public void speichereAbitur(Abitur abitur) throws SQLException {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            try {
                speichereSchueler(connection, abitur);
                speichereLehrer(connection, abitur);
                speichereRaeume(connection, abitur);
                speichereKurse(connection, abitur);
                speicherePruefungstage(connection, abitur);
                speicherePruefungen(connection, abitur);

                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }

    }

    private void speichereSchueler(Connection connection, Abitur abitur) throws SQLException {
        String sql = """
                INSERT INTO schueler (schild_id, nachname, vorname, geburtsdatum, geschlecht) VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Schueler schueler : abitur.getSchueler()) {
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

    private void speichereLehrer(Connection connection, Abitur abitur) throws SQLException {
        String lehrerSql = """ 
                INSERT INTO lehrer (kuerzel, anrede, nachname, vorname, amtsbezeichnung) VALUES (?, ?, ?, ?, ?)
                """;

        String fakultasSql = """ 
                INSERT INTO lehrer_fakultaet (lehrer_kuerzel, fach) VALUES (?, ?)
                """;

        try (PreparedStatement lehrerStatement = connection.prepareStatement(lehrerSql);
             PreparedStatement fakultasStatement = connection.prepareStatement(fakultasSql)) {

            for (Lehrer lehrer : abitur.getLehrer()) {
                lehrerStatement.setString(1, lehrer.getKuerzel());
                lehrerStatement.setString(2, lehrer.getAnrede());
                lehrerStatement.setString(3, lehrer.getNachname());
                lehrerStatement.setString(4, lehrer.getVorname());
                lehrerStatement.setString(5, lehrer.getAmtsbez());
                lehrerStatement.addBatch();

                for (String fakultas : lehrer.getFakultas()) {
                    if (fakultas != null && !fakultas.isBlank()) {
                        fakultasStatement.setString(1, lehrer.getKuerzel());
                        fakultasStatement.setString(2, fakultas);
                        fakultasStatement.addBatch();
                    }
                }
            }

            lehrerStatement.executeBatch();
            fakultasStatement.executeBatch();
        }
    }

    private void speichereRaeume(Connection connection, Abitur abitur) throws SQLException {
        String sql = """
                INSERT INTO raum (bezeichnung, kapazitaet) VALUES (?, ?) 
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Raum raum : abitur.getRaeume()) {
                statement.setString(1, raum.getBezeichnung());
                statement.setInt(2, raum.getKapazitaet());
                statement.addBatch();
            }

            statement.executeBatch();
        }
    }

    private void speichereKurse(Connection connection, Abitur abitur) throws SQLException {
        String sql = """
                INSERT INTO kurs
                (bezeichnung, fach, fachlehrer_kuerzel)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Kurs kurs : abitur.getKurse()) {
                statement.setString(1, kurs.getBezeichnung());
                statement.setString(2, kurs.getFach());

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

    private void speicherePruefungstage(Connection connection, Abitur abitur) throws SQLException {
        String sql = """
                INSERT INTO pruefungstag (datum)
                VALUES (?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Pruefungstag pruefungstag : abitur.getPruefungstage()) {
                statement.setString(1, pruefungstag.getDatum().toString());
                statement.addBatch();
            }

            statement.executeBatch();
        }
    }

    private void speicherePruefungen(Connection connection, Abitur abitur) throws SQLException {
        String pruefungSql = """
                INSERT INTO pruefung (schueler_id, kurs_bezeichnung, abiturfach, pruefungsform) VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(pruefungSql, Statement.RETURN_GENERATED_KEYS)) {
            for (Pruefung pruefung : abitur.getPruefungen()) {
                statement.setString(1, pruefung.getSchueler().getSchildId());
                statement.setString(2, pruefung.getKurs().getBezeichnung());
                statement.setString(3, pruefung.getAbiturfach().name());
                statement.setString(4, pruefung.getPruefungsform().name());

                statement.executeUpdate();

                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Keine ID für Prüfung erzeugt.");
                    }

                    long pruefungId = keys.getLong(1);
                    pruefung.setPruefungId(pruefungId);
                    speicherePruefungsplanung(connection, pruefungId, pruefung, abitur);
                }
            }
        }
    }

    private void speicherePruefungsplanung(Connection connection, long pruefungId, Pruefung pruefung, Abitur abitur) throws SQLException {
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

    public Abitur ladeAbitur() throws SQLException {
        Abitur abitur = new Abitur();

        Map<String, Schueler> schuelerMap = new HashMap<>(); //Die Maps sind nur für den Ladevorgang, um Datensätze anhand des Schlüssels leicht identifizieren zu können.
        Map<String, Lehrer> lehrerMap = new HashMap<>();
        Map<String, Raum> raumMap = new HashMap<>();
        Map<String, Kurs> kursMap = new HashMap<>();
        Map<Long, Pruefungstag> pruefungstagMap = new HashMap<>();
        Map<Long, Pruefung> pruefungMap = new HashMap<>();

        try (Connection connection = getConnection()) {
            ladeSchueler(connection, abitur, schuelerMap);
            ladeLehrer(connection, abitur, lehrerMap);
            ladeFakultas(connection, lehrerMap);
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
                Lehrer lehrer = new Lehrer(kuerzel);
                lehrer.aktualisiereStammdaten(resultSet.getString("anrede"), resultSet.getString("nachname"), resultSet.getString("vorname"), resultSet.getString("amtsbezeichnung"));
                abitur.addLehrer(lehrer);
                lehrerMap.put(kuerzel, lehrer);
            }
        }
    }

    private void ladeFakultas(Connection connection, Map<String, Lehrer> lehrerMap) throws SQLException {
        String sql = "SELECT lehrer_kuerzel, fach FROM lehrer_fakultaet";

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String kuerzel = resultSet.getString("lehrer_kuerzel");
                String fach = resultSet.getString("fach");

                Lehrer lehrer = lehrerMap.get(kuerzel);

                if (lehrer == null) {
                    throw new SQLException("Lehrer für Fakultas nicht gefunden: " + kuerzel);
                }

                lehrer.getFakultas().add(fach);
            }
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

                Kurs kurs = new Kurs(bezeichnung, fach, fachlehrer);

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
        String sql = "SELECT pruefung_id, schueler_id, kurs_bezeichnung, abiturfach, pruefungsform FROM pruefung";

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                long pruefungId = resultSet.getLong("pruefung_id");
                String schuelerId = resultSet.getString("schueler_id");
                String kursBezeichnung = resultSet.getString("kurs_bezeichnung");

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

    public void aktualisiereSchueler(Abitur abitur) throws SQLException {
        String sql = """
                INSERT INTO schueler (schild_id, nachname, vorname, geburtsdatum, geschlecht) VALUES (?, ?, ?, ?, ?) ON CONFLICT(schild_id) DO UPDATE SET
                nachname = excluded.nachname, vorname = excluded.vorname,
                geburtsdatum = excluded.geburtsdatum,
                geschlecht = excluded.geschlecht
                """;

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Schueler schueler : abitur.getSchueler()) {
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

    public void aktualisiereLehrer(Abitur abitur) throws SQLException {
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
                    lehrerStatement.setString(5, lehrer.getAmtsbez());
                    lehrerStatement.executeUpdate();

                    fakultasLoeschenStatement.setString(1, lehrer.getKuerzel());
                    fakultasLoeschenStatement.executeUpdate();

                    for (String fakultas : lehrer.getFakultas()) {
                        if (fakultas != null && !fakultas.isBlank()) {
                            fakultasEinfuegenStatement.setString(1, lehrer.getKuerzel());
                            fakultasEinfuegenStatement.setString(2, fakultas);
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

    public void aktualisiereRaeume(Abitur abitur) throws SQLException {
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

    private void aktualisiereKurse(Connection connection, Abitur abitur) throws SQLException {
        String sql = """
            INSERT INTO kurs (bezeichnung, fach, fachlehrer_kuerzel) VALUES (?, ?, ?) ON CONFLICT(bezeichnung) DO UPDATE SET
                fach = excluded.fach,
                fachlehrer_kuerzel = excluded.fachlehrer_kuerzel
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Kurs kurs : abitur.getKurse()) {
                statement.setString(1, kurs.getBezeichnung());
                statement.setString(2, kurs.getFach());

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

    private void aktualisierePruefungen(Connection connection, Abitur abitur) throws SQLException {
        String updateSql = """
            UPDATE pruefung SET schueler_id = ?, kurs_bezeichnung = ?, abiturfach = ?, pruefungsform = ? WHERE pruefung_id = ?
            """;

        String insertSql = """
            INSERT INTO pruefung (schueler_id, kurs_bezeichnung, abiturfach, pruefungsform) VALUES (?, ?, ?, ?)
            """;

        try (PreparedStatement updateStatement = connection.prepareStatement(updateSql);
             PreparedStatement insertStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

            for (Pruefung pruefung : abitur.getPruefungen()) {
                if (pruefung.getPruefungId() != null) {
                    updateStatement.setString(1, pruefung.getSchueler().getSchildId());
                    updateStatement.setString(2, pruefung.getKurs().getBezeichnung());
                    updateStatement.setString(3, pruefung.getAbiturfach().name());
                    updateStatement.setString(4, pruefung.getPruefungsform().name());
                    updateStatement.setLong(5, pruefung.getPruefungId());

                    updateStatement.executeUpdate();

                } else {
                    insertStatement.setString(1, pruefung.getSchueler().getSchildId());
                    insertStatement.setString(2, pruefung.getKurs().getBezeichnung());
                    insertStatement.setString(3, pruefung.getAbiturfach().name());
                    insertStatement.setString(4, pruefung.getPruefungsform().name());

                    insertStatement.executeUpdate();

                    try (ResultSet keys = insertStatement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Keine ID für neue Prüfung erzeugt.");
                        }

                        long pruefungId = keys.getLong(1);
                        pruefung.setPruefungId(pruefungId);

                        speicherePruefungsplanung(connection, pruefungId, pruefung, abitur);
                    }
                }
            }
        }
    }

    public void aktualisiereLeistungsdaten(Abitur abitur) throws SQLException {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            try {
                aktualisiereKurse(connection, abitur);
                aktualisierePruefungen(connection, abitur);
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }
}
