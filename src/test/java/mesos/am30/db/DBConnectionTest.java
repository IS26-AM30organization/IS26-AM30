package mesos.am30.db;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class DBConnectionTest {
    private static final String URL  = "jdbc:h2:mem:test-db;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    // Schema creation
    @BeforeAll
    static void createSchema() throws IOException, SQLException {
        DBConnection.configureForTesting(URL, USER, PASSWORD);

        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement()) {

            // create Table GAMES
            statement.execute("""
                CREATE TABLE GAMES (
                    -- attributes
                    GameID        int           NOT NULL AUTO_INCREMENT,
                    PlayedAt      datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    PlayersNumber tinyint       NOT NULL CHECK (PlayersNumber >= 2 AND PlayersNumber <= 5),
            
                    -- constraints
                    CONSTRAINT pk_GAMES PRIMARY KEY (GameID)
                );
            """);

            // create Table RESULTS
            statement.execute("""
                CREATE TABLE RESULTS (
                    -- attributes
                    GameID    int               NOT NULL,
                    Nickname  varchar(64)       NOT NULL,
                    Score     int               NOT NULL,
            
                    -- constraints
                    CONSTRAINT pk_RESULTS PRIMARY KEY (GameID, Nickname),
                    CONSTRAINT FK_RESULTS_GAMES FOREIGN KEY (GameID) REFERENCES GAMES(GameID) ON DELETE CASCADE
                );
            """);
        }
    }

    // Reset DB Data for every Test
    @BeforeEach
    void setUp() throws IOException, SQLException {
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement()) {

            // reset the DB
            statement.execute("DELETE FROM GAMES WHERE GameID > 0");
            statement.execute("ALTER TABLE GAMES ALTER COLUMN GameID RESTART WITH 1");
            statement.execute("INSERT INTO GAMES (PlayersNumber) VALUES (2), (3)");
            statement.execute("""
                INSERT INTO RESULTS (GameID, Nickname, Score) VALUES
                    (1, 'Alice',   200),
                    (1, 'Bob',     90),
                    (2, 'Charlie', 310),
                    (2, 'Alice',   300),
                    (2, 'Bob',     280)
            """);
        }
    }

    @Test
    void getFromEnv() throws IOException, SQLException {
        // Act
        DBConnection.setInitialized(false);

        // get the Result of a Statement
        DBConnection.getConnection();

        // Assert
        Properties env = new Properties();
        env.load(new FileReader("db/.env"));
        assertEquals("jdbc:mysql://localhost:3306/" + env.getProperty("MYSQL_DATABASE"), DBConnection.getURL());
        assertEquals(env.getProperty("MYSQL_USER"), DBConnection.getUSER());
        assertEquals(env.getProperty("MYSQL_PASSWORD"), DBConnection.getPASSWORD());

        // reset DB
        DBConnection.configureForTesting(URL, USER, PASSWORD);
    }

    @Test
    void getResults_Count() throws IOException, SQLException {
        // set the Query
        String query = "SELECT * " +
                "FROM GAMES NATURAL JOIN RESULTS " +
                "ORDER BY GameID, Score DESC";

        // Act
        List<Map<String, String>> rows;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            rows = DBConnection.getResults(statement);
        }

        // Assert
        assertEquals(5, rows.size());
    }

    @Test
    void getResults_Order() throws IOException, SQLException {
        // set the Query
        String query = "SELECT * " +
                "FROM GAMES NATURAL JOIN RESULTS " +
                "ORDER BY GameID, Score DESC";

        // Act
        List<Map<String, String>> rows;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            rows = DBConnection.getResults(statement);
        }

        /* Game 1:
         *  - Alice | 200
         *  - Bob   | 90
         */
        assertEquals("Alice", rows.get(0).get("NICKNAME"));
        assertEquals("200",   rows.get(0).get("SCORE"));
        assertEquals("Bob",   rows.get(1).get("NICKNAME"));
        assertEquals("90",    rows.get(1).get("SCORE"));

        /* Game 2:
         *  - Charlie   | 310
         *  - Alice     | 300
         *  - Bob       | 280
         */
        assertEquals("Charlie", rows.get(2).get("NICKNAME"));
        assertEquals("310",     rows.get(2).get("SCORE"));
        assertEquals("Alice",   rows.get(3).get("NICKNAME"));
        assertEquals("300",     rows.get(3).get("SCORE"));
        assertEquals("Bob",     rows.get(4).get("NICKNAME"));
        assertEquals("280",     rows.get(4).get("SCORE"));
    }

    @Test
    void getResults_ParametricGameID() throws IOException, SQLException {
        // set the Query
        String query = "SELECT * " +
                "FROM GAMES NATURAL JOIN RESULTS " +
                "WHERE GAMES.GameID = ? " +
                "ORDER BY Score DESC";

        // Act
        List<Map<String, String>> rows;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            rows = DBConnection.getResults(statement,1);
        }

        /* Game 1:
         *  - Alice | 200
         *  - Bob   | 90
         */
        assertEquals(2, rows.size());
        assertTrue(rows.stream().allMatch(r -> r.get("GAMEID").equals("1")));
        assertEquals("Alice", rows.get(0).get("NICKNAME"));
        assertEquals("200",   rows.get(0).get("SCORE"));
        assertEquals("Bob",   rows.get(1).get("NICKNAME"));
        assertEquals("90",    rows.get(1).get("SCORE"));
    }

    @Test
    void getResults_ParametricNickname() throws IOException, SQLException {
        // set the Query
        String query = "SELECT * " +
                "FROM GAMES NATURAL JOIN RESULTS " +
                "WHERE Nickname = ? " +
                "ORDER BY Score DESC";

        // Act
        List<Map<String, String>> rows;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            rows = DBConnection.getResults(statement,"Alice");
        }

        /* Game 1:
         *  - Alice | 200
         *  - Bob   | 90
         */
        assertEquals("Alice", rows.get(0).get("NICKNAME"));
        assertEquals("300",   rows.get(0).get("SCORE"));

        /* Game 2:
         *  - Charlie   | 310
         *  - Alice     | 300
         *  - Bob       | 280
         */
        assertEquals("Alice",   rows.get(1).get("NICKNAME"));
        assertEquals("200",     rows.get(1).get("SCORE"));
    }

    @Test
    void getResults_NullParameter() throws IOException, SQLException {
        // set the query
        String query = "SELECT * " +
                "FROM GAMES NATURAL JOIN RESULTS " +
                "WHERE GAMES.GameID = ? " +
                "ORDER BY Score DESC";

        // Act
        List<Map<String, String>> rows;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            rows = DBConnection.getResults(statement, (Object) null);
        }

        // Assert
        assertEquals(0, rows.size());
    }

    @Test
    void getResults_EmptyResult() throws IOException, SQLException {
        // set the Query
        String query = "SELECT * " +
                "FROM GAMES NATURAL JOIN RESULTS " +
                "WHERE GAMES.GameID = ? " +
                "ORDER BY Score DESC";

        // Act
        List<Map<String, String>> rows;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            rows = DBConnection.getResults(statement,999);
        }

        // Assert
        assertTrue(rows.isEmpty());
    }

    @Test
    void setQuery() throws IOException, SQLException {
        // set the Query
        String query = "SELECT * " +
                "FROM GAMES NATURAL JOIN RESULTS " +
                "WHERE GAMES.GameID = ? AND Nickname != ? AND Score < ?";

        // Act
        List<Map<String, String>> rows;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            rows = DBConnection.getResults(statement, 1, "Alice", 350.5);
        }

        /* Game 1:
         *  - Alice | 200
         *  - Bob   | 90
         */
        assertEquals(1, rows.size());
        assertEquals("1",       rows.getFirst().get("GAMEID"));
        assertEquals("Bob",     rows.getFirst().get("NICKNAME"));
        assertEquals("90",      rows.getFirst().get("SCORE"));
    }

    @Test
    void setQuery_Default() throws IOException, SQLException {
        // set the Query
        String query = "SELECT * " +
                "FROM GAMES NATURAL JOIN RESULTS " +
                "WHERE GAMES.GameID = ? AND Score < ?";

        // Act
        List<Map<String, String>> rows;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            rows = DBConnection.getResults(statement, 1, 200L);
        }

        /* Game 1:
         *  - Alice | 200
         *  - Bob   | 90
         */
        assertEquals(1, rows.size());
        assertEquals("1",       rows.getFirst().get("GAMEID"));
        assertEquals("Bob",     rows.getFirst().get("NICKNAME"));
        assertEquals("90",      rows.getFirst().get("SCORE"));
    }

    @Test
    void updateSchema_Insert() throws IOException, SQLException {
        // set the Query
        String queryGAMES = "INSERT INTO GAMES (PlayersNumber) VALUES (?)";
        String queryGameID = "SELECT max(GameID) FROM GAMES";
        String queryUPDATE = "INSERT INTO RESULTS (GameID, Nickname, Score) VALUES (?, ?, ?)";
        String queryResults = "SELECT * FROM RESULTS WHERE GameID = ?";
        List<String> nicknames = new ArrayList<>();
        nicknames.add("Alice");
        nicknames.add("Bob");

        // Act
        Integer gameID;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(queryGAMES)) {
            DBConnection.updateSchema(statement, nicknames.size());
        }
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(queryGameID)) {
            List<Map<String, String>> rows = DBConnection.getResults(statement);
            gameID = Integer.valueOf(rows.getFirst().get("MAX(GAMEID)"));
        }
        for (String nickname : nicknames) {
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(queryUPDATE)) {
                DBConnection.updateSchema(statement, gameID, nickname, nicknames.indexOf(nickname) * 100);
            }
        }

        // Assert
        List<Map<String, String>> rows;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(queryResults)) {
            rows = DBConnection.getResults(statement, gameID);
        }
        assertFalse(rows.isEmpty());
        assertEquals(nicknames.size(), rows.size());
        assertTrue(rows.stream().allMatch(row -> row.get("GAMEID").equals(gameID.toString())));
        assertEquals("Alice",   rows.get(0).get("NICKNAME"));
        assertEquals("0",       rows.get(0).get("SCORE"));
        assertEquals("Bob",     rows.get(1).get("NICKNAME"));
        assertEquals("100",     rows.get(1).get("SCORE"));
    }
}