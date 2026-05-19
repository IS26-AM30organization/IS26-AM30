package mesos.am30.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mesos.am30.common.Move;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.Card;
import mesos.am30.gameModel.card.CharacterCard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class TableGui extends Gui {
    ViewModel vBoard;
    String nickname;
    VirtualView vView;

    @FXML    private Button upper1;
    @FXML    private Button upper2;
    @FXML    private Button upper3;
    @FXML    private Button upper4;
    @FXML    private Button upper5;
    @FXML    private Button upper6;
    @FXML    private Button upper7;
    @FXML    private Button upper8;
    @FXML    private Button upper9;
    @FXML    private Button lower1;
    @FXML    private Button lower2;
    @FXML    private Button lower3;
    @FXML    private Button lower4;
    @FXML    private Button lower5;
    @FXML    private Button lower6;
    @FXML    private Button lower7;
    @FXML    private Button lower8;
    @FXML    private Button lower9;
    @FXML    private Button upB1;
    @FXML    private Button upB2;
    @FXML    private Button upB3;
    @FXML    private Button upB4;
    @FXML    private Button upB5;
    @FXML    private Button downB1;
    @FXML    private Button downB2;
    @FXML    private Button downB3;
    @FXML    private Button downB4;
    @FXML    private Button downB5;

    @FXML    private Label turnLabel;
    @FXML    private Label eraLabel;

    @FXML    private HBox tiles;

    @FXML    private VBox player1;
    @FXML    private VBox player2;
    @FXML    private VBox player3;
    @FXML    private VBox player4;
    @FXML    private VBox player5;
    @FXML    private Label name1;
    @FXML    private Label name2;
    @FXML    private Label name3;
    @FXML    private Label name4;
    @FXML    private Label name5;
    @FXML    private Label pt1;
    @FXML    private Label pt2;
    @FXML    private Label pt3;
    @FXML    private Label pt4;
    @FXML    private Label pt5;
    @FXML    private Label food1;
    @FXML    private Label food2;
    @FXML    private Label food3;
    @FXML    private Label food4;
    @FXML    private Label food5;
    @FXML    private Label people1;
    @FXML    private Label people2;
    @FXML    private Label people3;
    @FXML    private Label people4;
    @FXML    private Label people5;
    @FXML    private Label buildnum1;
    @FXML    private Label buildnum2;
    @FXML    private Label buildnum3;
    @FXML    private Label buildnum4;
    @FXML    private Label buildnum5;
    @FXML    private Label build1;
    @FXML    private Label build2;
    @FXML    private Label build3;
    @FXML    private Label build4;
    @FXML    private Label build5;
    @FXML    private Label gathnum1;
    @FXML    private Label gathnum2;
    @FXML    private Label gathnum3;
    @FXML    private Label gathnum4;
    @FXML    private Label gathnum5;
    @FXML    private Label gath1;
    @FXML    private Label gath2;
    @FXML    private Label gath3;
    @FXML    private Label gath4;
    @FXML    private Label gath5;
    @FXML    private Label artnum1;
    @FXML    private Label artnum2;
    @FXML    private Label artnum3;
    @FXML    private Label artnum4;
    @FXML    private Label artnum5;
    @FXML    private Label art1;
    @FXML    private Label art2;
    @FXML    private Label art3;
    @FXML    private Label art4;
    @FXML    private Label art5;
    @FXML    private Label invnum1;
    @FXML    private Label invnum2;
    @FXML    private Label invnum3;
    @FXML    private Label invnum4;
    @FXML    private Label invnum5;
    @FXML    private Label inv1;
    @FXML    private Label inv2;
    @FXML    private Label inv3;
    @FXML    private Label inv4;
    @FXML    private Label inv5;
    @FXML    private Label shamnum1;
    @FXML    private Label shamnum2;
    @FXML    private Label shamnum3;
    @FXML    private Label shamnum4;
    @FXML    private Label shamnum5;
    @FXML    private Label sham1;
    @FXML    private Label sham2;
    @FXML    private Label sham3;
    @FXML    private Label sham4;
    @FXML    private Label sham5;
    @FXML    private Label huntnum1;
    @FXML    private Label huntnum2;
    @FXML    private Label huntnum3;
    @FXML    private Label huntnum4;
    @FXML    private Label huntnum5;
    @FXML    private Label hunt1;
    @FXML    private Label hunt2;
    @FXML    private Label hunt3;
    @FXML    private Label hunt4;
    @FXML    private Label hunt5;
    @FXML    private HBox invlist1;
    @FXML    private HBox invlist2;
    @FXML    private HBox invlist3;
    @FXML    private HBox invlist4;
    @FXML    private HBox invlist5;

    private List<Button> uppers;
    private List<Button> lowers;
    private List<Button> upBs;
    private List<Button> downBs;
    private HashMap<String, Image> arts;
    private HashMap<String, Image> frames;
    private HashMap<Player, String> colors;
    private VBox[] players;
    private Label[] pNames;
    private Label[] pPts;
    private Label[] pFoods;
    private Label[] pPeoples;
    private Label[] pBuildnums;
    private Label[] pBuilds;
    private Label[] pGathnums;
    private Label[] pGaths;
    private Label[] pArtnums;
    private Label[] pArts;
    private Label[] pInvnums;
    private Label[] pInvs;
    private Label[] pShamnums;
    private Label[] pShams;
    private Label[] pHuntnums;
    private Label[] pHunts;
    private HBox[] pInvlists;


    @FXML
    public void initialize(){
        uppers = new ArrayList<>(List.of(upper1,upper2,upper3,upper4,upper5,upper6,upper7,upper8,upper9));
        lowers = new ArrayList<>(List.of(lower1,lower2,lower3,lower4,lower5,lower6,lower7,lower8,lower9));
        upBs = new ArrayList<>(List.of(upB1,upB2,upB3,upB4,upB5));
        downBs = new ArrayList<>(List.of(downB1,downB2,downB3,downB4,downB5));
        arts = new HashMap<>();
        frames = new HashMap<>();
        players = new VBox[]{player1, player2, player3, player4, player5};
        pNames = new Label[]{name1,name2,name3,name4,name5};
        pPts = new Label[]{pt1,pt2,pt3,pt4,pt5};
        pFoods = new Label[]{food1,food2,food3,food4,food5};
        pPeoples = new Label[]{people1,people2,people3,people4,people5};
        pBuildnums = new Label[]{buildnum1,buildnum2,buildnum3,buildnum4,buildnum5};
        pBuilds = new Label[]{build1,build2,build3,build4,build5};
        pGathnums = new Label[]{gathnum1,gathnum2,gathnum3,gathnum4};
        pGaths = new Label[]{gath1,gath2,gath3,gath4,gath5};
        pArtnums = new Label[]{artnum1,artnum2,artnum3,artnum4};
        pArts = new Label[]{art1,art2,art3,art4,art5};
        pInvnums = new Label[]{invnum1,invnum2,invnum3,invnum4};
        pInvs = new Label[]{inv1,inv2,inv3,inv4,inv5};
        pShamnums = new Label[]{shamnum1,shamnum2,shamnum3,shamnum4};
        pShams = new Label[]{sham1,sham2,sham3,sham4,sham5};
        pHuntnums = new Label[]{huntnum1,huntnum2,huntnum3,huntnum4};
        pHunts = new Label[]{hunt1,hunt2,hunt3,hunt4,hunt5};
        pInvlists = new HBox[]{invlist1,invlist2,invlist3,invlist4, invlist5};
    }

    public void setBoard(ViewModel vBoard) {
        this.vBoard = vBoard;
        colors = new HashMap<>();
        List<String> colorsToPickFrom = new ArrayList<String>(List.of("F15C3E","00A1C1","FFCD28","410B2C","EDEDEC"));
        int i = 0;
        for(Player p : vBoard.getPlayers()){
            colors.put(p, colorsToPickFrom.get(i));
            i++;
        }
    }

    public void setView(VirtualView view){
        this.vView = view;
    }

    public void refresh(ViewModel viewModel) {
        vBoard = viewModel;

        //setting upperRow
        for (int i = 0; i<9; i++){
            if (i>=vBoard.getUpperRow().size()){
                uppers.get(i).setVisible(false);
                uppers.get(i).setManaged(false);
            }
            else{
                uppers.get(i).setVisible(true);
                uppers.get(i).setManaged(true);
                Card card = vBoard.getUpperRow().get(i);
                ((ImageView) ((StackPane) uppers.get(i).getGraphic()).getChildren().get(0)).setImage(loadArt(card));
                if (card.isPickable()) {
                    ((StackPane) uppers.get(i).getGraphic()).getChildren().get(1).setVisible(true);
                    ((ImageView) ((StackPane) uppers.get(i).getGraphic()).getChildren().get(1)).setImage(loadFrame(card));
                } else
                    ((StackPane) uppers.get(i).getGraphic()).getChildren().get(1).setVisible(false);
            }
        }

        //setting lowerRow
        for (int i = 0; i<9; i++){
            if (i>=vBoard.getLowerRow().size()){
                lowers.get(i).setVisible(false);
                lowers.get(i).setManaged(false);
            }
            else{
                lowers.get(i).setVisible(true);
                lowers.get(i).setManaged(true);
                Card card = vBoard.getLowerRow().get(i);
                ((ImageView) ((StackPane) lowers.get(i).getGraphic()).getChildren().get(0)).setImage(loadArt(card));
                if (card.isPickable()) {
                    ((StackPane) lowers.get(i).getGraphic()).getChildren().get(1).setVisible(true);
                    ((ImageView) ((StackPane) lowers.get(i).getGraphic()).getChildren().get(1)).setImage(loadFrame(card));
                } else
                    ((StackPane) lowers.get(i).getGraphic()).getChildren().get(1).setVisible(false);
            }
        }

        //setting upperBuildings
        for (int i = 0; i<5; i++){
            if (i >=vBoard.getUpperBuildings().size()){
                upBs.get(i).setVisible(false);
                upBs.get(i).setManaged(false);
            }
            else {
                upBs.get(i).setVisible(true);
                upBs.get(i).setManaged(true);
                BuildingCard card = vBoard.getUpperBuildings().get(i);
                ((ImageView) ((StackPane) upBs.get(i).getGraphic()).getChildren().get(0)).setImage(loadArt(card));
                ((StackPane) upBs.get(i).getGraphic()).getChildren().get(1).setVisible(false);
            }
        }

        //setting lowerBuildings
        for (int i = 0; i<5; i++){
            if (i >=vBoard.getLowerBuildings().size()){
                downBs.get(i).setVisible(false);
                downBs.get(i).setManaged(false);
            }
            else {
                downBs.get(i).setVisible(true);
                downBs.get(i).setManaged(true);
                BuildingCard card = vBoard.getLowerBuildings().get(i);
                ((ImageView) ((StackPane) downBs.get(i).getGraphic()).getChildren().get(0)).setImage(loadArt(card));
                ((StackPane) downBs.get(i).getGraphic()).getChildren().get(1).setVisible(false);
            }
        }

        //setting tiles
        for (int i = 0; i<vBoard.getTiles().size(); i++){
            Optional<Player> p = vBoard.getTiles().get(i).getCurrentPlayer();
            if (p.isPresent()){
                ((Button) tiles.getChildren().get(i)).setStyle("-fx-background-color: #" +colors.get(p.get()));
            } else
                ((Button) tiles.getChildren().get(i)).setStyle("");
        }

        //setting era
        eraLabel.setText("ERA " +
                (switch (Math.max(vBoard.getUpperRow().getLast().getEra(), vBoard.getUpperRow().get(vBoard.getUpperRow().size() - 2).getEra())) {
                    default -> "0";
                    case 1 -> "I";
                    case 2 -> "II";
                    case 3 -> "III";
                }));

        //setting players
        for (int i =0; i < vBoard.getPlayers().size(); i++){
            Player p = vBoard.getPlayers().get(i);
            pPts[i].setText(""+p.getParameters().get(Parameter.PRESTIGE_POINTS));
            pFoods[i].setText(""+p.getParameters().get(Parameter.FOOD));
            pPeoples[i].setText(""+p.getTribe().values().stream().flatMap(List::stream).toList().size());
            pBuildnums[i].setText(""+p.getTribe().get(Parameter.BUILDER).size());
            pBuilds[i].setText(""+p.getParameters().get(Parameter.BUILDER));
            pGathnums[i].setText(""+p.getTribe().get(Parameter.GATHERER).size());
            pGaths[i].setText(""+p.getParameters().get(Parameter.GATHERER));
            pArtnums[i].setText(""+p.getTribe().get(Parameter.ARTIST).size());
            pArts[i].setText(""+p.getParameters().get(Parameter.ARTIST));
            pInvnums[i].setText(""+p.getTribe().get(Parameter.INVENTOR).size());
            pInvs[i].setText(""+p.getParameters().get(Parameter.INVENTOR));
            pShamnums[i].setText(""+p.getTribe().get(Parameter.SHAMAN).size());
            pShams[i].setText(""+p.getParameters().get(Parameter.SHAMAN));
            pHuntnums[i].setText(""+p.getTribe().get(Parameter.HUNTER).size());
            pHunts[i].setText(""+p.getParameters().get(Parameter.HUNTER));
            for (int bern = 0; bern < 10; bern++){
                if (p.getInventions().contains(bern))
                pInvlists[i].getChildren().get(bern).setOpacity(1);
                else
                pInvlists[i].getChildren().get(bern).setOpacity(0.2);
            }
        }
    }

    public void setName(String nickname){
        this.nickname = nickname;
    }

    public void printMove(String nickname, Move move){
        Platform.runLater(() -> {
            if (nickname.equals(this.nickname)) {
                turnLabel.setText("YOUR TURN TO " + getMove(move, true) + "!");
            } else
                turnLabel.setText(nickname.toUpperCase() + " HAS TO " + getMove(move, false));
        });
    }

    private String getMove(Move move, Boolean me){
        Platform.runLater(() -> {
            for (Button card : uppers){ card.setDisable(true); card.setOpacity(0.8);}
            for (Button card : lowers){ card.setDisable(true); card.setOpacity(0.8);}
            for (Button card : upBs){ card.setDisable(true); card.setOpacity(0.8);}
            for (Button card : downBs){ card.setDisable(true); card.setOpacity(0.8);}
            tiles.setDisable(true); tiles.setOpacity(0.9);
            switch (move){
                case move.PICK_FROM_DOWN -> {
                    for (Button card : lowers){ card.setDisable(!me); if(me) card.setOpacity(1);}
                    for (Button card : downBs){ card.setDisable(!me); if(me) card.setOpacity(1);}
                }
                case move.PICK_FROM_UP -> {
                    for (Button card : uppers){ card.setDisable(!me); if(me) card.setOpacity(1);}
                    for (Button card : upBs){ card.setDisable(!me); if(me) card.setOpacity(1);}
                }
                case move.PICK_ANY_CARD ->  {
                    for (Button card : uppers){ card.setDisable(!me); if(me) card.setOpacity(1);}
                    for (Button card : lowers){ card.setDisable(!me); if(me) card.setOpacity(1);}
                    for (Button card : upBs){ card.setDisable(!me); if(me) card.setOpacity(1);}
                    for (Button card : downBs){ card.setDisable(!me); if(me) card.setOpacity(1);}
                }
                case move.PICK_TILE ->  {
                    tiles.setDisable(!me); tiles.setOpacity(1);
                }
            };
        });
        return switch (move) {
            case move.PICK_FROM_DOWN -> "PICK FROM DOWN";
            case move.PICK_FROM_UP -> "PICK FROM UP";
            case move.PICK_ANY_CARD -> "PICK ANY CARD";
            case move.PICK_TILE -> "CHOOSE A TILE";
        };
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
                    if (tempBoard.getTiles().get(i).getUpArrows() < 1) {
                        ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(1).setVisible(false);
                        ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(1).setManaged(false);
                    }
                    if (tempBoard.getTiles().get(i).getUpArrows() < 2) {
                        ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(2).setVisible(false);
                        ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(2).setManaged(false);
                    }
                    if (tempBoard.getTiles().get(i).getDownArrows() < 1) {
                        ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(3).setVisible(false);
                        ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(3).setManaged(false);
                    }
                    if (tempBoard.getTiles().get(i).getDownArrows() < 2) {
                        ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(4).setVisible(false);
                        ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(4).setManaged(false);
                    }
                }
            }
            for (int i = 0; i<5; i++){
                if(i>=tempBoard.getPlayers().size()){
                    players[i].setVisible(false);
                    players[i].setManaged(false);
                }
                else {
                    pNames[i].setText(tempBoard.getPlayers().get(i).getNickname() +
                            (tempBoard.getPlayers().get(i).getNickname().equals(nickname) ?
                                    " (YOU)" : ""));
                }
            }
        });
    }

    @FXML public void up1() {upcCard(1);}
    @FXML public void up2() {upcCard(2);}
    @FXML public void up3() {upcCard(3);}
    @FXML public void up4() {upcCard(4);}
    @FXML public void up5() {upcCard(5);}
    @FXML public void up6() {upcCard(6);}
    @FXML public void up7() {upcCard(7);}
    @FXML public void up8() {upcCard(8);}
    @FXML public void up9() {upcCard(9);}
    @FXML public void down1() {downcCard(1);}
    @FXML public void down2() {downcCard(2);}
    @FXML public void down3() {downcCard(3);}
    @FXML public void down4() {downcCard(4);}
    @FXML public void down5() {downcCard(5);}
    @FXML public void down6() {downcCard(6);}
    @FXML public void down7() {downcCard(7);}
    @FXML public void down8() {downcCard(8);}
    @FXML public void down9() {downcCard(9);}
    @FXML public void upb1() {upbCard(1);}
    @FXML public void upb2() {upbCard(2);}
    @FXML public void upb3() {upbCard(3);}
    @FXML public void upb4() {upbCard(4);}
    @FXML public void upb5() {upbCard(5);}
    @FXML public void downb1() {downbCard(1);}
    @FXML public void downb2() {downbCard(2);}
    @FXML public void downb3() {downbCard(3);}
    @FXML public void downb4() {downbCard(4);}
    @FXML public void downb5() {downbCard(5);}
    @FXML public void til1() {tile(1);}
    @FXML public void til2() {tile(2);}
    @FXML public void til3() {tile(3);}
    @FXML public void til4() {tile(4);}
    @FXML public void til5() {tile(5);}
    @FXML public void til6() {tile(6);}
    @FXML public void til7() {tile(7);}

    private void upcCard(int i){
        try {
            vView.checkCharacterCard((CharacterCard) vBoard.getUpperRow().get(i-1));
        } catch (IOException ex) {
            String old = eraLabel.getText();
            eraLabel.setText(old+ " (RETRY)");
        }
    }

    private void downcCard(int i){
        try {
            vView.checkCharacterCard((CharacterCard) vBoard.getLowerRow().get(i-1));
        } catch (IOException ex) {
            String old = eraLabel.getText();
            eraLabel.setText(old+ " (RETRY)");
        }
    }

    private void upbCard(int i){
        try {
            vView.checkBuildingCard(vBoard.getUpperBuildings().get(i-1));
        } catch (IOException ex) {
            String old = eraLabel.getText();
            eraLabel.setText(old+ " (RETRY)");
        }
    }

    private void downbCard(int i){
        try {
            vView.checkBuildingCard(vBoard.getLowerBuildings().get(i-1));
        } catch (IOException ex) {
            String old = eraLabel.getText();
            eraLabel.setText(old+ " (RETRY)");
        }
    }

    private void tile(int i){
        try {
            vView.checkTile(vBoard.getTiles().get(i-1));
        } catch (IOException ex) {
            String old = eraLabel.getText();
            eraLabel.setText(old+ " (RETRY)");
        }
    }

    private Image loadArt(Card card){
        String art = card.getArt();
        System.out.println(art);
        if (!arts.containsKey(art))
            arts.put(art,new Image(getClass().getResource("/images/"+ art + ".png").toExternalForm()));

            return arts.get(art);
        }

    private Image loadFrame(Card card){
        String frame = card.getFrame();
        System.out.println(frame);
        if(!frames.containsKey(frame))
            frames.put(frame, new Image(getClass().getResource("/images/"+ frame + ".png").toExternalForm()));

        return frames.get(frame);
    }

}
