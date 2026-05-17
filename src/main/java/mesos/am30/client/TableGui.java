package mesos.am30.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import mesos.am30.gameModel.card.Card;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TableGui extends Gui {
    ViewModel vBoard;


    @FXML
    private Button upper1;
    @FXML
    private Button upper2;
    @FXML
    private Button upper3;
    @FXML
    private Button upper4;
    @FXML
    private Button upper5;
    @FXML
    private Button upper6;
    @FXML
    private Button upper7;
    @FXML
    private Button upper8;
    @FXML
    private Button upper9;
    @FXML
    private Button lower1;
    @FXML
    private Button lower2;
    @FXML
    private Button lower3;
    @FXML
    private Button lower4;
    @FXML
    private Button lower5;
    @FXML
    private Button lower6;
    @FXML
    private Button lower7;
    @FXML
    private Button lower8;
    @FXML
    private Button lower9;

    @FXML
    private HBox tiles;

    private List<Button> uppers;
    private List<Button> lowers;

    @FXML
    public void initialize(){
        uppers = new ArrayList<>(List.of(upper1,upper2,upper3,upper4,upper5,upper6,upper7,upper8,upper9));
        lowers = new ArrayList<>(List.of(lower1,lower2,lower3,lower4,lower5,lower6,lower7,lower8,lower9));
    }

    public void setBoard(ViewModel vBoard) {
        this.vBoard = vBoard;
    }

    public void refresh(ViewModel viewModel) {
        vBoard = viewModel;


    }

    public void createTable(){
        ViewModel tempBoard = vBoard;
        Platform.runLater(() -> {
            for(int i = 0; i<7; i++){
                System.out.println(i + "/" + tempBoard.getTiles().size());
                if (i>=tempBoard.getTiles().size()) {
                    tiles.getChildren().get(i).setVisible(false);
                    tiles.getChildren().get(i).setManaged(false);
                    //System.out.println(tiles.getChildren().get(i));
                }
                else {
                    if (tempBoard.getTiles().get(i).getFood() == null) {
                        ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(0).setVisible(false);
                        ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(0).setManaged(false);
                    }
                    if (tempBoard.getTiles().get(i).getUpArrows() == 0) {
                        ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(1).setVisible(false);
                        ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(1).setManaged(false);
                        ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(2).setVisible(false);
                        ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(2).setManaged(false);
                    }
                    else if (tempBoard.getTiles().get(i).getUpArrows() == 1) {
                        ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(2).setVisible(false);
                        ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(2).setManaged(false);
                    }
                    if (tempBoard.getTiles().get(i).getDownArrows() == 0) {
                        ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(3).setVisible(false);
                        ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(3).setManaged(false);
                        ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(4).setVisible(false);
                        ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(4).setManaged(false);
                    }
                    else if (tempBoard.getTiles().get(i).getDownArrows() == 1) {
                        ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(4).setVisible(false);
                        ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(4).setManaged(false);
                    }
                }
            }

            //setting upperRow
            uppers = List.of(upper1, upper2, upper3, upper4, upper5, upper6, upper7, upper8, upper9);
            lowers = List.of(lower1, lower2, lower3, lower4, lower5, lower6, lower7, lower8, lower9);
            for (int i = 0; i<9; i++){
                if (i>=tempBoard.getUpperRow().size()){
                    uppers.get(i).setVisible(false);
                    uppers.get(i).setManaged(false);
                }
                else{
                    uppers.get(i).setVisible(true);
                    uppers.get(i).setManaged(true);
                    Card card = tempBoard.getUpperRow().get(i);
                    if (card.isPickable()) {
                        ((ImageView) ((StackPane) uppers.get(i).getGraphic()).getChildren().get(0)).setImage(loadArt(card));
                        ((ImageView) ((StackPane) uppers.get(i).getGraphic()).getChildren().get(1)).setImage(loadFrame(card));
                    }
                }
            }
        });
    }

    private Image loadArt(Card card){
        System.out.println(card.getArt());
        return new Image(getClass().getResource("/images/"+ card.getArt() + ".png").toExternalForm());
    }

    private Image loadFrame(Card card){
        System.out.println(card.getFrame());
        return new Image(getClass().getResource("/images/"+ card.getFrame() + ".png").toExternalForm());
    }
}
