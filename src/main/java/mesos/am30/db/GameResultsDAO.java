package mesos.am30.db;

import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Game Results Data Access Object.
 * <br>This Class works as the DAO for the Game Results.
 * <br>It queries the DBConnection in order to get the results of the Ranking Query, then filters for the wanted request.
 *
 * @author LoreDN - Lorenzo Di Napoli
 * @version 1.0
 * @since 1.0
 */
public class GameResultsDAO {
    private static final String RANKING_QUERY = "SELECT Nickname, max(Score) AS Score " +
            "FROM GAMES NATURAL JOIN RESULTS " +
            "WHERE PlayersNumber = ? " +
            "GROUP BY Nickname " +
            "ORDER BY Score DESC";
    private static final String INSERT_GAME = "INSERT INTO GAMES " +
            "(PlayersNumber) " +
            "VALUES (?)";
    private static final String QUERY_GAMEID = "SELECT max(GameID) AS LASTID " +
            "FROM GAMES";
    private static final String INSERT_RESULTS = "INSERT INTO RESULTS " +
            "(GameID, Nickname, Score) " +
            "VALUES (?, ?, ?)";
    /**
     * Query the Global Ranking for games with N Players.
     * <br>This static method works by querying the DB, in order to get the Global Ranking of all Players (identified by the field "Nickname")
     * who have take part in at least one game with a given number of Players.
     * <br>For the Players who have taken part in more than one Game, will be taken in account only the best Score.
     * <br><strong>Pre:</strong> playersNumber >= 2 && playersNumber <= 5
     *
     * @param playersNumber Number of Players of the Games to take in account.
     *
     * @return List of rows like (NICKNAME, SCORE, RANK).
     * @throws IOException The DB cannot be instantiated correctly
     * @throws SQLException The DB connection cannot be established correctly
     */
    public static synchronized List<Map<String, String>> queryGlobalRanking(int playersNumber) throws IOException, SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(RANKING_QUERY);
        try {

            // get the Global Rankings
            statement.setInt(1, playersNumber);
            List<Map<String, String>> rows = DBConnection.getResults(statement);

            // set the Rankings
            for (int rank = 0; rank < rows.size(); rank++) {
                rows.get(rank).put("RANK", String.valueOf(rank + 1));
            }
            return rows;
        } finally {
            connection.close();
            statement.close();
        }
    }

    /**
     * Query the Rank of a Player for games with N Players.
     * <br>This static method works by querying the DB, in order to get the Rank of a given Player (identified by the field "Nickname")
     * who have take part in at least one game with a given number of Players.
     * <br>If the Player has taken part in more than one Game, will be taken in account only the best Score.
     * <br><strong>Pre:</strong> playersNumber >= 2 && playersNumber <= 5 && nickname != null
     *
     * @param playersNumber Number of Players of the Games to take in account
     * @param nickname Nickname of the Player
     *
     * @return Single Row like (NICKNAME, SCORE, RANK).
     * @throws IOException The DB cannot be instantiated correctly
     * @throws SQLException The DB connection cannot be established correctly
     */
    public static synchronized Map<String, String> queryPlayerRank(int playersNumber, String nickname) throws IOException, SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(RANKING_QUERY);
        try {
            // get the Global Rankings
            List<Map<String, String>> rows = DBConnection.getResults(statement, playersNumber);

            // filter for the Player Rank
            int rank;
            for (rank = 0; rank < rows.size(); rank++) {
                Map<String, String> row = rows.get(rank);
                if (row.containsValue(nickname)) {
                    row.put("RANK", String.valueOf(rank + 1));
                    return row;
                }
            }

            // Nickname not found
            return new LinkedHashMap<>();
        } finally {
            connection.close();
            statement.close();
        }
    }

    /**
     * Add the Results of a new Game to the DB.
     * <br>This static method works by first creating a new Game for the given number of players; then adding their Scores.
     * <br><strong>Pre:</strong> ( /forall playerResult : results; ; playerResults.get("Nickname") != null && playerResults.get("Score") != null )
     *
     * @param results Results of the Game as a List of Rows like (Nickname, Score)
     * @throws IOException The DB cannot be instantiated correctly
     * @throws SQLException The DB connection cannot be established correctly
     */
    public static synchronized void addNewResults(List<Map<String, String>> results) throws IOException, SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(INSERT_GAME);

        try {
            // add new Game
            DBConnection.updateSchema(statement, results.size());

            // got GameID
            statement = connection.prepareStatement(QUERY_GAMEID);
            List<Map<String, String>> rows = DBConnection.getResults(statement);
            int gameID = Integer.parseInt(rows.getFirst().get("LASTID"));

            // add new Results
            statement = connection.prepareStatement(INSERT_RESULTS);
            for (Map<String, String> playerResult : results) {
                DBConnection.updateSchema(statement, gameID, playerResult.get("Nickname"), Integer.valueOf(playerResult.get("Score")));
            }
        } finally {
            connection.close();
            statement.close();
        }
    }
}
