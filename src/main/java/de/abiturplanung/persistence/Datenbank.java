package de.abiturplanung.persistence;

import de.abiturplanung.model.*;

import java.sql.*;
import java.time.LocalTime;

public class Datenbank {

    private final String url;

    public Datenbank(String dateiname) {
        url = "jdbc:sqlite:" + dateiname;
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

    public void speichereAbitur(Abitur abitur) throws SQLException{
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

    public Abitur ladeAbitur() {
        return null; //Platzhalter
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
            INSERT INTO lehrer
            (kuerzel, anrede, nachname, vorname, amtsbezeichnung)
            VALUES (?, ?, ?, ?, ?)
            """;

        String fakultasSql = """ 
            INSERT INTO lehrer_fakultaet
            (lehrer_kuerzel, fach)
            VALUES (?, ?)
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
            INSERT INTO pruefung
            (schueler_id, kurs_bezeichnung, abiturfach, pruefungsform)
            VALUES (?, ?, ?, ?)
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
                    speicherePruefungsplanung(connection, pruefungId, pruefung, abitur);
                }
            }
        }
    }

    private void speicherePruefungsplanung(Connection connection, long pruefungId, Pruefung pruefung, Abitur abitur) throws SQLException {
        String sql = """
            INSERT INTO pruefungsplanung
            (pruefung_id, pruefungstag_id, beginn, planungsspalte, raum_bezeichnung,
             pruefer_kuerzel, schriftfuehrer_kuerzel, vorsitz_kuerzel)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
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


}