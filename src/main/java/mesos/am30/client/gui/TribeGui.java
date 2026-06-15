package mesos.am30.client.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.Card;
import mesos.am30.gameModel.card.CharacterCard;

import java.util.List;

public class TribeGui {
    @FXML    Label name;
    @FXML    ImageView totem;
    @FXML    VBox box;
    @FXML    AnchorPane gameTribe;
    @FXML    HBox title;

    private static Scene table;
    private static AnchorPane gameTable;

    public static void set(Scene setTable, Parent setGameTable){
        table = setTable;
        gameTable = (AnchorPane) setGameTable;
    }

    public void show(Player player, int i){
        Platform.runLater(()->{
            table.setRoot(gameTribe);
            totem.setImage(ImageLoader.loadArt(player, i));
            name.setText(player.getNickname().toUpperCase());
            name.setStyle("-fx-background-color: #" + TableGui.colors.get(player) + "; -fx-text-fill: " + ((i==0)||(i==1)||(i==3) ? "#FFFFFF" : "#000000") + "; -fx-font-size: 30; -fx-background-radius: 30;");
            for (List<CharacterCard> cards : player.getTribe().values()) {
                HBox tribe = new HBox();
                tribe.setAlignment(Pos.CENTER);
                box.getChildren().add(tribe);
                for (CharacterCard card : cards) {
                    StackPane pane = new StackPane();
                    ImageView image = new ImageView(ImageLoader.loadArt(card));
                    image.setFitHeight(120);
                    image.setPreserveRatio(true);
                    pane.getChildren().add(image);
                    image = new ImageView(ImageLoader.loadFrame(card));
                    image.setFitHeight(120);
                    image.setPreserveRatio(true);
                    pane.getChildren().add(image);
                    tribe.getChildren().add(pane);
                }
            }
            HBox tribe = new HBox();
            tribe.setAlignment(Pos.CENTER);
            box.getChildren().add(tribe);
            for (BuildingCard card : player.getBuildings()) {
                StackPane pane = new StackPane();
                ImageView image = new ImageView(ImageLoader.loadArt(card));
                image.setFitHeight(120);
                image.setPreserveRatio(true);
                pane.getChildren().add(image);
                tribe.getChildren().add(pane);
            }
        });
    }

    @FXML
    public void back(){
        table.setRoot(gameTable);
        box.getChildren().retainAll(title);
    }
}
