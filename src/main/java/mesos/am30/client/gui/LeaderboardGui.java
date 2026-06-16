package mesos.am30.client.gui;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

/**
 * Controller for the leaderboard scene, shown at end of game.
 * <br/>Manages the scene swap and populates the ranking list dynamically.
 */
public class LeaderboardGui {
    private static Scene table;
    private static AnchorPane gameTable;

    @FXML   private Label youLabel;
    @FXML   private VBox leaderboard;
    @FXML   private AnchorPane gameLeaderboard;

    /**
     * Initializes the static references to the main scene and game table root.
     * <br/><strong>Pre:</strong> setTable != null
     * <br/><strong>Pre:</strong> setGameTable != null
     *
     * @param setTable The main scene.
     * @param setGameTable The game table root node.
     */
    public static void set(Scene setTable, Parent setGameTable){
        table = setTable;
        gameTable = (AnchorPane) setGameTable;
    }

    /**
     * Switches to the leaderboard scene and populates it with ranking data.
     * <br/><strong>Pre:</strong> playerRank != null
     * <br/><strong>Pre:</strong> globalRankings != null
     *
     * @param playerRank The current player's rank entry.
     * @param globalRankings All players' rankings in order.
     */
    public void showRankings(Map<String, String> playerRank, List<Map<String, String>> globalRankings) {
        table.setRoot(gameLeaderboard);
        youLabel.setText("#" + playerRank.get("RANK") + " | " + playerRank.get("Nickname").toUpperCase() + " | " + playerRank.get("Score") + "PT");
        leaderboard.getChildren().clear();
        for (Map<String, String> ranking : globalRankings) {
            Label text = new Label();
            text.setStyle("-fx-font-size: 30; -fx-font-family: 'ChristmasChalk'");
            text.setText("#" + ranking.get("RANK") + " | " + ranking.get("Nickname").toUpperCase() + " | " + ranking.get("Score") + "PT");
            leaderboard.getChildren().add(text);
        }
    }

    /**
     * Returns to the game table scene.
     */
    @FXML
    public void back(){
        leaderboard.getChildren().clear();
        table.setRoot(gameTable);
    }
}
