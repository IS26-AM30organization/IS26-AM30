package mesos.am30.db;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class GameResultsDAOTest {
    private static final String URL  = "jdbc:h2:mem:dao-test;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

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

    @BeforeEach
    void setUp() throws IOException, SQLException {
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement()) {

            // reset the DB
            statement.execute("DELETE FROM GAMES WHERE GameID > 0");
            statement.execute("ALTER TABLE GAMES ALTER COLUMN GameID RESTART WITH 1");
            statement.execute("INSERT INTO GAMES (PlayersNumber) VALUES (2), (3), (2)");
            statement.execute("""
                INSERT INTO RESULTS (GameID, Nickname, Score) VALUES
                    (1, 'Alice', 200),
                    (1, 'Bob',   90),
                    (2, 'Alice', 300),
                    (2, 'Bob',   280),
                    (2, 'Charlie', 310),
                    (3, 'Alice', 250),
                    (3, 'Bob',   180)
            """);
        }
    }

    @Test
    void queryGlobalRanking_Count() throws IOException, SQLException {
        // Act
        List<Map<String, String>> rows = GameResultsDAO.queryGlobalRanking(2);

        // Assert
        assertEquals(2, rows.size());
    }

    @Test
    void queryGlobalRanking_orderedByScoreDesc() throws Exception {
        // Act
        List<Map<String, String>> rows = GameResultsDAO.queryGlobalRanking(2);

        // Assert
        int first  = Integer.parseInt(rows.get(0).get("SCORE"));
        int second = Integer.parseInt(rows.get(1).get("SCORE"));
        assertTrue(first >= second);
    }

    @Test
    void queryGlobalRanking_takesBestScore() throws Exception {
        // Act
        List<Map<String, String>> rows = GameResultsDAO.queryGlobalRanking(2);

        // Assert
        assertEquals("Alice",   rows.getFirst().get("NICKNAME"));
        assertEquals("250",     rows.getFirst().get("SCORE"));
        assertEquals("1",       rows.getFirst().get("RANK"));
        assertEquals("Bob",     rows.getLast().get("NICKNAME"));
        assertEquals("180",     rows.getLast().get("SCORE"));
        assertEquals("2",       rows.getLast().get("RANK"));
    }

    @Test
    void queryGlobalRanking_Empty() throws Exception {
        // Act
        List<Map<String, String>> rows = GameResultsDAO.queryGlobalRanking(4);

        // Assert
        assertTrue(rows.isEmpty());
    }

    @Test
    void queryPlayerRank_SinglePlayer() throws IOException, SQLException {
        // Act
        Map<String,String> row = GameResultsDAO.queryPlayerRank(2, "Bob");

        // Assert
        assertEquals(3, row.size());
        assertEquals("Bob",     row.get("NICKNAME"));
        assertEquals("180",     row.get("SCORE"));
        assertEquals("2",       row.get("RANK"));
    }

    @Test
    void queryPlayerRank_Empty() throws IOException, SQLException {
        // Act
        Map<String,String> row = GameResultsDAO.queryPlayerRank(2, "Unknown");

        // Assert
        assertTrue(row.isEmpty());
    }

    @Test
    void addNewResults() throws IOException, SQLException {
        // set the Results
        List<Map<String, String>> results = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            Map<String, String> playerResult = new LinkedHashMap<>(2);
            playerResult.put("Nickname", "Nickname" + i);
            playerResult.put("Score", String.valueOf(300 - (i * 50)));
            results.add(playerResult);
        }

        // Act
        GameResultsDAO.addNewResults(results);

        // Assert
        List<Map<String, String>> rows = GameResultsDAO.queryGlobalRanking(results.size());
        assertEquals(results.size(), rows.size());
        for (int i = 0; i < results.size(); i++) {
            Map<String, String> row = rows.get(i);
            Map<String, String> playerResult = results.get(i);
            assertEquals(playerResult.get("Nickname"), row.get("NICKNAME"));
            assertEquals(playerResult.get("Score"), row.get("SCORE"));
            assertEquals(String.valueOf(i + 1), row.get("RANK"));
        }
    }
}