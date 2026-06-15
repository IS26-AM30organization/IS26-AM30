package mesos.am30.client.gui;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

public class LeaderboardGui {
    private static Scene table;
    private static AnchorPane gameTable;

    @FXML   private Label youLabel;
    @FXML   private VBox leaderboard;
    @FXML   private AnchorPane gameLeaderboard;

    public static void set(Scene setTable, Parent setGameTable){
        table = setTable;
        gameTable = (AnchorPane) setGameTable;
    }

    public void showRankings(Map<String, String> playerRank, List<Map<String, String>> globalRankings) {
        table.setRoot(gameLeaderboard);
        youLabel.setText("#" + playerRank.get("RANK") + " | " + playerRank.get("Nickname").toUpperCase() + " | " + playerRank.get("Score") + "PT");
        for (Map<String, String> ranking : globalRankings) {
            leaderboard.getChildren().clear();
            Label text = new Label();
            text.setStyle("-fx-font-size: 30; -fx-font-family: 'ChristmasChalk'");
            text.setText("#" + ranking.get("RANK") + " | " + ranking.get("Nickname").toUpperCase() + " | " + ranking.get("Score") + "PT");
            leaderboard.getChildren().add(text);
        }
    }

    @FXML
    public void back(){
        leaderboard.getChildren().clear();
        table.setRoot(gameTable);
    }
}
